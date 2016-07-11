/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.managers;

import hmi.flipper.defaultInformationstate.DefaultRecord;

/**
 * The information retrieval manager for providing information, such as answers to user's questions,
 * an elaboration or admitting not knowing the information.
 * @author WaterschootJB
 */
public class InfoProvideManager extends DefaultManager {
    
    public InfoProvideManager(DefaultRecord is, long interval) {
        super(is, interval);
    }
    
}
