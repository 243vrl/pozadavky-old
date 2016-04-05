/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl;

import java.util.Objects;



/**
 *
 * @author vena
 */
public class Slouzici {
    private final String jmeno;
    private long plneVolneDny;//pro binarni operace plny 1, volny 0
    private float maxPocetSluzeb;
    private float planujSluzeb;
    private String skupina;
    private final String dojizdeni;
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.jmeno);
        hash = 29 * hash + (int) (this.plneVolneDny ^ (this.plneVolneDny >>> 32));
        hash = 29 * hash + Float.floatToIntBits(this.maxPocetSluzeb);
        hash = 29 * hash + Float.floatToIntBits(this.planujSluzeb);
        hash = 29 * hash + Objects.hashCode(this.skupina);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Slouzici other = (Slouzici) obj;
        return Objects.equals(this.jmeno, other.jmeno);
    }
    
    public void pridejSluzbuDoSkupiny(String typSluzby){
        this.skupina += ";"+typSluzby;
    }
    public int getPocetPlnychDnu(){
        long pom = this.plneVolneDny;
        int vratka = 0;
        while(pom != 0){
            if((pom & 1) == 1){
                vratka++;
            }
            pom = pom>>>1;
        }
        return vratka;
    }
    
    public Slouzici(String jmeno, String skupina, String dojizdeni){
        this.jmeno = jmeno;
        this.skupina = skupina;
        this.dojizdeni = dojizdeni;
        if(skupina.equals("")){
            this.planujSluzeb = 9;
        }else{
            this.planujSluzeb = 0;
        }
    }
    
    public String getDojizdeni() {
        return dojizdeni;
    }

    public long getPlneVolneDny() {
        return plneVolneDny;
    }

    public void setPlneVolneDny(long plneVolneDny) {
        this.plneVolneDny = plneVolneDny;
    }

    public float getMaxPocetSluzeb() {
        return maxPocetSluzeb;
    }

    public void setMaxPocetSluzeb(float maxPocetSluzeb) {
        this.maxPocetSluzeb = maxPocetSluzeb;
    }

    public float getPlanujSluzeb() {
        return planujSluzeb;
    }

    public void setPlanujSluzeb(float planujSluzeb) {
        this.planujSluzeb = planujSluzeb;
    }

    public String getJmeno() {
        return jmeno;
    }

    public String getSkupina() {
        return skupina;
    }

    @Override
    public String toString() {
        return "Slouzici{" + "jmeno=" + jmeno + ", plneVolneDny=" + plneVolneDny + ", maxPocetSluzeb=" + maxPocetSluzeb + ", planujSluzeb=" + planujSluzeb + ", skupina=" + skupina + ", dojizdeni=" + dojizdeni + '}';
    }
    
    
}
    
