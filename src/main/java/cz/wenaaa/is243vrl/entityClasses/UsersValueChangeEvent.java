/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.entityClasses;

/**
 *
 * @author vena
 */
public class UsersValueChangeEvent extends MyValueChangeEvent {
    
    private final Users user;
    private final String stareJmeno;
    
    public UsersValueChangeEvent(Users user, String stareJmeno) {
        super(user.getClass().getName());
        this.user = user;
        this.stareJmeno = stareJmeno;
    }

    public Users getUser() {
        return user;
    }

    public String getStareJmeno() {
        return stareJmeno;
    }

    @Override
    public String toString() {
        return "UsersValueChangeEvent{" + "user=" + user + ", stareJmeno=" + stareJmeno + '}';
    }

    
}
