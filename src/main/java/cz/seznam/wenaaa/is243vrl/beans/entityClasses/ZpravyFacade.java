/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.beans.entityClasses;

import cz.seznam.wenaaa.is243vrl.entityClasses.Zpravy;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author vena
 */
@Stateless
public class ZpravyFacade extends AbstractFacade<Zpravy> {
    @PersistenceContext(unitName = "pozadavky_PU")
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    public ZpravyFacade() {
        super(Zpravy.class);
    }
    
    
}
