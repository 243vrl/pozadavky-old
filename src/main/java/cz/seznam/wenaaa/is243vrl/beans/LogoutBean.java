/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.beans;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author vena
 */
@Named(value = "logoutBean")
@RequestScoped
public class LogoutBean {
    
    private static Logger log = Logger.getLogger(LogoutBean.class.getName());

    public String logout(){
        String destination = "/common/index?faces-redirect=true";
        
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = 
                (HttpServletRequest) context.getExternalContext().getRequest();
        try{
            request.logout();
            context.getExternalContext().invalidateSession();
        }catch(ServletException e){
            log.log(Level.SEVERE,"Failed to logout user",e);
            destination = "/loginerror?faces-redirect=true";
        }
        return destination; //goto destination
    }
    /**
     * Creates a new instance of LogoutBean
     */
    public LogoutBean() {
    }
    
}
