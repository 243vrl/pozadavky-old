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
public class UsersValueChangeEvent extends MyValueChangeEvent {
    
    private final Users user;
    
    
    public UsersValueChangeEvent(Users user) {
        super(user.getClass().getName());
        this.user = user;
        
    }

    public Users getUser() {
        return user;
    }

    
}
