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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import cz.wenaaa.is243vrl.planovani.Slouzici;
/**
 *
 * @author vena
 */
@Entity
@Table(name = "letajici_sluzby2")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LetajiciSluzby.findClassSlouziciPalubari", query = "SELECT new Slouzici(l.letajici, l.dojizdeni) FROM LetajiciSluzby2 l  WHERE l.poradi > 1000 AND l.poradi < 10000 ORDER BY l.poradi ASC"),
    @NamedQuery(name = "LetajiciSluzby.findClassSlouziciPiloti", query = "SELECT new Slouzici(l.letajici, l.dojizdeni) FROM LetajiciSluzby2 l  WHERE l.poradi < 1000 ORDER BY l.poradi ASC"),
    @NamedQuery(name = "LetajiciSluzby.findAll", query = "SELECT l FROM LetajiciSluzby2 l  ORDER BY l.poradi ASC"),
    @NamedQuery(name = "LetajiciSluzby.findPiloti", query = "SELECT l FROM LetajiciSluzby2 l WHERE l.poradi < 1000 ORDER BY l.poradi ASC"),
    @NamedQuery(name = "LetajiciSluzby.findPalubari", query = "SELECT l FROM LetajiciSluzby2 l WHERE l.poradi > 1000 AND l.poradi < 10000 ORDER BY l.poradi ASC"),
    @NamedQuery(name = "LetajiciSluzby.findPilotiByName", query = "SELECT l FROM LetajiciSluzby2 l WHERE l.poradi < 1000 ORDER BY l.letajici ASC"),
    @NamedQuery(name = "LetajiciSluzby.findPalubariByName", query = "SELECT l FROM LetajiciSluzby2 l WHERE l.poradi > 1000 AND l.poradi < 10000 ORDER BY l.letajici ASC"),
    @NamedQuery(name = "LetajiciSluzby.findByLetajici", query = "SELECT l FROM LetajiciSluzby2 l WHERE l.letajici = :letajici"),
    @NamedQuery(name = "LetajiciSluzby.findByPoradi", query = "SELECT l FROM LetajiciSluzby2 l WHERE l.poradi = :poradi"),
    @NamedQuery(name = "LetajiciSluzby.findByPocetSluzeb", query = "SELECT l FROM LetajiciSluzby2 l WHERE l.pocetSluzeb = :pocetSluzeb"),
    @NamedQuery(name = "LetajiciSluzby.findByPocetVsednichSvatku", query = "SELECT l FROM LetajiciSluzby2 l WHERE l.pocetVsednichSvatku = :pocetVsednichSvatku"),
    @NamedQuery(name = "LetajiciSluzby.findByPocetSobot", query = "SELECT l FROM LetajiciSluzby2 l WHERE l.pocetSobot = :pocetSobot"),
    @NamedQuery(name = "LetajiciSluzby.findByPocetNedeli", query = "SELECT l FROM LetajiciSluzby2 l WHERE l.pocetNedeli = :pocetNedeli"),
    @NamedQuery(name = "LetajiciSluzby.findByPocetPatku", query = "SELECT l FROM LetajiciSluzby2 l WHERE l.pocetPatku = :pocetPatku"),
    @NamedQuery(name = "LetajiciSluzby.findByDoLiniSvozem", query = "SELECT l FROM LetajiciSluzby2 l WHERE l.doLiniSvozem = :doLiniSvozem")})
public class LetajiciSluzby2 implements Serializable {
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
    private int pocetSluzeb;
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
    @Basic(optional = false)
    @NotNull
    @Column(name = "pocet_sluzeb_h120")
    private int pocetSluzebH120;
    @Basic(optional = false)
    @NotNull
    @Column(name = "pocet_vsednich_svatku_h120")
    private int pocetVsednichSvatkuH120;
    @Basic(optional = false)
    @NotNull
    @Column(name = "pocet_sobot_h120")
    private int pocetSobotH120;
    @Basic(optional = false)
    @NotNull
    @Column(name = "pocet_nedeli_h120")
    private int pocetNedeliH120;
    @Basic(optional = false)
    @NotNull
    @Column(name = "pocet_patku_h120")
    private int pocetPatkuH120;
    @JoinColumn(name = "dojizdeni", referencedColumnName = "dojizdeni")
    @ManyToOne
    private SchemataDojizdeni dojizdeni;
    @JoinColumn(name = "letajici", referencedColumnName = "username", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Users users;

    public LetajiciSluzby2() {
    }

    public LetajiciSluzby2(String letajici) {
        this.letajici = letajici;
    }

    public LetajiciSluzby2(String letajici, int pocetSluzeb, int pocetVsednichSvatku, int pocetSobot, int pocetNedeli, int pocetPatku, boolean doLiniSvozem, int pocetSluzebH120, int pocetVsednichSvatkuH120, int pocetSobotH120, int pocetNedeliH120, int pocetPatkuH120) {
        this.letajici = letajici;
        this.pocetSluzeb = pocetSluzeb;
        this.pocetVsednichSvatku = pocetVsednichSvatku;
        this.pocetSobot = pocetSobot;
        this.pocetNedeli = pocetNedeli;
        this.pocetPatku = pocetPatku;
        this.doLiniSvozem = doLiniSvozem;
        this.pocetSluzebH120 = pocetSluzebH120;
        this.pocetVsednichSvatkuH120 = pocetVsednichSvatkuH120;
        this.pocetSobotH120 = pocetSobotH120;
        this.pocetNedeliH120 = pocetNedeliH120;
        this.pocetPatkuH120 = pocetPatkuH120;
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

    public int getPocetSluzebH120() {
        return pocetSluzebH120;
    }

    public void setPocetSluzebH120(int pocetSluzebH120) {
        this.pocetSluzebH120 = pocetSluzebH120;
    }

    public int getPocetVsednichSvatkuH120() {
        return pocetVsednichSvatkuH120;
    }

    public void setPocetVsednichSvatkuH120(int pocetVsednichSvatkuH120) {
        this.pocetVsednichSvatkuH120 = pocetVsednichSvatkuH120;
    }

    public int getPocetSobotH120() {
        return pocetSobotH120;
    }

    public void setPocetSobotH120(int pocetSobotH120) {
        this.pocetSobotH120 = pocetSobotH120;
    }

    public int getPocetNedeliH120() {
        return pocetNedeliH120;
    }

    public void setPocetNedeliH120(int pocetNedeliH120) {
        this.pocetNedeliH120 = pocetNedeliH120;
    }

    public int getPocetPatkuH120() {
        return pocetPatkuH120;
    }

    public void setPocetPatkuH120(int pocetPatkuH120) {
        this.pocetPatkuH120 = pocetPatkuH120;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (letajici != null ? letajici.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LetajiciSluzby2)) {
            return false;
        }
        LetajiciSluzby2 other = (LetajiciSluzby2) object;
        if ((this.letajici == null && other.letajici != null) || (this.letajici != null && !this.letajici.equals(other.letajici))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        //return "cz.seznam.wenaaa.is243vrl.entityClasses.LetajiciSluzby2[ letajici=" + letajici + " ]";
        return letajici;
    }
    
}
