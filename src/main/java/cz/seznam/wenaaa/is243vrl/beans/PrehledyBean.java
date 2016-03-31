/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.beans;

import cz.seznam.wenaaa.is243vrl.entityClasses.ModelListenerFactory;
import cz.seznam.wenaaa.is243vrl.entityClasses.MyValueChangeEvent;
import cz.seznam.wenaaa.is243vrl.entityClasses.MyValueChangeListener;
import cz.seznam.wenaaa.is243vrl.entityClasses.Pozadavky;
import cz.seznam.wenaaa.is243vrl.entityClasses.PrumeryH120;
import cz.seznam.wenaaa.is243vrl.entityClasses.PrumerySluzeb;
import cz.seznam.wenaaa.is243vrl.entityClasses.Sluzby;
import cz.seznam.wenaaa.is243vrl.entityClasses.jsf.LetajiciSluzbyController;
import cz.seznam.wenaaa.utils.Kalendar;
import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.transaction.UserTransaction;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author vena
 */
@Named(value = "prehledyBean")
@SessionScoped
public class PrehledyBean implements Serializable, MyValueChangeListener{
    
    @PersistenceContext(unitName = "pozadavky_PU")
    private EntityManager em;
    @Inject
    UserTransaction ut;
    @Inject
    LetajiciSluzbyController lsc;
    @Inject
    LoggedBean lb;
    private final GregorianCalendar gc;
    private List<List<String>> sluzbyPodleDni;
    private List<List<String>> sluzbyPodlePilotu;
    private List<List<String>> sluzbyPodlePalubaru;
    private List<PrumeryH120> h120 = null;
    private List<ColumnModelIII> columns = new ArrayList<>();
    private List<PrumerySluzeb> prumeryLS = null;
    
