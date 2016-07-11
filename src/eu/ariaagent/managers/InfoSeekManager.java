/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.managers;

import hmi.flipper.defaultInformationstate.DefaultRecord;

/**
 * The information seek manager provides statements and question posed by the 
 * virtual agent.
 * @author WaterschootJB
 */
public class InfoSeekManager extends DefaultManager {
    
    public InfoSeekManager(DefaultRecord is, long interval) {
        super(is, interval);
    }
    
}
