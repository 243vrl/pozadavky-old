/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.beans.entityClasses;

import cz.seznam.wenaaa.is243vrl.entityClasses.Sluzby;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    
}
