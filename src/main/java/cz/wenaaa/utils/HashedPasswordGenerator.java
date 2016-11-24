/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 *
 * @author vena
 */
public class HashedPasswordGenerator {
    private HashedPasswordGenerator(){
        //no instanace needed
    }
    
    public static String generateHash(String password){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes("UTF-8"));
            return ((new HexBinaryAdapter()).marshal(md.digest())).toLowerCase();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            //System.out.println(ex.getMessage());
            return "";
        }
    }
    
}
