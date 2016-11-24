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

/**
 *
 * @author vena
 * @param <F>
 * @param <N>
 */
public class SearchTreeFirstDepthThenValue<F extends NodeItemFactory, N> extends SearchTree<F, N> {

    /**
     *
     * @param nodeFactory
     *
     */
    public SearchTreeFirstDepthThenValue(F nodeFactory) {
        super(nodeFactory);
    }

    @Override
    protected Comparator<Node> getComparator() {
        return new Comparator<Node>() {

            @Override
            public int compare(Node o1, Node o2) {
                if (!o1.getClass().equals(o2.getClass())) {
                    throw new IllegalArgumentException("Argument should be the same class.");
                }
                double valueFirstItem = nodeFactory.getPathValue(o1.getPath());
                double valueSecondItem = nodeFactory.getPathValue(o2.getPath());
                return o1.getDepth() == o2.getDepth() ? Double.compare(valueFirstItem, valueSecondItem) :
                 o1.getDepth() > o2.getDepth() ? -1 : 1;
                
            }

        };
    }

}
