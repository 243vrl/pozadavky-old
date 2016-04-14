/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.entityClasses;

import cz.seznam.wenaaa.utils.HashedPasswordGenerator;
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
@Table(name = "users")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
    @NamedQuery(name = "Users.findByUsername", query = "SELECT u FROM Users u WHERE u.username = :username"),
    @NamedQuery(name = "Users.findByPasswd", query = "SELECT u FROM Users u WHERE u.passwd = :passwd")})
public class Users implements Serializable {
    /*@OneToOne(cascade = CascadeType.ALL, mappedBy = "users")
    private LetajiciSluzby2 letajiciSluzby2;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "users")
    private LetajiciSluzby2 letajiciSluzby;*/
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 260)
    @Column(name = "username")
    private String username;
    @Size(max = 260)
    @Column(name = "passwd")
    private String passwd;
    

    public Users() {
    }

    public Users(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        fireValueChanged();
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = HashedPasswordGenerator.generateHash(passwd);
        fireValueChanged();
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (username != null ? username.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Users)) {
            return false;
        }
        Users other = (Users) object;
        if ((this.username == null && other.username != null) || (this.username != null && !this.username.equals(other.username))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return username;
    }
    
    public void fireValueChanged(){
        UsersValueChangeEvent uvche = new UsersValueChangeEvent(this);
        ModelListenerFactory.valueChanged(uvche);
    }
/*
    public LetajiciSluzby getLetajiciSluzby() {
        return letajiciSluzby;
    }

    public void setLetajiciSluzby(LetajiciSluzby letajiciSluzby) {
        this.letajiciSluzby = letajiciSluzby;
    }

    public LetajiciSluzby2 getLetajiciSluzby2() {
        return letajiciSluzby2;
    }

    public void setLetajiciSluzby2(LetajiciSluzby2 letajiciSluzby2) {
        this.letajiciSluzby2 = letajiciSluzby2;
    }
*/
}
