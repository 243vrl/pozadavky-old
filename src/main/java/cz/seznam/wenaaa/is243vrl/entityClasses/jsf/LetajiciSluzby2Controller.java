/*package cz.seznam.wenaaa.is243vrl.entityClasses.jsf;

import cz.seznam.wenaaa.is243vrl.entityClasses.LetajiciSluzby2;
import cz.seznam.wenaaa.is243vrl.entityClasses.jsf.util.JsfUtil;
import cz.seznam.wenaaa.is243vrl.entityClasses.jsf.util.PaginationHelper;
import cz.seznam.wenaaa.is243vrl.beans.entityClasses.LetajiciSluzby2Facade;

import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("letajiciSluzby2Controller")
@SessionScoped
public class LetajiciSluzby2Controller implements Serializable {

    private LetajiciSluzby2 current;
    private DataModel items = null;
    @EJB
    private cz.seznam.wenaaa.is243vrl.beans.entityClasses.LetajiciSluzby2Facade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public LetajiciSluzby2Controller() {
    }

    public LetajiciSluzby2 getSelected() {
        if (current == null) {
            current = new LetajiciSluzby2();
            selectedItemIndex = -1;
        }
        return current;
    }

    private LetajiciSluzby2Facade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (LetajiciSluzby2) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new LetajiciSluzby2();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundleknicemu").getString("LetajiciSluzby2Created"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundleknicemu").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (LetajiciSluzby2) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundleknicemu").getString("LetajiciSluzby2Updated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundleknicemu").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (LetajiciSluzby2) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundleknicemu").getString("LetajiciSluzby2Deleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundleknicemu").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public LetajiciSluzby2 getLetajiciSluzby2(java.lang.String id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = LetajiciSluzby2.class)
    public static class LetajiciSluzby2ControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LetajiciSluzby2Controller controller = (LetajiciSluzby2Controller) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "letajiciSluzby2Controller");
            return controller.getLetajiciSluzby2(getKey(value));
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
            if (object instanceof LetajiciSluzby2) {
                LetajiciSluzby2 o = (LetajiciSluzby2) object;
                return getStringKey(o.getLetajici());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + LetajiciSluzby2.class.getName());
            }
        }

    }

}*/
