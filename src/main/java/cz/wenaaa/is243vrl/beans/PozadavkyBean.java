/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.beans;

import cz.wenaaa.is243vrl.entityClasses.Zpravy;
import cz.wenaaa.is243vrl.controllers.LetajiciSluzbyController;
import cz.wenaaa.is243vrl.controllers.ZpravyController;
import cz.wenaaa.utils.Kalendar;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.sql.Date;
import javax.annotation.PostConstruct;

/**
 *
 * @author vena
 */
@Named(value = "pozadavkyBean")
@SessionScoped
public class PozadavkyBean implements Serializable{

    
    @PersistenceContext(unitName = "pozadavky_PU")
    private EntityManager em;
    @Inject
    UserTransaction ut;
    @Inject
    LetajiciSluzbyController lsc;
    @Inject
    ZpravyController zc;
    @Inject
    LoggedBean lb;
    private int zacatek;
    private int konec;
    private String vybranyTypPozadavku;
    private String[] vybranyLetajiciPozadavky;
    private List<ColumnModel> columns = new ArrayList<>();
    private GregorianCalendar gc;
    private List<String> letajici;
    private int indexLetajiciho;
    private List<List<String>> pozadavkyNaMesic;
    private List<Zpravy> zpravyNaMesic;
    
    public int getKonec() {
        return konec;
    }

    public List<List<String>> getPozadavkyNaMesic() {
        return pozadavkyNaMesic;
    }
    
    private void setZpravyNaMesic(){
        zpravyNaMesic = zc.getNaMesic(gc);
    }

    public List<Zpravy> getZpravyNaMesic() {
        return zpravyNaMesic;
    }
    
