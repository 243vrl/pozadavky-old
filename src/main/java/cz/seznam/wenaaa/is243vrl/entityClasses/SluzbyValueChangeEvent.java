/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.entityClasses;

import java.util.Date;


/**
 *
 * @author vena
 */
public class SluzbyValueChangeEvent extends MyValueChangeEvent{
    
    private final Sluzby sluzba;
    private final String typSluzby;
    private final String starySlouzici;
    
    public SluzbyValueChangeEvent(Sluzby sluzba, String typSluzby, String starySlouzici) {
        super(sluzba.getClass().getName());
        this.sluzba = sluzba;
        this.typSluzby = typSluzby;
        this.starySlouzici = starySlouzici;
    }

    public Sluzby getSluzba() {
        return sluzba;
    }

    public String getTypSluzby() {
        return typSluzby;
    }

    public String getStarySlouzici() {
        return starySlouzici;
    }

    
}
