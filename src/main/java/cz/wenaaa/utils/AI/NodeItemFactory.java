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

    List<N> getNexts(N toEvolve);

    boolean isAim(N nodeItem);
    
    double getNodeItemValue(N nodeItem);
}
