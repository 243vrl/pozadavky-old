/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.utils.AI;

import java.util.List;

/**
 *
 * @author vena
 * @param <N>
 */
public interface NodeItemFactory<N> {

    List<N> getInitialNodes();

    List<N> getNexts(List<N> actualPath);

    boolean isAim(List<N> actualPath);
    
    double getPathValue(List<N> actualPath);
    
    double getPathValueForReducing(ComparatorTypes ct, List<N> path);
    
    double getRefValueForReducing(ComparatorTypes ct);
}
