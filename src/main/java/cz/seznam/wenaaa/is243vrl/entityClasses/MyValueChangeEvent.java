/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.entityClasses;

/**
 *
 * @author vena
 */
public class MyValueChangeEvent {
    
    private String classSending;

    public MyValueChangeEvent(String classSending) {
        this.classSending = classSending;
    }

    public String getClassSending() {
        return classSending;
    }

}
