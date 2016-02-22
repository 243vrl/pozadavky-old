/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.beans;

import cz.seznam.wenaaa.is243vrl.Slouzici;
import cz.seznam.wenaaa.is243vrl.SluzboDen;
import cz.seznam.wenaaa.is243vrl.entityClasses.jsf.LetajiciSluzbyController;
import cz.seznam.wenaaa.utils.Kalendar;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 *
 * @author vena
 */
@Named(value = "planovaniBean")
@SessionScoped
public class PlanovaniBean implements Serializable{
    
    @PersistenceContext(unitName = "pozadavky_PU")
    private EntityManager em;
    @Inject
    private UserTransaction ut;
    @Inject
    LetajiciSluzbyController lsc;
    private boolean[] dnySvozu;
    private boolean planuj; 
    private List<ColumnModelvII> columns = new ArrayList<>();
    private int zacatek;
    private int konec;
    private String text;
    private boolean naplanovano;
    private boolean nenaplanovano;
    private boolean vPlanovani;
    private SluzboDen navrhSluzeb;
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

    /**
     * @return the MIN_PLANOVAT
     */
    public static int getMIN_PLANOVAT() {
        return MIN_PLANOVAT;
    }
    
    public boolean isNaplanovano() {
        return naplanovano;
    }
    public boolean isNenaplanovano() {
        return nenaplanovano;
    }
    public String proLastColumn(String letajici){
        int[] pom = pocetSluzeb(letajici, navrhSluzeb);
        return String.format("%d: %d+%d", pom[0]+pom[1],pom[0],pom[1]);
    }
    public int[] pocetSluzeb(String letajici, SluzboDen sd){
        SluzboDen pom = sd;
        int[] vratka = new int[2];
        vratka[0] = 0;
        vratka[1] = 0;
        while(pom != null){
            if(pom.getSlouzici().equals(letajici)){
                if(pom.getTypsluzby().startsWith("L")){
                    vratka[0]++;
                }
                else{
                    vratka[1]++;
                }
            }
            pom = pom.getNahoru();
        }
        return vratka;
    }
    public String getText() {
        return text;
    }

    public void setvPlanovani(boolean vPlanovani) {
        this.vPlanovani = vPlanovani;
    }
    
    public boolean isvPlanovani() {
        return vPlanovani;
    }

