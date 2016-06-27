/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.flipper;

import eu.ariaagent.managers.DefaultManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

/**
 *
 * @author WaterschootJB
 */
public class ManagerController {
    
    private Collection managers;
    private Collection templates;
    private Collection functions;
    private Collection queue;
    
    public ManagerController(){
        this.managers = new ArrayList();
        this.templates = new ArrayList();
        this.functions = new ArrayList();
        this.queue = new PriorityQueue<DefaultManager>();
    }

    void start() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    //Manager controller for references to all managers and reading the templates
    
}
