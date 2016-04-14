/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl;

import cz.seznam.wenaaa.is243vrl.entityClasses.LetajiciSluzby2;
import cz.seznam.wenaaa.is243vrl.entityClasses.Sluzby;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author vena
 */
public class SluzboDenTest {    
    
    private static final String LK = "LK";
    private static final String LD = "LD";
    private static final String JA = "JA";
    private static final String ON = "ON";
    private SluzboDen instance;
    @Mock
    private Slouzici slouzici;
    @Mock
    private Slouzici slCizi;
    @Mock
    private Sluzby sluzbaVlastni;
    @Mock
    private Sluzby sluzbaCizi;
    @Mock
    private LetajiciSluzby2 lsVlastni;
    @Mock
    private LetajiciSluzby2 lsCizi;
    
    public SluzboDenTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getTypsluzby method, of class SluzboDen.
     */
    @Test
    public void testSetGetTypsluzby() {
        instance = new SluzboDen(1, LK, null, slouzici);
        String expResult = LK;
        String result = instance.getTypsluzby();
        assertEquals(expResult, result);
        instance.setTypsluzby(LD);
        result = instance.getTypsluzby();
        expResult = LD;
        assertEquals(expResult, result);
    }

    /**
     * Test of getNahoru method, of class SluzboDen.
     */
    @Test
    public void testGetNahoru() {
        SluzboDen nahoru = new SluzboDen(3, LK, null, slouzici);
        SluzboDen expResult = nahoru;
        instance = new SluzboDen(1, LK, nahoru, slouzici);
        SluzboDen result = instance.getNahoru();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSlouzici method, of class SluzboDen.
     */
    @Test
    public void testGetSetSlouzici() {
        Slouzici sl = new Slouzici(JA, LK, LK);
        instance = new SluzboDen(1, LK, null, sl);
        Slouzici expResult = sl;
        Slouzici result = instance.getSlouzici();
        assertEquals(expResult, result);
        sl = new Slouzici(JA + "on", LK, LK);
        instance.setSlouzici(sl);
        expResult = sl;
        result = instance.getSlouzici();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDen method, of class SluzboDen.
     */
    @Test
    public void testGetDen() {
        instance = new SluzboDen(1, LK, null, slouzici);
        int expResult = 1;
        int result = instance.getDen();
        assertEquals(expResult, result);
    }

    /**
     * Test of isValid method, of class SluzboDen.
     */
    @Test
    public void testIsValidNaPozadavek() {
        final int den = 2;
        instance = new SluzboDen(den, LK, null, slouzici);
        when(slouzici.getPlneVolneDny()).thenReturn(0b001100L, 0b001000L);
        boolean expResult = false;
        boolean result = instance.isValid();
        assertEquals(expResult, result);
        expResult = true;
        result = instance.isValid();
        assertEquals(expResult, result);
    }

    /**
     * Test of isValid method, of class SluzboDen.
     */
    @Test
    public void testIsValidNaTenSamyDenDenPoDenPred() {
        when(slouzici.getPlneVolneDny()).thenReturn(0L);
        SluzboDen sd = vytvorstrukturu(0b000100);
        final int den = 3;
        instance = new SluzboDen(den, LK, sd, slouzici);
        boolean expresult = false;
        boolean result = instance.isValid();
        assertEquals("nemuze na ten samy den", expresult, result);
        
        instance = new SluzboDen(den + 1, LK, sd, slouzici);
        expresult = false;
        result = instance.isValid();
        assertEquals("nemuze na den po", expresult, result);
        
        instance = new SluzboDen(den - 1, LK, sd, slouzici);
        expresult = false;
        result = instance.isValid();
        assertEquals("nemuze na den pred", expresult, result);
        
        instance = new SluzboDen(den - 2, LK, sd, slouzici);
        expresult = true;
        result = instance.isValid();
        assertEquals("muze na 2 dny pred", expresult, result);
        
        instance = new SluzboDen(den + 2, LK, sd, slouzici);
        expresult = true;
        result = instance.isValid();
        assertEquals("muze na 2 dny po", expresult, result);
    }

    /**
     * Test of isValid method, of class SluzboDen.
     */
    @Test
    public void testIsValidNaObdenkyVMesici() {
        when(slouzici.getPlneVolneDny()).thenReturn(0L);
        
        SluzboDen sd = vytvorstrukturu(0b0001010100);
        
        instance = new SluzboDen(1, LK, sd, slouzici);
        boolean expresult = false;
        boolean result = instance.isValid();
        assertEquals("nemuze na obden zacatek", expresult, result);
        
        instance = new SluzboDen(9, LK, sd, slouzici);
        expresult = false;
        result = instance.isValid();
        assertEquals("nemuze na obden konec", expresult, result);
        
        instance = new SluzboDen(10, LK, sd, slouzici);
        expresult = true;
        result = instance.isValid();
        assertEquals("muze - pauza dva dny", expresult, result);
        
        sd = vytvorstrukturu(0b000101000100);
        
        instance = new SluzboDen(5, LK, sd, slouzici);
        expresult = false;
        result = instance.isValid();
        assertEquals("nemuze na prostredni obdenku", expresult, result);
        
        sd = vytvorstrukturu(0b0001010000100);
        instance = new SluzboDen(5, LK, sd, slouzici);
        expresult = true;
        result = instance.isValid();
        assertEquals("muze na prostredni - dva dny pauza", expresult, result);
    }

    /**
     * Test of isValid method, of class SluzboDen.
     */
    @Test
    public void testIsValidNaObdenkyDvaZaSebouZMinulehoMesice() {
        when(slouzici.getPlneVolneDny()).thenReturn(0L);
        when(slouzici.getJmeno()).thenReturn(JA);
        when(slCizi.getJmeno()).thenReturn(ON);
        when(sluzbaVlastni.getLd()).thenReturn(lsVlastni);
        when(lsVlastni.getLetajici()).thenReturn(JA);
        
        SluzboDen sd = vytvorstrukturu(0b1000000);
        List<Sluzby> minMesiv = vytvorMinulyMesic(0b101010);
        SluzboDen pom = sd;
        while (pom.nahoru != null) {
            pom = pom.nahoru;
        }
        pom.setMinulyMesic(minMesiv);
        instance = new SluzboDen(1, LK, sd, slouzici);
        boolean expresult = false;
        boolean result = instance.isValid();
        verify(slouzici, Mockito.atLeastOnce()).getJmeno();
        verify(lsVlastni, Mockito.atLeastOnce()).getLetajici();
        assertEquals("nemuze na obden zacatek (1. novy + 3 obden v minulem)", expresult, result);
        
        sd = vytvorstrukturu(0b101000);
        minMesiv = vytvorMinulyMesic(0b1);
        pom = sd;
        while (pom.nahoru != null) {
            pom = pom.nahoru;
        }
        pom.setMinulyMesic(minMesiv);
        instance = new SluzboDen(2, LK, sd, slouzici);
        expresult = false;
        result = instance.isValid();
        verify(slouzici, Mockito.atLeastOnce()).getJmeno();
        verify(lsVlastni, Mockito.atLeastOnce()).getLetajici();
        assertEquals("nemuze na obden zacatek (2 4 6 novy + posledni v minulem)", expresult, result);
        
        sd = vytvorstrukturu(0b100);
        minMesiv = vytvorMinulyMesic(0b1010);
        pom = sd;
        while (pom.nahoru != null) {
            pom = pom.nahoru;
        }
        pom.setMinulyMesic(minMesiv);
        instance = new SluzboDen(1, LK, sd, slouzici);
        expresult = false;
        result = instance.isValid();
        verify(slouzici, Mockito.atLeastOnce()).getJmeno();
        verify(lsVlastni, Mockito.atLeastOnce()).getLetajici();
        assertEquals("nemuze na obden zacatek (1 3 novy + -2 -4 v minulem)", expresult, result);
        
        sd = vytvorstrukturu(0b1000);
        minMesiv = vytvorMinulyMesic(0b1010);
        pom = sd;
        while (pom.nahoru != null) {
            pom = pom.nahoru;
        }
        pom.setMinulyMesic(minMesiv);
        instance = new SluzboDen(2, LK, sd, slouzici);
        expresult = true;
        result = instance.isValid();
        verify(slouzici, Mockito.atLeastOnce()).getJmeno();
        verify(lsVlastni, Mockito.atLeastOnce()).getLetajici();
        assertEquals("muze na obden zacatek (2 4 novy + -2 -4 v minulem)", expresult, result);
        
        sd = vytvorstrukturu(0b1000000);
        minMesiv = vytvorMinulyMesic(0b000001);
        pom = sd;
        while (pom.nahoru != null) {
            pom = pom.nahoru;
        }
        pom.setMinulyMesic(minMesiv);
        instance = new SluzboDen(1, LK, sd, slouzici);
        expresult = false;
        result = instance.isValid();
        verify(slouzici, Mockito.atLeastOnce()).getJmeno();
        verify(lsVlastni, Mockito.atLeastOnce()).getLetajici();
        assertEquals("nemuze na posledniho+prvniho zacatek", expresult, result);
    }

    /**
     * Test of jeMensiNezParam method, of class SluzboDen.
     */
    @Test
    public void testJeMensiNezParam() {
        instance = new SluzboDen(1, LK, null, slouzici);
        SluzboDen pom = new SluzboDen(1, LK, null, slouzici);
        boolean expResult = false;
        assertEquals(expResult, instance.jeMensiNezParam(pom));
        instance = new SluzboDen(1, LK, pom, slouzici);
        assertEquals(expResult, pom.jeMensiNezParam(instance));
        assertEquals(expResult, instance.jeMensiNezParam(instance));
        expResult = true;
        assertEquals(expResult, instance.jeMensiNezParam(pom));
    }
    /**
     * Test of setMinulyMesic of class SluzboDen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetMinulymesicThrowsIllegalArgumentException(){
        Random rand = new Random();
        List<Sluzby> mm = vytvorMinulyMesic(rand.nextInt(0b111111));
        instance = new SluzboDen(1, LK, null, slouzici);
        Sluzby sl = mm.remove(0);
        instance.setMinulyMesic(mm);
    }
    /**
     * Test of setMinulyMesic of class SluzboDen
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetMinulymesicThrowsIllegalArgumentExceptionII(){
        Random rand = new Random();
        List<Sluzby> mm = vytvorMinulyMesic(rand.nextInt(0b111111));
        instance = new SluzboDen(1, LK, null, slouzici);
        mm.add(new Sluzby());
        instance.setMinulyMesic(mm);
    }
    /**
     * Test of getHloubka method, of class SluzboDen.
     */
    @Test
    public void testGetHloubka() {
        instance = new SluzboDen(1, LK, null, slouzici);
        SluzboDen pom = new SluzboDen(1, LK, instance, slouzici);
        assertEquals(0, instance.getHloubka());
        assertEquals(1, pom.getHloubka());
    }

    /**
     * Test of toStringII method, of class SluzboDen.
     */
    @Test
    public void testToStringII() {
        final int den = 3;
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH, 1);
        gc.set(Calendar.DAY_OF_MONTH, den);
        instance = new SluzboDen(den, LK, null, slouzici);
        String expected = String.format("%s:%d", LK, gc.get(Calendar.DAY_OF_MONTH));
        String result = String.format("%s:%d", instance.typsluzby, instance.datum.get(Calendar.DAY_OF_MONTH));
        assertEquals(expected, result);
    }

    /**
     * Test of toString method, of class SluzboDen.
     */
    @Test
    public void testToString() {
        final int den = 3;
        when(slouzici.toString()).thenReturn(JA);
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.add(Calendar.MONTH, 1);
        gc.set(Calendar.DAY_OF_MONTH, den);
        instance = new SluzboDen(den, LK, null, slouzici);
        String expected = "SluzboDen{" + new SimpleDateFormat("dd").format(gc.getTime()) + ", " + LK + ", " + slouzici + "," + 0 + '}';
        String result = "SluzboDen{" + new SimpleDateFormat("dd").format(instance.datum.getTime()) + ", " + instance.typsluzby + ", " + instance.slouzici + "," + instance.hloubka + '}';
        assertEquals(expected, result);
    }

    /**
     * Test of hashCode method, of class SluzboDen.
     */
    @Test
    public void testHashCode() {
        
    }

    /**
     * Test of equals method, of class SluzboDen.
     */
    @Test
    public void testEquals() {
        
    }

    /**
     * Test of getMaxsluzebpresmiru method, of class SluzboDen.
     */
    @Test
    public void testGetMaxsluzebpresmiru() {
        
    }

    /**
     * Test of getMaxpocetsvatku method, of class SluzboDen.
     */
    @Test
    public void testGetMaxpocetsvatku() {
        
    }

    /**
     * Test of getMaxpocetsobot method, of class SluzboDen.
     */
    @Test
    public void testGetMaxpocetsobot() {
        
    }

    /**
     * Test of getMaxpocetnedel method, of class SluzboDen.
     */
    @Test
    public void testGetMaxpocetnedel() {
        
    }

    /**
     * Test of getMaxpocetpatku method, of class SluzboDen.
     */
    @Test
    public void testGetMaxpocetpatku() {
        
    }
    
    private SluzboDen vytvorstrukturu(int dnykdyslouzi) {
        SluzboDen sd = null;
        int i = 1;
        while (dnykdyslouzi != 0) {
            SluzboDen pom = null;
            if ((dnykdyslouzi & 1) == 1) {
                pom = new SluzboDen(i++, LK, sd, slouzici);
            } else {
                pom = new SluzboDen(i++, LK, sd, slCizi);
            }
            sd = pom;
            dnykdyslouzi >>= 1;
        }
        return sd;
    }

    /**
     *
     * @param slouzi v binarni tvaru nejnizsi bit je posledni sluzba v mesici
     * @return
     */
    private List<Sluzby> vytvorMinulyMesic(int slouzi) {
        List<Sluzby> vratka = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            if ((slouzi & 1) == 1) {
                vratka.add(sluzbaVlastni);
            } else {
                vratka.add(sluzbaCizi);
            }
            slouzi >>= 1;
        }
        return vratka;
    }
}
