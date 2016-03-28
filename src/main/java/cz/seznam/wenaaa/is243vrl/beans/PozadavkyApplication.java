/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.beans;

import java.util.TimeZone;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author vena
 */
@Named(value = "pozApplication")
@ApplicationScoped
public class PozadavkyApplication {

    /**
     * Creates a new instance of Application
     */
    public PozadavkyApplication() {
    }
    public TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }
}
