/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.flipper.subscribableInformationstate;

import eu.ariaagent.flipper.subscribableInformationstate.SubscribableRecord.UpdateType;
import hmi.flipper.informationstate.Item;

/**
 *
 * @author WaterschootJB
 */
public interface RecordSubscriber {
    
    public void update(SubscribableRecord sr, UpdateType ut, Item value);    
    
}
