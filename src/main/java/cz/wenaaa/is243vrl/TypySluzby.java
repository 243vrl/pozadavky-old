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

    LK("LK",false,"L"),
    LD("LD",false,"L"),
    LP("LP",true,"L"),
    SK("SK",false,"S"),
    SD("SD",false,"S"),
    SP("SP",true,"S"),
    BK("BK",false,"B"),
    BD("BD",false,"B"),
    BP("BP",true,"B"),
    HK("HK",false,"H"),
    HD("HD",false,"H"),
    HP("HP",true,"H");

    public static List<TypySluzby> getFEPlan() {
        List<TypySluzby> vratka = new ArrayList<>();
        for(TypySluzby pol:getFlightEngineersTypySluzby()){
            if(pol.compareTo(SP)>0){
                continue;
            }
            vratka.add(pol);
        }
        return vratka;
    }

    public static List<TypySluzby> getPPlan() {
        List<TypySluzby> vratka = new ArrayList<>();
        for(TypySluzby pol:getPilotsTypySluzby()){
            if(pol.compareTo(SP)>0){
                continue;
            }
            vratka.add(pol);
        }
        return vratka;
    }

    private final String sTypSluzby;
    private final boolean palubarsky;
    private final String doPrehledu;

    private TypySluzby(String sTypSluzby, boolean palubarsky, String doPrehledu) {
        this.sTypSluzby = sTypSluzby;
        this.palubarsky = palubarsky;
        this.doPrehledu = doPrehledu;
    }

    public String getsTypSluzby() {
        return sTypSluzby;
    }

    public String getDoPrehledu() {
        return doPrehledu;
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
