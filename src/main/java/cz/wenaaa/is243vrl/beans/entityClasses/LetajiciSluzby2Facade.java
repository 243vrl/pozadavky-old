/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.beans.entityClasses;

import cz.wenaaa.is243vrl.entityClasses.LetajiciSluzby2;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author vena
 */
@Stateless
public class LetajiciSluzby2Facade extends AbstractFacade<LetajiciSluzby2> {
    @PersistenceContext(unitName = "pozadavky_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LetajiciSluzby2Facade() {
        super(LetajiciSluzby2.class);
    }
    
    public List<LetajiciSluzby2> getPiloti(){
        Query q = em.createNamedQuery("LetajiciSluzby.findPiloti");
        return q.getResultList();
    }
    public List<LetajiciSluzby2> getPalubaci(){
        Query q = em.createNamedQuery("LetajiciSluzby.findPalubari");
        return q.getResultList();
    }
}
