package cz.wenaaa.is243vrl.entityClasses.jsf;

import cz.wenaaa.is243vrl.beans.MyActionEvent;
import cz.wenaaa.is243vrl.beans.MyActionListener;
import cz.wenaaa.is243vrl.beans.PlanovaniBean;
import cz.wenaaa.is243vrl.beans.SluzbyBean;
import cz.wenaaa.is243vrl.beans.ViewListenerFactory;
import cz.wenaaa.is243vrl.entityClasses.Sluzby;
import cz.wenaaa.is243vrl.entityClasses.jsf.util.JsfUtil;
import cz.wenaaa.is243vrl.entityClasses.jsf.util.JsfUtil.PersistAction;
import cz.wenaaa.is243vrl.beans.entityClasses.SluzbyFacade;
import cz.wenaaa.is243vrl.entityClasses.ModelListenerFactory;
import cz.wenaaa.is243vrl.entityClasses.MyValueChangeEvent;
import cz.wenaaa.is243vrl.entityClasses.MyValueChangeListener;
import cz.wenaaa.is243vrl.entityClasses.SluzbyValueChangeEvent;
import cz.wenaaa.utils.Kalendar;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

@Named("sluzbyController")
@SessionScoped
public class SluzbyController implements Serializable, MyValueChangeListener, MyActionListener {

    @EJB
    private cz.wenaaa.is243vrl.beans.entityClasses.SluzbyFacade ejbFacade;
    private Sluzby selected;
    private GregorianCalendar gc;
    private List<Sluzby> naMesic;
    //private List<List<String>> sluzbyPodlePilotu;
    //private List<List<String>> sluzbyPodlePalubaru;
    @Inject
    private LetajiciSluzbyController lsc;
    @Inject
    private PlanovaniBean pb;
    //private List<ColumnModelIV> columns = new ArrayList<>();
    private SluzbyValueChangeEvent svche;
    private String zmeny;
    
