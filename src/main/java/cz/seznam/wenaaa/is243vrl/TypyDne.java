/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl;

import cz.seznam.wenaaa.utils.Kalendar;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author vena
 */
public enum TypyDne {
    VSEDNI_DVOJSVATEK,
    VSEDNI_SVATEK,
    SOBOTA,
    NEDELE,
    PATEK,
    VSEDNI;
    
    
    public static TypyDne getTypDne(GregorianCalendar _gc){
        TypyDne typDne = VSEDNI;
        GregorianCalendar gc = (GregorianCalendar)_gc.clone();
        switch(gc.get(Calendar.DAY_OF_WEEK)){
            case Calendar.MONDAY:
            case Calendar.TUESDAY:
            case Calendar.WEDNESDAY:
            case Calendar.THURSDAY:
                if(vsedniDvojSvatek(gc)){
                    typDne = VSEDNI_DVOJSVATEK;
                    break;
                }
                if(vsedniSvatek(gc)){
                    typDne = VSEDNI_SVATEK;
                    break;
                }
                break;
            case Calendar.FRIDAY:
                if(vsedniSvatek(gc)) {
                    typDne = VSEDNI_SVATEK;
                }
                else {
                    typDne = PATEK;
                }
                break;
            case Calendar.SATURDAY:
                typDne = SOBOTA;
                break;
            case Calendar.SUNDAY:
                if(vsedniSvatek(gc)){
                    typDne = VSEDNI_SVATEK;
                }
                else{
                    typDne = NEDELE;
                }
                break;
        }
        return typDne;
    }
    
    private static boolean vsedniDvojSvatek(GregorianCalendar pomGC){
        switch(pomGC.get(Calendar.DAY_OF_WEEK)){
            case Calendar.FRIDAY:
            case Calendar.SATURDAY:
            case Calendar.SUNDAY:
                return false;
        }
        if(!Kalendar.jeSvatek(pomGC)) return false;
        pomGC.add(Calendar.DAY_OF_MONTH, 1);
        boolean vratka = Kalendar.jeSvatek(pomGC);
        pomGC.add(Calendar.DAY_OF_MONTH, -1);
        return vratka;
    }
    private static boolean vsedniSvatek(GregorianCalendar pomGC) {
        switch(pomGC.get(Calendar.DAY_OF_WEEK)){
            case Calendar.SATURDAY:
                return false;
            case Calendar.SUNDAY:
                pomGC.add(Calendar.DAY_OF_MONTH, 1);
                return Kalendar.jeSvatek(pomGC);
            case Calendar.FRIDAY:
                return Kalendar.jeSvatek(pomGC);
        }
        if(Kalendar.jeSvatek(pomGC)) return true;
        pomGC.add(Calendar.DAY_OF_MONTH, 1);
        boolean vratka = Kalendar.jeSvatek(pomGC);
        pomGC.add(Calendar.DAY_OF_MONTH, -1);
        return vratka;
    }
}
