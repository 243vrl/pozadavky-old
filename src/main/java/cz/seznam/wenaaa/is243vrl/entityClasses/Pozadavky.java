/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.entityClasses;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vena
 */
@Entity
@Table(name = "pozadavky")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pozadavky.findAll", query = "SELECT p FROM Pozadavky p"),
    @NamedQuery(name = "Pozadavky.findByDatum", query = "SELECT p FROM Pozadavky p WHERE p.pozadavkyPK.datum = :datum"),
    @NamedQuery(name = "Pozadavky.findByLetajici", query = "SELECT p FROM Pozadavky p WHERE p.pozadavkyPK.letajici = :letajici")})
public class Pozadavky implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PozadavkyPK pozadavkyPK;
    @JoinColumn(name = "letajici", referencedColumnName = "letajici", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private LetajiciSluzby letajiciSluzby;
    @JoinColumn(name = "pozadavek", referencedColumnName = "pozadavek")
    @ManyToOne(optional = false)
    private TypyPozadavku pozadavek;

    public Pozadavky() {
    }

    public Pozadavky(PozadavkyPK pozadavkyPK) {
        this.pozadavkyPK = pozadavkyPK;
    }

    public Pozadavky(Date datum, String letajici) {
        this.pozadavkyPK = new PozadavkyPK(datum, letajici);
    }

    public PozadavkyPK getPozadavkyPK() {
        return pozadavkyPK;
    }

    public void setPozadavkyPK(PozadavkyPK pozadavkyPK) {
        this.pozadavkyPK = pozadavkyPK;
    }

    public LetajiciSluzby getLetajiciSluzby() {
        return letajiciSluzby;
    }

    public void setLetajiciSluzby(LetajiciSluzby letajiciSluzby) {
        this.letajiciSluzby = letajiciSluzby;
    }

    public TypyPozadavku getPozadavek() {
        return pozadavek;
    }

    public void setPozadavek(TypyPozadavku pozadavek) {
        this.pozadavek = pozadavek;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pozadavkyPK != null ? pozadavkyPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pozadavky)) {
            return false;
        }
        Pozadavky other = (Pozadavky) object;
        if ((this.pozadavkyPK == null && other.pozadavkyPK != null) || (this.pozadavkyPK != null && !this.pozadavkyPK.equals(other.pozadavkyPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.seznam.wenaaa.is243vrl.entityClasses.Pozadavky[ pozadavkyPK=" + pozadavkyPK + " ]";
    }
    
}
