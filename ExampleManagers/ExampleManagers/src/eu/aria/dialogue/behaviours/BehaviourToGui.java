/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.aria.dialogue.behaviours;

/**
 *
 * @author Siewart
 */
import eu.aria.dialogue.gui.GuiController;
import eu.aria.dialogue.util.Say;
import eu.ariaagent.managers.Manager;
import eu.ariaagent.util.ManageableBehaviourClass;
import java.util.ArrayList;

public class BehaviourToGui implements ManageableBehaviourClass{
    GuiController gui;
    
    Manager manager;
    
    private String agentName = "Agent";

    @Override
    public void execute(ArrayList<String> argNames, ArrayList<String> argValues) {
        
        Say newSay = new Say(argValues.get(0), agentName, true);
        if(this.gui == null)
        {
            this.gui = GuiController.getInstance(manager.getIS());
        }
        manager.getIS().set("$userstates.intention", "");
        
        gui.addAgentSay(newSay, true);
        
    }

    @Override
    public void prepare(ArrayList<String> argNames, ArrayList<String> argValues) {
        
    }
    
    @Override
    public Manager getManager(){
        return this.manager;
    }
    
    @Override
    public void setManager(Manager manager){
        this.manager = manager;
        
    }
    
}
