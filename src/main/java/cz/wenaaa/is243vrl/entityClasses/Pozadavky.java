/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.entityClasses;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "pozadavky")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pozadavky.findAll", query = "SELECT p FROM Pozadavky p"),
    @NamedQuery(name = "Pozadavky.naMesicProLetajiciho", query = "SELECT p FROM Pozadavky p WHERE (p.datum BETWEEN :od AND :do) AND p.letajici = :letajici AND p.pozadavek IN (SELECT t.pozadavek FROM TypyPozadavku t WHERE t.useracces = :ua) ORDER BY p.datum ASC"),
    @NamedQuery(name = "Pozadavky.naMesic", query = "SELECT p FROM Pozadavky p WHERE (p.datum BETWEEN :od AND :do) AND p.pozadavek IN (SELECT t.pozadavek FROM TypyPozadavku t WHERE t.useracces = :ua)"),
    @NamedQuery(name = "Pozadavky.findByDatumAndPozadavek", query = "SELECT p FROM Pozadavky p WHERE p.datum = :datum AND p.pozadavek = :pozadavek"),
    @NamedQuery(name = "Pozadavky.findByDatum", query = "SELECT p FROM Pozadavky p WHERE p.datum = :datum"),
    @NamedQuery(name = "Pozadavky.findByIdPozadavky", query = "SELECT p FROM Pozadavky p WHERE p.idPozadavky = :idPozadavky")})
public class Pozadavky implements Serializable {
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Column(name = "datum")
    @Temporal(TemporalType.DATE)
    private Date datum;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_pozadavky")
    private Integer idPozadavky;
    @Column(name = "pozadavek")
    private String pozadavek;
    @Column(name = "letajici")
    private String letajici;

    public Pozadavky() {
    }

    public Pozadavky(Integer idPozadavky) {
        this.idPozadavky = idPozadavky;
    }

    public Pozadavky(Integer idPozadavky, Date datum) {
        this.idPozadavky = idPozadavky;
        this.datum = datum;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public Integer getIdPozadavky() {
        return idPozadavky;
    }

    public void setIdPozadavky(Integer idPozadavky) {
        this.idPozadavky = idPozadavky;
    }

    public String getPozadavek() {
        return pozadavek;
    }

    public void setPozadavek(String pozadavek) {
        this.pozadavek = pozadavek;
    }

    public String getLetajici() {
        return letajici;
    }

    public void setLetajici(String letajici) {
        this.letajici = letajici;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPozadavky != null ? idPozadavky.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pozadavky)) {
            return false;
        }
        Pozadavky other = (Pozadavky) object;
        if ((this.idPozadavky == null && other.idPozadavky != null) || (this.idPozadavky != null && !this.idPozadavky.equals(other.idPozadavky))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.seznam.wenaaa.is243vrl.entityClasses.Pozadavky[ idPozadavky=" + idPozadavky + " ]";
    }
    
}
