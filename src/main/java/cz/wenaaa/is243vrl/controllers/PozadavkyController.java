package cz.wenaaa.is243vrl.controllers;

import cz.wenaaa.is243vrl.entityClasses.Pozadavky;
import cz.wenaaa.is243vrl.entityClasses.jsf.util.JsfUtil;
import cz.wenaaa.is243vrl.entityClasses.jsf.util.JsfUtil.PersistAction;
import cz.wenaaa.is243vrl.ejbs.PozadavkyFacade;
import cz.wenaaa.utils.Kalendar;

import java.io.Serializable;
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


@Named("pozadavkyController")
@SessionScoped
public class PozadavkyController implements Serializable {


    @EJB private cz.wenaaa.is243vrl.ejbs.PozadavkyFacade ejbFacade;
    private List<Pozadavky> items = null;
    private Pozadavky selected;
    
    

    public PozadavkyController() {
    }

    public Pozadavky getSelected() {
        return selected;
    }

    public void setSelected(Pozadavky selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private PozadavkyFacade getFacade() {
        return ejbFacade;
    }

    public Pozadavky prepareCreate() {
        selected = new Pozadavky();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("PozadavkyCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("PozadavkyUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("PozadavkyDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Pozadavky> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }
    
    public int dnuVmesici(int rok, int mesic){
        return Kalendar.dnuVMesici(rok, mesic);
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

    public Pozadavky getPozadavky(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Pozadavky> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Pozadavky> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass=Pozadavky.class)
    public static class PozadavkyControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PozadavkyController controller = (PozadavkyController)facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "pozadavkyController");
            return controller.getPozadavky(getKey(value));
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
            if (object instanceof Pozadavky) {
                Pozadavky o = (Pozadavky) object;
                return getStringKey(o.getIdPozadavky());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Pozadavky.class.getName()});
                return null;
            }
        }

    }

}
