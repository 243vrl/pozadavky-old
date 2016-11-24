/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.entityClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vena
 */
public final class ModelListenerFactory {
    
    private static Map<String, List<MyValueChangeListener>> data = new HashMap();
    
    private ModelListenerFactory(){
        
    }
    
    public static void registerListener(MyValueChangeListener posluchac, String forClass){
        if(!data.containsKey(forClass)){
            data.put(forClass,new ArrayList<MyValueChangeListener>());
        }
        data.get(forClass).add(posluchac);
        //System.out.format("Registrovan posluchac %s pro tridu %s",posluchac,forClass);
    }
    public static void unregisterListener(MyValueChangeListener posluchac){
        ArrayList<MyValueChangeListener> pomList = new ArrayList<>();
        pomList.add(posluchac);
        for(String pol : data.keySet()){
            data.get(pol).removeAll(pomList);
        }
        //System.out.format("Unregistrovan posluchac %s",posluchac);
    }

    public static void valueChanged(MyValueChangeEvent mvche) {
        List<MyValueChangeListener> pomList = data.get(mvche.getClassSending());
        for(MyValueChangeListener pol : pomList){
            pol.onValueChanged(mvche);
        }
    }
}
