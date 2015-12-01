/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.beans;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author vena
 */
@Named(value = "loggedBean")
@RequestScoped
public class LoggedBean {
    private String logged;
    private String notlogged;
    private String loggedAsBFU;
    private String loggedAsHead;
    private String loggedAsScheduler;
    private String loggedAsAdmin;
    private String loggedAsStaff;
    private String loginName;
    private String loginError;
    private String authorizationError;
    
    /**
     * Creates a new instance of pokusBean
     */
    public LoggedBean() {
    }

    /**
     * @return the login
     */
    public String getLogged() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = 
                (HttpServletRequest) context.getExternalContext().getRequest();
        logged = request.getRemoteUser();
        return (logged == null) ? "false": "true";
    }
    
    
    /**
     * @return the notlogged
     */
    public String getNotlogged() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = 
                (HttpServletRequest) context.getExternalContext().getRequest();
        notlogged = request.getRemoteUser();
        return (notlogged == null) ? "true": "false";
    }

    /**
     * @return the loggedAsBFU
     */
    public String getLoggedAsBFU() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = 
                (HttpServletRequest) context.getExternalContext().getRequest();
        return request.isUserInRole("BFU") ? "true":"false";
    }

    /**
     * @return the loggedAsHead
     */
    public String getLoggedAsHead() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = 
                (HttpServletRequest) context.getExternalContext().getRequest();
        return request.isUserInRole("HEAD") ? "true":"false";
    }

    /**
     * @return the loggedAsScheduler
     */
    public String getLoggedAsScheduler() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = 
                (HttpServletRequest) context.getExternalContext().getRequest();
        return request.isUserInRole("SCHEDULER") ? "true":"false";
    }

    /**
     * @return the loggedAsAdmin
     */
    public String getLoggedAsAdmin() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = 
                (HttpServletRequest) context.getExternalContext().getRequest();
        return request.isUserInRole("ADMIN") ? "true":"false";
    }

    /**
     * @return the loggedAsStaff
     */
    public String getLoggedAsStaff() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = 
                (HttpServletRequest) context.getExternalContext().getRequest();
        return request.isUserInRole("STAFF") ? "true":"false";
    }

    /**
     * @return the loginName
     */
    public String getLoginName() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = 
                (HttpServletRequest) context.getExternalContext().getRequest();
        return request.getRemoteUser();
    }

    /**
     * @return the loginError
     */
    public String getLoginError() {
         FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = 
                (HttpServletRequest) context.getExternalContext().getRequest();
        return "le".equals(request.getParameter("er"))?"true":"false";
    }

    /**
     * @return the authorizationError
     */
    public String getAuthorizationError() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = 
                (HttpServletRequest) context.getExternalContext().getRequest();
        return "ae".equals(request.getParameter("er"))?"true":"false";
    }


}
