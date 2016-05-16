/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.planovani;

import cz.wenaaa.is243vrl.beans.entityClasses.PovoleniSluzebFacade;
import cz.wenaaa.is243vrl.beans.entityClasses.PozadavkyFacade;
import cz.wenaaa.is243vrl.entityClasses.PovoleniSluzeb;
import cz.wenaaa.is243vrl.entityClasses.Pozadavky;
import cz.wenaaa.is243vrl.entityClasses.Sluzby;
import cz.wenaaa.utils.Kalendar;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author vena
 */
public class Slouzici {

    public static final int PLANOVAT_SLUZEB = 6;
    private final String jmeno;
    private long plneVolneDny;//pro binarni operace plny 1, volny 0
    private int planujSluzeb;
    private final String dojizdeni;
    private List<String> typySluzeb;
    private float prPa;
    private float prSo;
    private float prNe;
    private float prSv;
    private int sluzeb;
    private int paSoNe;
    private int sv;

    private PovoleniSluzebFacade psf;
    private PozadavkyFacade pf;
    private final boolean doLiniSvozem;
    
    

    public Slouzici(String jmeno, String dojizdeni, boolean doLiniSvozem, float prumerPa, float prumerSo, float prumerNe, float prumerSv) {
        this.jmeno = jmeno;
        this.dojizdeni = dojizdeni;
        this.doLiniSvozem = doLiniSvozem;
        this.prPa = prumerPa;
        this.prSo = prumerSo;
        this.prNe = prumerNe;
        this.prSv = prumerSv;
        this.paSoNe = 0;
        this.sluzeb = 0;
        typySluzeb = new ArrayList<>();
        try {
            psf = (PovoleniSluzebFacade) InitialContext.doLookup("java:global/pozadavky/PovoleniSluzebFacade");
            pf = (PozadavkyFacade) InitialContext.doLookup("java:global/pozadavky/PozadavkyFacade");
        } catch (NamingException ex) {
            Logger.getLogger(Slouzici.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public String getJmeno() {
        return jmeno;
    }

    public long getPlneVolneDny() {
        return plneVolneDny;
    }

    public float getPlanujSluzeb() {
        return planujSluzeb;
    }

    public boolean isDoLiniSvozem() {
        return doLiniSvozem;
    }

    public String getDojizdeni() {
        return dojizdeni;
    }

    public void nastavNaMesic(GregorianCalendar gc) {
        setPlneVolneDny(gc);
        setPlanujSluzeb(gc);
    }

    float getPrPa() {
        return prPa;
    }

    float getPrSo() {
        return prSo;
    }

    float getPrNe() {
        return prNe;
    }

    float getPrSv() {
        return prSv;
    }

    
    public int getSluzeb() {
        return sluzeb;
    }

    public void setSluzeb(int sluzeb) {
        this.sluzeb = sluzeb;
    }

       
    void zvysSluzeb(){
        sluzeb++;
    }
    
    public int getPocetPlnychDnu() {
        long pom = this.plneVolneDny;
        int vratka = 0;
        while (pom != 0) {
            if ((pom & 1) == 1) {
                vratka++;
            }
            pom = pom >>> 1;
        }
        return vratka;
    }

    public List<String> getTypySluzeb() {
        return typySluzeb;
    }

    private void setPlneVolneDny(GregorianCalendar gc) {
        System.out.println("pozadavky pro "+jmeno);
        List<Pozadavky> pozadavkyNaMesic = pf.pozadavkyNaMesic(jmeno, gc);
        plneVolneDny = 0L;
        GregorianCalendar pomGC = new GregorianCalendar();
        for (Pozadavky pol : pozadavkyNaMesic) {
            pomGC.setTime((Date) pol.getDatum());
            int den = pomGC.get(Calendar.DAY_OF_MONTH);
            if (pol.getPozadavek().startsWith("X") || (den == 1)) {
                plneVolneDny += (long) Math.pow(2, den);
            } else {
                plneVolneDny += (long) Math.pow(2, den);
                if ((plneVolneDny & (long) Math.pow(2, den - 1)) == 0) {
                    plneVolneDny += (long) Math.pow(2, den - 1);
                }
            }
            System.out.println("\t> "+den+" / "+pol.getPozadavek());
        }
        System.out.println("plne volne dny > "+plneVolneDny);
    }

    int getFGVRozdil() {
        if(sluzeb > PLANOVAT_SLUZEB+1){
            return sluzeb;
        }
        double vratka = sluzeb - planujSluzeb;
        if (Math.abs(vratka) < 1) {
            return 0;
        }
        return (int) vratka;
    }

    private void setPlanujSluzeb(GregorianCalendar gc) {
        int dnu = Kalendar.dnuVMesici(gc);
        float pomerVolnych = (dnu - getPocetPlnychDnu()) / ((float) dnu);
        int pomI = 1;
        for (PovoleniSluzeb pol : psf.getByJmeno(jmeno)) {
            typySluzeb.add(pol.getTypSluzby().getId());
            if (pol.getTypSluzby().getId().startsWith("H")) {
                pomI = 0;
                break;
            }
        }
        planujSluzeb = (int) (PLANOVAT_SLUZEB * pomerVolnych);
        planujSluzeb += pomI;
    }

    boolean muze(SluzboDen dalsiSD, List<Slouzici> actualPath, List<SluzboDen> sluzbodny, List<Sluzby> minMesic) {
        //test na typ sluzby
        if (!getTypySluzeb().contains(dalsiSD.getTypsluzby().getsTypSluzby())) {
            return false;
        }
        long nemuze = getPlneVolneDny();
        long novaSluzba = (long) Math.pow(2, dalsiSD.datum.get(Calendar.DAY_OF_MONTH));
        if ((novaSluzba & nemuze) != 0) {
            //System.out.print("nemuze na pozadavek");
            return false;
        }// ze ma volny pozadavek
        long slouzi = kdySlouzi(actualPath, sluzbodny);
        long denPo = novaSluzba << 1;
        long denPred = novaSluzba >> 1;
        if ((novaSluzba & slouzi) != 0) {
            //System.out.print("nemuze na ten samy den");
            return false;
        }//ze neslouzi ten samy den
        /*
         if ((denPo & slouzi) != 0) {
         //System.out.print("nemuze na den po");
         return false;
         }//den po
         if ((denPred & slouzi) != 0) {
         //System.out.print("nemuze na den pred");
         return false;
         }//den pred
         */
        slouzi = slouzi | novaSluzba;
        slouzi = posunSlouziOMinulyMesic(slouzi, minMesic);
        do {
            if ((slouzi & 85) == 85) {
                //System.out.print("nemuze na obdenky");
                return false;
            }//85d = 1010101b
            if ((slouzi & 0b11) == 0b11) {
                return false;// na den po/pred
            }
            slouzi = slouzi >> 1;
        } while (slouzi != 0);
        //System.out.print("ok");
        return true;

    }

    private long posunSlouziOMinulyMesic(long slouzi, List<Sluzby> minulyMesic) {
        //pro konecne sluzby z minuleho mesice, jsou v sestupnem poradi
        //nastavim nulty bit na 1 pokud slouzi
        if (minulyMesic != null) {
            for (Sluzby sl : minulyMesic) {
                boolean jeVeSluzbe = false;
                try {
                    if (sl.getLd().getLetajici().equals(getJmeno())) {
                        jeVeSluzbe = true;
                    }
                } catch (NullPointerException e) {

                }
                try {
                    if (sl.getSd().getLetajici().equals(getJmeno())) {
                        jeVeSluzbe = true;
                    }
                } catch (NullPointerException e) {

                }
                try {
                    if (sl.getLk().getLetajici().equals(getJmeno())) {
                        jeVeSluzbe = true;
                    }
                } catch (NullPointerException e) {

                }
                try {
                    if (sl.getSk().getLetajici().equals(getJmeno())) {
                        jeVeSluzbe = true;
                    }
                } catch (NullPointerException e) {

                }
                try {
                    if (sl.getLp().getLetajici().equals(getJmeno())) {
                        jeVeSluzbe = true;
                    }
                } catch (NullPointerException e) {

                }
                try {
                    if (sl.getSp().getLetajici().equals(getJmeno())) {
                        jeVeSluzbe = true;
                    }
                } catch (NullPointerException e) {

                }
                if (jeVeSluzbe) {
                    //nastaveni nulteho bitu na jedna
                    slouzi |= 1;
                }
                slouzi <<= 1;
            }
        }
        return slouzi;
    }

    private long kdySlouzi(List<Slouzici> actualPath, List<SluzboDen> sluzbodny) {
        long slouzi = 0;
        if(actualPath == null){
            return slouzi;
        }
        for (int i = 0; i < actualPath.size(); i++) {
            if (this.equals(actualPath.get(i))) {
                slouzi += (long) Math.pow(2, sluzbodny.get(i).getDatum().get(Calendar.DAY_OF_MONTH));
            }
        }
        return slouzi;
    }

    @Override
    public String toString() {
        return "Slouzici{" + "jmeno=" + jmeno + ", plneVolneDny=" + plneVolneDny + ", planujSluzeb=" + planujSluzeb + ", typySluzeb=" + typySluzeb + ", prPa=" + prPa + ", prSo=" + prSo + ", prNe=" + prNe + ", prSv=" + prSv + ", sluzeb=" + sluzeb + ", paSoNe=" + paSoNe + ", sv=" + sv + ", doLiniSvozem=" + doLiniSvozem + '}';
    }

    
    
    
    public int getPaSoNe() {
        return paSoNe;
    }
    
    public void setPaSoNe(int pasone){
        this.paSoNe = pasone;
    }
       
    void zvysPaSoNe(){
        this.paSoNe++;
    }

    public int getSv() {
        return sv;
    }

    public void setSv(int sv) {
        this.sv = sv;
    }
    
    void zvysSv(){
        sv++;
    }
}
