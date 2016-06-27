/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.flipper;

import eu.ariaagent.managers.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Siewart
 */
public class Main {
    
    private Collection <Manager> managers;
    private BehaviourManager dm;

    /**
     * @param args the command line arguments
     */
    public static void main( String args[] )
    {
        Main dialogueManagementSystem = new Main();        
    }
    
    public Main(){                
        //InformationManager im = new InformationManager();
        //FeedbackManager fm = new FeedbackManager();
        //StructureManager sm = new StructureManager();
        this.managers = new ArrayList<>();
        //managers.add(im);
        //managers.add(fm);
        //managers.add(sm);        
        //dm = new BehaviourManager();
        dm.startDialogue();
        
    }    
    
}
