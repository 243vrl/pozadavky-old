package cz.seznam.wenaaa.is243vrl.entityClasses.jsf;

import cz.seznam.wenaaa.is243vrl.entityClasses.Pozadavky;
import cz.seznam.wenaaa.is243vrl.entityClasses.jsf.util.JsfUtil;
import cz.seznam.wenaaa.is243vrl.entityClasses.jsf.util.JsfUtil.PersistAction;
import cz.seznam.wenaaa.is243vrl.beans.entityClassesBeans.PozadavkyFacade;

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


    @EJB private cz.seznam.wenaaa.is243vrl.beans.entityClassesBeans.PozadavkyFacade ejbFacade;
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
            selected.getPozadavkyPK().setLetajici(selected.getLetajiciSluzby().getLetajici());
    }

    protected void initializeEmbeddableKey() {
        selected.setPozadavkyPK(new cz.seznam.wenaaa.is243vrl.entityClasses.PozadavkyPK());
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

    public Pozadavky getPozadavky(cz.seznam.wenaaa.is243vrl.entityClasses.PozadavkyPK id) {
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

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PozadavkyController controller = (PozadavkyController)facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "pozadavkyController");
            return controller.getPozadavky(getKey(value));
        }

        cz.seznam.wenaaa.is243vrl.entityClasses.PozadavkyPK getKey(String value) {
            cz.seznam.wenaaa.is243vrl.entityClasses.PozadavkyPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new cz.seznam.wenaaa.is243vrl.entityClasses.PozadavkyPK();
            key.setDatum(java.sql.Date.valueOf(values[0]));
            key.setLetajici(values[1]);
            return key;
        }

        String getStringKey(cz.seznam.wenaaa.is243vrl.entityClasses.PozadavkyPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getDatum());
            sb.append(SEPARATOR);
            sb.append(value.getLetajici());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Pozadavky) {
                Pozadavky o = (Pozadavky) object;
                return getStringKey(o.getPozadavkyPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Pozadavky.class.getName()});
                return null;
            }
        }

    }

}
