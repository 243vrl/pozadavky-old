/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.planovani;

import cz.wenaaa.is243vrl.TypyDne;
import cz.wenaaa.is243vrl.TypySluzby;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Objects;

/**
 *
 * @author vena
 */
class SluzboDen {
    
    GregorianCalendar datum;
    TypyDne typdne;
    TypySluzby typsluzby;
    Slouzici slouzici;
    private final int kolikLidiMuze;

    public SluzboDen(GregorianCalendar datum, TypySluzby typsluzby, int kolikLidiMuze) {
        this.datum = datum;
        this.typsluzby = typsluzby;
        this.kolikLidiMuze = kolikLidiMuze;
        slouzici = null;
        setTypDne();
    }

    public Slouzici getSlouzici() {
        return slouzici;
    }

    public void setSlouzici(Slouzici slouzici) {
        this.slouzici = slouzici;
    }

    

    public GregorianCalendar getDatum() {
        return datum;
    }

    public TypyDne getTypdne() {
        return typdne;
    }

    public TypySluzby getTypsluzby() {
        return typsluzby;
    }

    private void setTypDne() {
        typdne = TypyDne.getTypDne(datum);
    }

    public int getKolikLidiMuze() {
        return kolikLidiMuze;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.datum);
        hash = 47 * hash + Objects.hashCode(this.typdne);
        hash = 47 * hash + Objects.hashCode(this.typsluzby);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SluzboDen other = (SluzboDen) obj;
        if (!Objects.equals(this.datum, other.datum)) {
            return false;
        }
        if (this.typdne != other.typdne) {
            return false;
        }
        if (this.typsluzby != other.typsluzby) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SluzboDen{" + "datum=" + new SimpleDateFormat("yy/MMMM/dd").format(datum.getTime()) + ", typdne=" + typdne + ", typsluzby=" + typsluzby + ", slouzici=" + slouzici.getJmeno() + '}';
    }
    
    
    
}
