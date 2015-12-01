/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.entityClasses;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
    @NamedQuery(name = "PovoleniSluzeb.findByLetajici", query = "SELECT p FROM PovoleniSluzeb p WHERE p.povoleniSluzebPK.letajici = :letajici"),
    @NamedQuery(name = "PovoleniSluzeb.findByTypLetadla", query = "SELECT p FROM PovoleniSluzeb p WHERE p.povoleniSluzebPK.typLetadla = :typLetadla"),
    @NamedQuery(name = "PovoleniSluzeb.findByTypSluzby", query = "SELECT p FROM PovoleniSluzeb p WHERE p.povoleniSluzebPK.typSluzby = :typSluzby"),
    @NamedQuery(name = "PovoleniSluzeb.findByPovoleno", query = "SELECT p FROM PovoleniSluzeb p WHERE p.povoleno = :povoleno")})
public class PovoleniSluzeb implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PovoleniSluzebPK povoleniSluzebPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "povoleno")
    private boolean povoleno;
    @JoinColumn(name = "letajici", referencedColumnName = "letajici", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private LetajiciSluzby letajiciSluzby;
    @JoinColumn(name = "typ_letadla", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TypyLetadel typyLetadel;
    @JoinColumn(name = "typ_sluzby", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TypySluzeb typySluzeb;

    public PovoleniSluzeb() {
    }

    public PovoleniSluzeb(PovoleniSluzebPK povoleniSluzebPK) {
        this.povoleniSluzebPK = povoleniSluzebPK;
    }

    public PovoleniSluzeb(PovoleniSluzebPK povoleniSluzebPK, boolean povoleno) {
        this.povoleniSluzebPK = povoleniSluzebPK;
        this.povoleno = povoleno;
    }

    public PovoleniSluzeb(String letajici, String typLetadla, String typSluzby) {
        this.povoleniSluzebPK = new PovoleniSluzebPK(letajici, typLetadla, typSluzby);
    }

    public PovoleniSluzebPK getPovoleniSluzebPK() {
        return povoleniSluzebPK;
    }

    public void setPovoleniSluzebPK(PovoleniSluzebPK povoleniSluzebPK) {
        this.povoleniSluzebPK = povoleniSluzebPK;
    }

    public boolean getPovoleno() {
        return povoleno;
    }

    public void setPovoleno(boolean povoleno) {
        this.povoleno = povoleno;
    }

    public LetajiciSluzby getLetajiciSluzby() {
        return letajiciSluzby;
    }

    public void setLetajiciSluzby(LetajiciSluzby letajiciSluzby) {
        this.letajiciSluzby = letajiciSluzby;
    }

    public TypyLetadel getTypyLetadel() {
        return typyLetadel;
    }

    public void setTypyLetadel(TypyLetadel typyLetadel) {
        this.typyLetadel = typyLetadel;
    }

    public TypySluzeb getTypySluzeb() {
        return typySluzeb;
    }

    public void setTypySluzeb(TypySluzeb typySluzeb) {
        this.typySluzeb = typySluzeb;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (povoleniSluzebPK != null ? povoleniSluzebPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PovoleniSluzeb)) {
            return false;
        }
        PovoleniSluzeb other = (PovoleniSluzeb) object;
        if ((this.povoleniSluzebPK == null && other.povoleniSluzebPK != null) || (this.povoleniSluzebPK != null && !this.povoleniSluzebPK.equals(other.povoleniSluzebPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.seznam.wenaaa.is243vrl.entityClasses.PovoleniSluzeb[ povoleniSluzebPK=" + povoleniSluzebPK + " ]";
    }
    
}
