/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.flipper.subscribableInformationstate;

import hmi.flipper.defaultInformationstate.DefaultItem;
import hmi.flipper.defaultInformationstate.DefaultList;
import static hmi.flipper.defaultInformationstate.DefaultList.ADDFIRST;
import static hmi.flipper.defaultInformationstate.DefaultList.ADDLAST;
import static hmi.flipper.defaultInformationstate.DefaultList.FIRST;
import static hmi.flipper.defaultInformationstate.DefaultList.LAST;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import hmi.flipper.informationstate.Item;
import java.util.ArrayList;

/**
 * This class extends DefaultList, such that multiple managers can subscribe to different information states.
 * @author WaterschootJB
 */
public class SubscribableList extends DefaultList{
    
    /* The list with Items */
    private ArrayList<Item> list = new ArrayList<Item>();
    
    /**
     * Adapted set function for SubscribableRecords and
     * @param path
     * @param value 
     */
    @Override
    public void set( String path, Object value )
    {
        /* Get required index of list */
        String position;
        String valuePath;
        if( path.contains(".") ) {
            position = path.substring(0, path.indexOf("."));
            valuePath = path.substring(path.indexOf(".")+1, path.length());
        } else {
            position = path;
            valuePath = null;
        }

        if( position.toLowerCase().equals(FIRST) ) {
            if( list.size() != 0 ) {
                Item i = list.get(0);
                i.set(valuePath,value);
            } else {
                Item i = new DefaultItem(value);
                list.add(i);
                i.set(valuePath,value);
            }
        } else if( position.toLowerCase().equals(LAST) ) {
            if( list.size() != 0 ) {
                Item i = list.get(list.size()-1);
                i.set(valuePath,value);
            } else {
                Item i = new DefaultItem(value);
                list.add(i);
                i.set(valuePath,value);
            }
        } else if( position.toLowerCase().equals(ADDFIRST) ) {
            if( valuePath == null ) {
                Item i = new DefaultItem(value);
                list.add(0, i);
            } else {
                Item i;
                if( valuePath.startsWith("_") ) {
                    // Next item is a list
                    //TODO
                    i = new DefaultItem(new SubscribableList());
                    //i = new DefaultItem(new DefaultList());
                } else {
                    // Next item is a Record
                    //TODO
                    i = new DefaultItem(new SubscribableRecord());
                    //i = new DefaultItem(new DefaultRecord());
                }
                list.add(0, i);
                i.set(valuePath, value);
            }
        } else if( position.toLowerCase().equals(ADDLAST) ) {
            if( valuePath == null ) {
                Item i = new DefaultItem(value);
                list.add(i);
            } else {
                Item i;
                if( valuePath.startsWith("_") ) {
                    // Next item is a list
                    //TODO
                    i = new DefaultItem(new SubscribableList());
                    //i = new DefaultItem(new DefaultList());
                } else {
                    // Next item is a Record
                    //TODO
                    i = new DefaultItem(new SubscribableRecord());
                    //i = new DefaultItem(new DefaultRecord());
                }
                list.add(i);
                i.set(valuePath, value);
            }
        }
    }
    
}
