/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.utils.AI;

import static java.lang.Thread.interrupted;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author vena
 */
public class LowestValueSearchTree<F extends NodeItemFactory, N> implements Callable {

    private final F nodeFactory;
    private final boolean depthFirst;
    private ProcessInfo<N> processInfo;

    /**
     *
     * @param nodeFactory
     * @param depthFirst
     */
    public LowestValueSearchTree(F nodeFactory, boolean depthFirst) {
        this.depthFirst = depthFirst;
        this.nodeFactory = nodeFactory;
        this.processInfo = null;
    }

    public void setProcessInfo(ProcessInfo<N> processInfo) {
        this.processInfo = processInfo;
    }

    public ProcessInfo<N> getProcessInfo() {
        return processInfo;
    }

    @Override
    public List<N> call() throws Exception {
        List<Node> leafs = new ArrayList<>();
        List<N> vratka = null;
        long leafsEvolved = 0L;

        List<N> uvodniUzly = nodeFactory.getInitialNodes();
        leafs.addAll(nodeItemsToNodes(uvodniUzly, null));

        while (true) {
            if (interrupted()) {
                break;
            }
            if (leafs.isEmpty()) {
                break;
            }
            leafs.sort(new Comparator<Node>() {

                @Override
                public int compare(Node o1, Node o2) {
                    return o1.compareTo(o2);
                }

            });
            Node rozvijeny = leafs.remove(0);

            leafsEvolved++;

            if (processInfo != null) {
                passInfo(rozvijeny, leafs, leafsEvolved);
            }

            if (nodeFactory.isAim(rozvijeny.getPath())) {
                vratka = rozvijeny.getPath();
                break;
            }

            List<N> noveUzly = nodeFactory.getNexts(rozvijeny.getPath());
            leafs.addAll(nodeItemsToNodes(noveUzly, rozvijeny));

        }
        return vratka;
    }

    private void passInfo(Node rozvijeny, List<Node> leafs, long leafsEvolved) {
        if (processInfo.getLock().tryLock()) {
            try {
                processInfo.setActualNodeItem(rozvijeny.getItem());
                processInfo.setLeafsCount(leafs.size());
                processInfo.setLeafsEvolved(leafsEvolved);
            } finally {
                processInfo.getLock().unlock();
            }
        }
    }

    private ArrayList<Node> nodeItemsToNodes(List<N> uvodniUzly, Node previous) {
        ArrayList<Node> vratka = new ArrayList<>();
        for (N pol : uvodniUzly) {
            vratka.add(new Node(pol, previous));
        }
        return vratka;
    }

    /**
     *
     * @author vena
     */
    private class Node implements Comparable {

        private final N item;
        private final Node previous;

        Node(N item, Node previous) {
            this.item = item;
            this.previous = previous;
        }

        /**
         * !!! RETURNING 0 DOESNT MEAN THAT EQUALS RETURN TRUE !!! throws
         * IllegalArgumentException in case of incompatible types
         *
         * @param o
         * @return int -1,0,1 for smaller, same , greater then o
         */
        @Override
        public int compareTo(Object o) {
            if (!o.getClass().equals(this.getClass())) {
                throw new IllegalArgumentException("Argument should be the same class.");
            }
            int vratka;
            double valuetThisItem = nodeFactory.getPathValue(this.getPath());
            double valueOtherItem = nodeFactory.getPathValue(((Node) o).getPath());
            if (valuetThisItem == valueOtherItem) {
                if (depthFirst) {
                    int depthThis = getDepth(this);
                    int depthOther = getDepth((Node) o);
                    if (depthOther == depthThis) {
                        vratka = 0;
                    } else {
                        vratka = (depthThis > depthOther) ? -1 : 1;
                    }
                } else {
                    vratka = 0;
                }
            } else {
                vratka = (valuetThisItem > valueOtherItem) ? 1 : -1;
            }
            //System.out.println("comparing "+this.item+"["+valuetThisItem+"] with "+((Node) o).item+"["+valueOtherItem+"]"+" with result: "+vratka);

            return vratka;
        }

        private int getDepth(Node node) {
            Node pom = node;
            int depth = 0;
            while (pom != null) {
                pom = pom.previous;
                depth++;
            }
            return depth;
        }

        Node getPrevious() {
            return previous;
        }

        N getItem() {
            return item;
        }

        private List<N> getPath() {
            List<N> vratka = new ArrayList<>();
            Node pom = this;
            while (pom != null) {
                vratka.add(0, (N) pom.getItem());
                pom = pom.previous;
            }
            return vratka;
        }
    }

}
