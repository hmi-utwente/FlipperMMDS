/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.flipper.behaviours;

import hmi.flipper.behaviourselection.behaviours.BehaviourClass;
import java.util.ArrayList;

/**
 *
 * @author Siewart
 */
public class DefaultBehaviour implements BehaviourClass{
    public void execute( ArrayList<String> argNames, ArrayList<String> argValues )
    {
        System.out.println("Agent: " + argValues.get(0));
    }

    public void prepare( ArrayList<String> argNames, ArrayList<String> argValues )
    {
        
    } 
}
