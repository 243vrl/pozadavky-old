/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.planovani;

import cz.wenaaa.is243vrl.TypyDne;
import cz.wenaaa.is243vrl.TypySluzby;
import static cz.wenaaa.is243vrl.TypySluzby.BD;
import static cz.wenaaa.is243vrl.TypySluzby.BK;
import static cz.wenaaa.is243vrl.TypySluzby.BP;
import cz.wenaaa.is243vrl.beans.PlanovaniBean;
import cz.wenaaa.is243vrl.ejbs.LetajiciSluzby2Facade;
import cz.wenaaa.is243vrl.ejbs.PomtabFacade;
import cz.wenaaa.is243vrl.ejbs.PozadavkyFacade;
import cz.wenaaa.is243vrl.ejbs.SluzbyFacade;
import cz.wenaaa.is243vrl.entityClasses.LetajiciSluzby2;
import cz.wenaaa.is243vrl.entityClasses.Pomtab;
import cz.wenaaa.is243vrl.entityClasses.Pozadavky;
import cz.wenaaa.is243vrl.entityClasses.Sluzby;
import cz.wenaaa.utils.AI.ComparatorTypes;
import cz.wenaaa.utils.AI.SearchTreeFirstDepthThenValue;
import cz.wenaaa.utils.AI.NodeItemFactory;
import cz.wenaaa.utils.AI.ProcessInfo;
import cz.wenaaa.utils.Kalendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.event.ActionEvent;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author vena
 */
public class PlanovaniSluzeb implements NodeItemFactory<Slouzici> {

    public static float NAVYSENI_SVATKU = 1.5f;
    private static PlanovaniSluzeb pilotiThis = null;
    private static PlanovaniSluzeb palubariThis = null;
    private Thread searchThread;

