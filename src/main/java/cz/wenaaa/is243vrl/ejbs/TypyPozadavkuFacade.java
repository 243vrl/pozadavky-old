/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.ejbs;

import cz.wenaaa.is243vrl.entityClasses.TypyPozadavku;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author vena
 */
@Stateless
public class TypyPozadavkuFacade extends AbstractFacade<TypyPozadavku> {
    @PersistenceContext(unitName = "pozadavky_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TypyPozadavkuFacade() {
        super(TypyPozadavku.class);
    }
    
}
