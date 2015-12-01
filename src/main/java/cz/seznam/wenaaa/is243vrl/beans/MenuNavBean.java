/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.beans;

import javax.inject.Named;
import javax.enterprise.context.Dependent;

/**
 *
 * @author vena
 */
@Named(value = "menuNavBean")
@Dependent
public class MenuNavBean {
    
    private String nadpis;
    private String navCenter = "/users/newjsf.xhtml";

    public String getNavCenter() {
        return navCenter;
    }

    public void setNavCenter(String navCenter) {
        this.navCenter = navCenter;
    }
    /**
     * Creates a new instance of MenuNavBean
     */
    public MenuNavBean() {
        this.nadpis = "nadpis";
    }

    /**
     * @return the nadpis
     */
    public String getNadpis() {
        try{
            return ("true".equals(new LoggedBean().getLoggedAsAdmin()))?"/admin/newjsf.xhtml":"/users/newjsf.xhtml";
        }
        catch(Exception e){
            return nadpis;
        }
    }

    /**
     * @param nadpis the nadpis to set
     */
    public void setNadpis(String nadpis) {
        this.nadpis = nadpis;
    }
    
}