    /**
     * Creates a new instance of PrehledyBean
     */
    public PrehledyBean() {
        gc = new GregorianCalendar();
    }
    @PostConstruct
    private void uvodniNacteni(){
        ModelListenerFactory.registerListener(this, Sluzby.class.getName());
        gc.set(Calendar.DAY_OF_MONTH, 1);
        populateColumns();
        nactiPrumerySluzeb();
        nactiPrumeryH120();
        nactiSluzbyNaMesic();
    }
    @PreDestroy
    private void predKoncem(){
        ModelListenerFactory.unregisterListener(this);
    }
    private void nactiSluzbyNaMesic(){
        sluzbyPodlePilotu = new ArrayList<>();
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
        gc.set(Calendar.DAY_OF_MONTH, 1);
        sluzbyPodleDni = nactiSluzby_interni(sluzbyPodlePilotu, sluzbyPodlePalubaru);
        //System.out.format("prehledy, nacteno");
        populateColumns();
    }
    public void uberM(){
        gc.set(Calendar.DAY_OF_MONTH,1);
        gc.add(Calendar.MONTH, -1);
        //populateColumns();
        nactiSluzbyNaMesic();
    }
    public void pridejM(){
        gc.set(Calendar.DAY_OF_MONTH,1);
        gc.add(Calendar.MONTH, 1);
        //populateColumns();
        nactiSluzbyNaMesic();
    }
    private void populateColumns(){
        columns = new ArrayList<>();
        columns.add(new PrehledyBean.ColumnModelIII("","letajici"));
        for(int i = 1; i <= Kalendar.dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1);i++) {
            columns.add(new PrehledyBean.ColumnModelIII(String.format("%d", i), String.format("%d", i)));
        }
    }
    public boolean jePozadavek(String str){
        if (str.equals("\u271C"))
                return false;
        if (str.equals("\u26AB"))
                return false;
        if (str.equals("L"))
                return false;
        return true;
    }
    public void ppXLSsPozadavkysZvyraznenim(Object document){
        postProcessXLS(document, true, true);
    }
    public void ppXLSsPozadavky(Object document){
        postProcessXLS(document, true, false);
    }
    public void ppXLSsZvyraznenim(Object document){
        postProcessXLS(document, false, true);
    }
    public void ppXLS(Object document){
        postProcessXLS(document, false, false);
    }
    private void postProcessXLS(Object document, boolean sPozadavky, boolean seZvyraznenim) {
        XSSFWorkbook wb = (XSSFWorkbook) document;
        XSSFSheet sheet = wb.getSheetAt(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, sheet.getRow(4).getLastCellNum()-1));
        //nadpis style
        XSSFFont fontNadpis = wb.createFont();
        fontNadpis.setFontHeightInPoints((short) 20);
        fontNadpis.setColor(IndexedColors.BLUE.getIndex());
        CellStyle styleNadpis = wb.createCellStyle();
        styleNadpis.setFont(fontNadpis);
        styleNadpis.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        XSSFCell cellNadpis = sheet.getRow(0).getCell(0);
        cellNadpis.setCellStyle(styleNadpis);
        sheet.createRow(1);
        sheet.createRow(2);
        sheet.createRow(3);
        for(int i = 1; i < sheet.getRow(4).getLastCellNum();i++){
            sheet.getRow(1).createCell(i).setCellValue("wii");
        }
        sheet.getRow(1).setZeroHeight(true);
        sheet.getRow(2).setZeroHeight(true);
        sheet.getRow(3).setZeroHeight(true);
        // dny style
        XSSFFont fontDny = wb.createFont();
        fontDny.setFontHeightInPoints((short) 12);
        fontDny.setColor(IndexedColors.BLUE.getIndex());
        XSSFCellStyle styleDny = wb.createCellStyle();
        styleDny.setFont(fontDny);
        styleDny.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styleDny.setFillForegroundColor(new XSSFColor(new java.awt.Color(223,239,252)));
        styleDny.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // jmena style
        XSSFFont fontJmena = wb.createFont();
        fontJmena.setFontHeightInPoints((short) 12);
        fontJmena.setColor(IndexedColors.BLACK.getIndex());
        XSSFCellStyle styleJmena = wb.createCellStyle();
        styleJmena.setFont(fontJmena);
        // sluzby style
        XSSFFont fontSluzby = wb.createFont();
        fontSluzby.setFontHeightInPoints((short) 12);
        fontSluzby.setColor(IndexedColors.BLACK.getIndex());
        fontSluzby.setBold(true);
        XSSFCellStyle styleSluzby = wb.createCellStyle();
        styleSluzby.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styleSluzby.setFont(fontSluzby);
        // pozadavky style
        XSSFFont fontPozadavky = wb.createFont();
        fontPozadavky.setFontHeightInPoints((short) 12);
        fontPozadavky.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        XSSFCellStyle stylePozadavky = wb.createCellStyle();
        stylePozadavky.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        stylePozadavky.setFont(fontPozadavky);
        // ZvyraznenyJmena
        XSSFCellStyle styleZvyraznenyJmena = wb.createCellStyle();
        styleZvyraznenyJmena.setFont(fontJmena);
        styleZvyraznenyJmena.setAlignment(XSSFCellStyle.ALIGN_LEFT);
        styleZvyraznenyJmena.setFillForegroundColor(new XSSFColor(new java.awt.Color(151,254,152)));
        styleZvyraznenyJmena.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // ZvyraznenyPozadavky
        XSSFCellStyle styleZvyraznenyPozadavky = wb.createCellStyle();
        styleZvyraznenyPozadavky.setFont(fontPozadavky);
        styleZvyraznenyPozadavky.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styleZvyraznenyPozadavky.setFillForegroundColor(new XSSFColor(new java.awt.Color(151,254,152)));
        styleZvyraznenyPozadavky.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // ZvyraznenySluzby
        XSSFCellStyle styleZvyraznenySluzby = wb.createCellStyle();
        styleZvyraznenySluzby.setFont(fontSluzby);
        styleZvyraznenySluzby.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styleZvyraznenySluzby.setFillForegroundColor(new XSSFColor(new java.awt.Color(151,254,152)));
        styleZvyraznenySluzby.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // SvatekPozadavky
        XSSFCellStyle styleSvatekPozadavky = wb.createCellStyle();
        styleSvatekPozadavky.setFont(fontPozadavky);
        styleSvatekPozadavky.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styleSvatekPozadavky.setFillForegroundColor(new XSSFColor(new java.awt.Color(254,165,165)));
        styleSvatekPozadavky.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // SvatekSluzby
        XSSFCellStyle styleSvatekSluzby = wb.createCellStyle();
        styleSvatekSluzby.setFont(fontSluzby);
        styleSvatekSluzby.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styleSvatekSluzby.setFillForegroundColor(new XSSFColor(new java.awt.Color(254,165,165)));
        styleSvatekSluzby.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // SobotaPozadavky
        XSSFCellStyle styleSobotaPozadavky = wb.createCellStyle();
        styleSobotaPozadavky.setFont(fontPozadavky);
        styleSobotaPozadavky.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styleSobotaPozadavky.setFillForegroundColor(new XSSFColor(new java.awt.Color(254,255,127)));
        styleSobotaPozadavky.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // SobotaSluzby
        XSSFCellStyle styleSobotaSluzby = wb.createCellStyle();
        styleSobotaSluzby.setFont(fontSluzby);
        styleSobotaSluzby.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styleSobotaSluzby.setFillForegroundColor(new XSSFColor(new java.awt.Color(254,255,127)));
        styleSobotaSluzby.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // SvatekZvyraznenyPozadavek
        XSSFCellStyle styleSvatekZvyraznenyPozadavek = wb.createCellStyle();
        styleSvatekZvyraznenyPozadavek.setFont(fontPozadavky);
        styleSvatekZvyraznenyPozadavek.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styleSvatekZvyraznenyPozadavek.setFillForegroundColor(new XSSFColor(new java.awt.Color(187,165,99)));
        styleSvatekZvyraznenyPozadavek.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // SvatekZvyraznenySluzba
        XSSFCellStyle styleSvatekZvyraznenySluzba = wb.createCellStyle();
        styleSvatekZvyraznenySluzba.setFont(fontSluzby);
        styleSvatekZvyraznenySluzba.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styleSvatekZvyraznenySluzba.setFillForegroundColor(new XSSFColor(new java.awt.Color(187,165,99)));
        styleSvatekZvyraznenySluzba.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // SobotaZvyraznenyPozadavek
        XSSFCellStyle styleSobotaZvyraznenyPozadavek = wb.createCellStyle();
        styleSobotaZvyraznenyPozadavek.setFont(fontPozadavky);
        styleSobotaZvyraznenyPozadavek.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styleSobotaZvyraznenyPozadavek.setFillForegroundColor(new XSSFColor(new java.awt.Color(203,255,76)));
        styleSobotaZvyraznenyPozadavek.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // SobotaZvyraznenySluzba
        XSSFCellStyle styleSobotaZvyraznenySluzba = wb.createCellStyle();
        styleSobotaZvyraznenySluzba.setFont(fontSluzby);
        styleSobotaZvyraznenySluzba.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styleSobotaZvyraznenySluzba.setFillForegroundColor(new XSSFColor(new java.awt.Color(203,255,76)));
        styleSobotaZvyraznenySluzba.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        for (Row row : sheet) {
            for (Cell cell : row) {
                try{
                    if(row.getCell(1).getStringCellValue().equals("1")){
                        cell.setCellStyle(styleDny);
                        continue;
                    }
                    
                    if(row.getRowNum()>1 && !row.getCell(0).getStringCellValue().isEmpty()){
                        if(cell.getColumnIndex()==0){
                            if(seZvyraznenim && cell.getStringCellValue().equals(lb.getLoginName())){
                                cell.setCellStyle(styleZvyraznenyJmena);
                                continue;
                            }
                            cell.setCellStyle(styleJmena);
                        }
                        else{
                            if(!sPozadavky && jePozadavek(cell.getStringCellValue())){
                                cell.setCellValue("");
                            }
                            XSSFCellStyle stSluzby;
                            XSSFCellStyle stPoz;
                            int den = gc.get(Calendar.DAY_OF_MONTH);
                            gc.set(Calendar.DAY_OF_MONTH, cell.getColumnIndex());
                            boolean jeSvatek = Kalendar.jeSvatek(gc);
                            boolean jeVikend = false;
                            if(gc.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || gc.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
                                jeVikend = true;
                            }
                            gc.set(Calendar.DAY_OF_MONTH, den);
                            
                            if(seZvyraznenim && row.getCell(0).getStringCellValue().equals(lb.getLoginName())){
                                stSluzby = jeSvatek?styleSvatekZvyraznenySluzba:(jeVikend?styleSobotaZvyraznenySluzba:styleZvyraznenySluzby);
                                stPoz = jeSvatek?styleSvatekZvyraznenyPozadavek:(jeVikend?styleSobotaZvyraznenyPozadavek:styleZvyraznenyPozadavky);
                            }else{
                                stSluzby = jeSvatek?styleSvatekSluzby:(jeVikend?styleSobotaSluzby:styleSluzby);
                                stPoz = jeSvatek?styleSvatekPozadavky:(jeVikend?styleSobotaPozadavky:stylePozadavky);
                            }
                            if(jePozadavek(cell.getStringCellValue())){
                                cell.setCellStyle(stPoz);
                            }else{
                                cell.setCellStyle(stSluzby);
                            }
                        }
                    }
                } catch(NullPointerException ex){
                    //nic
                }
            }
        }
    }
    public void ppXLSDny(Object document){
        postProcessXLSPodleDnu(document, false);
    }
    public void ppXLSDnySeZvyraznenim(Object document){
        postProcessXLSPodleDnu(document, true);
    }
    private void postProcessXLSPodleDnu(Object document, boolean seZvyraznenim) {
        XSSFWorkbook wb = (XSSFWorkbook) document;
        XSSFSheet sheet = wb.getSheetAt(0);
        sheet.getPrintSetup().setLandscape(false);
        sheet.removeMergedRegion(0);
        sheet.getRow(4).getCell(0).setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, sheet.getRow(10).getLastCellNum()-1));
        
        //nadpis style
        XSSFFont fontNadpis = wb.createFont();
        fontNadpis.setFontHeightInPoints((short) 20);
        fontNadpis.setColor(IndexedColors.BLUE.getIndex());
        CellStyle styleNadpis = wb.createCellStyle();
        styleNadpis.setFont(fontNadpis);
        styleNadpis.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        XSSFCell cellNadpis = sheet.getRow(0).getCell(0);
        cellNadpis.setCellStyle(styleNadpis);
        sheet.createRow(1);
        sheet.createRow(2);
        sheet.createRow(3);
        for(int i = 0; i < sheet.getRow(10).getLastCellNum();i++){
            if(i == 0){
                sheet.getRow(1).createCell(i).setCellValue("111");
                continue;
            }
            sheet.getRow(1).createCell(i).setCellValue("CHOCH");
        }
        sheet.getRow(1).setZeroHeight(true);
        sheet.getRow(2).setZeroHeight(true);
        sheet.getRow(3).setZeroHeight(true);
        sheet.getRow(4).setZeroHeight(true);
        sheet.getRow(7).setZeroHeight(true);
        // dny style
        XSSFFont fontDny = wb.createFont();
        fontDny.setFontHeightInPoints((short) 12);
        fontDny.setColor(IndexedColors.BLUE.getIndex());
        XSSFCellStyle styleDny = wb.createCellStyle();
        styleDny.setFont(fontDny);
        styleDny.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styleDny.setFillForegroundColor(new XSSFColor(new java.awt.Color(223,239,252)));
        styleDny.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // jmena style
        XSSFFont fontJmena = wb.createFont();
        fontJmena.setFontHeightInPoints((short) 12);
        fontJmena.setColor(IndexedColors.BLACK.getIndex());
        XSSFCellStyle styleJmena = wb.createCellStyle();
        styleJmena.setFont(fontJmena);
        styleJmena.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        // ZvyraznenyJmena
        XSSFCellStyle styleZvyraznenyJmena = wb.createCellStyle();
        styleZvyraznenyJmena.setFont(fontJmena);
        styleZvyraznenyJmena.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styleZvyraznenyJmena.setFillForegroundColor(new XSSFColor(new java.awt.Color(151,254,152)));
        styleZvyraznenyJmena.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // SvatekJmena
        XSSFCellStyle styleSvatekJmena = wb.createCellStyle();
        styleSvatekJmena.setFont(fontJmena);
        styleSvatekJmena.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styleSvatekJmena.setFillForegroundColor(new XSSFColor(new java.awt.Color(254,165,165)));
        styleSvatekJmena.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // SobotaJmena
        XSSFCellStyle styleSobotaJmena = wb.createCellStyle();
        styleSobotaJmena.setFont(fontJmena);
        styleSobotaJmena.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styleSobotaJmena.setFillForegroundColor(new XSSFColor(new java.awt.Color(254,255,127)));
        styleSobotaJmena.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        for (Row row : sheet) {
            if(row.getRowNum() < 5) continue;
            XSSFCellStyle normStyle = styleJmena;
            if(row.getRowNum() == 5 || row.getRowNum() == 6){
                normStyle =  styleDny;
            }
            else{
                try{
                    int den = Integer.parseInt(row.getCell(0).getStringCellValue());
                    int pomden = gc.get(Calendar.DAY_OF_MONTH);
                    gc.set(Calendar.DAY_OF_MONTH, den);
                    boolean jeSvatek = Kalendar.jeSvatek(gc);
                    boolean jeVikend = false;
                    if(gc.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || gc.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
                        jeVikend = true;
                        }
                    gc.set(Calendar.DAY_OF_MONTH, pomden);
                    if(jeSvatek){
                        normStyle = styleSvatekJmena;
                    }else{
                        if(jeVikend){
                            normStyle = styleSobotaJmena;
                        }
                    }
                }
                catch(NumberFormatException | NullPointerException ex){
                    // nic
                }
                //nic

            }
            for (Cell cell : row) {
                try{
                    if(cell == null){
                        continue;
                    }
                    if(seZvyraznenim && cell.getStringCellValue().equals(lb.getLoginName())){
                        cell.setCellStyle(styleZvyraznenyJmena);
                    }
                    else{
                        cell.setCellStyle(normStyle);
                    }
                } catch(NullPointerException ex){
                    //nic
                }
            }
        }
    }
    private List<List<String>> nactiSluzby_interni(List<List<String>> podlePilotu, List<List<String>> podlePalubaru) {
        List<List<String>> vratka = new ArrayList<>();
        GregorianCalendar gc2 = new GregorianCalendar();
        gc2.set(Calendar.DAY_OF_MONTH, 1);
        gc2.set(Calendar.MONTH, gc.get(Calendar.MONTH));
        gc2.set(Calendar.YEAR, gc.get(Calendar.YEAR));
        gc2.add(Calendar.MONTH,1);
        gc2.add(Calendar.DAY_OF_MONTH, -1);
        Query q1 = em.createNamedQuery("Pozadavky.naMesic");
        q1.setParameter("od", gc, TemporalType.DATE);
        q1.setParameter("do", gc2, TemporalType.DATE);
        q1.setParameter("ua", true);
        List<Pozadavky> pom = q1.getResultList();
        for(Pozadavky poz: pom){
            String jmeno = poz.getLetajici();
            GregorianCalendar pomgc = new GregorianCalendar();
            pomgc.setTime(poz.getDatum());
            int den = pomgc.get(Calendar.DAY_OF_MONTH);
            for(List<String> pl: podlePilotu){
                if(jmeno.equals(pl.get(0))){
                    pl.set(den, poz.getPozadavek());
                }
            }
            for(List<String> pl: podlePalubaru){
                if(jmeno.equals(pl.get(0))){
                    pl.set(den, poz.getPozadavek());
                }
            }
        }
        Query q = em.createNativeQuery("SELECT * FROM sluzby WHERE datum BETWEEN ? AND ? ORDER BY datum");
        q.setParameter(1, gc, TemporalType.DATE);
        q.setParameter(2, gc2, TemporalType.DATE);
        //System.out.format("nacitam sluzby od %s do %s", new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()), new SimpleDateFormat("yy/MMMM/dd").format(gc2.getTime()));
        List<Object[]> qResult = q.getResultList();
        //System.out.format("vysledku %d", qResult.size());
        for(Object[] sl: qResult){
            List<String> pomVratka = new ArrayList<>();
            GregorianCalendar pomGC = new GregorianCalendar();
            pomGC.setTime((Date) sl[0]);
            int den = pomGC.get(Calendar.DAY_OF_MONTH);
            pomVratka.add(String.valueOf(den));
            for(int j = 1; j < sl.length; j++){
                String jmeno = (String) sl[j];
                if(jmeno == null){
                    pomVratka.add("");
                    continue;
                }
                pomVratka.add(jmeno);
                for(List<String> pl: podlePilotu){
                    if(jmeno.equals(pl.get(0))){
                        switch(j){
                            case 1:
                            case 2:
                            case 3:
                                pl.set(den, "L");
                                break;
                            case 4:
                            case 5:
                            case 6:
                                pl.set(den, "\u26AB");
                                break;
                            case 7:
                            case 8:
                            case 9:
                                pl.set(den, "\u271C");
                                break;
                        }
                    }
                }
                for(List<String> pl: podlePalubaru){
                    if(jmeno.equals(pl.get(0))){
                        switch(j){
                            case 1:
                            case 2:
                            case 3:
                                pl.set(den, "L");
                                break;
                            case 4:
                            case 5:
                            case 6:
                                pl.set(den, "\u26AB");
                                break;
                            case 7:
                            case 8:
                            case 9:
                                pl.set(den, "\u271C");
                                break;
                        }
                    }
                }
            }
            vratka.add(pomVratka);
        }
        return vratka;
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
    public String cellColor(int den){
        String vratka = "#ffffff";
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
    public String getStyle(int den){
        if (den == 0) return "null";
        //System.out.println("vstup  "+new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        //System.out.println(den);
        gc.set(Calendar.DAY_OF_MONTH, den);
        String vratka ="null";
        //System.out.println(new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        if(Kalendar.jeSvatek(gc)) vratka = "svatek";
        if((gc.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY)||(gc.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)) vratka="vikend";
        gc.set(Calendar.DAY_OF_MONTH, 1);
        //System.out.println("vystup  "+new SimpleDateFormat("yy/MMMM/dd").format(gc.getTime()));
        return vratka;
    }
    public List<List<String>> getSluzbyPodleDni() {
        return sluzbyPodleDni;
    }

    public List<List<String>> getSluzbyPodlePilotu() {
        return sluzbyPodlePilotu;
    }

    public List<List<String>> getSluzbyPodlePalubaru() {
        return sluzbyPodlePalubaru;
    }
    
    private void nactiPrumeryH120() {
        Query q = em.createNativeQuery("SELECT * FROM prumery_h120 WHERE pocet_sluzeb_h120 > 1 ORDER BY p_volne DESC");
        h120 = new ArrayList<>();
        for(Object obj: q.getResultList()){
            Object[] pole = (Object[]) obj;
            String letajici = (String)pole[0];
            int pocetSluzeb = (int)pole[1]-1;
            int pocetVsednichSvatku = (int)pole[2];
            double pSv = (double)pole[3];
            int pocetSobot = (int)pole[4];
            double pSo = (double)pole[5];
            int pocetNedeli = (int)pole[6];
            double pNe = (double)pole[7];
            int pocetPatku = (int)pole[8];
            double pPa = (double)pole[9];
            int volneDny = (int)pole[10];
            double pVolne = (double)pole[11];
            h120.add(new PrumeryH120(letajici,pocetSluzeb, pocetVsednichSvatku, pSv, pocetSobot,
            pSo, pocetNedeli, pNe, pocetPatku, pPa, volneDny, pVolne));
        }
    }
    private void nactiPrumerySluzeb() {
        Query q = em.createNativeQuery("SELECT * FROM prumery_sluzeb WHERE pocet_sluzeb > 1 ORDER BY p_volne DESC");
        prumeryLS = new ArrayList<>();
        for(Object obj: q.getResultList()){
            Object[] pole = (Object[]) obj;
            String letajici = (String)pole[0];
            int pocetSluzeb = (int)pole[1]-1;
            int pocetVsednichSvatku = (int)pole[2];
            double pSv = (double)pole[3];
            int pocetSobot = (int)pole[4];
            double pSo = (double)pole[5];
            int pocetNedeli = (int)pole[6];
            double pNe = (double)pole[7];
            int pocetPatku = (int)pole[8];
            double pPa = (double)pole[9];
            int volneDny = (int)pole[10];
            double pVolne = (double)pole[11];
            prumeryLS.add(new PrumerySluzeb(letajici,pocetSluzeb, pocetVsednichSvatku, pSv, pocetSobot,
            pSo, pocetNedeli, pNe, pocetPatku, pPa, volneDny, pVolne));
        }
    }
    public List<PrumeryH120> getH120() {
        return h120;
    }

    public List<PrumerySluzeb> getPrumeryLS() {
        return prumeryLS;
    }
    

    public List<ColumnModelIII> getColumns() {
        return columns;
    }

    @Override
    public void onValueChanged(MyValueChangeEvent mvche) {
        nactiSluzbyNaMesic();
    }
    
    static public class ColumnModelIII implements Serializable {
        private String header;
        private String property;
        public ColumnModelIII(String header, String property) {
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
