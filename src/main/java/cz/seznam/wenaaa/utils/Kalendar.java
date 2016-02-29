/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *
 * @author vena
 */
public class Kalendar {

    
    private Kalendar(){
        
    }
    
    public static boolean jePrestupny(int rok){
        if(rok%4 == 0){
            if(rok%100 == 0){
                return rok%400 == 0;
            }
            return true;
        }
        return false;
    }
    
    public static GregorianCalendar velikonocniNedele(int rok){
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.YEAR, rok);
        int zc = (rok % 19)+1;
        int je = (zc*11)%30;
        int st = rok/100 + 1;
        int so = (st-16)*3/4;
        int mo = (st-15)*8/25;
        int ge = (je-10-so+mo);
        while((ge>29)||(ge<0)){
            if (ge<0) ge += 30;
            if (ge>29) ge -= 30;
        }
        int pfm = 0;
        if(ge<=23){
            pfm = 44 - ge;
        }
        if(ge == 24){
            pfm = 49;
        }
        if(ge == 25){
            if(zc < 12){
                pfm = 49;
            }else{
                pfm = 48;
            }
        }
        if(ge>25) pfm = 74 - ge;
        if(pfm > 31){
            gc.set(Calendar.MONTH, Calendar.APRIL);
            gc.set(Calendar.DAY_OF_MONTH, pfm-31);
        }
        else{
            gc.set(Calendar.MONTH, Calendar.MARCH);
            gc.set(Calendar.DAY_OF_MONTH, pfm);
        }
        do{
            gc.add(Calendar.DAY_OF_MONTH, 1);
        }while(gc.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY);
        return gc;
    }
    
    public static boolean jeSvatek(GregorianCalendar gc){
        switch(gc.get(Calendar.MONTH)){
            case Calendar.JANUARY:
                if(gc.get(Calendar.DAY_OF_MONTH) == 1) return true;
                break;
            case Calendar.MARCH:
            case Calendar.APRIL:
                GregorianCalendar gc1 = velikonocniNedele(gc.get(Calendar.YEAR));
                gc1.add(Calendar.DAY_OF_MONTH, 1);
                if((gc.get(Calendar.DAY_OF_MONTH) == gc1.get(Calendar.DAY_OF_MONTH))&&(gc.get(Calendar.MONTH) == gc1.get(Calendar.MONTH))) return true;
                gc1.add(Calendar.DAY_OF_MONTH, -3);
                if((gc.get(Calendar.DAY_OF_MONTH) == gc1.get(Calendar.DAY_OF_MONTH))&&(gc.get(Calendar.MONTH) == gc1.get(Calendar.MONTH))) return true;
                break;
            case Calendar.MAY:
                if((gc.get(Calendar.DAY_OF_MONTH) == 1)||(gc.get(Calendar.DAY_OF_MONTH) == 8)) return true;
                break;
            case Calendar.JULY:
                if((gc.get(Calendar.DAY_OF_MONTH) == 5)||(gc.get(Calendar.DAY_OF_MONTH) == 6)) return true;
                break;
            case Calendar.SEPTEMBER:
                if(gc.get(Calendar.DAY_OF_MONTH) == 28) return true;
                break;
            case Calendar.OCTOBER:
                if(gc.get(Calendar.DAY_OF_MONTH) == 28) return true;
                break;
            case Calendar.NOVEMBER:
                if(gc.get(Calendar.DAY_OF_MONTH) == 17) return true;
                break;
            case Calendar.DECEMBER:
                if((gc.get(Calendar.DAY_OF_MONTH) > 23)&&(gc.get(Calendar.DAY_OF_MONTH) < 27)) return true;
                break;
        }
        return false;
    }
    public static int dnuVMesici(GregorianCalendar gc){
        return dnuVMesici(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1);
    }
    public static int dnuVMesici(int rok, int mesic){
        switch(mesic){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                if (jePrestupny(rok)){
                    return 29;
                }
                else{
                    return 28;
                }
        }
        return 30;
    }
    
}
