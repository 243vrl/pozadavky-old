/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.entityClasses;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
@Table(name = "letajici_sluzby")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LetajiciSluzby.findAll", query = "SELECT l FROM LetajiciSluzby l"),
    @NamedQuery(name = "LetajiciSluzby.findByLetajici", query = "SELECT l FROM LetajiciSluzby l WHERE l.letajici = :letajici"),
    @NamedQuery(name = "LetajiciSluzby.findByPoradi", query = "SELECT l FROM LetajiciSluzby l WHERE l.poradi = :poradi"),
    @NamedQuery(name = "LetajiciSluzby.findByPocetSluzeb", query = "SELECT l FROM LetajiciSluzby l WHERE l.pocetSluzeb = :pocetSluzeb"),
    @NamedQuery(name = "LetajiciSluzby.findByPocetVsednichSvatku", query = "SELECT l FROM LetajiciSluzby l WHERE l.pocetVsednichSvatku = :pocetVsednichSvatku"),
    @NamedQuery(name = "LetajiciSluzby.findByPocetSobot", query = "SELECT l FROM LetajiciSluzby l WHERE l.pocetSobot = :pocetSobot"),
    @NamedQuery(name = "LetajiciSluzby.findByPocetNedeli", query = "SELECT l FROM LetajiciSluzby l WHERE l.pocetNedeli = :pocetNedeli"),
    @NamedQuery(name = "LetajiciSluzby.findByPocetPatku", query = "SELECT l FROM LetajiciSluzby l WHERE l.pocetPatku = :pocetPatku"),
    @NamedQuery(name = "LetajiciSluzby.findByDoLiniSvozem", query = "SELECT l FROM LetajiciSluzby l WHERE l.doLiniSvozem = :doLiniSvozem")})
