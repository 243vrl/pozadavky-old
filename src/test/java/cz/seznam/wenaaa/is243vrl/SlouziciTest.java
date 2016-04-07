/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;


/**
 *
 * @author vena
 */
public class SlouziciTest {
    
    private Slouzici instance;
    private static final String DOJIZDENI = "dojizdeni";
    private static final String SKUPINA = "skupina";
    private static final String JMENO = "jmeno";
    
    public SlouziciTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new Slouzici(JMENO, SKUPINA, DOJIZDENI);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of hashCode method, of class Slouzici.
     */
    @org.junit.Test
    public void testHashCodeReturnsNotNull() {
        int result = instance.hashCode();
        assertNotNull(result);
    }

    /**
     * Test of equals method, of class Slouzici.
     */
    @org.junit.Test
    public void testEquals() {
        Slouzici pom = null;
        //null returns false
        boolean result = instance.equals(pom);
        assertFalse("slouzici equals> null > false", result);
        
        //different class returns false
        Object obj = new Object();
        result = instance.equals(obj);
        assertFalse("slouzici equals> different class > false", result);
        
        //stejne jmeno > true
        pom = new Slouzici(JMENO, SKUPINA+"rrr", DOJIZDENI+"rrr");
        result = instance.equals(pom);
        assertTrue("slouzici equals> stejne jmeno > true", result);
        
        //ruzne jmeno > false
        pom = new Slouzici(JMENO+"rrr", SKUPINA, DOJIZDENI);
        result = instance.equals(pom);
        assertFalse("slouzici equals> ruzne jmeno > false", result);
    }

    /**
     * Test of pridejSluzbuDoSkupiny method, of class Slouzici.
     */
    @org.junit.Test
    public void testPridejSluzbuDoSkupiny() {
        String typSluzby = "typ";
        instance.pridejSluzbuDoSkupiny(typSluzby);
        String novaSkupina = instance.getSkupina();
        assertEquals("slouzici pridejSluzbuDoSkupiny> SKUPINA+\";\"+typ", SKUPINA+";"+typSluzby, novaSkupina);
    }

    /**
     * Test of getPocetPlnychDnu method, of class Slouzici.
     */
    @org.junit.Test
    public void testGetPocetPlnychDnu() {
       //all nulls
        instance.setPlneVolneDny(0);
        assertEquals("slouzici getPocetPlnychDnu > 0 > 0", 0, instance.getPocetPlnychDnu());
        //some ones
        long pvd = (long) 0b0010001000100010001000100010001000100010001000100010001000100010L;
        instance.setPlneVolneDny(pvd);
        assertEquals("slouzici getPocetPlnychDnu > v kazdem bytu 2 > 16", 16, instance.getPocetPlnychDnu());
        //all ones, also negative number
        pvd = (long) 0b1111111111111111111111111111111111111111111111111111111111111111L;
        instance.setPlneVolneDny(pvd);
        assertEquals("slouzici getPocetPlnychDnu > v kazdem bytu 2 > 32", 64, instance.getPocetPlnychDnu());
    }

    /**
     * Test of getDojizdeni method, of class Slouzici.
     */
    @org.junit.Test
    public void testGetDojizdeni() {
        assertEquals("slouzici getDojizdeni >  DOJIZDENI",DOJIZDENI, instance.getDojizdeni());
    }

    /**
     * Test of getPlneVolneDny method, of class Slouzici.
     */
    @org.junit.Test
    public void testGetSetPlneVolneDny() {
        long expResult = 0L;
        long result = instance.getPlneVolneDny();
        assertEquals("Slouzici getPlneVolneDny > neiniciovany > 0",expResult, result);
        instance.setPlneVolneDny(123L);
        result = instance.getPlneVolneDny();
        expResult = 123L;
        assertEquals("Slouzici getPlneVolneDny > setPlne... 123L > 123L",expResult, result);
    }

    /**
     * Test of getMaxPocetSluzeb method, of class Slouzici.
     */
    @org.junit.Test
    public void testGetMaxPocetSluzeb() {
        float expResult = 0.0F;
        float result = instance.getMaxPocetSluzeb();
        assertEquals(expResult, result, 0.0);
        instance.setMaxPocetSluzeb(5.5F);
        expResult = 5.5F;
        result = instance.getMaxPocetSluzeb();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getPlanujSluzeb method, of class Slouzici.
     */
    @org.junit.Test
    public void testGetPlanujSluzeb() {
        float expResult = 0.0F;
        float result = instance.getPlanujSluzeb();
        assertEquals(expResult, result, 0.0);
        instance.setPlanujSluzeb(5.5F);
        expResult = 5.5F;
        result = instance.getPlanujSluzeb();
        assertEquals(expResult, result, 0.0);
        //pro skupinu "" ma byt 9 sluzeb
        Slouzici pom = new Slouzici(JMENO, "", DOJIZDENI);
        result = pom.getPlanujSluzeb();
        expResult = 9F;
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getJmeno method, of class Slouzici.
     */
    @org.junit.Test
    public void testGetJmeno() {
        String expResult = JMENO;
        String result = instance.getJmeno();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSkupina method, of class Slouzici.
     */
    @org.junit.Test
    public void testGetSkupina() {
        String expResult = SKUPINA;
        String result = instance.getSkupina();
        assertEquals(expResult, result);
    }

}
