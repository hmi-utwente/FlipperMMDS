/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.aria.dialogue.managers;

import eu.aria.dialogue.gui.GuiController;
import eu.ariaagent.managers.DefaultManager;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import java.util.Map;

/**
 *
 * @author Siewart
 */
public class GuiManager extends DefaultManager {
    GuiController controller;
    public GuiManager(DefaultRecord is) {
        super(is);
        // Enforce GUI
        controller = GuiController.getInstance(is);
        interval = 50; //quick default interval;
    }
    
    @Override 
    public void process(){
        super.process();
        controller.updateLastSayInIs();
    }
    
    @Override
    public void setParams(Map<String, String> params, Map<String, String[]> paramList)
    {
       String path = params.get("valence_is_path");
       if(path != null){
        controller.setValencePath(path);
       }
       path = params.get("anger_is_path");
       if(path != null){
        controller.setAngerPath(path);
       }
       path = params.get("arousal_is_path");
       if(path != null){
        controller.setArousalPath(path);
       }
       path = params.get("disgust_is_path");
       if(path != null){
        controller.setDisgustPath(path);
       }
       path = params.get("fear_is_path");
       if(path != null){
        controller.setFearPath(path);
       }
       path = params.get("happiness_is_path");
       if(path != null){
        controller.setHappinessPath(path);
       }
       path = params.get("interest_is_path");
       if(path != null){
        controller.setInterestPath(path);
       }
       path = params.get("neutral_is_path");
       if(path != null){
        controller.setNeutralPath(path);
       }
       path = params.get("surprised_is_path");
       if(path != null){
        controller.setSurprisedPath(path);
       }
       
       path = params.get("user_utterance_is_path");
       if(path != null){
        controller.setUserUtterancePath(path);
       }
       
       path = params.get("agent_utterance_is_path");
       if(path != null){
        controller.setAgentUtterancePath(path);
       }
       
    }
    
}
