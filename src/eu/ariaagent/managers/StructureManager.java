/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.managers;

import hmi.flipper.defaultInformationstate.DefaultRecord;

/**
 * Recognizes the purpose of the user and updates it in the information state.
 * @author WaterschootJB
 */
public class StructureManager extends DefaultManager {
    
    public StructureManager(DefaultRecord is, long interval) {
        super(is, interval);
    }    
}
