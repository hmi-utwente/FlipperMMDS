/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.managers;

import hmi.flipper.defaultInformationstate.DefaultRecord;

/**
 * The social manager takes care of social obligations, such as salutation, apologizing,
 * gratitude, introducing yourself and valediction.
 * @author WaterschootJB
 */
public class SocialManager extends DefaultManager{
    
    public SocialManager(DefaultRecord is, long interval) {
        super(is, interval);
    }
    
}
