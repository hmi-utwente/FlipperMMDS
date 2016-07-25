/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.aria.dialogue.managers.utils;

import hmi.flipper.defaultInformationstate.DefaultRecord;
import hmi.flipper.editor.istree.ISTreeModel;
import hmi.flipper.informationstate.Record;
import java.util.HashMap;
import java.util.Observable;


/**
 *
 * @author Siewart
 */
public class ISInspectorController extends Observable {
    ISTreeModel isTreeModel = null;
    private static HashMap<Record, ISInspectorController> instances = new HashMap<>();

    public synchronized static ISInspectorController getInstance(Record record) {
        
        ISInspectorController inst = instances.get(record);
        if ( inst == null) {
            inst = new ISInspectorController();
            instances.put(record, inst);
            ISInspectorGui gui = ISInspectorGui.getInstance(inst);
            inst.addObserver(gui);
        }
        return inst;
    }

    private ISInspectorController() {    }
    
    public synchronized void updateTreeModel(DefaultRecord is){
        isTreeModel = new ISTreeModel(is);
        setChanged();
        notifyObservers(isTreeModel);
    } 
    
    public synchronized ISTreeModel getISTreeModel(){
        return isTreeModel;
    }
}
