/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.beans;

import cz.seznam.wenaaa.is243vrl.entityClasses.jsf.LetajiciSluzbyController;
import cz.seznam.wenaaa.utils.Kalendar;
import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.transaction.UserTransaction;

/**
 *
 * @author vena
 */
@Named(value = "prehledyBean")
@SessionScoped
public class PrehledyBean implements Serializable{
    
    @PersistenceContext(unitName = "pozadavky_PU")
    private EntityManager em;
    @Inject
    UserTransaction ut;
    @Inject
    LetajiciSluzbyController lsc;
    private GregorianCalendar gc;
    private List<List<String>> sluzbyPodleDni;
    private List<List<String>> sluzbyPodlePilotu;
    private List<List<String>> sluzbyPodlePalubaru;
    private List<ColumnModelIII> columns = new ArrayList<>();
    /**
     * Creates a new instance of PrehledyBean
     */
    public PrehledyBean() {
        gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        populateColumns();
    }
    @PostConstruct
    private void nactiSluzbyNaMesic(){
        sluzbyPodlePilotu = new ArrayList<>();
        sluzbyPodlePalubaru = new ArrayList<>();
        //System.out.print("postconstruct nacitam pozadavky");
        List<String> letajici = lsc.getLetajici();
        for(String l: letajici){
            List<String> pom = new ArrayList<>();
            pom.add(l);
            for(int i = 0; i < Kalendar.dnuVMesici(gc);i++){
                pom.add("");
            }
            sluzbyPodlePilotu.add(pom);
        }
        List<String> palubari = lsc.getPalubari();
        for(String l: palubari){
            List<String> pom = new ArrayList<>();
            pom.add(l);
            for(int i = 0; i < Kalendar.dnuVMesici(gc);i++){
                pom.add("");
            }
            sluzbyPodlePalubaru.add(pom);
        }
        gc.set(Calendar.DAY_OF_MONTH, 1);
        sluzbyPodleDni = nactiSluzby_interni(sluzbyPodlePilotu, sluzbyPodlePalubaru);
        //System.out.format("post konstrukt, podle dni: %d, podle lidi %d", sluzbyPodleDni.size(), sluzbyPodleLidi.size());
        populateColumns();
    }
    public void uberM(){
        gc.set(Calendar.DAY_OF_MONTH,1);
        gc.add(Calendar.MONTH, -1);
        populateColumns();
        nactiSluzbyNaMesic();
    }
    public void pridejM(){
        gc.set(Calendar.DAY_OF_MONTH,1);
        gc.add(Calendar.MONTH, 1);
        populateColumns();
        nactiSluzbyNaMesic();
    }
    private void populateColumns(){
        columns = new ArrayList<>();
        columns.add(new PrehledyBean.ColumnModelIII("","letajici"));
        for(int i = 1; i <= Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1);i++) {
            columns.add(new PrehledyBean.ColumnModelIII(String.format("%d", i), String.format("%d", i)));
        }
    }
    private List<List<String>> nactiSluzby_interni(List<List<String>> podlePilotu, List<List<String>> podlePalubaru) {
        List<List<String>> vratka = new ArrayList<>();
        GregorianCalendar gc2 = new GregorianCalendar();
        gc2.set(Calendar.DAY_OF_MONTH, 1);
        gc2.set(Calendar.MONTH, gc.get(Calendar.MONTH));
        gc2.add(Calendar.MONTH,1);
        gc2.add(Calendar.DAY_OF_MONTH, -1);
        Query q = em.createNativeQuery("SELECT * FROM sluzby WHERE datum BETWEEN ? AND ? ORDER BY datum");
        q.setParameter(1, gc, TemporalType.DATE);
        q.setParameter(2, gc2, TemporalType.DATE);
        //System.out.format("nacitam sluzby od %s do %s", new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()), new SimpleDateFormat("yy/MMMM/dd").format(gc2.getTime()));
        List<Object[]> qResult = q.getResultList();
        //System.out.format("vysledku %d", qResult.size());
        for(Object[] sl: qResult){
            List<String> pomVratka = new ArrayList<>();
            GregorianCalendar pomGC = new GregorianCalendar();
            pomGC.setTime((Date) sl[0]);
            int den = pomGC.get(Calendar.DAY_OF_MONTH);
            pomVratka.add(String.valueOf(den));
            for(int j = 1; j < sl.length; j++){
                String jmeno = (String) sl[j];
                pomVratka.add(jmeno);
                for(List<String> pl: podlePilotu){
                    if(jmeno.equals(pl.get(0))){
                        switch(j){
                            case 1:
                            case 2:
                            case 3:
                                pl.set(den, "L");
                                break;
                            case 4:
                            case 5:
                            case 6:
                                pl.set(den, "S");
                                break;
                            case 7:
                            case 8:
                            case 9:
                                pl.set(den, "H");
                                break;
                        }
                    }
                }
                for(List<String> pl: podlePalubaru){
                    if(jmeno.equals(pl.get(0))){
                        switch(j){
                            case 1:
                            case 2:
                            case 3:
                                pl.set(den, "L");
                                break;
                            case 4:
                            case 5:
                            case 6:
                                pl.set(den, "S");
                                break;
                            case 7:
                            case 8:
                            case 9:
                                pl.set(den, "H");
                                break;
                        }
                    }
                }
            }
            vratka.add(pomVratka);
        }
        return vratka;
    }
    public String proMesic(){
        Map m = new HashMap();
        m.put(0, "Leden");
        m.put(1, "Únor");
        m.put(2, "Březen");
        m.put(3, "Duben");
        m.put(4, "Květen");
        m.put(5, "Červen");
        m.put(6, "Červenec");
        m.put(7, "Srpen");
        m.put(8, "Září");
        m.put(9, "Říjen");
        m.put(10, "Listopad");
        m.put(11, "Prosinec");
        return (String)m.get(gc.get(Calendar.MONTH))+" "+new SimpleDateFormat("yyyy").format(gc.getTime());
    }
    public String cellColor(int den){
        String vratka = "#ffffff";
        if (den == 0) return vratka;
        //System.out.println("vstup  "+new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        //System.out.println(den);
        gc.set(Calendar.DAY_OF_MONTH, den);
        //System.out.println(new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        if(Kalendar.jeSvatek(gc)) vratka = "#D20005";
        if((gc.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY)||(gc.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)) vratka="#ffd700";
        gc.set(Calendar.DAY_OF_MONTH, 1);
        //System.out.println("vystup  "+new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        return vratka;
    }

    public List<List<String>> getSluzbyPodleDni() {
        return sluzbyPodleDni;
    }

    public List<List<String>> getSluzbyPodlePilotu() {
        return sluzbyPodlePilotu;
    }

    public List<List<String>> getSluzbyPodlePalubaru() {
        return sluzbyPodlePalubaru;
    }

    

    public List<ColumnModelIII> getColumns() {
        return columns;
    }
    
    static public class ColumnModelIII implements Serializable {
        private String header;
        private String property;
        public ColumnModelIII(String header, String property) {
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
}
