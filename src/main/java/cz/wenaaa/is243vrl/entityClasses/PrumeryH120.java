/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.entityClasses;

import java.io.Serializable;

/**
 *
 * @author vena
 */
public class PrumeryH120 implements Serializable {
    private final String letajici;
    private final int pocetSluzeb;
    private final int pocetVsednichSvatku;
    private final double pSv;
    private final int pocetSobot;
    private final double pSo;
    private final int pocetNedeli;
    private final double pNe;
    private final int pocetPatku;
    private final double pPa;
    private final int volneDny;
    private final double pVolne;
    
    public PrumeryH120(String letajici, int pocetSluzeb, int pocetVsednichSvatku, double pSv, int pocetSobot,
            double pSo, int pocetNedeli, double pNe, int pocetPatku, double pPa, int volneDny, double pVolne) {
        this.letajici = letajici;
        this.pocetSluzeb = pocetSluzeb;
        this.pocetVsednichSvatku = pocetVsednichSvatku;
        this.pSv = pSv;
        this.pocetSobot = pocetSobot;
        this.pSo = pSo;
        this.pocetNedeli = pocetNedeli;
        this.pNe = pNe;
        this.pocetPatku = pocetPatku;
        this.pPa = pPa;
        this.volneDny = volneDny;
        this.pVolne = pVolne;
    }

    public String getLetajici() {
        return letajici;
    }

    public Integer getPocetSluzeb() {
        return pocetSluzeb;
    }

    public Integer getPocetVsednichSvatku() {
        return pocetVsednichSvatku;
    }

    public Double getpSv() {
        return pSv;
    }

    public Integer getPocetSobot() {
        return pocetSobot;
    }

    public Double getpSo() {
        return pSo;
    }

    public Integer getPocetNedeli() {
        return pocetNedeli;
    }

    public Double getpNe() {
        return pNe;
    }

    public Integer getPocetPatku() {
        return pocetPatku;
    }

    public Double getpPa() {
        return pPa;
    }

    public Integer getVolneDny() {
        return volneDny;
    }

    public Double getpVolne() {
        return pVolne;
    }

    
}
