/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.beans.entityClassesBeans;

import cz.seznam.wenaaa.is243vrl.entityClasses.LetajiciSluzby;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author vena
 */
@Stateless
public class LetajiciSluzbyFacade extends AbstractFacade<LetajiciSluzby> {
    @PersistenceContext(unitName = "pozadavky_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LetajiciSluzbyFacade() {
        super(LetajiciSluzby.class);
    }
    
}