    public List<ColumnModel> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnModel> columns) {
        this.columns = columns;
    }
    
    private void populateColumns(){
        columns = new ArrayList<>();
        columns.add(new ColumnModel("","letajici"));
        for(int i = 1; i <= Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1);i++) {
            columns.add(new ColumnModel(String.format("%d", i), String.format("%d", i)));
        }
    }
    
    public String sirkaSloupce(int sirka){
        if (sirka == 0) return "25";
        return "12";
    }
    
    public String dejIDproOutputText(int radek, int sloupec){
        return String.format("radek: %d, sloupec: %d", radek, sloupec);
    }
    
    public void vyberZacatek(){
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String myName1 = params.get("jmeno");
        //System.out.print(myName1);
        String myName2 = params.get("den");
        setZacatek(Integer.parseInt(myName2));
        setKonec(Integer.parseInt(myName2));
        indexLetajiciho = Integer.parseInt(myName1);
        
    }
    public void vyberKonec(){
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String myName1 = params.get("jmeno");
        String myName2 = params.get("den");
        if(indexLetajiciho == Integer.parseInt(myName1)){
            setKonec(Integer.parseInt(myName2));
        }
        else{
            indexLetajiciho = -1;
        }
    }
    public void ulozPozadavky(String poz){
        
        //FacesContext context = FacesContext.getCurrentInstance();
        //HttpServletRequest request = 
        //        (HttpServletRequest) context.getExternalContext().getRequest();
        //String uzivatel = request.getRemoteUser();
        //System.out.printf("%d",indexLetajiciho);
        if(indexLetajiciho==-1) return;
        String uzivatel = letajici.get(indexLetajiciho);
        //System.out.print(poz);
        //System.out.printf("%s: %s zacatek/konec: %d/%d",uzivatel,poz,zacatek,konec);
        if(this.konec != this.zacatek){
            if((poz.equals("LK"))||(poz.equals("LD"))||(poz.equals("SK"))||(poz.equals("SD"))) return;
        }
        try {
            ut.begin();
            em.joinTransaction();
            Query qDel = em.createNativeQuery("DELETE FROM pozadavky WHERE letajici = ? AND datum >= ? AND datum <= ?");
            qDel.setParameter(1, uzivatel);
            gc.set(Calendar.DAY_OF_MONTH, zacatek);
            qDel.setParameter(2, (GregorianCalendar) gc.clone(), TemporalType.DATE);
            gc.set(Calendar.DAY_OF_MONTH, konec);
            qDel.setParameter(3, gc, TemporalType.DATE);
            qDel.executeUpdate();
            if(!("".equals(poz))){
                Query qIns = em.createNativeQuery("INSERT INTO pozadavky (datum, letajici, pozadavek) VALUES ( ?, ?, ?)");
                qIns.setParameter(2, uzivatel);
                qIns.setParameter(3, poz);
                for(int i = zacatek; i <= konec; i++){
                    gc.set(Calendar.DAY_OF_MONTH, i);
                    qIns.setParameter(1, gc, TemporalType.DATE);
                    qIns.executeUpdate();
                }
            }
            ut.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            //ut.rollback();
            Logger.getLogger(PozadavkyBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            gc.set(Calendar.DAY_OF_MONTH, 1);
            pozadavkyNaMesic.set(indexLetajiciho, pozadavkyPro(uzivatel));
            this.indexLetajiciho = -1;
        }
    }
    public String getStyle(int den, boolean prihlasen){
        if (den == 0) return "null";
        //System.out.println("vstup  "+new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        //System.out.println(den);
        gc.set(Calendar.DAY_OF_MONTH, den);
        String vratka =prihlasen?"aktivni":"null";
        //System.out.println(new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        if(Kalendar.jeSvatek(gc)) vratka = prihlasen?"svatek-aktivni":"svatek";
        if((gc.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY)||(gc.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)) vratka=prihlasen?"vikend-aktivni":"vikend";
        gc.set(Calendar.DAY_OF_MONTH, 1);
        //System.out.println("vystup  "+new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        return vratka;
    }
    public String getPodtrzitkoStyle(int den){
        if (den == 0) return "null";
        //System.out.println("vstup  "+new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        //System.out.println(den);
        gc.set(Calendar.DAY_OF_MONTH, den);
        String vratka ="podtrzitko-prihlasen";
        //System.out.println(new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        if(Kalendar.jeSvatek(gc)) vratka = "podtrzitko-svatek";
        if((gc.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY)||(gc.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)) vratka="podtrzitko-vikend";
        gc.set(Calendar.DAY_OF_MONTH, 1);
        //System.out.println("vystup  "+new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        return vratka;
    }
    public String cellColor(int den/*, String kdo*/){
        String vratka = "#ffffff";
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = 
                (HttpServletRequest) context.getExternalContext().getRequest();
        /*if(kdo.equals(request.getRemoteUser())){
            vratka = "#dfeffc";
        }*/
        if (den == 0) return vratka;
        //System.out.println("vstup  "+new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        //System.out.println(den);
        gc.set(Calendar.DAY_OF_MONTH, den);
        //System.out.println(new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        if(Kalendar.jeSvatek(gc)) vratka = "#D20005";
        if((gc.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY)||(gc.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)) vratka="#ffd700";
        gc.set(Calendar.DAY_OF_MONTH, 1);
        //System.out.println("vystup  "+new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        return vratka;
    }
    public void uberM(){
        gc.set(Calendar.DAY_OF_MONTH,1);
        gc.add(Calendar.MONTH, -1);
        populateColumns();
        nactiPozadavkyNaMesic();
        setZpravyNaMesic();
    }
    public void pridejM(){
        gc.set(Calendar.DAY_OF_MONTH,1);
        gc.add(Calendar.MONTH, 1);
        populateColumns();
        nactiPozadavkyNaMesic();
        setZpravyNaMesic();
    }
    public void prenastavMesic(){
        String prip = lb.isLoggedAsMedved()?"Palubaci":"Piloti";
        Query q1 = em.createNativeQuery("SELECT max(pozadavkyod"+prip+") FROM pomtab");
        GregorianCalendar pomGC = new GregorianCalendar();
        pomGC.setTime((Date) q1.getSingleResult());
        //System.out.println("prenastav mesic");
        //System.out.println(new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        if(pomGC.get(Calendar.MONTH)>gc.get(Calendar.MONTH)){
            gc.set(Calendar.MONTH, pomGC.get(Calendar.MONTH));
        }
        //System.out.println(new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        populateColumns();
        nactiPozadavkyNaMesic();
        setZpravyNaMesic();
    }
    public boolean renderedCommnandLink(){
        String prip = lb.isLoggedAsMedved()?"Palubaci":"Piloti";
        Query q1 = em.createNativeQuery("SELECT max(pozadavkyod"+prip+") FROM pomtab");
        GregorianCalendar pomGC = new GregorianCalendar();
        pomGC.setTime((Date) q1.getSingleResult());
        return this.gc.get(Calendar.MONTH)>pomGC.get(Calendar.MONTH);
    }
    public String renderedCellvII(int sloupec, String hodnota, int typ){
        if("".equals(hodnota) && typ == 2 && sloupec != 0) return "true";
        if((!"".equals(hodnota)) && typ == 1 && sloupec != 0) return "true";
        if(sloupec == 0 && typ == 3) return "true";
        return "false";
    }
    public String renderedCell(String kdo, int sloupec, String hodnota, int typ){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = 
                (HttpServletRequest) context.getExternalContext().getRequest();
        if (typ == 4){// jini uzivatele
            if(!kdo.equals(request.getRemoteUser())) return "true";
            return "false";
        }
        //vylouceni u vsech jinych uzivatelu
        if(!kdo.equals(request.getRemoteUser())) return "false";
        if(typ == 3){
            if(sloupec == 0) return "true";
            return "false";
        }
        if(sloupec == 0) return "false";
        if (typ == 2) {//ten co se edituje a uz ma zaznam
            if("".equals(hodnota)) return "false";
            return "true";
        }
        if (typ == 1) {//ten co se edituje a nema zaznam
            if("".equals(hodnota)) return "true";
            return "false";
        }
        return "false";
    }
    public void setKonec(int konec) {
        this.konec = konec;
        
    }
    public int getZacatek() {
        return zacatek;
    }
    public void setZacatek(int zacatek) {
        this.zacatek = zacatek;
    }
    public String getVybranyTypPozadavku() {
        return vybranyTypPozadavku;
    }
    public void setVybranyTypPozadavku(String vybranyTypPozadavku) {
        this.vybranyTypPozadavku = vybranyTypPozadavku;
    }
    public String[] getVybranyLetajiciPozadavky() {
        return vybranyLetajiciPozadavky;
    }
    public void setVybranyLetajiciPozadavky(String[] vybranyLetajiciPozadavky) {
        this.vybranyLetajiciPozadavky = vybranyLetajiciPozadavky;
    }
    /**
     * Creates a new instance of PozadavkyBean
     */
    public PozadavkyBean() {
        this.indexLetajiciho = -1;
        gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH,1);
        gc.add(Calendar.MONTH, 1);
        populateColumns();
        //System.out.println("konstr");
        //System.out.println(new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
    }
    public int dnu(){
        return Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1);   
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
    private List<String> pozadavkyPro(String jmeno){
        //System.out.print("nacitam pozadavky pro "+jmeno);
        Query q = em.createNativeQuery("SELECT pozadavek FROM pozadavky WHERE letajici = ? AND datum = ?");
        List<String> pom = new ArrayList<>();
        pom.add(jmeno);
        //String txt = jmeno;
        q.setParameter(1, jmeno);
        //System.out.println(""+new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        for(int i = 1; i <= dnu();i++){
            String vysledek = "";
            gc.set(Calendar.DAY_OF_MONTH, i);
            q.setParameter(2, gc, TemporalType.DATE);
            try{
                 vysledek = (String)q.getSingleResult();
            } catch(NoResultException e){
                //nic
            }
            //txt += ";"+vysledek;
            pom.add(vysledek);
        }
        //System.out.print(txt);
        return pom;
    }
    
    @PostConstruct
    private void nactiPozadavkyNaMesic(){
        List<List<String>> vratka = new ArrayList<>();
        //System.out.print("postconstruct nacitam pozadavky");
        if(lb.isLoggedAsMedved()){
            letajici = lsc.getPalubari();
        }else{
            letajici = lsc.getLetajici();
        }
        for(String l: letajici){
            vratka.add(pozadavkyPro(l));
        }
        gc.set(Calendar.DAY_OF_MONTH, 1);
        pozadavkyNaMesic = vratka;
        populateColumns();
    }
    public void prechodPlanovani(){
        //System.out.format("mesic pred: %d",gc.get(Calendar.MONTH));
        gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH,1);
        gc.add(Calendar.MONTH, 1);
        nactiPozadavkyNaMesic();
        setZpravyNaMesic();
        //System.out.format("mesic po: %d",gc.get(Calendar.MONTH));
        try {
            
            FacesContext.getCurrentInstance().getExternalContext().redirect("List.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(PozadavkyBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    static public class ColumnModel implements Serializable {
        private String header;
        private String property;
        public ColumnModel(String header, String property) {
            this.header = header;
            this.property = property;
        }
        public String getHeader() {
            return header;
        }
        public String getProperty() {
            return property;
        }
    }
}
