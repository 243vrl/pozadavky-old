/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vena
 */
public final class ViewListenerFactory {
    
    private static Map<String, List<MyActionListener>> data = new HashMap();
    
    private ViewListenerFactory(){
        
    }
    
    public static void registerListener(MyActionListener posluchac, String forClass){
        if(!data.containsKey(forClass)){
            data.put(forClass,new ArrayList<MyActionListener>());
        }
        data.get(forClass).add(posluchac);
        //System.out.format("Registrovan posluchac %s pro tridu %s",posluchac,forClass);
    }
    public static void unregisterListener(MyActionListener posluchac){
        ArrayList<MyActionListener> pomList = new ArrayList<>();
        pomList.add(posluchac);
        for(String pol : data.keySet()){
            data.get(pol).removeAll(pomList);
        }
        //System.out.format("Unregistrovan posluchac %s",posluchac);
    }

    public static void actionPerformed(MyActionEvent mae) {
        List<MyActionListener> pomList = data.get(mae.getClassSending());
        for(MyActionListener pol : pomList){
            pol.onActionPerformed(mae);
        }
    }
}