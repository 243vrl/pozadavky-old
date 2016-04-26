/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.beans;

import cz.wenaaa.is243vrl.entityClasses.Sluzby;
import cz.wenaaa.is243vrl.entityClasses.jsf.SluzbyController;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;

/**
 *
 * @author vena
 */
@Named(value = "sluzbyBean")
@RequestScoped
public class SluzbyBean implements Serializable{
    
    @Inject
    SluzbyController sc;
    private List<Sluzby> naMesic;
    private Sluzby selected;
    private String zmeny;
    /**
     * Creates a new instance of SluzbyBean
     */
    public SluzbyBean() {
    }
    @PostConstruct
    private void poKonstruktu(){
        naMesic = sc.getNaMesic();
        selected = null;
        
    }
    public void onCellEdit(CellEditEvent ev){
        ViewListenerFactory.actionPerformed(new MyActionEvent(this.getClass().getName()));
    }
    public void uberMesic(){
        sc.uberMesic();
        setNaMesic();
    }
    public String proMesic(){
        return sc.proMesic();
    }
    public void pridejMesic(){
        sc.pridejMesic();
        setNaMesic();
    }
    public String getStyle(int den){
        return sc.getStyle(den);
    }

    public String getZmeny() {
        return sc.getZmeny();
    }

    public void vynulujZmeny() {
        sc.setZmeny("");
    }
    
    /*public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        //selected = naMesic.get(event.getRowIndex());
        if(newValue != null && !newValue.equals(oldValue)) {

            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        DataTable dt = (DataTable) event.getSource();
        selected = naMesic.get(event.getRowIndex());
        sc.onCellEdit(event);
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("sluzby2.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(SluzbyBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/

    public Sluzby getSelected() {
        return selected;
    }

    public void setSelected(Sluzby selected) {
        this.selected = selected;
    }

    public List<Sluzby> getNaMesic() {
        return naMesic;
    }
    
    private void setNaMesic(){
        naMesic = sc.getNaMesic();
    }
    
}
