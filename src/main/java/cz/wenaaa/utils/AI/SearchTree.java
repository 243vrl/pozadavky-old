/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.utils.AI;

import static java.lang.Thread.interrupted;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vena
 */
public abstract class SearchTree<F extends NodeItemFactory, N> implements Callable, Runnable {
    protected final F nodeFactory;
    protected ProcessInfo<N> processInfo;
    protected ArrayList<Node> leafs;
    protected final Comparator<Node> comparator;
    protected boolean running;
    private volatile boolean prerusit;

    public SearchTree(F nodeFactory) {
        this.nodeFactory = nodeFactory;
        this.processInfo = null;
        this.running = false;
        this.comparator = getComparator();
    }

    public boolean isRunning(){
        return running;
    }

    public boolean isPrerusit() {
        return prerusit;
    }

    public void setPrerusit(boolean prerusit) {
        this.prerusit = prerusit;
    }
    
    
    
    @Override
    public void run() {
        
        try {
            call();
        } catch (Exception ex) {
            Logger.getLogger(SearchTree.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void setProcessInfo(ProcessInfo<N> processInfo) {
        this.processInfo = processInfo;
    }

    public ProcessInfo<N> getProcessInfo() {
        return processInfo;
    }

    @Override
    public List<N> call() throws Exception {
        if (running) {
            throw new Exception("Duplicitní volání funkce.");
        }
        System.out.println("vstupuji do call");
        running = true;
        leafs = new ArrayList<>();
        List<N> vratka = null;
        long leafsEvolved = 0L;
        List<N> uvodniUzly = nodeFactory.getInitialNodes();
        System.out.println("nacteny initial nodes");
        leafs.addAll(nodeItemsToNodes(uvodniUzly, null));
        while (true) {
            if (interrupted()) {
                System.out.println("prerusuji na interupted");
                break;
            }
            if (leafs.isEmpty()) {
                System.out.println("prerusuji na leafs is empty");
                break;
            }
            if(prerusit){
                System.out.println("prerusuji na prerusit");
                break;
            }
            leafs.sort(comparator);
            Node rozvijeny = leafs.remove(0);
            leafsEvolved++;
            if (processInfo != null) {
                passInfo(rozvijeny, leafsEvolved);
            }
            if (nodeFactory.isAim(rozvijeny.getPath())) {
                vratka = rozvijeny.getPath();
                System.out.println("prerusuji na leafs is aim");
                break;
            }
            if(nodeFactory.shouldReduceTreeLeafs(rozvijeny.getPath())){
                reduceTreeLeafs(nodeFactory.getCTforReduce());
            }
            List<N> noveUzly = nodeFactory.getNexts(rozvijeny.getPath());
            leafs.addAll(nodeItemsToNodes(noveUzly, rozvijeny));
            System.out.println("Stale v call");
        }
        running = false;
        return vratka;
    }

    protected abstract Comparator<Node> getComparator();

    private void reduceTreeLeafs(ComparatorTypes ct) {
        double refValue;
        refValue = nodeFactory.getRefValueForReducing(ct);
        leafs.removeIf(new Predicate<Node>() {
            @Override
            public boolean test(Node t) {
                double actValue = nodeFactory.getPathValueForReducing(ct, t.getPath());
                switch (ct) {
                    case LO:
                        return actValue < refValue;
                    case LE:
                        return actValue <= refValue;
                    case EQ:
                        return actValue == refValue;
                    case GE:
                        return actValue >= refValue;
                    case GR:
                        return actValue > refValue;
                }
                return false;
            }
        });
    }

    void passInfo(Node rozvijeny, long leafsEvolved) {
        if (processInfo.getLock().tryLock()) {
            try {
                processInfo.setActualNodeItem((N) rozvijeny.getItem());
                processInfo.setLeafsCount(leafs.size());
                processInfo.setLeafsEvolved(leafsEvolved);
                processInfo.setAktDepth(rozvijeny.getDepth());
                //System.out.println(processInfo);
            } finally {
                processInfo.getLock().unlock();
            }
        } else {
            System.out.println("LWST neobdrzel lock nemuzu passinfo");
        }
    }

    ArrayList<Node> nodeItemsToNodes(List<N> uvodniUzly, Node previous) {
        ArrayList<Node> vratka = new ArrayList<>();
        for (N pol : uvodniUzly) {
            vratka.add(new Node(pol, previous));
        }
        return vratka;
    }
    
}
