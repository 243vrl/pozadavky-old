/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.ejbs;

import cz.wenaaa.is243vrl.TypySluzby;
import cz.wenaaa.is243vrl.beans.PlanovaniBean;
import cz.wenaaa.is243vrl.entityClasses.Sluzby;
import cz.wenaaa.utils.Kalendar;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

/**
 *
 * @author vena
 */
@Stateless
public class SluzbyFacade extends AbstractFacade<Sluzby> {
    @PersistenceContext(unitName = "pozadavky_PU")
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public SluzbyFacade() {
        super(Sluzby.class);
    }
    
    public List<Sluzby> nactiMinulyMesic(GregorianCalendar gc) {
        gc.set(Calendar.DAY_OF_MONTH, 1);
        Query q = getEntityManager().createNamedQuery("Sluzby.konecMesice");
        q.setMaxResults(6);
        q.setParameter("datum", gc, TemporalType.DATE);
        return q.getResultList();
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void ulozSluzbu(GregorianCalendar gc, TypySluzby typSluzby, String letajici) {
        String staryLetajici;
        int[] hodnotySluzby;
        int[] hodnotyStary = new int[5];
        int[] hodnotyNovy = new int[5];
        Object[] result;
        //EntityTransaction ut = em.getTransaction();
        try {
            //ut.begin();
            Query q1 = em.createNativeQuery("INSERT INTO sluzby (datum) SELECT ? WHERE NOT EXISTS (SELECT 1 FROM sluzby WHERE datum=?)");
            q1.setParameter(1, gc, TemporalType.DATE);
            q1.setParameter(2, gc, TemporalType.DATE);
            q1.executeUpdate();

            //ut.commit();
        } catch (Exception ex) {
            Logger.getLogger(PlanovaniBean.class.getName()).log(Level.SEVERE, null, ex);
            //if (ut.isActive()) {
            //    ut.rollback();
            //}
        }
        //ut = em.getTransaction();
        try {
            //ut.begin();
            try {
                Query qStary = em.createNativeQuery("SELECT " + typSluzby.getsTypSluzby().toLowerCase() + " FROM sluzby WHERE datum = ?");
                qStary.setParameter(1, gc, TemporalType.DATE);
                staryLetajici = (String) qStary.getSingleResult();
            } catch (NoResultException e) {
                staryLetajici = null;
            }
            if (letajici.equals(staryLetajici)) {
                //ut.commit();
                //System.out.print("rovnaj se");
                return;
            }
            hodnotySluzby = dejHodnotySluzby(gc);
            String tabulka = "letajici_sluzby2";
            String prip = "";
            if (typSluzby.getsTypSluzby().toLowerCase().startsWith("h")) {
                prip = "_h120";
            }
            Query qHodnoty = em.createNativeQuery("SELECT pocet_sluzeb" + prip + ", pocet_patku" + prip + ", pocet_sobot" + prip + ", pocet_nedeli" + prip + ", pocet_vsednich_svatku" + prip + " FROM " + tabulka + " WHERE letajici = ?");
            if (staryLetajici != null) {
                qHodnoty.setParameter(1, staryLetajici);
                result = (Object[]) qHodnoty.getSingleResult();
                for (int i = 0; i < hodnotySluzby.length; i++) {
                    hodnotyStary[i] = (int) result[i] - hodnotySluzby[i];
                }
            }
            qHodnoty.setParameter(1, letajici);
            result = (Object[]) qHodnoty.getSingleResult();
            for (int i = 0; i < hodnotySluzby.length; i++) {
                hodnotyNovy[i] = (int) result[i] + hodnotySluzby[i];
            }
            Query qUpdate = em.createNativeQuery("UPDATE " + tabulka + " SET pocet_sluzeb" + prip + " = ?, pocet_patku" + prip + " = ?, pocet_sobot" + prip + " = ?, pocet_nedeli" + prip + " = ?, pocet_vsednich_svatku" + prip + " = ? WHERE letajici  = ?");
            if (staryLetajici != null) {
                for (int i = 0; i < hodnotyStary.length; i++) {
                    qUpdate.setParameter(i + 1, hodnotyStary[i]);
                }
                qUpdate.setParameter(6, staryLetajici);
                qUpdate.executeUpdate();
            }
            for (int i = 0; i < hodnotyNovy.length; i++) {
                qUpdate.setParameter(i + 1, hodnotyNovy[i]);
            }
            qUpdate.setParameter(6, letajici);
            qUpdate.executeUpdate();
            Query qUpdate2 = em.createNativeQuery("UPDATE sluzby SET " + typSluzby.getsTypSluzby().toLowerCase() + " = ? WHERE datum = ?");
            qUpdate2.setParameter(1, letajici);
            qUpdate2.setParameter(2, gc, TemporalType.DATE);
            qUpdate2.executeUpdate();
            //ut.commit();
        } catch (Exception ex) {
            Logger.getLogger(PlanovaniBean.class.getName()).log(Level.SEVERE, null, ex);
            //if (ut.isActive()) {
            //    ut.rollback();
            //}
        }
    }
    private int[] dejHodnotySluzby(GregorianCalendar gc) {
        int[] vratka = {1, 0, 0, 0, 0};
        boolean[] svatky = new boolean[2];
        svatky[0] = Kalendar.jeSvatek(gc);
        gc.add(Calendar.DAY_OF_MONTH, 1);
        svatky[1] = Kalendar.jeSvatek(gc);
        gc.add(Calendar.DAY_OF_MONTH, -1);
        switch (gc.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SATURDAY:
                vratka[2]++;
                break;
            case Calendar.SUNDAY:
                vratka[3]++;
                if (svatky[1]) {
                    vratka[4]++;
                }
                break;
            case Calendar.FRIDAY:
                if (svatky[0]) {
                    vratka[4]++;
                } else {
                    vratka[1]++;
                }
                break;
            default:
                if (svatky[0]) {
                    vratka[4]++;
                }
                if (svatky[1]) {
                    vratka[4]++;
                }
                break;
        }
        return vratka;
    }
    public void ulozSluzbyZeSouboru() {
        FileReader fr = null;
        try {
            fr = new FileReader("C:\\Users\\vena\\Downloads\\prehled0715-0316.txt");
            BufferedReader br = new BufferedReader(fr);
            String radka;
            List<String> ts = new ArrayList<>();
            ts.add("lk");
            ts.add("ld");
            ts.add("lp");
            ts.add("sk");
            ts.add("sd");
            ts.add("sp");
            ts.add("hk");
            ts.add("hd");
            ts.add("hp");
            while ((radka = br.readLine()) != null) {
                if (radka.equals("")) {
                    break;
                }
                String[] tokens = radka.split(";");
                System.out.format("%s", radka);
                String[] t2 = tokens[0].split("\\.");
                GregorianCalendar gc = new GregorianCalendar(Integer.valueOf(t2[2]), Integer.valueOf(t2[1]) - 1, Integer.valueOf(t2[0]));
                System.out.format("gc je: %s", new SimpleDateFormat("DD.MM.YYYY"));
                for (String sluzba : ts) {
                    if (!"".equals(tokens[ts.indexOf(sluzba) + 1])) {
                        if (tokens[ts.indexOf(sluzba) + 1].equals("MAR")) {
                            tokens[ts.indexOf(sluzba) + 1] = "MAV";
                        }
                        ulozSluzbu(gc, TypySluzby.valueOf(sluzba), tokens[ts.indexOf(sluzba) + 1]);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PlanovaniBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PlanovaniBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException ex) {
                    Logger.getLogger(PlanovaniBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
