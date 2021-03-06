/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.beans;

import cz.wenaaa.is243vrl.SlouziciPuvodni;
import cz.wenaaa.is243vrl.SluzboDenPuvodni;
import cz.wenaaa.is243vrl.TypyDne;
import static cz.wenaaa.is243vrl.TypyDne.NEDELE;
import static cz.wenaaa.is243vrl.TypyDne.PATEK;
import static cz.wenaaa.is243vrl.TypyDne.SOBOTA;
import static cz.wenaaa.is243vrl.TypyDne.VSEDNI_SVATEK;
import cz.wenaaa.is243vrl.entityClasses.Sluzby;
import cz.wenaaa.is243vrl.controllers.LetajiciSluzbyController;
import cz.wenaaa.is243vrl.planovani.PlanovaniSluzeb;
import cz.wenaaa.utils.Kalendar;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

/**
 *
 * @author vena
 */
@Named(value = "planovaniBean")
@SessionScoped
public class PlanovaniBean implements Serializable {

    @PersistenceContext(unitName = "pozadavky_PU")
    private EntityManager em;
    //@Inject
    //private UserTransaction ut;
    @Inject
    LetajiciSluzbyController lsc;
    @Inject
    LoggedBean lb;
    private boolean[] dnySvozu;
    private final boolean planuj;
    private List<ColumnModelvII> columns = new ArrayList<>();
    private int zacatek;
    private int konec;
    private String text;
    private boolean naplanovano;
    private boolean nenaplanovano;
    private boolean vPlanovani;
    private SluzboDenPuvodni navrhSluzeb;
    private String jmenoProZmenu;
    private int denProZmenu;
    private static final int MAX_PLANOVAT = 7;
    private static final int MIN_PLANOVAT = 3;

    /**
     * @return the MAX_PLANOVAT
     */
    public static int getMAX_PLANOVAT() {
        return MAX_PLANOVAT;
    }
    private volatile boolean castecne;

    public void setPrerusit(boolean prerusit){
        //System.out.println("volano set prerusit");
        PlanovaniSluzeb.getInstance(lb.isLoggedAsMedved()).setPrerusit(prerusit);
    }
    public boolean isCastecne(){
        return castecne;
    }
    /**
     * @return the MIN_PLANOVAT
     */
    public static int getMIN_PLANOVAT() {
        return MIN_PLANOVAT;
    }

    public boolean isNaplanovano() {
        return PlanovaniSluzeb.getInstance(lb.isLoggedAsMedved()).isNaplanovano();
    }

    public boolean isNenaplanovano() {
        return nenaplanovano;
    }

    public void setNenaplanovano(boolean nenaplanovano){
        this.nenaplanovano = nenaplanovano;
    }
    
    public String proLastColumn(String letajici) {
        int[] pom = pocetSluzeb(letajici, navrhSluzeb);
        return String.format("%d: %d+%d+%d", pom[0] + pom[1] + pom[2], pom[0], pom[1], pom[2]);
    }

    public int[] pocetSluzeb(String letajici, SluzboDenPuvodni sd) {
        return PlanovaniSluzeb.getInstance(lb.isLoggedAsMedved()).pocetSluzeb(letajici);
    }

    public String getText() {
        return PlanovaniSluzeb.getInstance(lb.isLoggedAsMedved()).getText();
    }

    public void prechodKontrola(ActionEvent e) {
        //System.out.println("prechod kontrola 1");
        PlanovaniSluzeb.getInstance(lb.isLoggedAsMedved()).prechodKontrola();
    }

    public boolean isvPlanovani() {
        return PlanovaniSluzeb.getInstance(lb.isLoggedAsMedved()).isPlanuji();
    }

