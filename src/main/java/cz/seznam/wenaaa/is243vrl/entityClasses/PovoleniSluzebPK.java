/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.entityClasses;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author vena
 */
@Embeddable
public class PovoleniSluzebPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "letajici")
    private String letajici;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "typ_letadla")
    private String typLetadla;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "typ_sluzby")
    private String typSluzby;

    public PovoleniSluzebPK() {
    }

    public PovoleniSluzebPK(String letajici, String typLetadla, String typSluzby) {
        this.letajici = letajici;
        this.typLetadla = typLetadla;
        this.typSluzby = typSluzby;
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
        hash += (letajici != null ? letajici.hashCode() : 0);
        hash += (typLetadla != null ? typLetadla.hashCode() : 0);
        hash += (typSluzby != null ? typSluzby.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PovoleniSluzebPK)) {
            return false;
        }
        PovoleniSluzebPK other = (PovoleniSluzebPK) object;
        if ((this.letajici == null && other.letajici != null) || (this.letajici != null && !this.letajici.equals(other.letajici))) {
            return false;
        }
        if ((this.typLetadla == null && other.typLetadla != null) || (this.typLetadla != null && !this.typLetadla.equals(other.typLetadla))) {
            return false;
        }
        if ((this.typSluzby == null && other.typSluzby != null) || (this.typSluzby != null && !this.typSluzby.equals(other.typSluzby))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.seznam.wenaaa.is243vrl.entityClasses.PovoleniSluzebPK[ letajici=" + letajici + ", typLetadla=" + typLetadla + ", typSluzby=" + typSluzby + " ]";
    }
    
}
