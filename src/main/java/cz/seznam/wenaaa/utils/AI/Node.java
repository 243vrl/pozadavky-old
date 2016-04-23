/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.utils.AI;

/**
 *
 * @author vena
 */
class Node<N extends NodeItem> implements Comparable{
    
    private N item;
    private Node previous;

    Node(N item, Node previous) {
        this.item = item;
        this.previous = previous;
    }
    
    /**
     * !!! RETURNING 0 DOESNT MEAN THAT EQUALS RETURN TRUE !!!
     * throws IllegalArgumentException in case of incompatible types
     * @param o
     * @return int -1,0,1 for smaller, same , greater then o
     */
    @Override
    public int compareTo(Object o) {
        if(!o.getClass().equals(this.getClass())){
            throw new IllegalArgumentException("Argument should be the same class.");
        }
        if(this.item.getValue() == ((N)o).getValue()){
            return 0;
        }
        return this.item.getValue() > ((N)o).getValue()? 1 : -1;
    }

    public Node getPrevious() {
        return previous;
    }

    public N getItem() {
        return item;
    }
    
    
}
