/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.JPAcontrollers;

import cz.wenaaa.is243vrl.JPAcontrollers.exceptions.NonexistentEntityException;
import cz.wenaaa.is243vrl.JPAcontrollers.exceptions.RollbackFailureException;
import cz.wenaaa.is243vrl.entityClasses.Pomtab;
import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

/**
 *
 * @author vena
 */
public class PomtabJpaController implements Serializable {

    @Inject
    private UserTransaction utx;
    @PersistenceContext(unitName = "pozadavky_PU")
    private EntityManager em;
    

    public EntityManager getEntityManager() {
        return em;
    }

    public void create(Pomtab pomtab) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            em.persist(pomtab);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pomtab pomtab) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            pomtab = em.merge(pomtab);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pomtab.getId();
                if (findPomtab(id) == null) {
                    throw new NonexistentEntityException("The pomtab with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pomtab pomtab;
            try {
                pomtab = em.getReference(Pomtab.class, id);
                pomtab.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pomtab with id " + id + " no longer exists.", enfe);
            }
            em.remove(pomtab);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pomtab> findPomtabEntities() {
        return findPomtabEntities(true, -1, -1);
    }

    public List<Pomtab> findPomtabEntities(int maxResults, int firstResult) {
        return findPomtabEntities(false, maxResults, firstResult);
    }

    private List<Pomtab> findPomtabEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pomtab.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Pomtab findPomtab(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pomtab.class, id);
        } finally {
            em.close();
        }
    }

    public int getPomtabCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pomtab> rt = cq.from(Pomtab.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    
}