    public SluzbyController() {
        
    }
    @PostConstruct
    private void poKonstruktu(){
        ModelListenerFactory.registerListener(this, Sluzby.class.getName());
        ViewListenerFactory.registerListener(this, SluzbyBean.class.getName());
        zmeny = "";
        gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        nactiNaMesic();
    }
    @PreDestroy
    private void predKoncem(){
        ModelListenerFactory.unregisterListener(this);
        ViewListenerFactory.unregisterListener(this);
    }
    private void nactiNaMesic() {
        GregorianCalendar pomgc = new GregorianCalendar();
        pomgc.set(Calendar.DAY_OF_MONTH, 1);
        pomgc.set(Calendar.MONTH, gc.get(Calendar.MONTH));
        pomgc.set(Calendar.YEAR, gc.get(Calendar.YEAR));
        pomgc.add(Calendar.MONTH, 1);
        pomgc.add(Calendar.DAY_OF_MONTH, -1);
        //System.out.format("nacitam sluzby od %s do %s", new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()), new SimpleDateFormat("yy/MMMM/dd").format(pomgc.getTime()));
        Query q = getFacade().getEntityManager().createNamedQuery("Sluzby.naMesic");
        q.setParameter("od", gc, TemporalType.DATE);
        q.setParameter("do", pomgc, TemporalType.DATE);
        naMesic = q.getResultList();
        /*sluzbyPodlePilotu = new ArrayList<>();
        sluzbyPodlePalubaru = new ArrayList<>();
        //System.out.print("postconstruct nacitam pozadavky");
        List<String> letajici = lsc.getLetajici();
        for(String l: letajici){
            List<String> pom = new ArrayList<>();
            pom.add(l);
            for(int i = 0; i < Kalendar.dnuVMesici(gc);i++){
                pom.add("");
            }
            sluzbyPodlePilotu.add(pom);
        }
        List<String> palubari = lsc.getPalubari();
        for(String l: palubari){
            List<String> pom = new ArrayList<>();
            pom.add(l);
            for(int i = 0; i < Kalendar.dnuVMesici(gc);i++){
                pom.add("");
            }
            sluzbyPodlePalubaru.add(pom);
        }
        for(Sluzby sl: naMesic){
            pomgc.setTime(sl.getDatum());
            int den = pomgc.get(Calendar.DAY_OF_MONTH);
            int sluzebPrirazeno = 0;
            for(List<String> slPil: sluzbyPodlePilotu){
                String lk = sl.getLk() == null ? "": sl.getLk().getLetajici();
                String ld = sl.getLd() == null ? "": sl.getLd().getLetajici();
                String sk = sl.getSk() == null ? "": sl.getSk().getLetajici();
                String sd = sl.getSd() == null ? "": sl.getSd().getLetajici();
                String hk = sl.getHk() == null ? "": sl.getHk().getLetajici();
                String hd = sl.getHd() == null ? "": sl.getHd().getLetajici();
                if(lk.equals(slPil.get(0)) || ld.equals(slPil.get(0))){
                        slPil.set(den, "L");
                        sluzebPrirazeno++;
                        if(sluzebPrirazeno == 6) break;
                        continue;
                }
                if(sk.equals(slPil.get(0)) || sd.equals(slPil.get(0))){
                        slPil.set(den, "\u26AB");
                        sluzebPrirazeno++;
                        if(sluzebPrirazeno == 6) break;
                        continue;
                }
                if(hk.equals(slPil.get(0)) || hd.equals(slPil.get(0))){
                        slPil.set(den, "\u271C");
                        sluzebPrirazeno++;
                        if(sluzebPrirazeno == 6) break;
                }
            }
            for(List<String> slPal: sluzbyPodlePalubaru){
                String lp = sl.getLp() == null ? "": sl.getLp().getLetajici();
                String sp = sl.getSp() == null ? "": sl.getSp().getLetajici();
                String hp = sl.getHp() == null ? "": sl.getHp().getLetajici();
                if(lp.equals(slPal.get(0))){
                        slPal.set(den, "L");
                        sluzebPrirazeno++;
                        if(sluzebPrirazeno == 9) break;
                        continue;
                }
                if(sp.equals(slPal.get(0))){
                        slPal.set(den, "\u26AB");
                        sluzebPrirazeno++;
                        if(sluzebPrirazeno == 9) break;
                        continue;
                }
                if(hp.equals(slPal.get(0))){
                        slPal.set(den, "\u271C");
                        sluzebPrirazeno++;
                        if(sluzebPrirazeno == 9) break;
                }
            }
        }
        populateColumns();*/
    }
    public void pridejMesic(){
        gc.add(Calendar.MONTH, 1);
        nactiNaMesic();
    }
    public void uberMesic(){
        gc.add(Calendar.MONTH, -1);
        nactiNaMesic();
    }
    public String proMesic(){
        Map m = new HashMap();
        m.put(0, "Leden");
        m.put(1, "Únor");
        m.put(2, "Březen");
        m.put(3, "Duben");
        m.put(4, "Květen");
        m.put(5, "Červen");
        m.put(6, "Červenec");
        m.put(7, "Srpen");
        m.put(8, "Září");
        m.put(9, "Říjen");
        m.put(10, "Listopad");
        m.put(11, "Prosinec");
        return (String)m.get(gc.get(Calendar.MONTH))+" "+new SimpleDateFormat("yyyy").format(gc.getTime());
    }
    public void onCellEdit() {
        Sluzby sluzba = svche.getSluzba();
        String oldVal = svche.getStarySlouzici();
        String newVal;
        String typSluzby = svche.getTypSluzby();
        GregorianCalendar pomGC = new GregorianCalendar();
        pomGC.setTime(sluzba.getDatum());
        switch(typSluzby){
            case "LK":
                newVal = sluzba.getLk().getLetajici();
                break;
            case "LD":
                newVal = sluzba.getLd().getLetajici();
                break;
            case "LP":
                newVal = sluzba.getLp().getLetajici();
                break;
            case "SK":
                newVal = sluzba.getSk().getLetajici();
                break;
            case "SD":
                newVal = sluzba.getSd().getLetajici();
                break;
            case "SP":
                newVal = sluzba.getSp().getLetajici();
                break;
            case "HK":
                newVal = sluzba.getHk().getLetajici();
                break;
            case "HD":
                newVal = sluzba.getHd().getLetajici();
                break;
            case "HP":
                newVal = sluzba.getHp().getLetajici();
                break;
            default:
                System.out.println("spatny typ sluzby.");
                return;
        }
        pb.ulozSluzbu(pomGC, typSluzby, newVal);
        zmeny += String.format("%d. %s %s: %s -> %s\n", pomGC.get(Calendar.DAY_OF_MONTH), proMesic(), typSluzby, oldVal, newVal);
        svche = null;
        nactiNaMesic();
        //FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Sluzba zmenena", "Old: " + oldVal + ", New:" + newVal);
        //FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    public Sluzby getSelected() {
        return selected;
    }

