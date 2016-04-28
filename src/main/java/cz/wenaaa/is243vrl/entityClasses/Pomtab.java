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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vena
 */
@Entity
@Table(name = "pomtab")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pomtab.findAll", query = "SELECT p FROM Pomtab p"),
    @NamedQuery(name = "Pomtab.findByPozadavkyodpiloti", query = "SELECT p FROM Pomtab p WHERE p.pozadavkyodpiloti = :pozadavkyodpiloti"),
    @NamedQuery(name = "Pomtab.findById", query = "SELECT p FROM Pomtab p WHERE p.id = :id"),
    @NamedQuery(name = "Pomtab.findByPozadavkyodpalubaci", query = "SELECT p FROM Pomtab p WHERE p.pozadavkyodpalubaci = :pozadavkyodpalubaci")})
public class Pomtab implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "pozadavkyodpiloti")
    @Temporal(TemporalType.DATE)
    private Date pozadavkyodpiloti;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "pozadavkyodpalubaci")
    @Temporal(TemporalType.DATE)
    private Date pozadavkyodpalubaci;

    public Pomtab() {
    }

    public Pomtab(Integer id) {
        this.id = id;
    }

    public Date getPozadavkyodpiloti() {
        return pozadavkyodpiloti;
    }

    public void setPozadavkyodpiloti(Date pozadavkyodpiloti) {
        this.pozadavkyodpiloti = pozadavkyodpiloti;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getPozadavkyodpalubaci() {
        return pozadavkyodpalubaci;
    }

    public void setPozadavkyodpalubaci(Date pozadavkyodpalubaci) {
        this.pozadavkyodpalubaci = pozadavkyodpalubaci;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pomtab)) {
            return false;
        }
        Pomtab other = (Pomtab) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.wenaaa.is243vrl.entityClasses.Pomtab[ id=" + id + " ]";
    }
    
}
