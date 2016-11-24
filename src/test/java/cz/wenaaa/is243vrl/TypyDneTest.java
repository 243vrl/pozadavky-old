/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl;

import cz.wenaaa.is243vrl.TypyDne;
import static cz.wenaaa.is243vrl.TypyDne.NEDELE;
import static cz.wenaaa.is243vrl.TypyDne.PATEK;
import static cz.wenaaa.is243vrl.TypyDne.SOBOTA;
import static cz.wenaaa.is243vrl.TypyDne.VSEDNI;
import static cz.wenaaa.is243vrl.TypyDne.VSEDNI_DVOJSVATEK;
import static cz.wenaaa.is243vrl.TypyDne.VSEDNI_SVATEK;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vena
 */
public class TypyDneTest {

    public TypyDneTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of values method, of class TypyDne.
     */
    @Test
    public void testValues() {
        //System.out.println("values");
        TypyDne[] expResult = {VSEDNI_DVOJSVATEK, VSEDNI_SVATEK, SOBOTA, NEDELE, PATEK, VSEDNI};
        TypyDne[] result = TypyDne.values();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getTypDne method, of class TypyDne.
     */
    @Test
    public void testGetTypDne() {
        
        GregorianCalendar _gc = new GregorianCalendar(2015, Calendar.DECEMBER, 23);
        TypyDne[] expResult = {
            VSEDNI_SVATEK,
            VSEDNI_DVOJSVATEK,
            VSEDNI_SVATEK,
            SOBOTA,
            NEDELE,
            VSEDNI
        };
        for (int i = 0; i < expResult.length; i++) {
            TypyDne result = TypyDne.getTypDne(_gc);
            assertEquals(expResult[i], result);
            _gc.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

}
