package cz.wenaaa.is243vrl.controllers;

import cz.wenaaa.is243vrl.ejbs.LetajiciSluzby2Facade;
import cz.wenaaa.is243vrl.entityClasses.LetajiciSluzby2;
import cz.wenaaa.is243vrl.entityClasses.jsf.util.JsfUtil;
import cz.wenaaa.is243vrl.entityClasses.jsf.util.JsfUtil.PersistAction;
import cz.wenaaa.is243vrl.planovani.Slouzici;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Named("letajiciSluzbyController")
@SessionScoped
public class LetajiciSluzbyController implements Serializable  {

    @EJB
    private cz.wenaaa.is243vrl.ejbs.LetajiciSluzby2Facade ejbFacade;
    @PersistenceContext(unitName = "pozadavky_PU")
    private EntityManager em;
    private List<LetajiciSluzby2> items = null;
    private LetajiciSluzby2 selected;

    public LetajiciSluzbyController() {
    }

    public LetajiciSluzby2 getSelected() {
        return selected;
    }

    public void setSelected(LetajiciSluzby2 selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private LetajiciSluzby2Facade getFacade() {
        return ejbFacade;
    }

    public LetajiciSluzby2 prepareCreate() {
        selected = new LetajiciSluzby2();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("LetajiciSluzbyCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("LetajiciSluzbyUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("LetajiciSluzbyDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<LetajiciSluzby2> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }
    
    public List<String> getSlouzici(){
        List<String> vratka = new ArrayList<>();
        vratka.addAll(getLetajici());
        vratka.addAll(getPalubari());
        return vratka;
    }
    public List<LetajiciSluzby2> getPiloti(){
        Query q = em.createNamedQuery("LetajiciSluzby.findPiloti");
        return q.getResultList();
    }
    public List<LetajiciSluzby2> getPalubaci(){
        Query q = em.createNamedQuery("LetajiciSluzby.findPalubari");
        return q.getResultList();
    }
    public List<LetajiciSluzby2> getPilotiByName(){
        Query q = em.createNamedQuery("LetajiciSluzby.findPilotiByName");
        return q.getResultList();
    }
    public List<LetajiciSluzby2> getPalubaciByName(){
        Query q = em.createNamedQuery("LetajiciSluzby.findPalubariByName");
        return q.getResultList();
    }
    public List<String> getLetajici(){
        List<String> vratka = new ArrayList<>();
        Query q = em.createNativeQuery("SELECT letajici FROM letajici_sluzby2 WHERE poradi < 1000 ORDER BY poradi");
        for( Object pom : q.getResultList()){
            vratka.add((String)pom);
        }
        return vratka;
    }
    
    public List<String> getPalubari(){
        List<String> vratka = new ArrayList<>();
        Query q = em.createNativeQuery("SELECT letajici FROM letajici_sluzby2 WHERE poradi > 1000 AND poradi < 10000 ORDER BY poradi ");
        for( Object pom : q.getResultList()){
            vratka.add((String)pom);
        }
        return vratka;
    }
    public String getLetajici(int poradi){
        return getLetajici().get(poradi);
    }
    public String getPalubari(int poradi){
        return getPalubari().get(poradi);
    }
    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public LetajiciSluzby2 getLetajiciSluzby(java.lang.String id) {
        return getFacade().find(id);
    }

    public List<LetajiciSluzby2> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<LetajiciSluzby2> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    

}
