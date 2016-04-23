/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.utils.AI;

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
public class LowestValueSearchTree<F extends NodeFactory, N extends NodeItem> implements Callable{

    
    private final F nodeFactory;
    
    public LowestValueSearchTree(F nodeFactory){
        
        this.nodeFactory = nodeFactory;
    }
    
    @Override
    public List<N> call() throws Exception {
        List<Node> leafs = new ArrayList<>();
        List<N> vratka = null;
        
        List<N> uvodniUzly = nodeFactory.getInitialNodes();
        leafs.addAll(nodeItemsToNodes(uvodniUzly,null));
        
        while(true){
            if(interrupted()){
                break;
            }
            if(leafs.isEmpty()){
                break;
            }
            Node rozvijeny = leafs.remove(0);
            if(nodeFactory.isAim(rozvijeny.getItem())){
                vratka = vytvorVratku(rozvijeny);
                break;
            }
            List<N> noveUzly = nodeFactory.getNexts(rozvijeny.getItem());
            leafs.addAll(nodeItemsToNodes(noveUzly,rozvijeny));
            leafs.sort(new Comparator<Node>(){

                @Override
                public int compare(Node o1, Node o2) {
                    return o1.compareTo(o2);
                }
                
            });
        }
        return vratka;
    }

    private List<N> vytvorVratku(Node rozvijeny) {
        List<N> vratka = new ArrayList<>();
        Node pom = rozvijeny;
        while(pom != null){
            vratka.add(0,(N) pom.getItem());
        }        
        return vratka;
    }

    private Collection<Node> nodeItemsToNodes(List<N> uvodniUzly, Node previous) {
        Collection<Node> vratka = new ArrayList<>();
        for(N pol : uvodniUzly){
            vratka.add(new Node(pol,previous));
        }
        return vratka;
    }
    
}
