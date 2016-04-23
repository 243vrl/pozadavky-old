/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.utils.AI;

import java.util.List;

/**
 *
 * @author vena
 */
public interface NodeFactory<N  extends NodeItem> {
    
    List<N> getInitialNodes();
    List<N> getNexts(N toEvolve);
    boolean isAim(N nodeItem);
}
