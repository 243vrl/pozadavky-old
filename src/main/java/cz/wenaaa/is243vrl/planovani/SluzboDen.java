/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.planovani;

import cz.wenaaa.is243vrl.TypyDne;
import cz.wenaaa.is243vrl.TypySluzby;
import java.util.GregorianCalendar;

/**
 *
 * @author vena
 */
class SluzboDen {
    
    GregorianCalendar datum;
    TypyDne typdne;
    TypySluzby typsluzby;

    public SluzboDen(GregorianCalendar datum, TypySluzby typsluzby) {
        this.datum = datum;
        this.typsluzby = typsluzby;
        setTypDne();
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
    
    
}
