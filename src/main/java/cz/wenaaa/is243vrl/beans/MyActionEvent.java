/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.beans;

/**
 *
 * @author vena
 */
public class MyActionEvent {
    private String classSending;

    public MyActionEvent(String classSending) {
        this.classSending = classSending;
    }
    
    public String getClassSending() {
        return this.classSending;
    }
    
}
