/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.entityClasses;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vena
 */
@Entity
@Table(name = "sluzby")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sluzby.findAll", query = "SELECT s FROM Sluzby s"),
    @NamedQuery(name = "Sluzby.findByDatum", query = "SELECT s FROM Sluzby s WHERE s.datum = :datum"),
    @NamedQuery(name = "Sluzby.konecMesice", query = "SELECT s FROM Sluzby s WHERE s.datum < :datum ORDER BY s.datum DESC LIMIT 6"),
    @NamedQuery(name = "Sluzby.naMesic", query = "SELECT s FROM Sluzby s WHERE s.datum BETWEEN :od AND :do ORDER BY s.datum")})
public class Sluzby implements Serializable {
    @JoinColumn(name = "hd", referencedColumnName = "letajici")
    @ManyToOne
    private LetajiciSluzby2 hd;
    @JoinColumn(name = "hk", referencedColumnName = "letajici")
    @ManyToOne
    private LetajiciSluzby2 hk;
    @JoinColumn(name = "hp", referencedColumnName = "letajici")
    @ManyToOne
    private LetajiciSluzby2 hp;
    @JoinColumn(name = "ld", referencedColumnName = "letajici")
    @ManyToOne
    private LetajiciSluzby2 ld;
    @JoinColumn(name = "lk", referencedColumnName = "letajici")
    @ManyToOne
    private LetajiciSluzby2 lk;
    @JoinColumn(name = "lp", referencedColumnName = "letajici")
    @ManyToOne
    private LetajiciSluzby2 lp;
    @JoinColumn(name = "sd", referencedColumnName = "letajici")
    @ManyToOne
    private LetajiciSluzby2 sd;
    @JoinColumn(name = "sk", referencedColumnName = "letajici")
    @ManyToOne
    private LetajiciSluzby2 sk;
    @JoinColumn(name = "sp", referencedColumnName = "letajici")
    @ManyToOne
    private LetajiciSluzby2 sp;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "datum")
    @Temporal(TemporalType.DATE)
    private Date datum;
    
    
    public Sluzby() {
        
    }

    public Sluzby(Date datum) {
        this.datum = datum;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (datum != null ? datum.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sluzby)) {
            return false;
        }
        Sluzby other = (Sluzby) object;
        if ((this.datum == null && other.datum != null) || (this.datum != null && !this.datum.equals(other.datum))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.seznam.wenaaa.is243vrl.entityClasses.Sluzby[ datum=" + datum + " ]";
    }

    public LetajiciSluzby2 getHd() {
        return hd;
    }

    public void setHd(LetajiciSluzby2 hd) {
        if(hd == this.hd) return;
        String stary = this.hd == null?"":this.hd.getLetajici();
        this.hd = hd;
        fireValueChanged("HD", stary);
    }

    public LetajiciSluzby2 getHk() {
        return hk;
    }

    public void setHk(LetajiciSluzby2 hk) {
        if(hk == this.hk) return;
        String stary = this.hk == null?"":this.hk.getLetajici();
        this.hk = hk;
        fireValueChanged("HK", stary);
    }

    public LetajiciSluzby2 getHp() {
        return hp;
    }

    public void setHp(LetajiciSluzby2 hp) {
        if(hp == this.hp) return;
        String stary = this.hp == null?"":this.hp.getLetajici();
        this.hp = hp;
        fireValueChanged("HP", stary);
    }

    public LetajiciSluzby2 getLd() {
        return ld;
    }

    public void setLd(LetajiciSluzby2 ld) {
        if(ld == this.ld) return;
        String stary = this.ld == null?"":this.ld.getLetajici();
        this.ld = ld;
        fireValueChanged("LD", stary);
    }

    public LetajiciSluzby2 getLk() {
        return lk;
    }

    public void setLk(LetajiciSluzby2 lk) {
        //System.out.format("obdrzen lk: %s", lk.getLetajici());
        if(lk == this.lk) return;
        String stary = this.lk == null?"":this.lk.getLetajici();
        this.lk = lk;
        fireValueChanged("LK", stary);
    }

    public LetajiciSluzby2 getLp() {
        return lp;
    }

    public void setLp(LetajiciSluzby2 lp) {
        if(lp == this.lp) return;
        String stary = this.lp == null?"":this.lp.getLetajici();
        this.lp = lp;
        fireValueChanged("LP", stary);
    }

    public LetajiciSluzby2 getSd() {
        return sd;
    }

    public void setSd(LetajiciSluzby2 sd) {
        if(sd == this.sd) return;
        String stary = this.sd == null?"":this.sd.getLetajici();
        this.sd = sd;
        fireValueChanged("SD", stary);
    }

    public LetajiciSluzby2 getSk() {
        return sk;
    }

    public void setSk(LetajiciSluzby2 sk) {
        if(sk == this.sk) return;
        String stary = this.sk == null?"":this.sk.getLetajici();
        this.sk = sk;
        fireValueChanged("SK", stary);
    }

    public LetajiciSluzby2 getSp() {
        return sp;
    }

    public void setSp(LetajiciSluzby2 sp) {
       if(sp == this.sp) return;
        String stary = this.sp == null?"":this.sp.getLetajici();
        this.sp = sp;
        fireValueChanged("SP", stary);
    }
    
    public void fireValueChanged(String typSluzby, String starySlouzici){
        SluzbyValueChangeEvent svche = new SluzbyValueChangeEvent(this, typSluzby, starySlouzici);
        ModelListenerFactory.valueChanged(svche);
    }

}
