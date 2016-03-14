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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vena
 */
@Entity
@Table(name = "zpravy")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Zpravy.findAll", query = "SELECT z FROM Zpravy z"),
    @NamedQuery(name = "Zpravy.findById", query = "SELECT z FROM Zpravy z WHERE z.id = :id"),
    @NamedQuery(name = "Zpravy.findByDatumOd", query = "SELECT z FROM Zpravy z WHERE z.datumOd = :datumOd"),
    @NamedQuery(name = "Zpravy.findByDatumDo", query = "SELECT z FROM Zpravy z WHERE z.datumDo = :datumDo"),
    @NamedQuery(name = "Zpravy.findByTextZpravy", query = "SELECT z FROM Zpravy z WHERE z.textZpravy = :textZpravy"),
    @NamedQuery(name = "Zpravy.findByAutomaticky", query = "SELECT z FROM Zpravy z WHERE z.automaticky = :automaticky"),
    @NamedQuery(name = "Zpravy.naMesic", query = "SELECT z FROM Zpravy z WHERE (z.automaticky = false) AND ((z.datumOd BETWEEN :od AND :do) OR (z.datumDo BETWEEN :od AND :do))")})
public class Zpravy implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "datum_od")
    @Temporal(TemporalType.DATE)
    private Date datumOd;
    @Column(name = "datum_do")
    @Temporal(TemporalType.DATE)
    private Date datumDo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "text_zpravy")
    private String textZpravy;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "podal")
    private String podal;
    @Basic(optional = false)
    @NotNull
    @Column(name = "automaticky")
    private boolean automaticky;

    public Zpravy() {
    }

    public Zpravy(Integer id) {
        this.id = id;
    }

    public Zpravy(Integer id, Date datumOd, String textZpravy, boolean automaticky) {
        this.id = id;
        this.datumOd = datumOd;
        this.textZpravy = textZpravy;
        this.automaticky = automaticky;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDatumOd() {
        return datumOd;
    }

    public void setDatumOd(Date datumOd) {
        this.datumOd = datumOd;
    }

    public Date getDatumDo() {
        return datumDo;
    }

    public void setDatumDo(Date datumDo) {
        this.datumDo = datumDo;
    }

    public String getTextZpravy() {
        return textZpravy;
    }

    public void setTextZpravy(String textZpravy) {
        this.textZpravy = textZpravy;
    }

    public boolean getAutomaticky() {
        return automaticky;
    }

    public void setAutomaticky(boolean automaticky) {
        this.automaticky = automaticky;
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
        if (!(object instanceof Zpravy)) {
            return false;
        }
        Zpravy other = (Zpravy) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.seznam.wenaaa.is243vrl.entityClasses.Zpravy[ id=" + id + " ]";
    }
    public String getPodal(){
        return this.podal;
    }
    public void setPodal(String loginName) {
        this.podal = loginName;
    }
    
}
