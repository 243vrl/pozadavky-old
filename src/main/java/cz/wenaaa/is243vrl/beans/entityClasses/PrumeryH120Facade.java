/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.beans.entityClasses;

import cz.wenaaa.is243vrl.entityClasses.PrumeryH120;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author vena
 */
@Stateless
public class PrumeryH120Facade extends AbstractFacade<PrumeryH120> {
    @PersistenceContext(unitName = "pozadavky_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PrumeryH120Facade() {
        super(PrumeryH120.class);
    }
    
}