    public void setZmeny(String zmeny) {
        this.zmeny = zmeny;
    }
    
    public String getZmeny() {
        return zmeny;
    }
    /*
    public List<List<String>> getSluzbyPodlePilotu() {
        return sluzbyPodlePilotu;
    }

    public List<List<String>> getSluzbyPodlePalubaru() {
        return sluzbyPodlePalubaru;
    }*/
    
    public void setSelected(Sluzby selected) {
        
        this.selected = selected;
    }
    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage("Selected", ((Sluzby) event.getObject()).getDatum().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage("Unselected", ((Sluzby) event.getObject()).getDatum().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    public List<Sluzby> getNaMesic() {
        return naMesic;
    }
    
    private SluzbyFacade getFacade() {
        return ejbFacade;
    }
/*
    public Sluzby prepareCreate() {
        selected = new Sluzby();
        initializeEmbeddableKey();
        return selected;
    }*/

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("SluzbyUpdated"));
    }

    
    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
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

    public Sluzby getSluzby(java.util.Date id) {
        return getFacade().find(id);
    }
    /*public List<ColumnModelIV> getColumns() {
        return columns;
    }*/
    public String getStyle(int den){
        if (den == 0) return "null";
        //System.out.println("vstup  "+new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        //System.out.println(den);
        GregorianCalendar pomgc = new GregorianCalendar();
        pomgc.set(Calendar.DAY_OF_MONTH,1);
        pomgc.set(Calendar.MONTH,gc.get(Calendar.MONTH));
        pomgc.set(Calendar.DAY_OF_MONTH,den);
        String vratka ="null";
        //System.out.println(new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        if(Kalendar.jeSvatek(pomgc)) vratka = "svatek";
        if((pomgc.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY)||(pomgc.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)) vratka="vikend";
        //System.out.println("vystup  "+new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        return vratka;
    }
    /*
    private void populateColumns(){
        columns = new ArrayList<>();
        columns.add(new SluzbyController.ColumnModelIV("","letajici"));
        for(int i = 1; i <= Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1);i++) {
            columns.add(new SluzbyController.ColumnModelIV(String.format("%d", i), String.format("%d", i)));
        }
    }*/

    public List<Sluzby> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Sluzby> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @Override
    public void onValueChanged(MyValueChangeEvent mvche) {
        this.svche = (SluzbyValueChangeEvent) mvche;
        //System.out.println("obdrzen valuechangeevent");
        
    }

    @Override
    public void onActionPerformed(MyActionEvent mae) {
        //System.out.println("obdrzen MyActionEvent");
        if(svche != null && mae.getClassSending().equals(SluzbyBean.class.getName())){
            onCellEdit();
        }
    }
    
    @FacesConverter(forClass = Sluzby.class)
    public static class SluzbyControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SluzbyController controller = (SluzbyController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "sluzbyController");
            return controller.getSluzby(getKey(value));
        }

        java.util.Date getKey(String value) {
            java.util.Date key;
            key = java.sql.Date.valueOf(value);
            return key;
        }

        String getStringKey(java.util.Date value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Sluzby) {
                Sluzby o = (Sluzby) object;
                return getStringKey(o.getDatum());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Sluzby.class.getName()});
                return null;
            }
        }

    }
    /*
    static public class ColumnModelIV implements Serializable {
        private String header;
        private String property;
        public ColumnModelIV(String header, String property) {
            this.header = header;
            this.property = property;
        }
        public String getHeader() {
            return header;
        }
        public String getProperty() {
            return property;
        }
    }*/
}
