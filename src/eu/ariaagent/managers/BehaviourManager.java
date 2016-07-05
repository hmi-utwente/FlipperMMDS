/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
Create a Dialogue Manager with all available Managers. Create a templatecontroller for each manager to check the rules. Let managers write/read to different information states.
Notify managers of changes in the information states
*/
package eu.ariaagent.managers;

import eu.ariaagent.flipper.dm.UserSay;
import eu.ariaagent.flipper.subscribableInformationstate.SubscribableRecord;
import eu.ariaagent.util.*;
import hmi.flipper.behaviourselection.TemplateController;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

/**
 * The BehaviourManager is responsible for merging the output of different managers into a good response
 from the agent.
 * Input: Output of all managers, user interaction, template controller
 * Output: Response of the system
 * @author WaterschootJB
 */
public class BehaviourManager extends DefaultManager{
    
    private Collection <Manager> managers;
    //protected TemplateController controller;
    private DefaultRecord is;
    private TemplateController controller;
    private ArrayList<String> userSayAL;
    private SentencesToKeywords sk;
    private ArrayList<Rules> rules;
    private RulesReader rulesReader;
    //private DMPool.DMLogger logger;
    //private EmotionHolder emotionHolder;
    private UserSay lastUserSay = null;
    
    /* Set up all managers for updating the information states */
    public BehaviourManager(DefaultRecord is, long interval){
        super(is,interval);
    }
    
    public BehaviourManager(DefaultRecord is){
        super(is);
    }
    
    public void startDialogue(){
        
        is.set("userstate", new SubscribableRecord());
        controller = new TemplateController();
        controller.processTemplateFile("D:/GitHub/FlipperExample/Example.xml");
        //controller.addFunction(new HelloWorldFunctions());
        String userText = "";
        is.set("userstate.utterance", userText);        
        Scanner in = new Scanner(System.in);        
        while( !userText.equals("quit") && !userText.equals("exit") ) {
            System.out.println("You can start speaking.");
            System.out.print(">> ");
            userText = in.nextLine();
            long shortestNext = Long.MAX_VALUE;
            for (Manager m : managers){
                long time = m.timeUntilNextProcess();
                if(shortestNext > time){
                    shortestNext = time;
                }                    
                if(time <= 0){
                    m.process();
                }
            }
            controller.checkTemplates(is);
        }
    }

    
}