    private PlanovaniSluzeb(boolean proPalubare) {
        planuji = false;
        naplanovano = false;
        chyba = false;
        prerusit = false;
        pocetReseni = 0;
        this.proPalubare = proPalubare;
        try {
            pf = (PozadavkyFacade) InitialContext.doLookup("java:global/pozadavky/PozadavkyFacade");
            pomf = (PomtabFacade) InitialContext.doLookup("java:global/pozadavky/PomtabFacade");
            sf = (SluzbyFacade) InitialContext.doLookup("java:global/pozadavky/SluzbyFacade");
            lsf = (LetajiciSluzby2Facade) InitialContext.doLookup("java:global/pozadavky/LetajiciSluzby2Facade");
        } catch (NamingException ex) {
            Logger.getLogger(PlanovaniSluzeb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static synchronized PlanovaniSluzeb getInstance(boolean proPalubare) {
        if (proPalubare) {
            if (palubariThis == null) {
                palubariThis = new PlanovaniSluzeb(proPalubare);
            }
            return palubariThis;
        }

        if (pilotiThis == null) {
            pilotiThis = new PlanovaniSluzeb(proPalubare);
        }
        return pilotiThis;
    }

    public static synchronized PlanovaniSluzeb createInstance(boolean proPalubare) {
        if (proPalubare) {
            if (palubariThis != null) {
                nullInstance(palubariThis);
            }
            palubariThis = new PlanovaniSluzeb(proPalubare);
            return palubariThis;
        }

        if (pilotiThis != null) {
            nullInstance(pilotiThis);
        }
        pilotiThis = new PlanovaniSluzeb(proPalubare);
        return pilotiThis;
    }

    private static void nullInstance(PlanovaniSluzeb instance) {
        instance.castecne = false;
        instance.chyba = false;
        instance.dnySvozu = null;
        instance.listSlouzici = null;
        instance.minulyMesic = null;
        instance.pInfo = null;
        instance.pb = null;
        instance.poradiSluzeb = null;
        instance.prerusit = true;
        instance.searchThread = null;
        instance.searchTree = null;
        instance.sluzbyKNaplanovani = null;
        instance.textInfo = null;
    }

    private PlanovaniBean pb;
    private volatile boolean castecne;
    private volatile boolean planuji;
    private volatile boolean naplanovano;
    private volatile boolean chyba;
    private volatile boolean prerusit;
    private boolean proPalubare;
    private int pocetReseni;
    private ProcessInfo<Slouzici> pInfo;
    private List<Sluzby> minulyMesic;
    private List<TypySluzby> sluzbyKNaplanovani;
    private List<SluzboDen> poradiSluzeb;
    private List<SluzboDen> poradiBechyne;
    private List<Slouzici> listSlouzici;
    private float idealPaSoNe;
    private int pocetPaSoNe;
    private int maxHloubka;

    private PozadavkyFacade pf;
    private PomtabFacade pomf;
    private SluzbyFacade sf;
    private LetajiciSluzby2Facade lsf;

    private boolean[] dnySvozu;
    private String[] textInfo = new String[TI_ERROR + 1];
    private static final int TI_ZACATEK = 0;
    private static final int TI_DB_CLOSE = 1;
    private static final int TI_NACTENI_SLOUZICICH = 2;
    private static final int TI_SLUZBY_K_NAPLANOVANI = 3;
    private static final int TI_PROCESS_INFO = 4;
    private static final int TI_STATUS = 5;
    private static final int TI_ERROR = 6;
    private SearchTreeFirstDepthThenValue<NodeItemFactory, Slouzici> searchTree;

    public void naplanuj2(GregorianCalendar gc, boolean[] dnysvozu) {
        //zamezeni dvojiteho volani
        if (planuji) {
            return;
        }
        searchTree = new SearchTreeFirstDepthThenValue(this);
        pInfo = new ProcessInfo();
        getInfo();
        pb.setCastecne(false);

        planuji = true;
        prerusit = false;
        naplanovano = false;
        dnySvozu = dnysvozu;
        maxHloubka = 0;
        textInfo = new String[TI_ERROR + 1];
        textInfo[TI_ZACATEK] = "PlĂˇnuji ...";
        try {
            uzavriDB(gc);
            System.out.println("nacitam slouzici............");
            listSlouzici = nactiSlouzici(gc);
            System.out.println("nacitam typy sluzeb...................");
            sluzbyKNaplanovani = nactiSluzbyKNaplanovani();
            System.out.println("nacitam sluzbodny....................");
            poradiSluzeb = nactiPoradiSluzeb(gc);
            System.out.println("nacitam minuly mesic................");
            minulyMesic = sf.nactiMinulyMesic(gc);
            //System.out.println("nacitam pocty typu dne..................");
            //nactiPoctyTypuDne(gc);
            System.out.println("searchTree.setProcessInfo(pInfo);..........." + searchTree);
            searchTree.setProcessInfo(pInfo);
            searchThread = new Thread(searchTree);
            try {
                System.out.println("spoustim vypocet THread.....................");
                searchThread.start();
                while (!searchTree.isRunning()) {
                    Thread.currentThread().sleep(100);
                }
                handlePlanning();
            } catch (InterruptedException ex) {
                Logger.getLogger(PlanovaniSluzeb.class.getName()).log(Level.SEVERE, null, ex);
            } finally {

            }

        } catch (VicJakJedenSlouziciException | NikdoNemuzeException | SpatnyPocetSluzboDnuException ex) {
            textInfo[TI_ERROR] = "Warning: " + ex.getMessage();
            chyba = true;
            Logger.getLogger(PlanovaniSluzeb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handlePlanning() {
        if (searchTree.isRunning()) {
            System.out.println("Search tree still running");
            if (prerusit) {
                searchTree.setPrerusit(prerusit);
                searchThread.interrupt();
            }
        } else {
            setridVysledek();
            printVysledek();
            vytiskniSlouzici();
            planuji = false;
            prerusit = false;
            naplanovano = false;
            chyba = false;
            System.out.println("prerusuji");
        }
        if (planuji) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handlePlanning();
                }
            }, 100);
        }
    }

    public void naplanuj(GregorianCalendar gc, boolean[] dnysvozu) {
        //zamezeni dvojiteho volani
        if (planuji) {
            return;
        }
        PlanovaniSluzeb instance = this;
        pInfo = new ProcessInfo();
        planuji = true;
        pb.setNenaplanovano(false);
        getInfo();
        Thread planThread = new Thread(new Runnable() {

            @Override
            public void run() {
                SearchTreeFirstDepthThenValue<NodeItemFactory, Slouzici> searchTree;
                searchTree = new SearchTreeFirstDepthThenValue(instance);
                pb.setCastecne(false);

                prerusit = false;
                naplanovano = false;
                dnySvozu = dnysvozu;
                maxHloubka = 0;
                textInfo = new String[TI_ERROR + 1];
                textInfo[TI_ZACATEK] = "Plánuji ...";
                try {
                    uzavriDB(gc);
                    System.out.println("nacitam slouzici............");
                    listSlouzici = nactiSlouzici(gc);
                    System.out.println("nacitam typy sluzeb...................");
                    sluzbyKNaplanovani = nactiSluzbyKNaplanovani();
                    System.out.println("nacitam sluzbodny....................");
                    poradiSluzeb = nactiPoradiSluzeb(gc);
                    System.out.println("nacitam minuly mesic................");
                    minulyMesic = sf.nactiMinulyMesic(gc);
                    //System.out.println("nacitam pocty typu dne..................");
                    //nactiPoctyTypuDne(gc);
                    nactiBechyni(gc);
                    System.out.println("searchTree.setProcessInfo(pInfo);..........." + searchTree);
                    searchTree.setProcessInfo(pInfo);
                    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
                    System.out.println("ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);................." + executor);

                    Future<List<Slouzici>> future;
                    try {
                        System.out.println("spoustim vypocet.....................");
                        future = executor.submit((Callable<List<Slouzici>>) searchTree);
                        //System.out.println("future is done......" + future.isDone());
                        while (!future.isDone()) {
                            if (prerusit) {
                                executor.shutdownNow();
                                //planuji = false;
                                prerusit = false;
                                naplanovano = false;
                                chyba = false;
                                System.out.println("prerusuji");
                                break;
                            }
                            //System.out.println("future is done......" + future.isDone());
                        }
                        setridVysledek();
                        printVysledek();
                        vytiskniSlouzici();
                        //planuji = false;
                        naplanovano = true;
                        chyba = false;
                        prerusit = false;
                    } finally {
                        executor.shutdown();
                    }

                } catch (VicJakJedenSlouziciException | NikdoNemuzeException | SpatnyPocetSluzboDnuException ex) {
                    textInfo[TI_ERROR] = "Warning: " + ex.getMessage();
                    chyba = true;
                    pb.setNenaplanovano(true);
                    Logger
                            .getLogger(PlanovaniSluzeb.class
                                    .getName()).log(Level.SEVERE, null, ex);
                }
            }

        });

        planThread.start();
    }

    public void setMe(PlanovaniBean pb) {
        this.pb = pb;
    }

    private void ulozVysledek(List<Slouzici> path) {
        try {
            if (pocetReseni == 0 || (hodnotaPath(path) < hodnotaVysledku())) {
                for (int i = 0; i < poradiSluzeb.size(); i++) {
                    poradiSluzeb.get(i).setSlouzici(path.get(i));
                }
                textInfo[TI_STATUS] = String.format("Naplánováno %d. řešení.", ++pocetReseni);
                System.out.format("reseni %d > %f", pocetReseni, hodnotaPath(path));
                pb.setCastecne(true);
            }
        } catch (Exception ex) {
            textInfo[TI_ERROR] = "Warning: " + ex.getMessage();
            chyba = true;
            Logger
                    .getLogger(PlanovaniSluzeb.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getText() {
        String vratka = "";
        for (String pol : textInfo) {
            vratka += pol + "\n";
        }
        return vratka;
    }

    private void setridVysledek() {
        poradiSluzeb.sort(new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                SluzboDen sd1 = (SluzboDen) o1;
                SluzboDen sd2 = (SluzboDen) o2;
                return sd1.getDatum().compareTo(sd2.getDatum()) != 0 ? sd1.getDatum().compareTo(sd2.getDatum()) : sd1.getTypsluzby().compareTo(sd2.getTypsluzby());
            }

        });
    }

    private void printVysledek() {
        System.out.println("--- Vysledek --------------------\n");
        Iterator<SluzboDen> it = poradiSluzeb.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (!proPalubare) {
                System.out.format("%d : %s %s | %s %s", ++i, it.next().slouzici.getJmeno(), it.next().slouzici.getJmeno(), it.next().slouzici.getJmeno(), it.next().slouzici.getJmeno());
            } else {
                System.out.format("%d : %s | %s", ++i, it.next().slouzici.getJmeno(), it.next().slouzici.getJmeno());
            }
        }
    }

    private void vytiskniSlouzici() {
        for (Slouzici sl : listSlouzici) {
            System.out.println(sl);
        }
    }

    public String getNaplanovanaSluzba(String jmeno, int den) {
        String vratka = "";
        for (SluzboDen pol : poradiSluzeb) {
            if (jmeno.equals(pol.getSlouzici().getJmeno()) && den == pol.getDatum().get(Calendar.DAY_OF_MONTH)) {
                vratka += pol.getTypsluzby().getsTypSluzby();
            }
        }
        return vratka;
    }

    public void setNaplanovanaSluzba(String sluzba, String jmeno, int den) {
        Slouzici slouzici = null;
        for (Slouzici sl : listSlouzici) {
            if (jmeno.equals(sl.getJmeno())) {
                slouzici = sl;
                break;
            }
        }
        for (SluzboDen pol : poradiSluzeb) {
            if (sluzba.equals(pol.getTypsluzby().getsTypSluzby()) && den == pol.getDatum().get(Calendar.DAY_OF_MONTH)) {
                pol.setSlouzici(slouzici);
            }
        }
    }

    public void ulozNaplanovane() {
        for (SluzboDen sl : poradiSluzeb) {
            sf.ulozSluzbu(sl.datum, sl.getTypsluzby(), sl.getSlouzici().getJmeno());
        }

    }

    public int[] pocetSluzeb(String letajici) {
        int[] vratka = new int[3];
        vratka[0] = 0;
        vratka[1] = 0;
        vratka[2] = 0;
        for (SluzboDen pol : poradiSluzeb) {
            if (pol.getSlouzici().getJmeno().equals(letajici)) {
                if (pol.getTypsluzby().getsTypSluzby().startsWith("L")) {
                    vratka[0]++;
                }
                if (pol.getTypsluzby().getsTypSluzby().startsWith("S")) {
                    vratka[1]++;
                }
                if (pol.getTypsluzby().getsTypSluzby().startsWith("B")) {
                    vratka[2]++;
                }
            }
        }
        return vratka;
    }

    public boolean isPlanuji() {
        return planuji;
    }

    private void getInfo2() {
        try {
            //System.out.println("jdu spinkat");
            Thread.sleep(1000);
        } catch (InterruptedException ex) {

        }
        if (pInfo.getLock().tryLock()) {
            try {
                long lc = pInfo.getLeafsCount();
                long le = pInfo.getLeafsEvolved();
                long ad = pInfo.getAktDepth();
                maxHloubka = (int) (ad > maxHloubka ? ad : maxHloubka);
                textInfo[TI_PROCESS_INFO] = String.format("Počet listů stromu řešení: %d Počet rozvitých listů: %d Akt. hloubka: %d Max. hloubka: %d Cíl. hloubka: %d", lc, le, ad, maxHloubka, poradiSluzeb.size());

                //System.out.println("vstavam");
            } finally {
                pInfo.getLock().unlock();
            }
        } else {
            System.out.println("planovani - neobdrzel lock");
        }
    }

    private void getInfo() {
        if (pInfo.getLock().tryLock()) {
            try {
                long lc = pInfo.getLeafsCount();
                long le = pInfo.getLeafsEvolved();
                long ad = pInfo.getAktDepth();
                maxHloubka = (int) (ad > maxHloubka ? ad : maxHloubka);
                if (poradiSluzeb != null) {
                    textInfo[TI_PROCESS_INFO] = String.format("Počet listů stromu řešení: %d Počet rozvitých listů: %d Akt. hloubka: %d Max. hloubka: %d Cíl. hloubka: %d", lc, le, ad, maxHloubka, poradiSluzeb.size());
                }
                //System.out.println("vstavam");
            } finally {
                pInfo.getLock().unlock();
            }
        } else {
            System.out.println("planovani - neobdrzel lock");
        }
        if (planuji && !chyba) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    getInfo();
                }
            }, 1000);
        }
    }

    public boolean isNaplanovano() {
        return naplanovano;
    }

    private void uzavriDB(GregorianCalendar gc) {
        List<Pomtab> pomtabL = pomf.findAll();
        Pomtab pomtab = pomtabL.get(0);
        textInfo[TI_DB_CLOSE] = "Uzavírám DB ...";
        if (proPalubare) {
            pomtab.setPozadavkyodpalubaci(gc.getTime());
        } else {
            pomtab.setPozadavkyodpiloti(gc.getTime());
        }
        try {
            pomf.edit(pomtab);
            textInfo[TI_DB_CLOSE] += "done";

        } catch (Exception ex) {
            Logger.getLogger(PlanovaniSluzeb.class
                    .getName()).log(Level.SEVERE, null, ex);
            textInfo[TI_DB_CLOSE] += "WARNING: " + ex.getMessage();
        }
    }

    private List<TypySluzby> nactiSluzbyKNaplanovani() {
        textInfo[TI_SLUZBY_K_NAPLANOVANI] = "Služby k naplánování ... úvodní načtení ... ";
        return proPalubare ? TypySluzby.getFEPlan() : TypySluzby.getPPlan();
    }

    private List<Slouzici> nactiSlouzici(GregorianCalendar gc) {
        textInfo[TI_NACTENI_SLOUZICICH] = "Načítámm sloužící ... ";
        List<LetajiciSluzby2> lsl = proPalubare ? lsf.getPalubaci() : lsf.getPiloti();
        List<Slouzici> vratka = new ArrayList<>();
        for (LetajiciSluzby2 pol : lsl) {
            Slouzici sl = new Slouzici(pol.getLetajici(), pol.getDojizdeni().getDojizdeni(), pol.getDoLiniSvozem(),
                    pol.getPocetPatku() / (float) pol.getPocetSluzeb(), pol.getPocetSobot() / (float) pol.getPocetSluzeb(),
                    pol.getPocetNedeli() / (float) pol.getPocetSluzeb(), pol.getPocetVsednichSvatku() / (float) pol.getPocetSluzeb());
            sl.nastavNaMesic(gc);
            vratka.add(sl);
        }
        textInfo[TI_NACTENI_SLOUZICICH] += "ok";
        return vratka;
    }

    private List<SluzboDen> nactiPoradiSluzeb(GregorianCalendar gc) throws VicJakJedenSlouziciException, NikdoNemuzeException, SpatnyPocetSluzboDnuException {
        textInfo[TI_SLUZBY_K_NAPLANOVANI] += "ok. Upřesnění požadavků ... ";
        List<SluzboDen> vratka = new ArrayList<>();
        //System.out.println("Poradi sluzeb.............");
        for (GregorianCalendar den : dejPoradiDnu(gc)) {
            //System.out.println("na den " + new SimpleDateFormat("yy/MMMM/dd").format(den.getTime()));
            List<Pozadavky> pozadavky = pf.pozadavkyNaDen(den);
            for (Pozadavky pozadavek : pozadavky) {
                try {
                    //pokud pozadavek nebude typ sluzby vyhodi illegalargexception
                    TypySluzby typSluzby = TypySluzby.valueOf(pozadavek.getPozadavek());
                    if (!this.sluzbyKNaplanovani.contains(typSluzby)) {
                        //palubarsky pro piloty, H120 pri LZS atd...
                        continue;
                    }
                    SluzboDen pomSD = new SluzboDen(den, typSluzby, 0);
                    if (vratka.contains(pomSD)) {
                        throw new VicJakJedenSlouziciException(String.format("Na den %s jsi naplánoval do %s víc jak jednoho sloužícího!", new SimpleDateFormat("yy/MMMM/dd").format(den.getTime()), pozadavek.getPozadavek()));
                    }
                    //System.out.println("pridavam > " + pomSD);
                    vratka.add(pomSD);
                } catch (IllegalArgumentException ex) {
                    //nic
                }
            }
            for (TypySluzby ts : sluzbyKNaplanovani) {
                int muze = 0;
                for (Slouzici sl : this.listSlouzici) {
                    //System.out.println("zkousim > " + sl);
                    if (!sl.getTypySluzeb().contains(ts.getsTypSluzby())) {
                        //System.out.println("\tnema " + ts.getsTypSluzby());
                        continue;
                    }
                    if (ts.getsTypSluzby().startsWith("L")) {
                        if (sl.isDoLiniSvozem() && !dnySvozu[den.get(Calendar.DAY_OF_MONTH)]) {
                            //System.out.println("\nejede mu svoz");
                            continue;
                        }
                    }
                    long volneDny = sl.getPlneVolneDny();
                    if (((long) Math.pow(2, den.get(Calendar.DAY_OF_MONTH)) & volneDny) == 0) {
                        muze++;
                    }
                }
                if (muze == 0) {
                    throw new NikdoNemuzeException(String.format("Den %s služba %s nikdo nemůže sloužit!", new SimpleDateFormat("yy/MMMM/dd").format(den.getTime()), ts.getsTypSluzby()));
                }
                SluzboDen pomSD = new SluzboDen(den, ts, muze);
                if (!vratka.contains(pomSD)) {
                    vratka.add(pomSD);
                }
            }
        }
        vratka.sort(new Comparator<SluzboDen>() {

            @Override
            public int compare(SluzboDen o1, SluzboDen o2) {
                return o1.getKolikLidiMuze() - o2.getKolikLidiMuze();
            }

        });
        /*for (SluzboDen pol : vratka) {
         System.out.println(pol);
         }*/
        if (vratka.size() != sluzbyKNaplanovani.size() * Kalendar.dnuVMesici(gc)) {
            //System.out.println("vratka size > " + vratka.size());
            //System.out.println("k naplanovani size > " + (sluzbyKNaplanovani.size() * Kalendar.dnuVMesici(gc)));
            throw new SpatnyPocetSluzboDnuException("Internal Error: Špatný počet službodnů!");
        }
        textInfo[TI_SLUZBY_K_NAPLANOVANI] += "ok.";
        //System.out.println("leaving nacti poradi sluzeb...");
        return vratka;
    }

    @Override
    public List<Slouzici> getInitialNodes() {
        return getNexts(null);
    }

    @Override
    public List<Slouzici> getNexts(List<Slouzici> actualPath) {
        if (actualPath != null) {
            if ((actualPath.size() == poradiSluzeb.size())
                    || ((pocetReseni > 0)
                    && (getRefValueForReducing(ComparatorTypes.GR) < getPathValueForReducing(ComparatorTypes.GR, actualPath)))) {
                return new ArrayList<>();
            }
        }

        List<Slouzici> vratka = new ArrayList<>();
        int hloubka;
        if (actualPath != null) {
            hloubka = actualPath.size();
        } else {
            hloubka = 0;
        }
        SluzboDen dalsiSD = poradiSluzeb.get(hloubka);
        List<Pozadavky> planovane = pf.findByDenTyp(dalsiSD.getDatum(), dalsiSD.getTypsluzby().getsTypSluzby());
        if (!planovane.isEmpty()) {
            for (Pozadavky pol : planovane) {
                for (Slouzici sl : listSlouzici) {
                    if (sl.getJmeno().equals(pol.getLetajici())) {
                        vratka.add(sl);
                        return vratka;
                    }
                }
            }
        }
        for (Slouzici sl : listSlouzici) {
            if (sl.muze(dalsiSD, actualPath, poradiSluzeb, minulyMesic)) {
                vratka.add(sl);
            }
        }

        vratka.sort(new Comparator<Slouzici>() {

            @Override
            public int compare(Slouzici o1, Slouzici o2) {
                switch (dalsiSD.typdne) {
                    case SOBOTA:
                        return o1.getPrSo() == o2.getPrSo() ? 0 : (o1.getPrSo() < o2.getPrSo() ? -1 : 1);
                    case PATEK:
                        return o1.getPrPa() == o2.getPrPa() ? 0 : (o1.getPrPa() < o2.getPrPa() ? -1 : 1);
                    case NEDELE:
                        return o1.getPrNe() == o2.getPrNe() ? 0 : (o1.getPrNe() < o2.getPrNe() ? -1 : 1);
                    case VSEDNI_DVOJSVATEK:
                    case VSEDNI_SVATEK:
                        return o1.getPrSv() == o2.getPrSv() ? 0 : (o1.getPrSv() < o2.getPrSv() ? -1 : 1);
                }
                return 0;
            }

        });
        return vratka;
    }

    @Override
    public boolean isAim(List<Slouzici> actualPath) {
        if (actualPath.size() == poradiSluzeb.size()) {
            ulozVysledek(actualPath);
            if (jeBlizkeIdealu(actualPath)) {
                return true;
            }
        }
        return false;
    }

    private boolean jeBlizkeIdealu(List<Slouzici> path) {
        nactiSluzbyZPath(path);
        listSlouzici.sort(new Comparator<Slouzici>() {

            @Override
            public int compare(Slouzici o1, Slouzici o2) {
                return (int) (Math.abs(o2.getSluzeb() - o2.getPlanujSluzeb()) - (Math.abs(o2.getSluzeb() - o2.getPlanujSluzeb())));
            }

        });
        return (listSlouzici.get(0).getSluzeb() <= 1);
    }

    private void nactiSluzbyZPath(List<Slouzici> path) {
        vynulujPaSoNeSvSluzebSlouzicich();
        for (int i = 0; i < path.size(); i++) {
            path.get(i).zvysSluzeb();
            switch (poradiSluzeb.get(i).typdne) {
                case PATEK:
                case SOBOTA:
                case NEDELE:
                    path.get(i).zvysPaSoNe();
                    break;
                case VSEDNI_DVOJSVATEK:
                    path.get(i).zvysSv();
                case VSEDNI_SVATEK:
                    path.get(i).zvysSv();
                    break;
            }
        }
    }

    @Override
    public double getPathValue(List<Slouzici> actualPath) {
        nactiSluzbyZPath(actualPath);
        return hodnotaOdchylkyPoctuSluzeb() + getPaSoNeSvValue();
    }

    private List<GregorianCalendar> dejPoradiDnu(GregorianCalendar puvodniGC) {
        GregorianCalendar gc = (GregorianCalendar) puvodniGC.clone();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        List<GregorianCalendar> poradiDnu = new ArrayList<>();
        int mesic = gc.get(Calendar.MONTH);
        while (mesic == gc.get(Calendar.MONTH)) {
            poradiDnu.add((GregorianCalendar) gc.clone());
            gc.add(Calendar.DAY_OF_MONTH, 1);
        }

        poradiDnu.sort(new Comparator<GregorianCalendar>() {
            @Override
            public int compare(GregorianCalendar o1, GregorianCalendar o2) {
                return TypyDne.getTypDne(o1).compareTo(TypyDne.getTypDne(o2));
            }

        });
        return poradiDnu;
    }
    /*
     private void nactiPoctyTypuDne(GregorianCalendar gc) {
     pocetPaSoNe = 0;
     GregorianCalendar pomgc = new GregorianCalendar(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), 1);
     int mesic = pomgc.get(Calendar.MONTH);
     while (mesic == pomgc.get(Calendar.MONTH)) {
     TypyDne typDne = TypyDne.getTypDne(pomgc);
     switch (typDne) {
     case PATEK:
     case SOBOTA:
     case NEDELE:
     pocetPaSoNe++;
     }
     pomgc.add(Calendar.DAY_OF_MONTH, 1);
     }
     }
     */

    public boolean isChyba() {
        return chyba;
    }

    public void setPrerusit(boolean prerusit) {
        System.out.println("prerusit > " + prerusit);
        this.prerusit = prerusit;
    }

    public String[] getTextInfo() {
        return textInfo;
    }

    @Override
    public double getPathValueForReducing(ComparatorTypes ct, List<Slouzici> path) {

        nactiSluzbyZPath(path);
        listSlouzici.sort(new Comparator<Slouzici>() {

            @Override
            public int compare(Slouzici o1, Slouzici o2) {
                return o2.getSluzeb() - o1.getSluzeb();
            }

        });
        return listSlouzici.get(0).getSluzeb();
    }

    @Override
    public double getRefValueForReducing(ComparatorTypes ct) {
        vynulujPaSoNeSvSluzebSlouzicich();
        for (SluzboDen sd : poradiSluzeb) {
            sd.getSlouzici().zvysSluzeb();
            switch (sd.typdne) {
                case PATEK:
                case SOBOTA:
                case NEDELE:
                    sd.getSlouzici().zvysPaSoNe();
                    break;
                case VSEDNI_DVOJSVATEK:
                    sd.getSlouzici().zvysSv();
                case VSEDNI_SVATEK:
                    sd.getSlouzici().zvysSv();
                    break;
            }
        }
        listSlouzici.sort(new Comparator<Slouzici>() {

            @Override
            public int compare(Slouzici o1, Slouzici o2) {
                return o2.getSluzeb() - o1.getSluzeb();
            }

        });
        return listSlouzici.get(0).getSluzeb();
    }

    private double getPaSoNeSvValue() {
        double vratka = 0;
        for (Slouzici sl : listSlouzici) {
            vratka += Math.pow(sl.getPaSoNe(), 2) + Math.pow(sl.getSv() * NAVYSENI_SVATKU, 2);
        }
        return vratka;
    }

    private void vynulujPaSoNeSvSluzebSlouzicich() {
        for (Slouzici sl : listSlouzici) {
            sl.setPaSoNe(0);
            sl.setSluzeb(0);
        }
    }

    private double hodnotaOdchylkyPoctuSluzeb() {
        double vratka = 0;
        for (Slouzici sl : listSlouzici) {
            vratka += Math.pow(sl.getFGVRozdil(), 2);
        }
        return vratka;
    }

    private double hodnotaPath(List<Slouzici> path) {
        double vratka = getPathValueForReducing(ComparatorTypes.GR, path);
        vratka += hodnotaOdchylkyPoctuSluzeb();
        return vratka;
    }

    private double hodnotaVysledku() {
        double vratka = getRefValueForReducing(ComparatorTypes.GR);
        vratka += hodnotaOdchylkyPoctuSluzeb();
        return vratka;
    }

    @Override
    public boolean shouldReduceTreeLeafs(List<Slouzici> actualPath) {
        return actualPath.size() == poradiSluzeb.size();
    }

    @Override
    public ComparatorTypes getCTforReduce() {
        return ComparatorTypes.GE;

    }

    public void prechodKontrola() {
        System.out.println("prechazim kontrola");
        planuji = false;
        pridejBechyni();
    }

    private void pridejBechyni() {
        for(SluzboDen bs : poradiBechyne){
            poradiSluzeb.add(bs);
        }
    }

    private void nactiBechyni(GregorianCalendar gc) throws NikdoNemuzeException, VicJakJedenSlouziciException {
        poradiBechyne = new ArrayList<>();
        ArrayList<TypySluzby> pozB = new ArrayList<>();
        if(proPalubare){
            pozB.add(BP);
        }else{
            pozB.add(BK);
            pozB.add(BD);
        }
        GregorianCalendar pomgc = new GregorianCalendar();
        pomgc.setTime(gc.getTime());
        pomgc.set(Calendar.DAY_OF_MONTH, 1);
        int mesic = pomgc.get(Calendar.MONTH);
        while (pomgc.get(Calendar.MONTH) == mesic) {
            for( TypySluzby poz : pozB){
                List<Pozadavky> pom = pf.findByDenTyp(pomgc, poz.getsTypSluzby());
                if(pom == null || pom.size() == 0){
                    throw new NikdoNemuzeException(String.format("Den %s služba %s nikdo nemůže sloužit!", new SimpleDateFormat("yy/MMMM/dd").format(pomgc.getTime()), poz.getsTypSluzby()));
                }
                if(pom.size() > 1){
                    throw new VicJakJedenSlouziciException(String.format("Na den %s jsi naplánoval do %s víc jak jednoho sloužícího!", new SimpleDateFormat("yy/MMMM/dd").format(pomgc.getTime()), poz.getsTypSluzby()));
                }
                SluzboDen sd = new SluzboDen(pomgc, poz, 0);
                sd.setSlouzici(getSlouzici(pom.get(0).getLetajici()));
                poradiBechyne.add(sd);
            }
            pomgc.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private Slouzici getSlouzici(String jmeno){
        for(Slouzici sl : listSlouzici){
            if(sl.getJmeno().equals(jmeno)){
                return sl;
            }
        }
        return null;
    }
    
    private static class VicJakJedenSlouziciException extends Exception {

        public VicJakJedenSlouziciException(String zprava) {
            super(zprava);
        }
    }

    private static class NikdoNemuzeException extends Exception {

        public NikdoNemuzeException(String zprava) {
            super(zprava);
        }
    }

    private static class SpatnyPocetSluzboDnuException extends Exception {

        public SpatnyPocetSluzboDnuException(String zprava) {
            super(zprava);
        }
    }

}
