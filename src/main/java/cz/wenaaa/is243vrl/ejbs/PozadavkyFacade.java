/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.ejbs;

import cz.wenaaa.is243vrl.entityClasses.Pozadavky;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

/**
 *
 * @author vena
 */
@Stateless
public class PozadavkyFacade extends AbstractFacade<Pozadavky> {
    @PersistenceContext(unitName = "pozadavky_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PozadavkyFacade() {
        super(Pozadavky.class);
    }
    
    public List<Pozadavky> pozadavkyNaMesic(String letajici, GregorianCalendar gc){
        Query q = getEntityManager().createNamedQuery("Pozadavky.naMesicProLetajiciho");
        q.setParameter("letajici", letajici);
        q.setParameter("ua", true);
        gc.set(Calendar.DAY_OF_MONTH, 1);
        GregorianCalendar pomgc = (GregorianCalendar)gc.clone();
        pomgc.add(Calendar.MONTH, 1);
        pomgc.add(Calendar.DAY_OF_MONTH, -1);
        q.setParameter("od", gc, TemporalType.DATE);
        q.setParameter("do", pomgc, TemporalType.DATE);
        return q.getResultList();
    }
    
    public List<Pozadavky> pozadavkyNaDen(GregorianCalendar gc){
        Query q = getEntityManager().createNamedQuery("Pozadavky.findByDatum");
        q.setParameter("datum", gc, TemporalType.DATE);
        return q.getResultList();
    }
    
    public List<Pozadavky> findByDenTyp(GregorianCalendar gc, String pozadavek){
        Query q = getEntityManager().createNamedQuery("Pozadavky.findByDatumAndPozadavek");
        q.setParameter("datum", gc, TemporalType.DATE);
        q.setParameter("pozadavek", pozadavek);
        return q.getResultList();
    }
}