    public PlanovaniBean() {
        this.vPlanovani = false;
        planuj = true;
        naplanovano = false;
        text = "";
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH, 1);
        this.dnySvozu = new boolean[Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1)+1];
        populateColumns();
    }
    public void nastavAtribZmeny(){
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        //System.out.format("parametry %s / %s",params.get("jmeno"),params.get("den") );
        jmenoProZmenu =lsc.getLetajici(Integer.parseInt(params.get("jmeno")));
        denProZmenu = Integer.parseInt(params.get("den"));
        //System.out.print("nastav atrib zmeny: "+jmenoProZmenu+"/"+String.valueOf(denProZmenu));
    }
    public void zmenSluzbuZKontroly(String typSluzby){
        //System.out.print("menim sluzbu");
        SluzboDen pom = navrhSluzeb;
        while(pom != null){
            if(pom.getDen()==denProZmenu && pom.getTypsluzby().equals(typSluzby)){
                pom.setSlouzici(jmenoProZmenu);
                //System.out.print("zmenneno");
                break;
            }
            pom=pom.getNahoru();
        }
    }
    public String dejSluzbuProKontrolu(String slouzici, int den){
        String vratka = "";
        SluzboDen pom = navrhSluzeb;
        while(pom != null){
            if(pom.getSlouzici().equals(slouzici) && den == pom.getDen()){
                vratka += pom.getTypsluzby();
            }
            pom = pom.getNahoru();
        }
        return vratka;
    }
    public void naplanuj(ActionEvent e){
        //if(vPlanovani) return;
        vPlanovani = true;
        int zvysovani = 0;
        SluzboDen vysledek = null;
        int mezPaSoNe = 1;
        int mezSv;
        float mezPresMiru = 1;
        List<PomSDClass> poradiSD = null;
        nenaplanovano = false;
        naplanovano = false;
        navrhSluzeb = null;
        text = String.format("Uzaviram db...");
        uzavriDB();
        text = text+String.format("\nNačítám seznam sloužících...");
        Slouzici seznamSlouzicich = nactiSlouzici();
        
        try{
            text = text+String.format("\nNačítám službodny...");
            poradiSD = dejPoradiSluzbodnu(seznamSlouzicich);
        }catch(NoResultException ex){
            text = text+"\n"+ex.getMessage();
            System.out.print(ex.getMessage());
            nenaplanovano = true;
            vPlanovani = false;
            return;
        }
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH,1);
        switch(gc.get(Calendar.MONTH)){
            case Calendar.JULY:
            case Calendar.DECEMBER:
                mezSv = 2;
                break;
            default:
                mezSv = 1;
                break;
        }
        //uvodni hledani
        while(vysledek == null){
            if(mezPaSoNe > 5){
                text = text +"\nNenalezeno, uprav požadavky/svoz.";
                nenaplanovano = true;
                vPlanovani = false;
                return;
            }
            text = text+"\n"+String.format("uvodni hledani> presMiru: %f, PaSoNe: %d, Sv: %d >", mezPresMiru, mezPaSoNe, mezSv);
            vysledek = naplanuj(mezPresMiru<2?2:25,mezPresMiru, mezPaSoNe, mezSv,seznamSlouzicich, poradiSD,true);
            if(vysledek == null){
                if(mezPresMiru < 2){
                    mezPresMiru += 0.5;
                    continue;
                }
                if((1 & zvysovani++)==0){
                    mezPaSoNe++;
                }else{
                    mezPresMiru += 1;
                }
                if(zvysovani>2) mezSv++;
            }
        }
        //zlepsovani
        mezPresMiru=vysledek.getMaxsluzebpresmiru();
        //float minulaPresMiru = mezPresMiru;
        while(true){
            boolean ukonci = true;
            text = text + "\n"+String.format("vylepšování hloubka> presMiru: %f, PaSoNe: %d, Sv: %d >", mezPresMiru, mezPaSoNe, mezSv);
            SluzboDen pom = naplanuj(25,mezPresMiru, mezPaSoNe, mezSv, seznamSlouzicich, poradiSD,true);
            if (pom != null){
                vysledek = pom;
                ukonci = false;
            }
            if(ukonci) break;
            //minulaPresMiru = mezPresMiru;
            mezPresMiru = vysledek.getMaxsluzebpresmiru();
        }
        /*mezPresMiru = minulaPresMiru+(float)0.01;
        while(true){
            boolean ukonci = true;
            text = text + "\n"+String.format("vylepšování čtverec> presMiru: %f, PaSoNe: %d, Sv: %d >", mezPresMiru, mezPaSoNe, mezSv);
            SluzboDen pom = naplanuj(25,mezPresMiru, mezPaSoNe, mezSv, seznamSlouzicich, poradiSD,false);
            if (pom != null){
                vysledek = pom;
                ukonci = false;
            }
            if(ukonci) break;
            mezPresMiru = vysledek.getMaxsluzebpresmiru();
        }
        {
            Slouzici pom = seznamSlouzicich;
            while(pom != null){
                int[] ps = pocetSluzeb(pom.getJmeno(), vysledek);
                int psSoucet = ps[0]+ps[1];
                float zmena = pom.getPlanujSluzeb()-psSoucet;
                if (zmena > 1){
                    psSoucet += (int) zmena;
                }
                pom.setMaxPocetSluzeb(psSoucet);
                pom = pom.getDalsi();
            }
        }
        text = text + "\n"+String.format("úprava > ");
        mezPresMiru = (float)0.1;*/
        SluzboDen pomSD = naplanuj(300,mezPresMiru, mezPaSoNe, mezSv, seznamSlouzicich, poradiSD,true);
        if (pomSD != null){
            vysledek = pomSD;
        }
        
        vypisKolik(vysledek,seznamSlouzicich);
        navrhSluzeb = vysledek;
        text = text+String.format("\ndone");
        naplanovano = true;
    }
    private SluzboDen naplanuj(int trvani, float mezPresMiru, int mezPaSoNeSv, int mezSv, Slouzici seznamSlouzicich, List<PomSDClass> poradiSD,boolean naHloubku){
        
        List<SluzboDen> sluzbodny = new ArrayList<>();
        for(String letajici: dejPoradiLetajicich(poradiSD.get(0).typSluzby, poradiSD.get(0).den, "")){
            SluzboDen pom = new SluzboDen(poradiSD.get(0).den,poradiSD.get(0).typSluzby,null,letajici,seznamSlouzicich);
            if(pom.isValid(seznamSlouzicich)){
                sluzbodny.add(pom);
            }
        }   
        SluzboDen rozvijeny = null;
        //for(int i = 0; i < 1000; i++){
        int i = 0;
        while(true){
            if(trvani*1000 < i++){
            //if((System.currentTimeMillis()-ted)>trvani*1000){
                //System.out.format("stop cas, hloubka: %d", rozvijeny.getHloubka());
                return null;
            }
            if(sluzbodny.isEmpty()){
                return null;
            }
            //System.out.print("----------------------------");
            try{
                String[] arrText = text.split("\n");
                String[] arrText2 = arrText[arrText.length-1].split(" ");
                Integer.parseInt(arrText2[arrText2.length-1]);
                arrText2[arrText2.length-1]=String.format("%d", i);
                arrText[arrText.length-1] = String.join(" ",arrText2);
                text = String.join("\n", arrText);
            }
            catch(NumberFormatException ex){
                text = text+String.format(" %d", i);
            }
            rozvijeny = sluzbodny.get(0);
            for(SluzboDen pom: sluzbodny){
                if(pom.jeMensiNezParam(rozvijeny,naHloubku,seznamSlouzicich)){
                    rozvijeny = pom;
                }
            }
            sluzbodny.remove(rozvijeny);
            /*{
                SluzboDen pomS =rozvijeny;
                while(pomS != null){
                    System.out.print(pomS);
                    pomS = pomS.getNahoru();
                }
            }*/
            
            if((rozvijeny.getHloubka() + 1)==poradiSD.size()){
                break;
            }
            int novaHloubka = rozvijeny.getHloubka() + 1;
            String dojizdeni = dejSchemaDojizdeni(rozvijeny,poradiSD.get(novaHloubka).typSluzby,poradiSD.get(novaHloubka).den);
            SluzboDen predchozi = rozvijeny;
            //System.out.format("rozbaluji: %s", rozvijeny);
            //System.out.format("%d : %s : %d",novaHloubka,poradiSD.get(novaHloubka).typSluzby,poradiSD.get(novaHloubka).den);
            for(String letajici: dejPoradiLetajicich(poradiSD.get(novaHloubka).typSluzby, poradiSD.get(novaHloubka).den, dojizdeni)){
                SluzboDen pom = new SluzboDen(poradiSD.get(novaHloubka).den,poradiSD.get(novaHloubka).typSluzby,predchozi,letajici,seznamSlouzicich);
                //System.out.print(pomS);
                if (pom.getMaxsluzebpresmiru()>=mezPresMiru) continue;
                if (pom.getMaxpocetsvatku()>mezSv) continue;
                if (pom.getMaxpocetsobot()>mezPaSoNeSv) continue;
                if (pom.getMaxpocetnedel()>mezPaSoNeSv) continue;
                if (pom.getMaxpocetpatku()>mezPaSoNeSv) continue;
                if(pom.isValid(seznamSlouzicich)){
                    sluzbodny.add(pom);
                }
            }
            //if(nejde) System.out.print("nejde");
            //System.out.print("///////////////////////////////////////////////////");
            
        }
        return rozvijeny;
    }
    public List<ColumnModelvII> getColumns() {
        return columns;
    }
    public void setColumns(List<ColumnModelvII> columns) {
        this.columns = columns;
    }
    private List<PomSDClass> dejPoradiSluzbodnu(Slouzici seznamSlouzicich) {
        //System.out.println("Nacitam poradi sluzbodnu...");
        List<PomSDClass> vratka = new ArrayList<>();
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH, 1);
        for(Integer den: dejPoradiDnu()){
            for(String typ_sluzby: dejPoradiSluzeb()){
                int volnych = 0;
                gc.set(Calendar.DAY_OF_MONTH, den);
                Query q1 = em.createNativeQuery("SELECT letajici FROM pozadavky WHERE datum = ? AND pozadavek = ?");
                q1.setParameter(1, gc, TemporalType.DATE);
                q1.setParameter(2, typ_sluzby);
                try{
                    Object pom = q1.getSingleResult();
                    int k = vratka.size();
                    for(int i = 0; i <= k; i++){
                        if(i == k) vratka.add(new PomSDClass(den, typ_sluzby, -1));
                        if(volnych < vratka.get(i).volnychPolicek){
                            vratka.add(i, new PomSDClass(den, typ_sluzby, -1));
                            break;
                        }
                    }
                    continue;
                } catch(NoResultException e){
                    //nic
                }
                List<Object> mozny_slouzici = null;
                if(typ_sluzby.startsWith("L") && !dnySvozu[den]){
                    Query qSvoz = em.createNativeQuery("SELECT ls.letajici FROM letajici_sluzby AS ls, povoleni_sluzeb AS ps WHERE ls.letajici = ps.letajici AND ps.typ_sluzby = ? AND ps.povoleno = true AND ls.do_lini_svozem = ?");
                    qSvoz.setParameter(1, typ_sluzby);
                    qSvoz.setParameter(2, dnySvozu[den]);
                    mozny_slouzici = qSvoz.getResultList();
                }
                else{
                    Query q2 = em.createNativeQuery("SELECT ls.letajici FROM letajici_sluzby AS ls, povoleni_sluzeb AS ps WHERE ls.letajici = ps.letajici AND ps.typ_sluzby = ? AND ps.povoleno = true");
                    q2.setParameter(1, typ_sluzby);
                    mozny_slouzici = q2.getResultList();
                }
                for(Object pom: mozny_slouzici){
                    long volneDny = seznamSlouzicich.getPlneVolneDny((String) pom);
                    if(((long)Math.pow(2, den) & volneDny) == 0){
                        volnych++;
                    }
                }
                if(volnych == 0){
                    //System.out.print("nikdo nemuze: "+Integer.toString(den));
                    throw new NoResultException("nikdo nemuze: "+Integer.toString(den)+"  : "+typ_sluzby);
                }
                //System.out.print("vratka size pred: "+Integer.toString(vratka.size()));
                int k = vratka.size();
                for(int i = 0; i <= k; i++){
                    if(i == k) vratka.add(new PomSDClass(den, typ_sluzby, volnych));
                    if(volnych < vratka.get(i).volnychPolicek){
                        vratka.add(i, new PomSDClass(den, typ_sluzby, volnych));
                        break;
                    }
                }
                //System.out.print("vratka size po: "+Integer.toString(vratka.size()));
            }
        }
        return vratka;
    }
    private List<String> dejPoradiSluzeb(){
        List<String> vratka = new ArrayList<>();
        vratka.add("LD");
        vratka.add("SD");
        vratka.add("LK");
        vratka.add("SK");
        return vratka;
    }
    private List<Integer> dejPoradiDnu(){
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH,1);
        List<Integer> poradiDnu = new ArrayList<>();
        for(int i = 1; i<=Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1); i++){
            gc.set(Calendar.DAY_OF_MONTH, i);
            if(vsedniDvojSvatek(i)) poradiDnu.add(i);
        }
        for(int i = 1; i<=Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1); i++){
            gc.set(Calendar.DAY_OF_MONTH, i);
            if(vsedniDvojSvatek(i)) continue;
            if(vsedniSvatek(i)) poradiDnu.add(i);
        }
        for(int i = 1; i<=Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1); i++){
            gc.set(Calendar.DAY_OF_MONTH, i);
            if(vsedniDvojSvatek(i)) continue;
            if(vsedniSvatek(i)) continue;
            if(gc.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY) poradiDnu.add(i);
        }
        for(int i = 1; i<=Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1); i++){
            gc.set(Calendar.DAY_OF_MONTH, i);
            if(vsedniDvojSvatek(i)) continue;
            if(vsedniSvatek(i)) continue;
            if(gc.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) poradiDnu.add(i);
        }
        for(int i = 1; i<=Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1); i++){
            gc.set(Calendar.DAY_OF_MONTH, i);
            if(vsedniDvojSvatek(i)) continue;
            if(vsedniSvatek(i)) continue;
            if(gc.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY) poradiDnu.add(i);
        }
        for(int i = 1; i<=Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1); i++){
            gc.set(Calendar.DAY_OF_MONTH, i);
            if(vsedniDvojSvatek(i)) continue;
            if(vsedniSvatek(i)) continue;
            if(gc.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY) continue;
            if(gc.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY) continue;
            if(gc.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) continue;
            poradiDnu.add(i);
        }
        return poradiDnu;
    }
    private boolean vsedniDvojSvatek(int den){
        GregorianCalendar pomGC = new GregorianCalendar();
        pomGC.set(Calendar.DAY_OF_MONTH,1);
        pomGC.add(Calendar.MONTH, 1);
        pomGC.set(Calendar.DAY_OF_MONTH, den);
        switch(pomGC.get(Calendar.DAY_OF_WEEK)){
            case Calendar.FRIDAY:
            case Calendar.SATURDAY:
            case Calendar.SUNDAY:
                return false;
        }
        if(!Kalendar.jeSvatek(pomGC)) return false;
        pomGC.add(Calendar.DAY_OF_MONTH, 1);
        return Kalendar.jeSvatek(pomGC);
    }
    private boolean vsedniSvatek(int den) {
        GregorianCalendar pomGC = new GregorianCalendar();
        pomGC.set(Calendar.DAY_OF_MONTH,1);
        pomGC.add(Calendar.MONTH, 1);
        pomGC.set(Calendar.DAY_OF_MONTH, den);
        switch(pomGC.get(Calendar.DAY_OF_WEEK)){
            case Calendar.SATURDAY:
                return false;
            case Calendar.SUNDAY:
                pomGC.add(Calendar.DAY_OF_MONTH, 1);
                return Kalendar.jeSvatek(pomGC);
            case Calendar.FRIDAY:
                return Kalendar.jeSvatek(pomGC);
        }
        if(Kalendar.jeSvatek(pomGC)) return true;
        pomGC.add(Calendar.DAY_OF_MONTH, 1);
        return Kalendar.jeSvatek(pomGC);
    }
    private void uzavriDB() {
        GregorianCalendar pomGC = new GregorianCalendar();
        pomGC.set(Calendar.DAY_OF_MONTH, 1);
        pomGC.add(Calendar.MONTH, 2);
        try {
            ut.begin();
            em.joinTransaction();
            Query qUpd = em.createNativeQuery("UPDATE pomtab SET pozadavkyod = ?");
            qUpd.setParameter(1, pomGC, TemporalType.DATE);
            qUpd.executeUpdate();
            ut.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(PlanovaniBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private List<String> dejPoradiLetajicich(String typ_sluzby, int den, String dojizdeni) {
        List<String> vratka = new ArrayList<>();
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH, 1);
        gc.set(Calendar.DAY_OF_MONTH, den);
        Query q1 = em.createNativeQuery("SELECT letajici FROM pozadavky WHERE datum = ? AND pozadavek = ?");
        q1.setParameter(1, gc, TemporalType.DATE);
        q1.setParameter(2, typ_sluzby);
        try{
            vratka.add((String)q1.getSingleResult());
            return vratka;
        } catch(NoResultException e){
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
        if(typ_sluzby.startsWith("L")){
            zaklad = dnySvozu[den]?zaklad:zakladZachr;
            zaklad += " "+zachranka;
            paramZachranka = true;
            prvni = false;
        }
        if(!"".equals(dojizdeni)){
            if (prvni) zaklad += " "+podledojizdeni;
            else zaklad += " , "+podledojizdeni;
            paramDojizdeni = true;
            prvni = false;
        }
        switch(gc.get(Calendar.DAY_OF_WEEK)){
            case Calendar.MONDAY:
            case Calendar.TUESDAY:
            case Calendar.WEDNESDAY:
            case Calendar.THURSDAY:
                if(vsedniSvatek(gc.get(Calendar.DAY_OF_MONTH)) || vsedniDvojSvatek(gc.get(Calendar.DAY_OF_MONTH))) {
                    if(prvni)zaklad += " "+podleSvatku;
                    else zaklad += " , "+podleSvatku;
                    prvni = false;
                }
                break;
            case Calendar.FRIDAY:
                if(vsedniSvatek(gc.get(Calendar.DAY_OF_MONTH)) || vsedniDvojSvatek(gc.get(Calendar.DAY_OF_MONTH))) {
                    if(prvni)zaklad += " "+podleSvatku;
                    else zaklad += " , "+podleSvatku;
                    prvni = false;
                }
                else {
                    if(prvni)zaklad += " "+podlePatku;
                    else zaklad += " , "+podlePatku;
                    prvni = false;
                }
                break;
            case Calendar.SATURDAY:
                if(prvni)zaklad += " "+podleSobot;
                else zaklad += " , "+podleSobot;
                prvni = false;
                break;
            case Calendar.SUNDAY:
                if(vsedniSvatek(gc.get(Calendar.DAY_OF_MONTH))){
                    if(prvni)zaklad += " "+podleSvatku;
                    else zaklad += " , "+podleSvatku;
                    prvni = false;
                }
                else{
                    if(prvni)zaklad += " "+podleNedel;
                    else zaklad += " , "+podleNedel;
                    prvni = false;
                }
                break;
        }
        if(prvni)zaklad += " "+konecS;
        else zaklad += " , "+konecS;
        //zaklad += " , "+konecS;
        try {
            ut.begin();
            em.joinTransaction();
            Query qSel = em.createNativeQuery(zaklad);
            int i = 1;
            qSel.setParameter(i++, typ_sluzby);
            if(paramZachranka) qSel.setParameter(i++, jedeSvoz(gc));
            if(paramDojizdeni) qSel.setParameter(i, dojizdeni);
            //System.out.print(zaklad);
            for(Object letajici: qSel.getResultList()){
                vratka.add((String)letajici);
            }
            ut.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(PlanovaniBean.class.getName()).log(Level.SEVERE, null, ex);
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
    public void vyberZacatek(){
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        //String myName1 = params.get("jmeno");
        String myName2 = params.get("den");
        this.zacatek = Integer.parseInt(myName2);
        this.konec = Integer.parseInt(myName2);
        //System.out.format("Zacatek : %d",zacatek);
    }
    public void vyberKonec(){
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        //String myName1 = params.get("jmeno");
        String myName2 = params.get("den");
        this.konec = Integer.parseInt(myName2);
        //System.out.format("Konec : %d",konec);
    }
    public void nastavSvoz(boolean jede){
        for(int i = 1; i < this.dnySvozu.length; i++){
            if(i>=this.zacatek && i<=this.konec){
                dnySvozu[i] = jede;
                //System.out.format("den: %d svoz %s", i, dnySvozu[i]?"jede":"nejede");
            }
        }
    }
    private void populateColumns(){
        columns = new ArrayList<>();
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH, 1);
        columns.add(new ColumnModelvII("","mezera"));
        for(int i = 1; i <= Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1);i++) {
            columns.add(new ColumnModelvII(String.format("%d", i), String.format("%d", i)));
        }
    }
    private Slouzici nactiSlouzici() {
        Slouzici vratka = null;
        List<String> poradiSluzeb = dejPoradiSluzeb();
        //System.out.print("Nacitam slouzici...");
        Query q1 = em.createNativeQuery("SELECT CAST(count(*) AS int) FROM letajici_sluzby");
        int pocetSlouzicich = (Integer) q1.getSingleResult();
        //System.out.format("\tcelkem slouzicich...%d",pocetSlouzicich);
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH, 1);
        int pocetChlivku = pocetSlouzicich*Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1);
        //System.out.format("\tcelkem chlivku...%d",pocetChlivku);
        Query q2 = em.createNativeQuery("SELECT CAST(count(*) AS int) FROM pozadavky WHERE pozadavek NOT IN (SELECT pozadavek FROM typy_pozadavku WHERE NOT useracces) AND datum BETWEEN ? AND ?");
        gc.set(Calendar.DAY_OF_MONTH, 1);
        q2.setParameter(1, gc, TemporalType.DATE);
        GregorianCalendar gc1 = new GregorianCalendar();
        gc1.set(Calendar.DAY_OF_MONTH, 1);
        gc1.add(Calendar.MONTH, 2);
        gc1.add(Calendar.DAY_OF_MONTH, -1);
        q2.setParameter(2, gc1, TemporalType.DATE);
        int pocetPozadavku = (Integer) q2.getSingleResult();
        //System.out.format("\tcelkem pozadavku...%d",pocetPozadavku);
        Query q3 = em.createNativeQuery("SELECT letajici, dojizdeni FROM letajici_sluzby ORDER BY poradi");
        for(Object letajiciDojizdeni: q3.getResultList()){
            Object[] ld = (Object[])letajiciDojizdeni;
            String letajici = (String)ld[0];
            String dojizdeni = (String)ld[1];
            Query q4 = em.createNativeQuery("SELECT CAST(count(*) AS int) FROM pozadavky WHERE pozadavek NOT IN (SELECT pozadavek FROM typy_pozadavku WHERE NOT useracces) AND datum BETWEEN ? AND ?  AND letajici=?");
            q4.setParameter(1, gc, TemporalType.DATE);
            q4.setParameter(2, gc1, TemporalType.DATE);
            q4.setParameter(3, letajici);
            //planovat = celkemsluzeb*(volnychdnu)/(celkemvolnychdnu)
            float planovat = (float)(Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1)*this.dejPoradiSluzeb().size())*(Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1)-(Integer) q4.getSingleResult())/(pocetChlivku-pocetPozadavku);
            float maxSluzeb;
            planovat = (planovat > getMAX_PLANOVAT())?getMAX_PLANOVAT():planovat;
            maxSluzeb = planovat;
            maxSluzeb = (maxSluzeb < getMIN_PLANOVAT())?getMIN_PLANOVAT():maxSluzeb;
            Query q5 = em.createNativeQuery("SELECT pozadavek, datum FROM pozadavky WHERE datum BETWEEN ? AND ?  AND letajici=?");
            q5.setParameter(1, gc, TemporalType.DATE);
            q5.setParameter(2, gc1, TemporalType.DATE);
            q5.setParameter(3, letajici);
            long nemuze = 0;
            //System.out.print((String)letajici);
            for(Object pom: q5.getResultList()){
                Object[] pom1 = (Object[])pom;
                String pom2 = (String)(pom1[0]);
                GregorianCalendar pomGC = new GregorianCalendar();
                pomGC.setTime((Date) pom1[1]);
                int den = pomGC.get(Calendar.DAY_OF_MONTH);
                //System.out.print(pom2+" : "+Integer.toString(den));
                if(poradiSluzeb.contains(pom2)){
                    if ((nemuze & (long) Math.pow(2, den-1)) == 0) nemuze += (long) Math.pow(2,den-1);
                    if ((nemuze & (long) Math.pow(2, den+1)) == 0) nemuze += (long) Math.pow(2,den+1);
                }
                else{
                    if(pom2.startsWith("X")){
                        nemuze += (long) Math.pow(2, den);
                    }else {
                        nemuze += (long) Math.pow(2,den);
                        if ((nemuze & (long) Math.pow(2, den-1)) == 0) nemuze += (long) Math.pow(2,den-1);
                  }  
                }
                //System.out.print(nemuze);
            }
            //System.out.format("%s : %f : %d", (String)mozny_slouzici,planovat,nemuze);
            if(vratka == null) vratka = new Slouzici(letajici,nemuze,maxSluzeb,planovat,dojizdeni);
            else vratka.addSlouzici(new Slouzici(letajici,nemuze,maxSluzeb,planovat,dojizdeni));
        }
        return vratka;
    }
    private String dejSchemaDojizdeni(SluzboDen sd, String typSluzby, int den) {
        String pomS = "";
        if(typSluzby.equals("LD")){
            pomS = "LK";
        }
        if(typSluzby.equals("LK")){
            pomS = "LD";
        }
        if(typSluzby.equals("SD")){
            pomS = "SK";
        }
        if(typSluzby.equals("SK")){
            pomS = "SD";
        }
        SluzboDen pom = sd;
        String letajici = "";
        while(pom != null){
            if((pom.getDen()==den)&&(pom.getTypsluzby().equals(pomS))){
                letajici = pom.getSlouzici();
                break;
            }
            pom = pom.getNahoru();
        }
        if(letajici.equals("")) return "";
        Query q = em.createNativeQuery("SELECT dojizdeni FROM letajici_sluzby WHERE letajici = ?");
        q.setParameter(1, letajici);
        try{
            return (String)q.getSingleResult();
        } catch(NoResultException e){
            return "";
        }
    }
    private void vypisKolik(SluzboDen vysledek, Slouzici seznamSlouzicich) {
        Slouzici ss = seznamSlouzicich;
        while(ss != null){
            int[] pom = pocetSluzeb(ss.getJmeno(), vysledek);
            float zmena = ss.getPlanujSluzeb()-pom[0]-pom[1];
            if(Math.abs(pom[0]+pom[1]-ss.getPlanujSluzeb())>=1){
                text = text + String.format("\n%s: planovat: %.2f / skutecnost %d / pridat %d",ss.getJmeno(),ss.getPlanujSluzeb(),(pom[0]+pom[1]),(int)zmena);
            }
            
            ss = ss.getDalsi();
        }
    }
    private int[] dejHodnotySluzby(GregorianCalendar gc){
        int[] vratka = {1,0,0,0,0};
        boolean[] svatky = new boolean[2];
        svatky[0] = Kalendar.jeSvatek(gc);
        gc.add(Calendar.DAY_OF_MONTH, 1);
        svatky[1] = Kalendar.jeSvatek(gc);
        gc.add(Calendar.DAY_OF_MONTH, -1);
        switch (gc.get(Calendar.DAY_OF_WEEK)){
            case Calendar.SATURDAY: 
                vratka[2]++;
                break;
            case Calendar.SUNDAY:
                vratka[3]++;
                if(svatky[1]) vratka[4]++;
                break;
            case Calendar.FRIDAY:
                if(svatky[0]) vratka[4]++;
                else vratka[1]++;
                break;
            default:
                if(svatky[0]) vratka[4]++;
                if(svatky[1]) vratka[4]++;
                break;
        }
        return vratka;
    }
    public void ulozNaplanovane(){
        if(naplanovano){
            GregorianCalendar gc = new GregorianCalendar();
            gc.set(Calendar.DAY_OF_MONTH,1);
            gc.add(Calendar.MONTH, 1);
            SluzboDen pom = navrhSluzeb;
            while(pom != null){
                gc.set(Calendar.DAY_OF_MONTH, pom.getDen());
                ulozSluzbu(gc, pom.getTypsluzby(), pom.getSlouzici());
                pom = pom.getNahoru();
            }
        }
    }
    public void ulozSluzbu(GregorianCalendar gc, String typSluzby, String letajici){
        String staryLetajici = null;
        int[] hodnotySluzby = null;
        int[] hodnotyStary = new int[5];
        int[] hodnotyNovy = new int[5];
        Object[] result = null;
        try {
            ut.begin();
            em.joinTransaction();
            Query q1 = em.createNativeQuery("INSERT INTO sluzby (datum) SELECT ? WHERE NOT EXISTS (SELECT 1 FROM sluzby WHERE datum=?)");
            q1.setParameter(1, gc, TemporalType.DATE);
            q1.setParameter(2, gc, TemporalType.DATE);
            q1.executeUpdate();
            
            ut.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(PlanovaniBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        try{
            ut.begin();
            em.joinTransaction();
            try{
                Query qStary = em.createNativeQuery("SELECT "+typSluzby.toLowerCase()+ " FROM sluzby WHERE datum = ?");
                qStary.setParameter(1, gc, TemporalType.DATE);
                staryLetajici = (String) qStary.getSingleResult();                
            }catch(NoResultException e){
                staryLetajici = null;
            }
            if(letajici.equals(staryLetajici)) {
                ut.commit();
                //System.out.print("rovnaj se");
                return;
            }
            hodnotySluzby = dejHodnotySluzby(gc);
            Query qHodnoty = em.createNativeQuery("SELECT pocet_sluzeb, pocet_patku, pocet_sobot, pocet_nedeli, pocet_vsednich_svatku FROM letajici_sluzby WHERE letajici = ?");
            if(staryLetajici != null){
                qHodnoty.setParameter(1, staryLetajici);
                result = (Object[])qHodnoty.getSingleResult();
                for(int i=0;i<hodnotySluzby.length;i++){
                    hodnotyStary[i] = (int)result[i] - hodnotySluzby[i];
                }
            }
            qHodnoty.setParameter(1, letajici);
            result = (Object[])qHodnoty.getSingleResult();
            for(int i=0;i<hodnotySluzby.length;i++){
                hodnotyNovy[i] = (int)result[i] + hodnotySluzby[i];
            }
            Query qUpdate = em.createNativeQuery("UPDATE letajici_sluzby SET pocet_sluzeb = ?, pocet_patku = ?, pocet_sobot = ?, pocet_nedeli = ?, pocet_vsednich_svatku = ? WHERE letajici  = ?");
            if(staryLetajici != null){
                for(int i = 0; i<hodnotyStary.length;i++){
                    qUpdate.setParameter(i+1, hodnotyStary[i]);
                }
                qUpdate.setParameter(6,staryLetajici);
                qUpdate.executeUpdate();
            }
            for(int i = 0; i<hodnotyNovy.length;i++){
                qUpdate.setParameter(i+1, hodnotyNovy[i]);
            }
            qUpdate.setParameter(6,letajici);
            qUpdate.executeUpdate();
            Query qUpdate2 = em.createNativeQuery("UPDATE sluzby SET "+typSluzby.toLowerCase()+" = ? WHERE datum = ?");
            qUpdate2.setParameter(1, letajici);
            qUpdate2.setParameter(2, gc, TemporalType.DATE);
            qUpdate2.executeUpdate();
            ut.commit();
        }
        catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(PlanovaniBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    static public class ColumnModelvII implements Serializable {
        private String header;
        private String property;
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

        @Override
        public String toString() {
            return "PomSDClass{" + "den=" + den + ", typSluzby=" + typSluzby + ", volnychPolicek=" + volnychPolicek + '}';
        }

        public PomSDClass(int den, String typSluzby, int volnychPolicek) {
            this.den = den;
            this.typSluzby = typSluzby;
            this.volnychPolicek = volnychPolicek;
        }
    }
    
}
