package cz.wenaaa.is243vrl.controllers;

import cz.wenaaa.is243vrl.beans.LoggedBean;
import cz.wenaaa.is243vrl.entityClasses.Zpravy;
import cz.wenaaa.is243vrl.entityClasses.jsf.util.JsfUtil;
import cz.wenaaa.is243vrl.entityClasses.jsf.util.JsfUtil.PersistAction;
import cz.wenaaa.is243vrl.ejbs.ZpravyFacade;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TemporalType;

@Named("zpravyController")
@SessionScoped
public class ZpravyController implements Serializable {

    @EJB
    private cz.wenaaa.is243vrl.ejbs.ZpravyFacade ejbFacade;
    private List<Zpravy> items = null;
    private Zpravy selected;
    @Inject
    LoggedBean lb;

    public ZpravyController() {
    }

    public Zpravy getSelected() {
        return selected;
    }

    public void setSelected(Zpravy selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ZpravyFacade getFacade() {
        return ejbFacade;
    }

    public Zpravy prepareCreate() {
        selected = new Zpravy();
        initializeEmbeddableKey();
        selected.setPodal(lb.getLoginName());
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/ZpravyBundle").getString("ZpravyCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/ZpravyBundle").getString("ZpravyUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/ZpravyBundle").getString("ZpravyDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Zpravy> getItems() {
        if (items == null) {
            Query q = getFacade().getEntityManager().createNamedQuery("Zpravy.findAll");
            items = q.getResultList();
        }
        return items;
    }
    
    public List<Zpravy> getNaMesic(GregorianCalendar gc){
        List<Zpravy> vratka;
        GregorianCalendar pomgc = new GregorianCalendar();
        GregorianCalendar pomgc2 = new GregorianCalendar();
        pomgc.set(Calendar.DAY_OF_MONTH, 1);
        pomgc2.set(Calendar.DAY_OF_MONTH, 1);
        pomgc.set(Calendar.MONTH, gc.get(Calendar.MONTH));
        pomgc2.set(Calendar.MONTH, gc.get(Calendar.MONTH));
        pomgc2.add(Calendar.MONTH, 1);
        pomgc2.add(Calendar.DAY_OF_MONTH, -1);
        Query q = getFacade().getEntityManager().createNamedQuery("Zpravy.naMesic");
        q.setParameter("od", pomgc, TemporalType.DATE);
        q.setParameter("do", pomgc2, TemporalType.DATE);
        vratka = q.getResultList();
        return vratka;
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/ZpravyBundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/ZpravyBundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Zpravy getZpravy(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Zpravy> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Zpravy> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Zpravy.class)
    public static class ZpravyControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ZpravyController controller = (ZpravyController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "zpravyController");
            return controller.getZpravy(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Zpravy) {
                Zpravy o = (Zpravy) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Zpravy.class.getName()});
                return null;
            }
        }

    }

}
