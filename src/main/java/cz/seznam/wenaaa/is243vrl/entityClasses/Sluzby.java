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
    @NamedQuery(name = "Sluzby.findByDatum", query = "SELECT s FROM Sluzby s WHERE s.datum = :datum")})
public class Sluzby implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "datum")
    @Temporal(TemporalType.DATE)
    private Date datum;
    @JoinColumn(name = "ld", referencedColumnName = "letajici")
    @ManyToOne
    private LetajiciSluzby ld;
    @JoinColumn(name = "lk", referencedColumnName = "letajici")
    @ManyToOne
    private LetajiciSluzby lk;
    @JoinColumn(name = "lp", referencedColumnName = "letajici")
    @ManyToOne
    private LetajiciSluzby lp;
    @JoinColumn(name = "sd", referencedColumnName = "letajici")
    @ManyToOne
    private LetajiciSluzby sd;
    @JoinColumn(name = "sk", referencedColumnName = "letajici")
    @ManyToOne
    private LetajiciSluzby sk;
    @JoinColumn(name = "sp", referencedColumnName = "letajici")
    @ManyToOne
    private LetajiciSluzby sp;

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

    public LetajiciSluzby getLd() {
        return ld;
    }

    public void setLd(LetajiciSluzby ld) {
        this.ld = ld;
    }

    public LetajiciSluzby getLk() {
        return lk;
    }

    public void setLk(LetajiciSluzby lk) {
        this.lk = lk;
    }

    public LetajiciSluzby getLp() {
        return lp;
    }

    public void setLp(LetajiciSluzby lp) {
        this.lp = lp;
    }

    public LetajiciSluzby getSd() {
        return sd;
    }

    public void setSd(LetajiciSluzby sd) {
        this.sd = sd;
    }

    public LetajiciSluzby getSk() {
        return sk;
    }

    public void setSk(LetajiciSluzby sk) {
        this.sk = sk;
    }

    public LetajiciSluzby getSp() {
        return sp;
    }

    public void setSp(LetajiciSluzby sp) {
        this.sp = sp;
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
    
}
