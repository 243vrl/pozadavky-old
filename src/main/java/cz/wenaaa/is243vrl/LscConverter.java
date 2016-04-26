/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl;

import cz.wenaaa.is243vrl.entityClasses.LetajiciSluzby2;
import cz.wenaaa.is243vrl.entityClasses.jsf.LetajiciSluzbyController;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author vena
 */
@FacesConverter("lscConverter")
    public class LscConverter implements Converter {

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
            if (object instanceof LetajiciSluzby2) {
                LetajiciSluzby2 o = (LetajiciSluzby2) object;
                return getStringKey(o.getLetajici());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), LetajiciSluzby2.class.getName()});
                return null;
            }
        }

    }
