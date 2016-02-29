package cz.seznam.wenaaa.is243vrl.entityClasses.jsf;

import cz.seznam.wenaaa.is243vrl.entityClasses.LetajiciSluzby;
import cz.seznam.wenaaa.is243vrl.entityClasses.jsf.util.JsfUtil;
import cz.seznam.wenaaa.is243vrl.entityClasses.jsf.util.JsfUtil.PersistAction;
import cz.seznam.wenaaa.is243vrl.beans.entityClasses.LetajiciSluzbyFacade;

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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Named("letajiciSluzbyController")
@SessionScoped
public class LetajiciSluzbyController implements Serializable {

    @EJB
    private cz.seznam.wenaaa.is243vrl.beans.entityClasses.LetajiciSluzbyFacade ejbFacade;
    @PersistenceContext(unitName = "pozadavky_PU")
    private EntityManager em;
    private List<LetajiciSluzby> items = null;
    private LetajiciSluzby selected;

    public LetajiciSluzbyController() {
    }

    public LetajiciSluzby getSelected() {
        return selected;
    }

    public void setSelected(LetajiciSluzby selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private LetajiciSluzbyFacade getFacade() {
        return ejbFacade;
    }

    public LetajiciSluzby prepareCreate() {
        selected = new LetajiciSluzby();
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

    public List<LetajiciSluzby> getItems() {
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
    public List<String> getLetajici(){
        List<String> vratka = new ArrayList<>();
        Query q = em.createNativeQuery("SELECT letajici FROM letajici_sluzby WHERE poradi < 1000 ORDER BY poradi");
        for( Object pom : q.getResultList()){
            vratka.add((String)pom);
        }
        return vratka;
    }
    
    public List<String> getPalubari(){
        List<String> vratka = new ArrayList<>();
        Query q = em.createNativeQuery("SELECT letajici FROM letajici_sluzby WHERE poradi > 1000 AND poradi < 10000 ORDER BY poradi ");
        for( Object pom : q.getResultList()){
            vratka.add((String)pom);
        }
        return vratka;
    }
    public String getLetajici(int poradi){
        return getLetajici().get(poradi);
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

    public LetajiciSluzby getLetajiciSluzby(java.lang.String id) {
        return getFacade().find(id);
    }

    public List<LetajiciSluzby> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<LetajiciSluzby> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = LetajiciSluzby.class)
    public static class LetajiciSluzbyControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LetajiciSluzbyController controller = (LetajiciSluzbyController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "letajiciSluzbyController");
            return controller.getLetajiciSluzby(getKey(value));
        }

        java.lang.String getKey(String value) {
            java.lang.String key;
            key = value;
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof LetajiciSluzby) {
                LetajiciSluzby o = (LetajiciSluzby) object;
                return getStringKey(o.getLetajici());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), LetajiciSluzby.class.getName()});
                return null;
            }
        }

    }

}
