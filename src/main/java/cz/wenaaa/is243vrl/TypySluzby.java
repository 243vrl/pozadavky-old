/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vena
 */
public enum TypySluzby {

    LK("LK",false),
    LD("LD",false),
    LP("LP",true),
    SK("SK",false),
    SD("SD",false),
    SP("SP",true),
    HK("HK",false),
    HD("HD",false),
    HP("HP",true);

    private final String sTypSluzby;
    private final boolean palubarsky;

    private TypySluzby(String sTypSluzby, boolean palubarsky) {
        this.sTypSluzby = sTypSluzby;
        this.palubarsky = palubarsky;
    }

    public String getsTypSluzby() {
        return sTypSluzby;
    }

    public static List<TypySluzby> getPilotsTypySluzby() {
        List<TypySluzby> vratka = new ArrayList<>();
        for(TypySluzby typ:TypySluzby.values()){
            if(!typ.palubarsky){
                vratka.add(typ);
            }
        }
        return vratka;
    }
    
    public static List<TypySluzby> getFlightEngineersTypySluzby() {
        List<TypySluzby> vratka = new ArrayList<>();
        for(TypySluzby typ:TypySluzby.values()){
            if(typ.palubarsky){
                vratka.add(typ);
            }
        }
        return vratka;
    }
}
