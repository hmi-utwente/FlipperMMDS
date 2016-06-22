/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.managers;

import eu.ariaagent.flipper.subscribableInformationstate.SubscribableRecord;
import eu.ariaagent.util.Rules;
import eu.ariaagent.util.RulesReader;
import eu.ariaagent.util.State;
import hmi.flipper.behaviourselection.TemplateController;
import hmi.flipper.informationstate.Item;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The InformationManager is responsible for semantically correct information. It should resolve resolution problems of the utterance
 * and proceed with giving a semantically proper response.
 * Input: user utterance, dialog history
 * Output: agent utterance
 * @author WaterschootJB
 */
public class InformationManager extends DefaultManager{
   
    //private QAMatcher qamatcher;
    private ArrayList <String> userUtteranceAll;
    private String userUtterance;
    private String agentUtterance;
    private final String rule_file;
    private final String template_file;
    private final ArrayList<Rules> rules;
    private final RulesReader rulesreader;
    private TemplateController itc;
    private SubscribableRecord is;
    
    public InformationManager()
    {        
        rule_file = "D:/Github/FlipperExample/Rules.xml";
        template_file = "D:/GitHub/FlipperMMDS/resources/Example.xml";
        rulesreader = new RulesReader(rule_file);
        rules = rulesreader.getRules();
//        itc = new TemplateController();
//        itc.processTemplateFile(template_file);
    }
    
    public void informationRetrieval(){
        
    }
    
    @Override
    public void process(SubscribableRecord sr){
        this.is = sr;
        String userText = is.getRecord("userstate").getString("utterance");
        if(userText.toLowerCase().contains("hi") || userText.toLowerCase().contains("hello")) {
            is.getRecord("userstate").set("intention", "greeting");
        } 
        else if( userText.toLowerCase().contains(" goodbye ") || userText.toLowerCase().contains("bye") ) {
            is.getRecord("userstate").set("intention", "ending");
        } 
        else {
            is.getRecord("userstate").set("intention", "Unknown");
        }
        is.getRecord("userstate").set("utterance", userText);
        
    }
    
    public void checkRules(){
        boolean keywordFound = false;
        for (Rules rule : rules) {
            int count = 0;
            for (String word : rule.getWords()) {
                for (String userUtt : userUtteranceAll) {
                    if (userUtt.toLowerCase().contains(word)) count++;
                }
            }
            if (count == rule.getWords().size()) {
                for (State state : rule.getStates()) {
                    is.set(state.getName(), state.getValue());
                }
                keywordFound = true;
                break;
            }
        }
    }

    public ArrayList<String> getUserUtteranceAll() {
        return userUtteranceAll;
    }

    public void setUserUtteranceAll(ArrayList<String> userUtteranceAll) {
        this.userUtteranceAll = userUtteranceAll;
    }

    public String getUserUtterance() {
        return userUtterance;
    }

    public void setUserUtterance(String userUtterance) {
        this.userUtterance = userUtterance;
    }

    public String getAgentUtterance() {
        return agentUtterance;
    }

    public void setAgentUtterance(String agentUtterance) {
        this.agentUtterance = agentUtterance;
    }
    
    

    @Override
    public void update(SubscribableRecord sr, SubscribableRecord.UpdateType ut, Item value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
