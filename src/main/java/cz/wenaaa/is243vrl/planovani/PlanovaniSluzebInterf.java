/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.planovani;

import cz.wenaaa.is243vrl.TypySluzby;
import cz.wenaaa.is243vrl.entityClasses.TypySluzeb;
import java.util.GregorianCalendar;

/**
 *
 * @author vena
 */
public interface PlanovaniSluzebInterf {

    String getText();

    boolean isNaplanovano();

    boolean isPlanuji();

    void naplanuj(GregorianCalendar gc, boolean[] dnysvozu);

    int[] pocetSluzeb(String letajici);

    void setNaplanovanaSluzba(String sluzba, String jmeno, int den);
    
    TypySluzby  getNaplanovanaSluzba(String slouzici, int den);

    void setPrerusit(boolean prerusit);

    void ulozNaplanovane();
    
}
