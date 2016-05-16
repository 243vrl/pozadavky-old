/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.entityClasses;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author vena
 */
@Entity
@Table(name = "typy_sluzeb")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TypySluzeb.findAll", query = "SELECT t FROM TypySluzeb t"),
    @NamedQuery(name = "TypySluzeb.findById", query = "SELECT t FROM TypySluzeb t WHERE t.id = :id"),
    @NamedQuery(name = "TypySluzeb.findByPopis", query = "SELECT t FROM TypySluzeb t WHERE t.popis = :popis")})
public class TypySluzeb implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "typSluzby")
    private Collection<PovoleniSluzeb> povoleniSluzebCollection;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "id")
    private String id;
    @Size(max = 2147483647)
    @Column(name = "popis")
    private String popis;
    
    public TypySluzeb() {
    }

    public TypySluzeb(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
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
        if (!(object instanceof TypySluzeb)) {
            return false;
        }
        TypySluzeb other = (TypySluzeb) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.seznam.wenaaa.is243vrl.entityClasses.TypySluzeb[ id=" + id + " ]";
    }

    @XmlTransient
    public Collection<PovoleniSluzeb> getPovoleniSluzebCollection() {
        return povoleniSluzebCollection;
    }

    public void setPovoleniSluzebCollection(Collection<PovoleniSluzeb> povoleniSluzebCollection) {
        this.povoleniSluzebCollection = povoleniSluzebCollection;
    }

    
}
