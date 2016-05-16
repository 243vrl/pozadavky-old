/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.beans.entityClasses;

import cz.wenaaa.is243vrl.entityClasses.Sluzby;
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
}
