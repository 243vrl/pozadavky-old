/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.utils.AI;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author vena
 * @param <N>
 */
public class ProcessInfo<N> {
    
    private N actualNodeItem;
    private long leafsCount;
    private long leafsEvolved;
    
    private Lock lock = new ReentrantLock();

    public ProcessInfo(N actualNodeItem, long leafsCount, long leafsEvolved) {
        this.actualNodeItem = actualNodeItem;
        this.leafsCount = leafsCount;
        this.leafsEvolved = leafsEvolved;
    }

    public ProcessInfo() {
        actualNodeItem = null;
    }
    
    public N getActualNodeItem() {
        return actualNodeItem;
    }

    public long getLeafsCount() {
        return leafsCount;
    }

    public long getLeafsEvolved() {
        return leafsEvolved;
    }

    public void setActualNodeItem(N actualNodeItem) {
        this.actualNodeItem = actualNodeItem;
    }

    public void setLeafsCount(long leafsCount) {
        this.leafsCount = leafsCount;
    }

    public void setLeafsEvolved(long leafsEvolved) {
        this.leafsEvolved = leafsEvolved;
    }

    public Lock getLock() {
        return lock;
    }
    
    
    
}
