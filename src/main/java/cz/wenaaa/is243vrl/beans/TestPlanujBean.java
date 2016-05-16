/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.beans;

import cz.wenaaa.is243vrl.planovani.PlanovaniSluzeb;
import cz.wenaaa.utils.Kalendar;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.event.ActionEvent;

/**
 *
 * @author vena
 */
@Named(value = "testPlanujBean")
@Dependent
public class TestPlanujBean {

    /**
     * Creates a new instance of TestPlanujBean
     */
    public TestPlanujBean() {
    }
    
    
    public void planujPiloty(ActionEvent ev){
        boolean[] dnysvozu = new boolean[Kalendar.dnuVMesici(new GregorianCalendar())+1];
        for(int i = 13; i<28; i++){
            dnysvozu[i] = true;
        }
        PlanovaniSluzeb.getInstance(false).naplanuj(new GregorianCalendar(2016, Calendar.MAY, 4), dnysvozu);
    }
    
    public void planujPalubare(ActionEvent ev){
        boolean[] dnysvozu = new boolean[Kalendar.dnuVMesici(new GregorianCalendar())+1];
        for(int i = 10; i<25; i++){
            dnysvozu[i] = true;
        }
        PlanovaniSluzeb.getInstance(true).naplanuj(new GregorianCalendar(2016, Calendar.MAY, 4), dnysvozu);
    }
    
    public String getInfoPiloti(){
        String vratka = "";
        String[] ti = PlanovaniSluzeb.getInstance(false).getTextInfo();
        for(String pol:ti){
            if(pol != null && pol.isEmpty()){
                continue;
            }
            vratka += pol+" / ";
        }
        return vratka;
    }
    
    public String getInfoPalubari(){
        String vratka = "";
        String[] ti = PlanovaniSluzeb.getInstance(true).getTextInfo();
        for(String pol:ti){
            if(pol != null && pol.isEmpty()){
                continue;
            }
            vratka += pol+" / ";
        }
        return vratka;
    }
}
