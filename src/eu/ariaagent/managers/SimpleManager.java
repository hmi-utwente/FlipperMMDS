/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.managers;

import javax.validation.constraints.NotNull;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import java.util.Collection;

/**
 *
 * @author WaterschootJB
 */
public class SimpleManager extends DefaultManager{
        
    public SimpleManager(DefaultRecord is){
        super(is);     
    } 
}
