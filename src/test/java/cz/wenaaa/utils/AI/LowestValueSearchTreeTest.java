/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.utils.AI;

import cz.wenaaa.utils.AI.LowestValueSearchTree;
import cz.wenaaa.utils.AI.ProcessInfo;
import cz.wenaaa.utils.AI.NodeItemFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author vena
 */
public class LowestValueSearchTreeTest {

    @Mock
    private NodeItemFactory nf;
    @Mock
    private ProcessInfo pi;
    @Mock
    Object A;
    @Mock
    Object B;
    @Mock
    Object C;
    @Mock
    Object D;
    @Mock
    Object E;
    @Mock
    Object F;
    @Mock
    Object G;
    @Mock
    Object H;
    @Mock
    Object I;
    @Mock
    Object J;
    @Mock
    Lock lock;

    private static final boolean BREADTH_FIRST = false;
    private static final boolean DEPTH_FIRST = true;

    private List initials;
    private List nextsA;
    private List nextsB;
    private List nextsC;
    private List nextsE;
    private List nextsF;

    private List cesta;

    public LowestValueSearchTreeTest() {
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
        
        initials = new ArrayList();
        initials.add(A);
        initials.add(B);

        nextsA = new ArrayList();
        nextsA.add(C);
        nextsA.add(D);

        nextsB = new ArrayList();
        nextsB.add(E);

        nextsC = new ArrayList();
        nextsC.add(F);

        nextsE = new ArrayList();
        nextsE.add(G);
        nextsE.add(H);
        nextsE.add(I);

        nextsF = new ArrayList();
        nextsF.add(J);

        when(nf.getInitialNodes()).thenReturn(initials);
        when(nf.getNexts(A)).thenReturn(nextsA);
        when(nf.getNexts(B)).thenReturn(nextsB);
        when(nf.getNexts(C)).thenReturn(nextsC);
        when(nf.getNexts(D)).thenReturn(new ArrayList());
        when(nf.getNexts(E)).thenReturn(nextsE);
        when(nf.getNexts(F)).thenReturn(nextsF);
        when(nf.getNexts(G)).thenReturn(new ArrayList());
        when(nf.getNexts(H)).thenReturn(new ArrayList());
        when(nf.getNexts(I)).thenReturn(new ArrayList());
        when(nf.getNexts(J)).thenReturn(new ArrayList());

        when(nf.isAim(A)).thenReturn(Boolean.FALSE);
        when(nf.isAim(B)).thenReturn(Boolean.FALSE);
        when(nf.isAim(C)).thenReturn(Boolean.FALSE);
        when(nf.isAim(D)).thenReturn(Boolean.FALSE);
        when(nf.isAim(E)).thenReturn(Boolean.FALSE);
        when(nf.isAim(F)).thenReturn(Boolean.FALSE);
        when(nf.isAim(G)).thenReturn(Boolean.FALSE);
        when(nf.isAim(H)).thenReturn(Boolean.TRUE);
        when(nf.isAim(I)).thenReturn(Boolean.FALSE);
        when(nf.isAim(J)).thenReturn(Boolean.FALSE);

        when(pi.getLock()).thenReturn(lock);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                cesta.add(args[0]);
                //System.out.println(args[0]);
                return null;
            }
        }).when(pi).setActualNodeItem(any());
        /*
         Mockito.doAnswer(new Answer() {
         @Override
         public Object answer(InvocationOnMock invocation) throws Throwable {
         Object[] args = invocation.getArguments();
         System.out.println(args[0]);
         return null;
         }
         }).when(pi).setLeafsCount(anyLong());
         Mockito.doAnswer(new Answer() {
         @Override
         public Object answer(InvocationOnMock invocation) throws Throwable {
         Object[] args = invocation.getArguments();
         System.out.println(args[0]);
         return null;
         }
         }).when(pi).setLeafsEvolved(anyLong());*/

        when(A.toString()).thenReturn("A");
        when(B.toString()).thenReturn("B");
        when(C.toString()).thenReturn("C");
        when(D.toString()).thenReturn("D");
        when(E.toString()).thenReturn("E");
        when(F.toString()).thenReturn("F");
        when(G.toString()).thenReturn("G");
        when(H.toString()).thenReturn("H");
        when(I.toString()).thenReturn("I");
        when(J.toString()).thenReturn("J");

        when(lock.tryLock()).thenReturn(Boolean.TRUE);

        cesta = new ArrayList();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of call method, of class LowestValueSearchTree.
     */
    @Test
    public void testCallBreadthFirstSameNodeValue() throws Exception {

         when(nf.getNodeItemValue(any())).thenReturn(new Double(0));

        LowestValueSearchTree instance = new LowestValueSearchTree(nf, BREADTH_FIRST);
        instance.setProcessInfo(pi);

        List result = instance.call();

        List<Object> expectedWay = new ArrayList<Object>();
        expectedWay.add(A);
        expectedWay.add(B);
        expectedWay.add(C);
        expectedWay.add(D);
        expectedWay.add(E);
        expectedWay.add(F);
        expectedWay.add(G);
        expectedWay.add(H);

        List<Object> expectedResult = new ArrayList<Object>();
        expectedResult.add(B);
        expectedResult.add(E);
        expectedResult.add(H);
        
        assertEquals(expectedWay, cesta);
        assertEquals(expectedResult, result);
    }
    
    /**
     * Test of call method, of class LowestValueSearchTree.
     */
    @Test
    public void testCallDepthFirstSameNodeValue() throws Exception {

        when(nf.getNodeItemValue(any())).thenReturn(new Double(0));

        LowestValueSearchTree instance = new LowestValueSearchTree(nf, DEPTH_FIRST);
        instance.setProcessInfo(pi);

        List result = instance.call();

        List<Object> expectedWay = new ArrayList<Object>();
        expectedWay.add(A);
        expectedWay.add(C);
        expectedWay.add(F);
        expectedWay.add(J);
        expectedWay.add(D);
        expectedWay.add(B);
        expectedWay.add(E);
        expectedWay.add(G);
        expectedWay.add(H);

        List<Object> expectedResult = new ArrayList<Object>();
        expectedResult.add(B);
        expectedResult.add(E);
        expectedResult.add(H);
        
        assertEquals(expectedWay, cesta);
        assertEquals(expectedResult, result);
    }
    
    /**
     * Test of call method, of class LowestValueSearchTree.
     */
    @Test
    public void testCallBreadthFirstDifferentNodeValue() throws Exception {

        when(nf.getNodeItemValue(B)).thenReturn(new Double(0));
        
        when(nf.getNodeItemValue(A)).thenReturn(new Double(1));
        when(nf.getNodeItemValue(D)).thenReturn(new Double(1));
        when(nf.getNodeItemValue(E)).thenReturn(new Double(1));
        
        when(nf.getNodeItemValue(C)).thenReturn(new Double(2));
        when(nf.getNodeItemValue(F)).thenReturn(new Double(2));
        when(nf.getNodeItemValue(I)).thenReturn(new Double(2));
        
        when(nf.getNodeItemValue(G)).thenReturn(new Double(3));
        when(nf.getNodeItemValue(H)).thenReturn(new Double(3));
        when(nf.getNodeItemValue(J)).thenReturn(new Double(3));

        LowestValueSearchTree instance = new LowestValueSearchTree(nf, BREADTH_FIRST);
        instance.setProcessInfo(pi);

        List result = instance.call();

        List<Object> expectedWay = new ArrayList<Object>();
        expectedWay.add(B);
        expectedWay.add(A);
        expectedWay.add(E);
        expectedWay.add(D);
        expectedWay.add(C);
        expectedWay.add(I);
        expectedWay.add(F);
        expectedWay.add(G);
        expectedWay.add(H);

        List<Object> expectedResult = new ArrayList<Object>();
        expectedResult.add(B);
        expectedResult.add(E);
        expectedResult.add(H);
        
        assertEquals(expectedWay, cesta);
        assertEquals(expectedResult, result);
    }
    
    /**
     * Test of call method, of class LowestValueSearchTree.
     */
    @Test
    public void testCallDepthFirstDifferentNodeValue() throws Exception {
        
        when(nf.getNodeItemValue(B)).thenReturn(new Double(0));
        
        when(nf.getNodeItemValue(A)).thenReturn(new Double(1));
        when(nf.getNodeItemValue(D)).thenReturn(new Double(1));
        when(nf.getNodeItemValue(E)).thenReturn(new Double(1));
        
        when(nf.getNodeItemValue(C)).thenReturn(new Double(2));
        when(nf.getNodeItemValue(F)).thenReturn(new Double(2));
        when(nf.getNodeItemValue(I)).thenReturn(new Double(2));
        
        when(nf.getNodeItemValue(G)).thenReturn(new Double(3));
        when(nf.getNodeItemValue(H)).thenReturn(new Double(3));
        when(nf.getNodeItemValue(J)).thenReturn(new Double(3));
        
        LowestValueSearchTree instance = new LowestValueSearchTree(nf, DEPTH_FIRST);
        instance.setProcessInfo(pi);

        List result = instance.call();

        List<Object> expectedWay = new ArrayList<Object>();
        expectedWay.add(B);
        expectedWay.add(E);
        expectedWay.add(A);
        expectedWay.add(D);
        expectedWay.add(I);
        expectedWay.add(C);
        expectedWay.add(F);
        expectedWay.add(J);
        expectedWay.add(G);
        expectedWay.add(H);

        List<Object> expectedResult = new ArrayList<Object>();
        expectedResult.add(B);
        expectedResult.add(E);
        expectedResult.add(H);
        
        
        
        assertEquals(expectedWay, cesta);
        assertEquals(expectedResult, result);
    }
    /**
     * Test of setProcessInfo method, of class LowestValueSearchTree.
     */
    @Test
    public void testSetGetProcessInfo() {

        LowestValueSearchTree instance = new LowestValueSearchTree(nf, true);
        instance.setProcessInfo(pi);
        ProcessInfo result = instance.getProcessInfo();
        assertEquals(result, pi);

    }

}
