/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.flipper.subscribableInformationstate;

import hmi.flipper.defaultInformationstate.DefaultItem;
import hmi.flipper.defaultInformationstate.DefaultList;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import hmi.flipper.informationstate.Item;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class extends DefaultRecord, such that multiple (dialogue) managers can subscribe to a particular
 * information state. Subscription meaning that a manager can listen and/or update the particular information
 * state it's subscribed to.
 * 
 * @author WaterschootJB
 */
public class SubscribableRecord extends DefaultRecord{
    
    private ArrayList <RecordSubscriber> subscribers = new ArrayList<>();
    public enum UpdateType{CREATE,UPDATE,REMOVE};
    
    /**
     * For any information state, a manager can subscribe to it.
     * 
     * @param rs, the manager to subscribe
     */
    public void subscribe(RecordSubscriber rs){
        Collection <RecordSubscriber> crs = new ArrayList();
        crs.add(rs);
        subscribe(crs);
    }
    
    /**
     * For an information state, a manager can unsubscribe from it.
     * 
     * @param rs, the manager to unsubscribe
     */
    public void unsubscribe(RecordSubscriber rs){
        Collection <RecordSubscriber> crs = new ArrayList();
        crs.add(rs);
        unsubscribe(crs);
    }
    
    /**
     * For any information state, a collection of managers can subscribe to it.
     * 
     * @param rs, the collection of managers
     */
    public void subscribe(Collection <RecordSubscriber> rs){
        subscribers.addAll(rs);
        for (Iterator<Item> i = is.values().iterator(); i.hasNext(); ){
            Item ci;
            ci = i.next();
            if(ci.getType() == Item.Type.Record && ci.getRecord() instanceof SubscribableRecord){
                SubscribableRecord sr = (SubscribableRecord) ci.getRecord();
                sr.subscribe(rs);
            }            
        }
    }
    
    /**
     * For any information state, a collection of managers can unsubscribe from it.
     * 
     * @param rs, the collection of managers
     */
    public void unsubscribe(Collection <RecordSubscriber> rs) {
        subscribers.removeAll(rs);
        for (Iterator<Item> i = is.values().iterator(); i.hasNext(); ){
            Item ci;
            ci = i.next();
            if(ci.getType() == Item.Type.Record && ci.getRecord() instanceof SubscribableRecord){
                SubscribableRecord sr = (SubscribableRecord) ci.getRecord();
                sr.unsubscribe(rs);
            }            
        }
    }
    
    /**
     * Set function is modified to let subscribers of an information state know when there is an update.
     * Furthermore, it sets a new variable with the given path and the given object as value.
     * 
     * @param path, the location of the information state
     * @param value, the new value of the information state 
     */    
    @Override
    public void set( String path, Object value )
    {
        UpdateType ut = UpdateType.UPDATE;
        
        /* Check if the path starts with a $ */
        if( path.charAt(0) == '$' ) {
            path = path.substring(1);
        }

        if( path.contains(".") ) { //Is SubscribableRecord or SubscribableList
            String p1 = path.substring(0, path.indexOf("."));
            String p2 = path.substring(path.indexOf(".")+1, path.length());
            Item i = is.get(p1);
            boolean is_list_or_record = false;
            if( i == null ) {
                is_list_or_record = true;
                if( p2.startsWith(DefaultList.FIRST) || p2.startsWith(DefaultList.LAST) 
                        || p2.startsWith(DefaultList.ADDFIRST) || p2.startsWith(DefaultList.ADDLAST) ) {
                    //TODO
                    //i = new DefaultItem(new DefaultList());
                    i = new DefaultItem(new SubscribableList());
                    ut = UpdateType.CREATE;
                } else {
                    SubscribableRecord sr = new SubscribableRecord();
                    sr.subscribe(subscribers);
                    i = new DefaultItem(sr);
                    ut = UpdateType.CREATE;                    
                }
                
            }
            if(value instanceof SubscribableRecord){
                SubscribableRecord sr = (SubscribableRecord) i;
                sr.subscribe(subscribers);                
            }
            is.put(p1,i);
            if(is_list_or_record){ //If created a new SubscribableRecord or SubscribableList, notify subscribers.
                notifyAllSubscribers(ut,i);
            }
            i.set( p2, value );                    
        } else {
            if(!(is.containsKey(path))){
                ut = UpdateType.CREATE;
            }
            Item i = new DefaultItem(value);
            is.put(path, i);
            notifyAllSubscribers(ut,i);
        }
    }
    
    /**
     * Removes the variable with the given path.
     * 
     * @param path - the name of the variable to delete
     */
    @Override
    public void remove( String path )
    {
        /* Check if the path starts with a $ */
        if( path.charAt(0) == '$' ) {
            path = path.substring(1);
        }

        /* Determine the name (the name of the variable of this InformationState) 
         * and the valuePath (the path of the substructure of the first Name, only used if the value of the first Name is a Record or a List) */
        String name;
        String valuePath;
        if( path.contains(".") ) {
            name = path.substring(0, path.indexOf("."));
            valuePath = path.substring(path.indexOf(".")+1, path.length());
        } else {
            name = path;
            valuePath = null;
        }

        /* Process the remaining ValuePath in the Item at the found place, and return the resulting Item */
        Item i = is.get(name);
        if( i == null ) {
            // Done, nothing to remove.
            return;
        } else if( valuePath == null ) {
            Object tsr = is.remove(name);
            if(tsr instanceof SubscribableRecord){
                SubscribableRecord sr = (SubscribableRecord) tsr;
                sr.unsubscribeAll();
            }
        } else if( i.getType() == Item.Type.List ) {
            //TODO Notify all subscribers of records in the list.
            for(Iterator<Item> j = is.values().iterator(); j.hasNext();){
                if(j instanceof SubscribableRecord){
                    ((SubscribableRecord) j).unsubscribeAll();
                }
            }
            i.getList().remove(valuePath);
        } else if( i.getType() == Item.Type.Record ) {
            i.getRecord().remove(valuePath);
        } else {
            is.remove(name);
        }
    }
    
    /**
     * Delete all subscribed managers from the information state.
     */
    public void unsubscribeAll() {
        unsubscribe(subscribers);
    }
    
    /**
     * Notifies all subscribers to a SubscribableRecord of any changes.
     * 
     * If item values are retrieved and directly edited, you should call this method as well.
     * @param ut, the type of update (creation, update or removal of SubscribableRecord)
     * @param value, the new affected value
     */
    private void notifyAllSubscribers(UpdateType ut, Item value){
        for (RecordSubscriber subscriber : subscribers){
            subscriber.update(this,ut,value);
        }
    }

}
