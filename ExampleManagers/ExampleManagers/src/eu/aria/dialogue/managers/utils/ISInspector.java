/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.aria.dialogue.managers.utils;

import eu.ariaagent.managers.DefaultManager;
import hmi.flipper.defaultInformationstate.DefaultRecord;

/**
 *
 * @author Siewart
 */
public class ISInspector extends DefaultManager {
    ISInspectorController controller;
    public ISInspector(DefaultRecord is) {
        super(is);
        interval = 1000;
        controller = ISInspectorController.getInstance(is);
    }
    
    @Override
    public void process(){
        super.process();
        controller.updateTreeModel(is);
    }
}
