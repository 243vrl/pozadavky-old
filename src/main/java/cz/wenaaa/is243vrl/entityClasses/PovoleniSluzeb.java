/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.entityClasses;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vena
 */
@Entity
@Table(name = "povoleni_sluzeb")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PovoleniSluzeb.findAll", query = "SELECT p FROM PovoleniSluzeb p"),
    @NamedQuery(name = "PovoleniSluzeb.findByPovoleno", query = "SELECT p FROM PovoleniSluzeb p WHERE p.povoleno = :povoleno"),
    @NamedQuery(name = "PovoleniSluzeb.findByIdPovoleniSluzeb", query = "SELECT p FROM PovoleniSluzeb p WHERE p.idPovoleniSluzeb = :idPovoleniSluzeb")})
public class PovoleniSluzeb implements Serializable {
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Column(name = "povoleno")
    private boolean povoleno;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_povoleni_sluzeb")
    private Integer idPovoleniSluzeb;
    @Column(name = "letajici")
    private String letajici;
    @Column(name = "typ_letadla")
    private String typLetadla;
    @Column(name = "typ_sluzby")
    private String typSluzby;

    public PovoleniSluzeb() {
    }

    public PovoleniSluzeb(Integer idPovoleniSluzeb) {
        this.idPovoleniSluzeb = idPovoleniSluzeb;
    }

    public PovoleniSluzeb(Integer idPovoleniSluzeb, boolean povoleno) {
        this.idPovoleniSluzeb = idPovoleniSluzeb;
        this.povoleno = povoleno;
    }

    public boolean getPovoleno() {
        return povoleno;
    }

    public void setPovoleno(boolean povoleno) {
        this.povoleno = povoleno;
    }

    public Integer getIdPovoleniSluzeb() {
        return idPovoleniSluzeb;
    }

    public void setIdPovoleniSluzeb(Integer idPovoleniSluzeb) {
        this.idPovoleniSluzeb = idPovoleniSluzeb;
    }

    public String getLetajici() {
        return letajici;
    }

    public void setLetajici(String letajici) {
        this.letajici = letajici;
    }

    public String getTypLetadla() {
        return typLetadla;
    }

    public void setTypLetadla(String typLetadla) {
        this.typLetadla = typLetadla;
    }

    public String getTypSluzby() {
        return typSluzby;
    }

    public void setTypSluzby(String typSluzby) {
        this.typSluzby = typSluzby;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPovoleniSluzeb != null ? idPovoleniSluzeb.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PovoleniSluzeb)) {
            return false;
        }
        PovoleniSluzeb other = (PovoleniSluzeb) object;
        if ((this.idPovoleniSluzeb == null && other.idPovoleniSluzeb != null) || (this.idPovoleniSluzeb != null && !this.idPovoleniSluzeb.equals(other.idPovoleniSluzeb))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.seznam.wenaaa.is243vrl.entityClasses.PovoleniSluzeb[ idPovoleniSluzeb=" + idPovoleniSluzeb + " ]";
    }
    
}
