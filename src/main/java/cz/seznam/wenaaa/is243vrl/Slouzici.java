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
    private final String jmeno;
    long plneVolneDny;//pro binarni operace plny 1, volny 0
    float maxPocetSluzeb;
    private final float planujSluzeb;
    Slouzici dalsi;

    public Slouzici(String jmeno, long plneVolneDny, float maxPocetSluzeb, float planujSluzeb, String dojizdeni) {
        this.dalsi = null;
        this.jmeno = jmeno;
        this.plneVolneDny = plneVolneDny;
        this.maxPocetSluzeb = maxPocetSluzeb;
        this.planujSluzeb = planujSluzeb;
        
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
            if(pom.getJmeno().equals(jmeno))return pom.plneVolneDny;
            pom = pom.dalsi;
        }while(pom != null);
        //System.out.format("nevyslo");
        throw new IllegalArgumentException();
    }
    public float getMaxPocetSluzeb(String jmeno) {
        Slouzici pom = this;
        do{
            if(pom.getJmeno().equals(jmeno))return pom.maxPocetSluzeb;
            pom = pom.dalsi;
        }while(pom != null);
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return "Slouzici{" + "jmeno=" + getJmeno() + ", plneVolneDny=" + plneVolneDny + ", maxPocetSluzeb=" + maxPocetSluzeb + '}';
    }

    /**
     * @return the planujSluzeb
     */
    public float getPlanujSluzeb() {
        return planujSluzeb;
    }

    /**
     * @return the jmeno
     */
    public String getJmeno() {
        return jmeno;
    }
    
}
