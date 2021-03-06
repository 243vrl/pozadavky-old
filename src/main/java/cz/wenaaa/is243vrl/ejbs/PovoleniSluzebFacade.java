/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.ejbs;

import cz.wenaaa.is243vrl.entityClasses.LetajiciSluzby2;
import cz.wenaaa.is243vrl.entityClasses.PovoleniSluzeb;
import cz.wenaaa.is243vrl.entityClasses.TypySluzeb;
import java.util.ArrayList;
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
public class PovoleniSluzebFacade extends AbstractFacade<PovoleniSluzeb> {

    @PersistenceContext(unitName = "pozadavky_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PovoleniSluzebFacade() {
        super(PovoleniSluzeb.class);
    }

    public List<PovoleniSluzeb> getByJmeno(String name) {
        /*
         Query q = em.createNativeQuery("PovoleniSluzeb.findByJmeno");
         q.setParameter("povoleno", true);
         q.setParameter("jmeno", name);
         return q.getResultList();
        */
        
        List<PovoleniSluzeb> answ = new ArrayList<>();
        Query q = em.createNativeQuery("SELECT * FROM povoleni_sluzeb WHERE letajici = ? AND povoleno = ?");
        q.setParameter(1, name);
        q.setParameter(2, true);
        for(Object pol:q.getResultList()){
            Object[] pom = (Object[])pol;
            PovoleniSluzeb ps = new PovoleniSluzeb();
            ps.setTypSluzby(new TypySluzeb((String) pom[2]));
            answ.add(ps);
        }
        
        return answ;
    }
}
