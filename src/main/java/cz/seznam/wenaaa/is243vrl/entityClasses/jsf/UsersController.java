package cz.seznam.wenaaa.is243vrl.entityClasses.jsf;

import cz.seznam.wenaaa.is243vrl.entityClasses.Users;
import cz.seznam.wenaaa.is243vrl.entityClasses.jsf.util.JsfUtil;
import cz.seznam.wenaaa.is243vrl.entityClasses.jsf.util.JsfUtil.PersistAction;
import cz.seznam.wenaaa.is243vrl.beans.entityClasses.UsersFacade;
import cz.seznam.wenaaa.is243vrl.entityClasses.ModelListenerFactory;
import cz.seznam.wenaaa.is243vrl.entityClasses.MyValueChangeEvent;
import cz.seznam.wenaaa.is243vrl.entityClasses.MyValueChangeListener;
import cz.seznam.wenaaa.is243vrl.entityClasses.UsersValueChangeEvent;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

@Named("usersController")
@SessionScoped
public class UsersController implements Serializable, MyValueChangeListener {

    @EJB
    private cz.seznam.wenaaa.is243vrl.beans.entityClasses.UsersFacade ejbFacade;
    private List<Users> items = null;
    private Users selected;
    private boolean jeVeZmene = false;

    public UsersController() {
    }
    @PostConstruct
    private void uvodniNacteni(){
        ModelListenerFactory.registerListener(this, Users.class.getName());
        jeVeZmene = false;
    }
    public Users getSelected() {
        return selected;
    }

    public void setSelected(Users selected) {
        jeVeZmene = false;
        this.selected = selected;
    }
    
    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private UsersFacade getFacade() {
        return ejbFacade;
    }

    public Users prepareCreate() {
        selected = new Users();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/BundleAdministraceUzivatele").getString("UsersCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/BundleAdministraceUzivatele").getString("UsersUpdated"));
        
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/BundleAdministraceUzivatele").getString("UsersDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Users> getItems() {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/BundleAdministraceUzivatele").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/BundleAdministraceUzivatele").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Users getUsers(java.lang.String id) {
        return getFacade().find(id);
    }

    public List<Users> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Users> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }
    
    public void zmenaJmena(ActionEvent ev){
        jeVeZmene = true;
    }
    public void resetHesla(ActionEvent ev){
        jeVeZmene = true;
        selected.setPasswd("heslo");
        
    }
    @Override
    public void onValueChanged(MyValueChangeEvent mvche) {
        UsersValueChangeEvent uvche = ( UsersValueChangeEvent) mvche;
        if(jeVeZmene && uvche.getUser().equals(selected)){
            update();
            jeVeZmene = false;
        }
    }

    @FacesConverter(forClass = Users.class)
    public static class UsersControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UsersController controller = (UsersController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "usersController");
            return controller.getUsers(getKey(value));
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
            if (object instanceof Users) {
                Users o = (Users) object;
                return getStringKey(o.getUsername());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Users.class.getName()});
                return null;
            }
        }

    }

}
