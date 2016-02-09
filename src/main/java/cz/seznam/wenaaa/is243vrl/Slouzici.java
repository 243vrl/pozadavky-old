/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl;



/**
 *
 * @author vena
 */
public class Slouzici {
    String jmeno;
    long plneVolneDny;//pro binarni operace plny 1, volny 0
    float maxPocetSluzeb;
    Slouzici dalsi;

    public Slouzici(String jmeno, long plneVolneDny, float maxPocetSluzeb) {
        this.dalsi = null;
        this.jmeno = jmeno;
        this.plneVolneDny = plneVolneDny;
        this.maxPocetSluzeb = maxPocetSluzeb;
    }
    
    public void addSlouzici(Slouzici sl){
        Slouzici pom  = this;
        while(pom.dalsi != null){
            pom = pom.dalsi;
        }
        pom.dalsi = sl;
    }

    public Slouzici getDalsi() {
        return dalsi;
    }

    public void setDalsi(Slouzici dalsi) {
        this.dalsi = dalsi;
    }

    public long getPlneVolneDny(String jmeno) {
        Slouzici pom = this;
        do{
            //System.out.format("%s/%s:%b", jmeno,pom.jmeno,pom.jmeno.equals(jmeno));
            if(pom.jmeno.equals(jmeno))return pom.plneVolneDny;
            pom = pom.dalsi;
        }while(pom != null);
        //System.out.format("nevyslo");
        throw new IllegalArgumentException();
    }

    public float getMaxPocetSluzeb(String jmeno) {
        Slouzici pom = this;
        do{
            if(pom.jmeno.equals(jmeno))return pom.maxPocetSluzeb;
            pom = pom.dalsi;
        }while(pom != null);
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return "Slouzici{" + "jmeno=" + jmeno + ", plneVolneDny=" + plneVolneDny + ", maxPocetSluzeb=" + maxPocetSluzeb + '}';
    }
    
}
