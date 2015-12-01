/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.entityClasses;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vena
 */
@Entity
@Table(name = "typy_pozadavku")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TypyPozadavku.findAll", query = "SELECT t FROM TypyPozadavku t"),
    @NamedQuery(name = "TypyPozadavku.findByPozadavek", query = "SELECT t FROM TypyPozadavku t WHERE t.pozadavek = :pozadavek"),
    @NamedQuery(name = "TypyPozadavku.findByPopis", query = "SELECT t FROM TypyPozadavku t WHERE t.popis = :popis"),
    @NamedQuery(name = "TypyPozadavku.findByUseracces", query = "SELECT t FROM TypyPozadavku t WHERE t.useracces = :useracces"),
    @NamedQuery(name = "TypyPozadavku.findByHeadacces", query = "SELECT t FROM TypyPozadavku t WHERE t.headacces = :headacces"),
    @NamedQuery(name = "TypyPozadavku.findByScheduleracces", query = "SELECT t FROM TypyPozadavku t WHERE t.scheduleracces = :scheduleracces")})
public class TypyPozadavku implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "pozadavek")
    private String pozadavek;
    @Size(max = 2147483647)
    @Column(name = "popis")
    private String popis;
    @Basic(optional = false)
    @NotNull
    @Column(name = "useracces")
    private boolean useracces;
    @Basic(optional = false)
    @NotNull
    @Column(name = "headacces")
    private boolean headacces;
    @Basic(optional = false)
    @NotNull
    @Column(name = "scheduleracces")
    private boolean scheduleracces;

    public TypyPozadavku() {
    }

    public TypyPozadavku(String pozadavek) {
        this.pozadavek = pozadavek;
    }

    public TypyPozadavku(String pozadavek, boolean useracces, boolean headacces, boolean scheduleracces) {
        this.pozadavek = pozadavek;
        this.useracces = useracces;
        this.headacces = headacces;
        this.scheduleracces = scheduleracces;
    }

    public String getPozadavek() {
        return pozadavek;
    }

    public void setPozadavek(String pozadavek) {
        this.pozadavek = pozadavek;
    }

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }

    public boolean getUseracces() {
        return useracces;
    }

    public void setUseracces(boolean useracces) {
        this.useracces = useracces;
    }

    public boolean getHeadacces() {
        return headacces;
    }

    public void setHeadacces(boolean headacces) {
        this.headacces = headacces;
    }

    public boolean getScheduleracces() {
        return scheduleracces;
    }

    public void setScheduleracces(boolean scheduleracces) {
        this.scheduleracces = scheduleracces;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pozadavek != null ? pozadavek.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TypyPozadavku)) {
            return false;
        }
        TypyPozadavku other = (TypyPozadavku) object;
        if ((this.pozadavek == null && other.pozadavek != null) || (this.pozadavek != null && !this.pozadavek.equals(other.pozadavek))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.seznam.wenaaa.is243vrl.entityClasses.TypyPozadavku[ pozadavek=" + pozadavek + " ]";
    }
    
}
