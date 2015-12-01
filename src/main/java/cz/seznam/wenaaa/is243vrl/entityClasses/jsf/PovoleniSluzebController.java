package cz.seznam.wenaaa.is243vrl.entityClasses.jsf;

import cz.seznam.wenaaa.is243vrl.entityClasses.PovoleniSluzeb;
import cz.seznam.wenaaa.is243vrl.entityClasses.jsf.util.JsfUtil;
import cz.seznam.wenaaa.is243vrl.entityClasses.jsf.util.JsfUtil.PersistAction;
import cz.seznam.wenaaa.is243vrl.beans.entityClassesBeans.PovoleniSluzebFacade;

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


@Named("povoleniSluzebController")
@SessionScoped
public class PovoleniSluzebController implements Serializable {


    @EJB private cz.seznam.wenaaa.is243vrl.beans.entityClassesBeans.PovoleniSluzebFacade ejbFacade;
    private List<PovoleniSluzeb> items = null;
    private PovoleniSluzeb selected;

    public PovoleniSluzebController() {
    }

    public PovoleniSluzeb getSelected() {
        return selected;
    }

    public void setSelected(PovoleniSluzeb selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
            selected.getPovoleniSluzebPK().setTypSluzby(selected.getTypySluzeb().getId());
            selected.getPovoleniSluzebPK().setLetajici(selected.getLetajiciSluzby().getLetajici());
            selected.getPovoleniSluzebPK().setTypLetadla(selected.getTypyLetadel().getId());
    }

    protected void initializeEmbeddableKey() {
        selected.setPovoleniSluzebPK(new cz.seznam.wenaaa.is243vrl.entityClasses.PovoleniSluzebPK());
    }

    private PovoleniSluzebFacade getFacade() {
        return ejbFacade;
    }

    public PovoleniSluzeb prepareCreate() {
        selected = new PovoleniSluzeb();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("PovoleniSluzebCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("PovoleniSluzebUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("PovoleniSluzebDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<PovoleniSluzeb> getItems() {
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

    public PovoleniSluzeb getPovoleniSluzeb(cz.seznam.wenaaa.is243vrl.entityClasses.PovoleniSluzebPK id) {
        return getFacade().find(id);
    }

    public List<PovoleniSluzeb> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<PovoleniSluzeb> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass=PovoleniSluzeb.class)
    public static class PovoleniSluzebControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PovoleniSluzebController controller = (PovoleniSluzebController)facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "povoleniSluzebController");
            return controller.getPovoleniSluzeb(getKey(value));
        }

        cz.seznam.wenaaa.is243vrl.entityClasses.PovoleniSluzebPK getKey(String value) {
            cz.seznam.wenaaa.is243vrl.entityClasses.PovoleniSluzebPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new cz.seznam.wenaaa.is243vrl.entityClasses.PovoleniSluzebPK();
            key.setLetajici(values[0]);
            key.setTypLetadla(values[1]);
            key.setTypSluzby(values[2]);
            return key;
        }

        String getStringKey(cz.seznam.wenaaa.is243vrl.entityClasses.PovoleniSluzebPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getLetajici());
            sb.append(SEPARATOR);
            sb.append(value.getTypLetadla());
            sb.append(SEPARATOR);
            sb.append(value.getTypSluzby());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof PovoleniSluzeb) {
                PovoleniSluzeb o = (PovoleniSluzeb) object;
                return getStringKey(o.getPovoleniSluzebPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), PovoleniSluzeb.class.getName()});
                return null;
            }
        }

    }

}