    public String getStyle(int den) {
        if (den == 0) {
            return "null";
        }
        //System.out.println("vstup  "+new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        //System.out.println(den);
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH, 1);
        gc.set(Calendar.DAY_OF_MONTH, den);
        String vratka = "aktivni";
        //System.out.println(new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        if (Kalendar.jeSvatek(gc)) {
            vratka = "svatek-aktivni";
        }
        if ((gc.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) || (gc.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
            vratka = "vikend-aktivni";
        }
        gc.set(Calendar.DAY_OF_MONTH, 1);
        //System.out.println("vystup  "+new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        return vratka;
    }

    public String getPodtrzitkoStyle(int den) {
        if (den == 0) {
            return "null";
        }
        //System.out.println("vstup  "+new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        //System.out.println(den);
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH, 1);
        gc.set(Calendar.DAY_OF_MONTH, den);
        String vratka = "podtrzitko";
        //System.out.println(new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        if (Kalendar.jeSvatek(gc)) {
            vratka = "podtrzitko-svatek";
        }
        if ((gc.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) || (gc.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
            vratka = "podtrzitko-vikend";
        }
        gc.set(Calendar.DAY_OF_MONTH, 1);
        //System.out.println("vystup  "+new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        return vratka;
    }

    public PlanovaniBean() {
        this.vPlanovani = false;
        planuj = true;
        naplanovano = false;
        text = "";
        castecne = false;
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH, 1);
        this.dnySvozu = new boolean[Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH) + 1) + 1];
        populateColumns();
    }

    public void nastavAtribZmeny() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        //System.out.format("parametry %s / %s",params.get("jmeno"),params.get("den") );
        if (lb.isLoggedAsMedved()) {
            jmenoProZmenu = lsc.getPalubari(Integer.parseInt(params.get("jmeno")));
        } else {
            jmenoProZmenu = lsc.getLetajici(Integer.parseInt(params.get("jmeno")));
        }
        denProZmenu = Integer.parseInt(params.get("den"));
        //System.out.print("nastav atrib zmeny: "+jmenoProZmenu+"/"+String.valueOf(denProZmenu));
    }

    public void zmenSluzbuZKontroly(String typSluzby) {
        //System.out.print("menim sluzbu");
        /*SluzboDenPuvodni pom = navrhSluzeb;
        while (pom != null) {
            if (pom.getDen() == denProZmenu && pom.getTypsluzby().equals(typSluzby)) {
                pom.setSlouzici(new SlouziciPuvodni(jmenoProZmenu, "", ""));
                //System.out.print("zmenneno");
                break;
            }
            pom = pom.getNahoru();
        }*/
        PlanovaniSluzeb.getInstance(lb.isLoggedAsMedved()).setNaplanovanaSluzba(typSluzby, jmenoProZmenu, denProZmenu);
    }

    public String dejSluzbuProKontrolu(String slouzici, int den) {
        /*String vratka = "";
         SluzboDenPuvodni pom = navrhSluzeb;
         while (pom != null) {
         if (pom.getSlouzici().getJmeno().equals(slouzici) && den == pom.getDen()) {
         vratka += pom.getTypsluzby();
         }
         pom = pom.getNahoru();
         }
         return vratka;*/
        return PlanovaniSluzeb.getInstance(lb.isLoggedAsMedved()).getNaplanovanaSluzba(slouzici, den);
    }

    public void setCastecne(boolean castecne) {
        //System.out.println("volano setCastecne > "+castecne);
        this.castecne = castecne;
    }

    
    public void naplanuj(ActionEvent e) {
        castecne = false;
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH, 1);
        PlanovaniSluzeb ps = PlanovaniSluzeb.createInstance(lb.isLoggedAsMedved());
        ps.setMe(this);
        ps.naplanuj(gc, dnySvozu);
        /*if (vPlanovani) {
         text = text + "\nnove volani fce";
         return;
         }
         vPlanovani = true;
         int zvysovani = 0;
         SluzboDenPuvodni vysledek = null;
         int mezPaSoNe = 1;
         int mezSv;
         float mezPresMiru = 8;
         List<PomSDClass> poradiSD;
         nenaplanovano = false;
         naplanovano = false;
         navrhSluzeb = null;
         text = String.format("Uzaviram db...");
         //uzavriDB();
         text = text + String.format("\nNačítám seznam sloužících...");
         List<SlouziciPuvodni> neplanovani = nactiNeplanovane();
         List<SlouziciPuvodni> seznamSlouzicich = nactiSlouzici();
         try {
         text = text + String.format("\nNačítám službodny...");
         poradiSD = dejPoradiSluzbodnu(seznamSlouzicich);
         } catch (Exception ex) {
         text = text + "\n" + ex.getMessage();
         //System.out.print(ex.getMessage());
         nenaplanovano = true;
         vPlanovani = false;
         return;
         }
         GregorianCalendar gc = new GregorianCalendar();
         gc.set(Calendar.DAY_OF_MONTH, 1);
         gc.add(Calendar.MONTH, 1);
         switch (gc.get(Calendar.MONTH)) {
         case Calendar.JULY:
         case Calendar.DECEMBER:
         mezSv = 2;
         break;
         default:
         mezSv = 1;
         break;
         }
         text = text + String.format("\nNačítám služby z konce předešlého měsíce...");
         List<Sluzby> minulyMesic = null;//nactiMinulyMesic(gc);
         //uvodni hledani
         while (vysledek == null) {
         if (mezPaSoNe > 5) {
         text = text + "\nkolikrat nelze naplanovat:";
         poradiSD.sort(new pomSDComparator());
         for (PomSDClass sd : poradiSD) {
         if (sd.kolikratNesel > 0) {
         text += String.format("\n%s:%d > %d", sd.typSluzby, sd.den, sd.kolikratNesel);
         }
         }
         text = text + "\nNenalezeno, uprav požadavky/svoz.";
         nenaplanovano = true;
         vPlanovani = false;
         return;
         }
         text = text + "\n" + String.format("uvodni hledani> presMiru: %d, PaSoNe: %d, Sv: %d >", (int) mezPresMiru, mezPaSoNe, mezSv);
         vysledek = naplanuj(250, mezPresMiru, mezPaSoNe, mezSv, seznamSlouzicich, poradiSD, true, neplanovani, minulyMesic);
         if (vysledek == null) {
         mezPaSoNe++;
         zvysovani++;
         if (zvysovani > 1) {
         mezSv++;
         }
         }
         }
         //zlepsovani
         mezPresMiru = vysledek.getMaxsluzebpresmiru();
         //float minulaPresMiru = mezPresMiru;
         while (true) {
         boolean ukonci = true;
         text = text + "\n" + String.format("vylepšování> presMiru: %f, PaSoNe: %d, Sv: %d >", mezPresMiru, mezPaSoNe, mezSv);
         SluzboDenPuvodni pom = naplanuj(250, (int) mezPresMiru, mezPaSoNe, mezSv, seznamSlouzicich, poradiSD, true, neplanovani, minulyMesic);
         if (pom != null) {
         vysledek = pom;
         ukonci = false;
         }
         if (ukonci) {
         break;
         }
         //minulaPresMiru = mezPresMiru;
         mezPresMiru = vysledek.getMaxsluzebpresmiru();
         }
         vypisKolik(vysledek, seznamSlouzicich);
         navrhSluzeb = vysledek;
         text = text + String.format("\ndone");
         naplanovano = true;
         */
    }

    private SluzboDenPuvodni naplanuj(int trvani, float mezPresMiru, int mezPaSoNeSv, int mezSv, List<SlouziciPuvodni> seznamSlouzicich, List<PomSDClass> poradiSD, boolean naHloubku, List<SlouziciPuvodni> neplanovani, List<Sluzby> minMesic) {
        //text += "\nvstup do naplanujII";
        List<SluzboDenPuvodni> sluzbodny = new ArrayList<>();
        for (String letajici : dejPoradiLetajicich(poradiSD.get(0).typSluzby, poradiSD.get(0).den, "")) {
            SlouziciPuvodni pomSl = null;
            for (SlouziciPuvodni sl : seznamSlouzicich) {
                if (sl.getJmeno().equals(letajici)) {
                    pomSl = sl;
                }
            }
            if (pomSl == null) {
                for (SlouziciPuvodni sl : neplanovani) {
                    if (sl.getJmeno().equals(letajici)) {
                        pomSl = sl;
                    }
                }
            }
            SluzboDenPuvodni pom = new SluzboDenPuvodni(poradiSD.get(0).den, poradiSD.get(0).typSluzby, null, pomSl);
            pom.setMinulyMesic(minMesic);
            if (pom.isValid()) {
                sluzbodny.add(pom);
            }
        }
        SluzboDenPuvodni rozvijeny = null;
        //for(int i = 0; i < 1000; i++){
        int i = 0;
        while (true) {
            //text += "\nvstup do while";
            if (trvani * 1000 < i++) {
                //if((System.currentTimeMillis()-ted)>trvani*1000){
                //text += String.format("\nstop cas, hloubka: %d", rozvijeny.getHloubka());
                return null;
            }
            if (sluzbodny.isEmpty()) {
                return null;
            }
            rozvijeny = sluzbodny.get(0);
            //System.out.print("----------------------------");

            /*for(SluzboDenPuvodni pom: sluzbodny){
             if(pom.jeMensiNezParam(rozvijeny,naHloubku,seznamSlouzicich)){
             rozvijeny = pom;
             }
             }*/
            sluzbodny.remove(rozvijeny);

            if ((rozvijeny.getHloubka() + 1) == poradiSD.size()) {
                break;
            }
            int novaHloubka = rozvijeny.getHloubka() + 1;
            String dojizdeni = dejSchemaDojizdeni(rozvijeny, poradiSD.get(novaHloubka).typSluzby, poradiSD.get(novaHloubka).den);
            SluzboDenPuvodni predchozi = rozvijeny;
            //text += String.format("\nrozbaluji: %s", rozvijeny);
            //String sDalsi = String.format("%s:%d",poradiSD.get(novaHloubka).typSluzby,poradiSD.get(novaHloubka).den);
            try {
                //text += "\nv try";
                String[] arrText = text.split("\n");
                String[] arrText2 = arrText[arrText.length - 1].split(" ");
                Integer.parseInt(arrText2[arrText2.length - 1]);
                arrText2[arrText2.length - 1] = String.format("%d", ++i);
                arrText[arrText.length - 1] = String.join(" ", arrText2);
                text = String.join("\n", arrText);
            } catch (NumberFormatException ex) {
                text = text + String.format(" %d", i);
            }
            boolean nenalezeno = true;
            for (String letajici : dejPoradiLetajicich(poradiSD.get(novaHloubka).typSluzby, poradiSD.get(novaHloubka).den, dojizdeni)) {
                SlouziciPuvodni pomSl = null;
                for (SlouziciPuvodni sl : seznamSlouzicich) {
                    if (sl.getJmeno().equals(letajici)) {
                        pomSl = sl;
                    }
                }
                if (pomSl == null) {
                    for (SlouziciPuvodni sl : neplanovani) {
                        if (sl.getJmeno().equals(letajici)) {
                            pomSl = sl;
                        }
                    }
                }
                SluzboDenPuvodni pom = new SluzboDenPuvodni(poradiSD.get(novaHloubka).den, poradiSD.get(novaHloubka).typSluzby, predchozi, pomSl);
                //System.out.print(pom);
                if (pom.getMaxsluzebpresmiru() >= mezPresMiru) {
                    continue;
                }
                if (pom.getMaxpocetsvatku() > mezSv) {
                    continue;
                }
                if (pom.getMaxpocetsobot() > mezPaSoNeSv) {
                    continue;
                }
                if (pom.getMaxpocetnedel() > mezPaSoNeSv) {
                    continue;
                }
                if (pom.getMaxpocetpatku() > mezPaSoNeSv) {
                    continue;
                }
                //System.out.print("prosel pres meze");
                if (pom.isValid()) {
                    boolean nevlozen = true;
                    for (int j = 0; j < sluzbodny.size(); j++) {
                        if (pom.jeMensiNezParam(sluzbodny.get(j))) {
                            sluzbodny.add(j, pom);
                            nevlozen = false;
                            break;
                        }
                    }
                    if (nevlozen) {
                        sluzbodny.add(pom);
                    }
                    nenalezeno = false;
                }
            }
            if (nenalezeno) {
                poradiSD.get(novaHloubka).kolikratNesel++;
            }
        }
        return rozvijeny;
    }

    public List<ColumnModelvII> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnModelvII> columns) {
        this.columns = columns;
    }

    private List<PomSDClass> dejPoradiSluzbodnu(List<SlouziciPuvodni> seznamSlouzicich) {
        //System.out.println("Nacitam poradi sluzbodnu...");
        List<PomSDClass> vratka = new ArrayList<>();
        for (GregorianCalendar gc : dejPoradiDnu()) {
            int den = gc.get(Calendar.DAY_OF_MONTH);
            //System.out.println("den: "+den );
            for (String typ_sluzby : dejPoradiSluzeb()) {
                //System.out.println("sluzba: "+typ_sluzby);
                int volnych = 0;
                Query q1 = em.createNativeQuery("SELECT letajici FROM pozadavky WHERE datum = ? AND pozadavek = ?");
                q1.setParameter(1, gc, TemporalType.DATE);
                q1.setParameter(2, typ_sluzby);
                try {
                    Object pom = q1.getSingleResult();
                    int k = vratka.size();
                    for (int i = 0; i <= k; i++) {
                        if (i == k) {
                            vratka.add(new PomSDClass(den, typ_sluzby, -1));
                            break;
                        }
                        if (volnych < vratka.get(i).volnychPolicek) {
                            vratka.add(i, new PomSDClass(den, typ_sluzby, -1));
                            break;
                        }
                    }
                    //System.out.println("prideleno "+(String)pom);
                    continue;
                    //javax.persistence.NonUniqueResultException: result returns more than one elements
                } catch (NoResultException e) {
                    //nic
                } catch (NonUniqueResultException e) {
                    throw new NonUniqueResultException(String.format("WARNING: na den %d. naplánován víc jak jeden létající do služby %s", den, typ_sluzby));
                }
                List<Object> mozny_slouzici;
                if (typ_sluzby.startsWith("L") && !dnySvozu[den]) {
                    Query qSvoz = em.createNativeQuery("SELECT ls.letajici FROM letajici_sluzby2 AS ls, povoleni_sluzeb AS ps WHERE ls.letajici = ps.letajici AND ps.typ_sluzby = ? AND ps.povoleno = true AND ls.do_lini_svozem = ?");
                    qSvoz.setParameter(1, typ_sluzby);
                    qSvoz.setParameter(2, dnySvozu[den]);
                    mozny_slouzici = qSvoz.getResultList();
                } else {
                    Query q2 = em.createNativeQuery("SELECT ls.letajici FROM letajici_sluzby2 AS ls, povoleni_sluzeb AS ps WHERE ls.letajici = ps.letajici AND ps.typ_sluzby = ? AND ps.povoleno = true");
                    q2.setParameter(1, typ_sluzby);
                    mozny_slouzici = q2.getResultList();
                }
                //System.out.println("Moznych slouzicich: "+mozny_slouzici.size());
                for (Object pom : mozny_slouzici) {
                    int intpom = seznamSlouzicich.indexOf(new SlouziciPuvodni((String) pom, "", ""));
                    long volneDny = seznamSlouzicich.get(intpom).getPlneVolneDny();
                    if (((long) Math.pow(2, den) & volneDny) == 0) {
                        volnych++;
                    }
                }
                if (volnych == 0) {
                    //System.out.print("nikdo nemuze: "+Integer.toString(den));
                    throw new NoResultException("nikdo nemuze: " + Integer.toString(den) + "  : " + typ_sluzby);
                }
                //System.out.print("vratka size pred: "+Integer.toString(vratka.size()));
                int k = vratka.size();
                for (int i = 0; i <= k; i++) {
                    if (i == k) {
                        vratka.add(new PomSDClass(den, typ_sluzby, volnych));
                    }
                    if (volnych < vratka.get(i).volnychPolicek) {
                        vratka.add(i, new PomSDClass(den, typ_sluzby, volnych));
                        break;
                    }
                }
                //System.out.print("vratka size po: "+Integer.toString(vratka.size()));
            }
        }
        text = text + String.format("%d...done", vratka.size());
        /*
         for(PomSDClass sd : vratka){
         //System.out.println(sd);
         }*/
        return vratka;
    }

    private List<String> dejPoradiSluzeb() {
        List<String> vratka = new ArrayList<>();
        if (lb.isLoggedAsMedved()) {
            vratka.add("LP");
            vratka.add("SP");
        } else {
            vratka.add("LD");
            vratka.add("SD");
            vratka.add("LK");
            vratka.add("SK");
        }
        return vratka;
    }

    private List<GregorianCalendar> dejPoradiDnu() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH, 1);
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
        /*
         //System.out.println("   dejPoradiDnu() > ");
         for (GregorianCalendar den : poradiDnu) {
         //System.out.println(new SimpleDateFormat("yy/MMMM/dd").format(den.getTime()));
         }
         //System.out.println("-------------------------------------------------");
         */
        return poradiDnu;
    }

    /*private void uzavriDB() {
     GregorianCalendar pomGC = new GregorianCalendar();
     pomGC.set(Calendar.DAY_OF_MONTH, 1);
     pomGC.add(Calendar.MONTH, 2);
     String prip = lb.isLoggedAsMedved() ? "Palubaci" : "Piloti";
     try {
     ut.begin();
     em.joinTransaction();
     Query qUpd = em.createNativeQuery("UPDATE pomtab SET pozadavkyod" + prip + " = ?");
     qUpd.setParameter(1, pomGC, TemporalType.DATE);
     qUpd.executeUpdate();
     ut.commit();
     text = text + "done";
     } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
     Logger.getLogger(PlanovaniBean.class.getName()).log(Level.SEVERE, null, ex);
     }
     }*/
    private List<String> dejPoradiLetajicich(String typ_sluzby, int den, String dojizdeni) {
        List<String> vratka = new ArrayList<>();
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH, 1);
        gc.set(Calendar.DAY_OF_MONTH, den);
        Query q1 = em.createNativeQuery("SELECT letajici FROM pozadavky WHERE datum = ? AND pozadavek = ?");
        q1.setParameter(1, gc, TemporalType.DATE);
        q1.setParameter(2, typ_sluzby);
        try {
            vratka.add((String) q1.getSingleResult());
            return vratka;
        } catch (NoResultException e) {
            //nic
        }
        boolean prvni = true;
        String zaklad = "SELECT prumery_sluzeb.letajici AS letajici FROM prumery_sluzeb, povoleni_sluzeb WHERE povoleni_sluzeb.letajici = prumery_sluzeb.letajici AND povoleni_sluzeb.typ_sluzby = ? AND povoleni_sluzeb.povoleno = true ORDER BY";
        String zakladZachr = "SELECT prumery_sluzeb.letajici AS letajici FROM prumery_sluzeb, povoleni_sluzeb WHERE povoleni_sluzeb.letajici = prumery_sluzeb.letajici AND povoleni_sluzeb.typ_sluzby = ? AND povoleni_sluzeb.povoleno = true AND prumery_sluzeb.do_lini_svozem = false ORDER BY";
        String zachranka = "prumery_sluzeb.do_lini_svozem = ? DESC";
        String podledojizdeni = "prumery_sluzeb.dojizdeni= ? DESC";
        String podleSvatku = "prumery_sluzeb.p_sv";
        String podleSobot = "prumery_sluzeb.p_so";
        String podleNedel = "prumery_sluzeb.p_ne";
        String podlePatku = "prumery_sluzeb.p_pa";
        String konecS = "random()";
        boolean paramZachranka = false;
        boolean paramDojizdeni = false;
        if (typ_sluzby.startsWith("L")) {
            zaklad = dnySvozu[den] ? zaklad : zakladZachr;
            zaklad += " " + zachranka;
            paramZachranka = true;
            prvni = false;
        }
        if (!"".equals(dojizdeni)) {
            if (prvni) {
                zaklad += " " + podledojizdeni;
            } else {
                zaklad += " , " + podledojizdeni;
            }
            paramDojizdeni = true;
            prvni = false;
        }
        TypyDne typDne = TypyDne.getTypDne(gc);
        switch (typDne) {
            case VSEDNI_DVOJSVATEK:
            case VSEDNI_SVATEK:
                if (prvni) {
                    zaklad += " " + podleSvatku;
                } else {
                    zaklad += " , " + podleSvatku;
                }
                prvni = false;
                break;
            case SOBOTA:
                if (prvni) {
                    zaklad += " " + podleSobot;
                } else {
                    zaklad += " , " + podleSobot;
                }
                prvni = false;
                break;
            case NEDELE:
                if (prvni) {
                    zaklad += " " + podleNedel;
                } else {
                    zaklad += " , " + podleNedel;
                }
                prvni = false;
            case PATEK:
                if (prvni) {
                    zaklad += " " + podlePatku;
                } else {
                    zaklad += " , " + podlePatku;
                }
                prvni = false;
                break;
            default:
                break;
        }

        if (prvni) {
            zaklad += " " + konecS;
        } else {
            zaklad += " , " + konecS;
        }
        //zaklad += " , "+konecS;
        EntityTransaction ut = em.getTransaction();
        try {
            ut.begin();
            //em.joinTransaction();
            Query qSel = em.createNativeQuery(zaklad);
            int i = 1;
            qSel.setParameter(i++, typ_sluzby);
            if (paramZachranka) {
                qSel.setParameter(i++, jedeSvoz(gc));
            }
            if (paramDojizdeni) {
                qSel.setParameter(i, dojizdeni);
            }
            //System.out.print(zaklad);
            for (Object letajici : qSel.getResultList()) {
                vratka.add((String) letajici);
            }
            ut.commit();
        } catch (Exception ex) {
            Logger.getLogger(PlanovaniBean.class.getName()).log(Level.SEVERE, null, ex);
            if (ut.isActive()) {
                ut.rollback();
            }
        }
        return vratka;
    }

    public boolean[] getDnySvozu() {
        return dnySvozu;
    }

    public void setDnySvozu(boolean[] dnySvozu) {
        this.dnySvozu = dnySvozu;
    }

    private boolean jedeSvoz(GregorianCalendar gc) {
        return this.dnySvozu[gc.get(Calendar.DAY_OF_MONTH)];
    }

    public void vyberZacatek() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        //String myName1 = params.get("jmeno");
        String myName2 = params.get("den");
        this.zacatek = Integer.parseInt(myName2);
        this.konec = Integer.parseInt(myName2);
        //System.out.format("Zacatek : %d",zacatek);
    }

    public void vyberKonec() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        //String myName1 = params.get("jmeno");
        String myName2 = params.get("den");
        this.konec = Integer.parseInt(myName2);
        //System.out.print(konec);
    }

    public void nastavSvoz(boolean jede) {
        for (int i = 1; i < this.dnySvozu.length; i++) {
            if (i >= this.zacatek && i <= this.konec) {
                dnySvozu[i] = jede;
                //System.out.format("den: %d svoz %s", i, dnySvozu[i]?"jede":"nejede");
            }
        }
    }

    private void populateColumns() {
        columns = new ArrayList<>();
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH, 1);
        columns.add(new ColumnModelvII("", "mezera"));
        for (int i = 1; i <= Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH) + 1); i++) {
            columns.add(new ColumnModelvII(String.format("%d", i), String.format("%d", i)));
        }
    }

    private List<SlouziciPuvodni> nactiSlouzici() {
        List<SlouziciPuvodni> vratka = new ArrayList<>();
        List<String> poradiSluzeb = dejPoradiSluzeb();

        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH, 1);
        GregorianCalendar gc1 = new GregorianCalendar();
        gc1.set(Calendar.DAY_OF_MONTH, 1);
        gc1.add(Calendar.MONTH, 2);
        gc1.add(Calendar.DAY_OF_MONTH, -1);
        int dnuVmesici = gc1.get(Calendar.DAY_OF_MONTH);
        //prvni nacteni slouzicich a jejich rozdeleni do skupin
        Query q3 = em.createNativeQuery("SELECT ls.letajici, ls.dojizdeni, ps.typ_sluzby FROM letajici_sluzby2 as ls, povoleni_sluzeb as ps WHERE ls.letajici = ps.letajici AND ps.povoleno = TRUE ORDER BY ps.typ_sluzby");
        for (Object letajiciDojizdeniSluzba : q3.getResultList()) {
            Object[] lds = (Object[]) letajiciDojizdeniSluzba;
            String letajici = (String) lds[0];
            String dojizdeni = (String) lds[1];
            String sluzba = (String) lds[2];
            if (!poradiSluzeb.contains(sluzba)) {
                continue;
            }
            SlouziciPuvodni pom = new SlouziciPuvodni(letajici, sluzba, dojizdeni);
            if (vratka.contains(pom)) {
                vratka.get(vratka.indexOf(pom)).pridejSluzbuDoSkupiny(sluzba);
            } else {
                vratka.add(pom);
            }
        }
        //nacteni plnych volnych dnu
        for (SlouziciPuvodni slouzici : vratka) {
            String letajici = slouzici.getJmeno();
            Query q5 = em.createNativeQuery("SELECT pozadavek, datum FROM pozadavky WHERE datum BETWEEN ? AND ?  AND letajici=? AND pozadavek NOT IN (SELECT pozadavek FROM typy_pozadavku WHERE NOT useracces) ORDER BY datum");
            q5.setParameter(1, gc, TemporalType.DATE);
            q5.setParameter(2, gc1, TemporalType.DATE);
            q5.setParameter(3, letajici);
            long nemuze = 0;
            //System.out.print((String)letajici);
            for (Object pom : q5.getResultList()) {
                Object[] pom1 = (Object[]) pom;
                String pom2 = (String) (pom1[0]);
                GregorianCalendar pomGC = new GregorianCalendar();
                pomGC.setTime((Date) pom1[1]);
                int den = pomGC.get(Calendar.DAY_OF_MONTH);
                //System.out.print(pom2+" : "+Integer.toString(den));
                if (poradiSluzeb.contains(pom2)) {
                    if ((nemuze & (long) Math.pow(2, den - 1)) == 0) {
                        nemuze += (long) Math.pow(2, den - 1);
                    }
                    if ((nemuze & (long) Math.pow(2, den + 1)) == 0) {
                        nemuze += (long) Math.pow(2, den + 1);
                    }
                } else {
                    if (pom2.startsWith("X") || (den == 1)) {
                        nemuze += (long) Math.pow(2, den);
                    } else {
                        nemuze += (long) Math.pow(2, den);
                        if ((nemuze & (long) Math.pow(2, den - 1)) == 0) {
                            nemuze += (long) Math.pow(2, den - 1);
                        }
                    }
                }
                //System.out.print(nemuze);
            }
            slouzici.setPlneVolneDny(nemuze);
            //System.out.format("%s : nemuze : %d", letajici,nemuze);
        }
        //vytvoreni skupin letajicich podle moznych sluzeb
        List<SkupinaSluzeb> skupiny = new ArrayList<>();
        int nepresunutelnePlanovanych = 0;
        for (SlouziciPuvodni slouzici : vratka) {
            Query q6 = em.createNativeQuery("SELECT CAST(count(*) AS int) FROM pozadavky WHERE datum BETWEEN ? AND ?  AND letajici = ? AND pozadavek IN (SELECT pozadavek FROM typy_pozadavku WHERE NOT useracces)");
            q6.setParameter(1, gc, TemporalType.DATE);
            q6.setParameter(2, gc1, TemporalType.DATE);
            q6.setParameter(3, slouzici.getJmeno());
            int nepresunutelnejch = (int) q6.getSingleResult();
            nepresunutelnePlanovanych += nepresunutelnejch;
            SkupinaSluzeb pomSkupina = new SkupinaSluzeb(slouzici.getSkupina());
            if (!skupiny.contains(pomSkupina)) {
                skupiny.add(pomSkupina);
            }
            skupiny.get(skupiny.indexOf(pomSkupina)).addLetajici(slouzici, dnuVmesici, nepresunutelnejch);
        }
        //upresneni poctu sluzeb k planovani a jejich "predani" do skupin
        int nepresunutelneVsech = 0;
        Query q7 = em.createNativeQuery("SELECT CAST(count(*) AS int) FROM pozadavky WHERE datum BETWEEN ? AND ?  AND pozadavek IN (SELECT pozadavek FROM typy_pozadavku WHERE NOT useracces) AND pozadavek = ?");
        for (String ts : poradiSluzeb) {
            q7.setParameter(1, gc, TemporalType.DATE);
            q7.setParameter(2, gc1, TemporalType.DATE);
            q7.setParameter(3, ts);
            int pocet = (int) q7.getSingleResult();
            nepresunutelneVsech += pocet;
            for (SkupinaSluzeb sksl : skupiny) {
                if (sksl.jmenoSkupiny.contains(ts)) {
                    sksl.pridejSluzbu(ts, dnuVmesici - pocet);
                    break;
                }
            }
        }
        //idealni prumer
        int volnychChlivu = 0;
        for (SkupinaSluzeb skupina : skupiny) {
            volnychChlivu += skupina.getVolnychChlivu();
        }
        int pocetSl = poradiSluzeb.size() * dnuVmesici;
        float idealPrumer = ((float) pocetSl - (nepresunutelneVsech - nepresunutelnePlanovanych)) / ((float) volnychChlivu);
        //vyrovnavani
        /*for(SkupinaSluzeb pppp:skupiny){
         //System.out.print(pppp);
         }*/
        while (true) {
            float ctverecChyb = 0;
            for (SkupinaSluzeb sksl : skupiny) {
                ctverecChyb += Math.pow(sksl.hodnotaChlivu() - idealPrumer, 2);
            }
            int odkud = -1;
            int kam = -1;
            float novyCtverec = ctverecChyb;
            //System.out.format("ctverec chyb %f",ctverecChyb);
            for (int i = 0; i < skupiny.size(); i++) {
                for (int j = 0; j < skupiny.size(); j++) {
                    //System.out.format("moznost %s dava %s",skupiny.get(i).jmenoSkupiny,skupiny.get(j).jmenoSkupiny);
                    if (skupiny.get(i).hodnotaChlivu() <= skupiny.get(j).hodnotaChlivu()) {
                        //System.out.format("\t%s ma mens chliv nez %s",skupiny.get(i).jmenoSkupiny,skupiny.get(j).jmenoSkupiny);
                        continue;
                    }
                    if (!skupiny.get(i).muzeDat(skupiny.get(j))) {
                        //System.out.format("\t%s nemuze dat %s",skupiny.get(i).jmenoSkupiny,skupiny.get(j).jmenoSkupiny);
                        continue;
                    }
                    float pomCtverec = 0;
                    for (int k = 0; k < skupiny.size(); k++) {
                        if (i == k) {
                            pomCtverec += Math.pow(skupiny.get(k).hodnotaChlivuMinus() - idealPrumer, 2);
                            continue;
                        }
                        if (j == k) {
                            pomCtverec += Math.pow(skupiny.get(k).hodnotaChlivuPlus() - idealPrumer, 2);
                            continue;
                        }
                        pomCtverec += Math.pow(skupiny.get(k).hodnotaChlivu() - idealPrumer, 2);
                    }
                    //System.out.format("pom ctverec chyb %f",pomCtverec);
                    if (pomCtverec < novyCtverec) {
                        odkud = i;
                        kam = j;
                        novyCtverec = pomCtverec;
                    }
                }
            }
            //System.out.format("novy ctverec chyb %f, stary %f",novyCtverec,ctverecChyb);
            if (novyCtverec == ctverecChyb) {
                break;
            }
            //System.out.format("presouvam od %s do %s", skupiny.get(odkud).jmenoSkupiny,skupiny.get(kam).jmenoSkupiny);
            try {
                skupiny.get(odkud).predejSluzbu(skupiny.get(kam));
                /*for(SkupinaSluzeb pppp:skupiny){
                 //System.out.print(pppp);
                 }*/
            } catch (NoResultException ex) {
                Logger.getLogger(PlanovaniBean.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
        //zapis do letajicich
        for (SlouziciPuvodni letajici : vratka) {
            for (SkupinaSluzeb sksl : skupiny) {
                if (letajici.getSkupina().equals(sksl.jmenoSkupiny)) {
                    float kUlozeni = (sksl.hodnotaChlivu() * (dnuVmesici - letajici.getPocetPlnychDnu())) > MAX_PLANOVAT ? MAX_PLANOVAT : (sksl.hodnotaChlivu() * (dnuVmesici - letajici.getPocetPlnychDnu()));
                    letajici.setPlanujSluzeb(kUlozeni);
                    break;
                }
            }
            //System.out.format("%s : planovat : %f", letajici.getJmeno(),letajici.getPlanujSluzeb());
        }
        text += String.format("...%d...done", vratka.size());
        return vratka;
    }

    private List<SlouziciPuvodni> nactiNeplanovane() {
        List<SlouziciPuvodni> vratka = new ArrayList<>();
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH, 1);
        GregorianCalendar gc1 = new GregorianCalendar();
        gc1.set(Calendar.DAY_OF_MONTH, 1);
        gc1.add(Calendar.MONTH, 2);
        gc1.add(Calendar.DAY_OF_MONTH, -1);
        Query qLast = em.createNativeQuery("SELECT letajici FROM pozadavky WHERE datum BETWEEN ? AND ?  AND pozadavek IN (SELECT pozadavek FROM typy_pozadavku WHERE NOT useracces) AND letajici NOT IN (SELECT letajici FROM povoleni_sluzeb WHERE povoleno = true) GROUP BY letajici");
        qLast.setParameter(1, gc, TemporalType.DATE);
        qLast.setParameter(2, gc1, TemporalType.DATE);
        Query q2 = em.createNativeQuery("SELECT dojizdeni FROM letajici_sluzby2 WHERE letajici = ?");
        for (Object lt : qLast.getResultList()) {
            String jmeno = (String) lt;
            q2.setParameter(1, jmeno);
            vratka.add(new SlouziciPuvodni(jmeno, "", (String) q2.getSingleResult()));
        }
        text += String.format("...%d", vratka.size());
        return vratka;
    }

    private String dejSchemaDojizdeni(SluzboDenPuvodni sd, String typSluzby, int den) {
        String pomS = "";
        if (typSluzby.equals("LD")) {
            pomS = "LK";
        }
        if (typSluzby.equals("LK")) {
            pomS = "LD";
        }
        if (typSluzby.equals("SD")) {
            pomS = "SK";
        }
        if (typSluzby.equals("SK")) {
            pomS = "SD";
        }
        SluzboDenPuvodni pom = sd;
        String letajici = "";
        while (pom != null) {
            if ((pom.getDen() == den) && (pom.getTypsluzby().equals(pomS))) {
                letajici = pom.getSlouzici().getJmeno();
                break;
            }
            pom = pom.getNahoru();
        }
        if (letajici.equals("")) {
            return "";
        }
        Query q = em.createNativeQuery("SELECT dojizdeni FROM letajici_sluzby2 WHERE letajici = ?");
        q.setParameter(1, letajici);
        try {
            return (String) q.getSingleResult();
        } catch (NoResultException e) {
            return "";
        }
    }

    private void vypisKolik(SluzboDenPuvodni vysledek, List<SlouziciPuvodni> seznamSlouzicich) {
        for (SlouziciPuvodni ss : seznamSlouzicich) {
            int[] pom = pocetSluzeb(ss.getJmeno(), vysledek);
            float zmena = ss.getPlanujSluzeb() - pom[0] - pom[1];
            if (Math.abs(pom[0] + pom[1] - ss.getPlanujSluzeb()) >= 1) {
                text = text + String.format("\n%s: planovat: %.2f / skutecnost %d / pridat %d", ss.getJmeno(), ss.getPlanujSluzeb(), (pom[0] + pom[1]), (int) zmena);
            }
        }
    }

    public void ulozNaplanovane() {
        /*if (naplanovano) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.set(Calendar.DAY_OF_MONTH, 1);
            gc.add(Calendar.MONTH, 1);
            SluzboDenPuvodni pom = navrhSluzeb;
            while (pom != null) {
                gc.set(Calendar.DAY_OF_MONTH, pom.getDen());
                ulozSluzbu(gc, pom.getTypsluzby(), pom.getSlouzici().getJmeno());
                pom = pom.getNahoru();
            }
        }*/
        PlanovaniSluzeb.getInstance(lb.isLoggedAsMedved()).ulozNaplanovane();
    }

    

    
    /*
     private List<Sluzby> nactiMinulyMesic(GregorianCalendar gc) {
     Query q = em.createNamedQuery("Sluzby.konecMesice");
     q.setMaxResults(6);
     q.setParameter("datum", gc, TemporalType.DATE);
     text += "done";
     return q.getResultList();
     }*/

    static public class ColumnModelvII implements Serializable {

        private final String header;
        private final String property;

        public ColumnModelvII(String header, String property) {
            this.header = header;
            this.property = property;
        }

        public String getHeader() {
            return header;
        }

        public String getProperty() {
            return property;
        }
    }

    private static class PomSDClass {

        private final int den;
        private final String typSluzby;
        private final int volnychPolicek;
        private long kolikratNesel = 0;

        @Override
        public String toString() {
            return "PomSDClass{" + "den=" + den + ", typSluzby=" + typSluzby + ", volnychPolicek=" + volnychPolicek + '}';
        }

        public PomSDClass(int den, String typSluzby, int volnychPolicek) {
            this.den = den;
            this.typSluzby = typSluzby;
            this.volnychPolicek = volnychPolicek;
            this.kolikratNesel = 0;
        }
    }

    private static class SkupinaSluzeb {

        int nepresunutelne;
        int LK;
        int LD;
        int LP;
        int SK;
        int SD;
        int SP;
        String jmenoSkupiny;
        List<Integer> volnychChlivu;

        SkupinaSluzeb(String jmenoSkupiny) {
            this.volnychChlivu = new ArrayList<>();
            this.SP = 0;
            this.SD = 0;
            this.SK = 0;
            this.LP = 0;
            this.LD = 0;
            this.LK = 0;
            this.nepresunutelne = 0;
            this.jmenoSkupiny = jmenoSkupiny;
        }

        int celkemSluzeb() {
            return nepresunutelne + LK + LD + LP + SK + SD + SP;
        }

        float hodnotaChlivu() {
            int[] freeChlivky = new int[volnychChlivu.size()];
            for (int i = 0; i < volnychChlivu.size(); i++) {
                freeChlivky[i] = volnychChlivu.get(i);
            }
            return recHodnotaChlivu(celkemSluzeb(), freeChlivky);
        }

        float hodnotaChlivuMinus() {
            int[] freeChlivky = new int[volnychChlivu.size()];
            for (int i = 0; i < volnychChlivu.size(); i++) {
                freeChlivky[i] = volnychChlivu.get(i);
            }
            return recHodnotaChlivu(celkemSluzeb() - 1, freeChlivky);
        }

        float hodnotaChlivuPlus() {
            int[] freeChlivky = new int[volnychChlivu.size()];
            for (int i = 0; i < volnychChlivu.size(); i++) {
                freeChlivky[i] = volnychChlivu.get(i);
            }
            return recHodnotaChlivu(celkemSluzeb() + 1, freeChlivky);
        }

        float recHodnotaChlivu(int pocetSluzeb, int[] freeChlivky) {
            int sum = 0;
            boolean konec = true;
            for (int pom : freeChlivky) {
                sum += pom;
            }
            if (sum == 0) {
                return MAX_PLANOVAT;//proste neco velkeho
            }
            float vratka = (float) pocetSluzeb / (float) sum;
            for (int i = 0; i < freeChlivky.length; i++) {
                if (freeChlivky[i] * vratka >= MAX_PLANOVAT) {
                    freeChlivky[i] = 0;
                    pocetSluzeb -= MAX_PLANOVAT;
                    konec = false;
                }
            }
            if (konec) {
                return vratka;
            }
            return recHodnotaChlivu(pocetSluzeb, freeChlivky);
        }

        void pridejSluzbu(String typ, int pocet) {
            switch (typ) {
                case "LK":
                    LK += pocet;
                    break;
                case "LD":
                    LD += pocet;
                    break;
                case "LP":
                    LP += pocet;
                    break;
                case "SK":
                    SK += pocet;
                    break;
                case "SD":
                    SD += pocet;
                    break;
                case "SP":
                    SP += pocet;
                    break;
            }
        }

        void addLetajici(SlouziciPuvodni sl, int dnuVmesici, int nepresunutelne) {
            if (!sl.getSkupina().equals(jmenoSkupiny)) {
                throw new IllegalArgumentException(String.format("spatne jmeno skupiny > %s / %s", sl.getJmeno(), sl.getSkupina()));
            }
            if (dnuVmesici - sl.getPocetPlnychDnu() < 0) {
                throw new IllegalArgumentException(String.format("spatny pocet plnych dnu > %s / %d", sl.getJmeno(), sl.getPocetPlnychDnu()));
            }
            volnychChlivu.add(dnuVmesici - sl.getPocetPlnychDnu());
            this.nepresunutelne += nepresunutelne;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SkupinaSluzeb other = (SkupinaSluzeb) obj;
            if (!Objects.equals(this.jmenoSkupiny, other.jmenoSkupiny)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + this.nepresunutelne;
            hash = 53 * hash + this.LK;
            hash = 53 * hash + this.LD;
            hash = 53 * hash + this.LP;
            hash = 53 * hash + this.SK;
            hash = 53 * hash + this.SD;
            hash = 53 * hash + this.SP;
            hash = 53 * hash + Objects.hashCode(this.jmenoSkupiny);
            return hash;
        }

        private int getVolnychChlivu() {
            int vratka = 0;
            for (Integer chliv : volnychChlivu) {
                vratka += chliv;
            }
            return vratka;
        }

        private boolean muzeDat(SkupinaSluzeb komu) {
            if (komu.celkemSluzeb() >= komu.volnychChlivu.size() * MAX_PLANOVAT) {
                return false;
            }
            if (jmenoSkupiny.contains("LK") && komu.jmenoSkupiny.contains("LK")
                    && LK > 0) {
                return true;
            }
            if (jmenoSkupiny.contains("LD") && komu.jmenoSkupiny.contains("LD")
                    && LD > 0) {
                return true;
            }
            if (jmenoSkupiny.contains("LP") && komu.jmenoSkupiny.contains("LP")
                    && LP > 0) {
                return true;
            }
            if (jmenoSkupiny.contains("SK") && komu.jmenoSkupiny.contains("SK")
                    && SK > 0) {
                return true;
            }
            if (jmenoSkupiny.contains("SD") && komu.jmenoSkupiny.contains("SD")
                    && SD > 0) {
                return true;
            }
            if (jmenoSkupiny.contains("SP") && komu.jmenoSkupiny.contains("SP")
                    && SP > 0) {
                return true;
            }
            return false;
        }

        private void predejSluzbu(SkupinaSluzeb komu) {
            if (jmenoSkupiny.contains("LK") && komu.jmenoSkupiny.contains("LK")) {
                pridejSluzbu("LK", -1);
                komu.pridejSluzbu("LK", 1);
                return;
            }
            if (jmenoSkupiny.contains("LD") && komu.jmenoSkupiny.contains("LD")) {
                pridejSluzbu("LD", -1);
                komu.pridejSluzbu("LD", 1);
                return;
            }
            if (jmenoSkupiny.contains("LP") && komu.jmenoSkupiny.contains("LP")) {
                pridejSluzbu("LP", -1);
                komu.pridejSluzbu("LP", 1);
                return;
            }
            if (jmenoSkupiny.contains("SK") && komu.jmenoSkupiny.contains("SK")) {
                pridejSluzbu("SK", -1);
                komu.pridejSluzbu("SK", 1);
                return;
            }
            if (jmenoSkupiny.contains("SD") && komu.jmenoSkupiny.contains("SD")) {
                pridejSluzbu("SD", -1);
                komu.pridejSluzbu("SD", 1);
                return;
            }
            if (jmenoSkupiny.contains("SP") && komu.jmenoSkupiny.contains("SP")) {
                pridejSluzbu("SP", -1);
                komu.pridejSluzbu("SP", 1);
                return;
            }
            throw new NoResultException("nepodarilo se predat sluzbu");
        }

        @Override
        public String toString() {
            String chl = "[";
            for (Integer i : this.volnychChlivu) {
                chl += String.format(" %d,", i);
            }
            return "Skupina " + jmenoSkupiny + ": sluzeb=" + celkemSluzeb() + " n=" + nepresunutelne + ", LK=" + LK + ", LD=" + LD + ", LP=" + LP + ", SK=" + SK + ", SD=" + SD + ", SP=" + SP + ", letajicich=" + volnychChlivu.size() + ", chlivek=" + hodnotaChlivu() + ", chlivekM=" + hodnotaChlivuMinus() + ", chlivekP=" + hodnotaChlivuPlus() + ", chlivy=" + chl + "]";
        }

    }

    private static class pomSDComparator implements Comparator<PomSDClass> {

        public pomSDComparator() {
        }

        @Override
        public int compare(PomSDClass o1, PomSDClass o2) {
            return (int) (o2.kolikratNesel - o1.kolikratNesel);
        }
    }

}
