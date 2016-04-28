/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.planovani;

import cz.wenaaa.is243vrl.beans.entityClasses.PovoleniSluzebFacade;
import cz.wenaaa.is243vrl.entityClasses.PovoleniSluzeb;
import cz.wenaaa.utils.Kalendar;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

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
    @PersistenceContext(unitName = "pozadavky_PU")
    private EntityManager em;
    @Inject
    private PovoleniSluzebFacade psf;

    public Slouzici(String jmeno, String dojizdeni) {
        this.jmeno = jmeno;
        this.dojizdeni = dojizdeni;
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

    public String getDojizdeni() {
        return dojizdeni;
    }

    public void nastavNaMesic(GregorianCalendar gc) {
        setPlneVolneDny(gc);
        setPlanujSluzeb(gc);
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

    private void setPlneVolneDny(GregorianCalendar gc) {
        GregorianCalendar gc1 = new GregorianCalendar();
        gc1.set(Calendar.DAY_OF_MONTH, 1);
        gc1.set(Calendar.MONTH, gc.get(Calendar.MONTH));
        gc1.set(Calendar.YEAR, gc.get(Calendar.YEAR));
        gc1.add(Calendar.MONTH, 1);
        gc1.add(Calendar.DAY_OF_MONTH, -1);
        Query q5 = em.createNativeQuery("SELECT pozadavek, datum FROM pozadavky WHERE datum BETWEEN ? AND ?  AND letajici=? AND pozadavek NOT IN (SELECT pozadavek FROM typy_pozadavku WHERE NOT useracces) ORDER BY datum");
        q5.setParameter(1, gc, TemporalType.DATE);
        q5.setParameter(2, gc1, TemporalType.DATE);
        q5.setParameter(3, jmeno);
        plneVolneDny = 0L;
        for (Object pom : q5.getResultList()) {
            Object[] pom1 = (Object[]) pom;
            String pom2 = (String) (pom1[0]);
            GregorianCalendar pomGC = new GregorianCalendar();
            pomGC.setTime((Date) pom1[1]);
            int den = pomGC.get(Calendar.DAY_OF_MONTH);
            if (pom2.startsWith("X") || (den == 1)) {
                plneVolneDny += (long) Math.pow(2, den);
            } else {
                plneVolneDny += (long) Math.pow(2, den);
                if ((plneVolneDny & (long) Math.pow(2, den - 1)) == 0) {
                    plneVolneDny += (long) Math.pow(2, den - 1);
                }
            }
        }
    }

    private void setPlanujSluzeb(GregorianCalendar gc) {
        int dnu = Kalendar.dnuVMesici(gc);
        float pomerVolnych = (dnu-getPocetPlnychDnu())/((float)dnu);
        int pomI = 1;
        for(PovoleniSluzeb pol:psf.getByName(jmeno)){
            if(pol.getTypSluzby().startsWith("H")){
                pomI = 0;
                break;
            }
        }
        planujSluzeb = (int) (PLANOVAT_SLUZEB * pomerVolnych);
        planujSluzeb += 1;
    }
}
