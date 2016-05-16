/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.utils.AI;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vena
 */
class Node<N> {

    private final N item;
    private final Node previous;

    Node(N item, Node previous) {
        this.item = item;
        this.previous = previous;
    }

    int getDepth() {
        Node pom = this;
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

    List<N> getPath() {
        List<N> vratka = new ArrayList<>();
        Node pom = this;
        while (pom != null) {
            vratka.add(0, (N) pom.getItem());
            pom = pom.previous;
        }
        return vratka;
    }
}
