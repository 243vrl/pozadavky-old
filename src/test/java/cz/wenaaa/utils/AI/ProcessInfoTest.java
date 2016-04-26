/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.utils.AI;

import cz.wenaaa.utils.AI.ProcessInfo;
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
public class ProcessInfoTest {
    
    public ProcessInfoTest() {
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
     * Test of getActualNodeItem method, of class ProcessInfo.
     */
    @Test
    public void testGetActualNodeItem() {
        System.out.println("getActualNodeItem");
        ProcessInfo instance = new ProcessInfo();
        Object expResult = null;
        Object result = instance.getActualNodeItem();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLeafsCount method, of class ProcessInfo.
     */
    @Test
    public void testGetLeafsCount() {
        System.out.println("getLeafsCount");
        ProcessInfo instance = new ProcessInfo();
        long expResult = 0L;
        long result = instance.getLeafsCount();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLeafsEvolved method, of class ProcessInfo.
     */
    @Test
    public void testGetLeafsEvolved() {
        System.out.println("getLeafsEvolved");
        ProcessInfo instance = new ProcessInfo();
        long expResult = 0L;
        long result = instance.getLeafsEvolved();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setActualNodeItem method, of class ProcessInfo.
     */
    @Test
    public void testSetActualNodeItem() {
        System.out.println("setActualNodeItem");
        Object actualNodeItem = null;
        ProcessInfo instance = new ProcessInfo();
        instance.setActualNodeItem(actualNodeItem);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLeafsCount method, of class ProcessInfo.
     */
    @Test
    public void testSetLeafsCount() {
        System.out.println("setLeafsCount");
        long leafsCount = 0L;
        ProcessInfo instance = new ProcessInfo();
        instance.setLeafsCount(leafsCount);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLeafsEvolved method, of class ProcessInfo.
     */
    @Test
    public void testSetLeafsEvolved() {
        System.out.println("setLeafsEvolved");
        long leafsEvolved = 0L;
        ProcessInfo instance = new ProcessInfo();
        instance.setLeafsEvolved(leafsEvolved);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