public class LetajiciSluzby implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "letajici")
    private String letajici;
    @Column(name = "poradi")
    private Integer poradi;
    @Basic(optional = false)
    @NotNull
    @Column(name = "pocet_sluzeb")
    private int pocetSluzeb = 1;
    @Basic(optional = false)
    @NotNull
    @Column(name = "pocet_vsednich_svatku")
    private int pocetVsednichSvatku;
    @Basic(optional = false)
    @NotNull
    @Column(name = "pocet_sobot")
    private int pocetSobot;
    @Basic(optional = false)
    @NotNull
    @Column(name = "pocet_nedeli")
    private int pocetNedeli;
    @Basic(optional = false)
    @NotNull
    @Column(name = "pocet_patku")
    private int pocetPatku;
    @Basic(optional = false)
    @NotNull
    @Column(name = "do_lini_svozem")
    private boolean doLiniSvozem;
    @JoinColumn(name = "dojizdeni", referencedColumnName = "dojizdeni")
    @ManyToOne
    private SchemataDojizdeni dojizdeni;
    @JoinColumn(name = "letajici", referencedColumnName = "username", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Users users;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "letajiciSluzby")
    private Collection<Pozadavky> pozadavkyCollection;
    @OneToMany(mappedBy = "ld")
    private Collection<Sluzby> sluzbyCollection;
    @OneToMany(mappedBy = "lk")
    private Collection<Sluzby> sluzbyCollection1;
    @OneToMany(mappedBy = "lp")
    private Collection<Sluzby> sluzbyCollection2;
    @OneToMany(mappedBy = "sd")
    private Collection<Sluzby> sluzbyCollection3;
    @OneToMany(mappedBy = "sk")
    private Collection<Sluzby> sluzbyCollection4;
    @OneToMany(mappedBy = "sp")
    private Collection<Sluzby> sluzbyCollection5;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "letajiciSluzby")
    private Collection<PovoleniSluzeb> povoleniSluzebCollection;

    public LetajiciSluzby() {
    }

    public LetajiciSluzby(String letajici) {
        this.letajici = letajici;
    }

    public LetajiciSluzby(String letajici, int pocetSluzeb, int pocetVsednichSvatku, int pocetSobot, int pocetNedeli, int pocetPatku, boolean doLiniSvozem) {
        this.letajici = letajici;
        this.pocetSluzeb = pocetSluzeb;
        this.pocetVsednichSvatku = pocetVsednichSvatku;
        this.pocetSobot = pocetSobot;
        this.pocetNedeli = pocetNedeli;
        this.pocetPatku = pocetPatku;
        this.doLiniSvozem = doLiniSvozem;
    }

    public String getLetajici() {
        return letajici;
    }

    public void setLetajici(String letajici) {
        this.letajici = letajici;
    }

    public Integer getPoradi() {
        return poradi;
    }

    public void setPoradi(Integer poradi) {
        this.poradi = poradi;
    }

    public int getPocetSluzeb() {
        return pocetSluzeb;
    }

    public void setPocetSluzeb(int pocetSluzeb) {
        this.pocetSluzeb = pocetSluzeb;
    }

    public int getPocetVsednichSvatku() {
        return pocetVsednichSvatku;
    }

    public void setPocetVsednichSvatku(int pocetVsednichSvatku) {
        this.pocetVsednichSvatku = pocetVsednichSvatku;
    }

    public int getPocetSobot() {
        return pocetSobot;
    }

    public void setPocetSobot(int pocetSobot) {
        this.pocetSobot = pocetSobot;
    }

    public int getPocetNedeli() {
        return pocetNedeli;
    }

    public void setPocetNedeli(int pocetNedeli) {
        this.pocetNedeli = pocetNedeli;
    }

    public int getPocetPatku() {
        return pocetPatku;
    }

    public void setPocetPatku(int pocetPatku) {
        this.pocetPatku = pocetPatku;
    }

    public boolean getDoLiniSvozem() {
        return doLiniSvozem;
    }

    public void setDoLiniSvozem(boolean doLiniSvozem) {
        this.doLiniSvozem = doLiniSvozem;
    }

    public SchemataDojizdeni getDojizdeni() {
        return dojizdeni;
    }

    public void setDojizdeni(SchemataDojizdeni dojizdeni) {
        this.dojizdeni = dojizdeni;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    @XmlTransient
    public Collection<Pozadavky> getPozadavkyCollection() {
        return pozadavkyCollection;
    }

    public void setPozadavkyCollection(Collection<Pozadavky> pozadavkyCollection) {
        this.pozadavkyCollection = pozadavkyCollection;
    }

    @XmlTransient
    public Collection<Sluzby> getSluzbyCollection() {
        return sluzbyCollection;
    }

    public void setSluzbyCollection(Collection<Sluzby> sluzbyCollection) {
        this.sluzbyCollection = sluzbyCollection;
    }

    @XmlTransient
    public Collection<Sluzby> getSluzbyCollection1() {
        return sluzbyCollection1;
    }

    public void setSluzbyCollection1(Collection<Sluzby> sluzbyCollection1) {
        this.sluzbyCollection1 = sluzbyCollection1;
    }

    @XmlTransient
    public Collection<Sluzby> getSluzbyCollection2() {
        return sluzbyCollection2;
    }

    public void setSluzbyCollection2(Collection<Sluzby> sluzbyCollection2) {
        this.sluzbyCollection2 = sluzbyCollection2;
    }

    @XmlTransient
    public Collection<Sluzby> getSluzbyCollection3() {
        return sluzbyCollection3;
    }

    public void setSluzbyCollection3(Collection<Sluzby> sluzbyCollection3) {
        this.sluzbyCollection3 = sluzbyCollection3;
    }

    @XmlTransient
    public Collection<Sluzby> getSluzbyCollection4() {
        return sluzbyCollection4;
    }

    public void setSluzbyCollection4(Collection<Sluzby> sluzbyCollection4) {
        this.sluzbyCollection4 = sluzbyCollection4;
    }

    @XmlTransient
    public Collection<Sluzby> getSluzbyCollection5() {
        return sluzbyCollection5;
    }

    public void setSluzbyCollection5(Collection<Sluzby> sluzbyCollection5) {
        this.sluzbyCollection5 = sluzbyCollection5;
    }

    @XmlTransient
    public Collection<PovoleniSluzeb> getPovoleniSluzebCollection() {
        return povoleniSluzebCollection;
    }

    public void setPovoleniSluzebCollection(Collection<PovoleniSluzeb> povoleniSluzebCollection) {
        this.povoleniSluzebCollection = povoleniSluzebCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (letajici != null ? letajici.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LetajiciSluzby)) {
            return false;
        }
        LetajiciSluzby other = (LetajiciSluzby) object;
        if ((this.letajici == null && other.letajici != null) || (this.letajici != null && !this.letajici.equals(other.letajici))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.seznam.wenaaa.is243vrl.entityClasses.LetajiciSluzby[ letajici=" + letajici + " ]";
    }
    
}
