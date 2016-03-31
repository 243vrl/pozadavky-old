/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.entityClasses;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
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
@Table(name = "schemata_dojizdeni")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SchemataDojizdeni.findAll", query = "SELECT s FROM SchemataDojizdeni s"),
    @NamedQuery(name = "SchemataDojizdeni.findByDojizdeni", query = "SELECT s FROM SchemataDojizdeni s WHERE s.dojizdeni = :dojizdeni")})
public class SchemataDojizdeni implements Serializable {
    @OneToMany(mappedBy = "dojizdeni")
    private Collection<LetajiciSluzby2> letajiciSluzby2Collection;
 /*   @OneToMany(mappedBy = "dojizdeni")
    private Collection<LetajiciSluzby2> letajiciSluzbyCollection;*/
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "dojizdeni")
    private String dojizdeni;
    
    public SchemataDojizdeni() {
    }

    public SchemataDojizdeni(String dojizdeni) {
        this.dojizdeni = dojizdeni;
    }

    public String getDojizdeni() {
        return dojizdeni;
    }

    public void setDojizdeni(String dojizdeni) {
        this.dojizdeni = dojizdeni;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dojizdeni != null ? dojizdeni.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SchemataDojizdeni)) {
            return false;
        }
        SchemataDojizdeni other = (SchemataDojizdeni) object;
        if ((this.dojizdeni == null && other.dojizdeni != null) || (this.dojizdeni != null && !this.dojizdeni.equals(other.dojizdeni))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.seznam.wenaaa.is243vrl.entityClasses.SchemataDojizdeni[ dojizdeni=" + dojizdeni + " ]";
    }
/*
    @XmlTransient
    public Collection<LetajiciSluzby2> getLetajiciSluzbyCollection() {
        return letajiciSluzbyCollection;
    }

    public void setLetajiciSluzbyCollection(Collection<LetajiciSluzby2> letajiciSluzbyCollection) {
        this.letajiciSluzbyCollection = letajiciSluzbyCollection;
    }
*/
    @XmlTransient
    public Collection<LetajiciSluzby2> getLetajiciSluzby2Collection() {
        return letajiciSluzby2Collection;
    }

    public void setLetajiciSluzby2Collection(Collection<LetajiciSluzby2> letajiciSluzby2Collection) {
        this.letajiciSluzby2Collection = letajiciSluzby2Collection;
    }

}
