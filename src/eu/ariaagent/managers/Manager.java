/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.managers;

import hmi.flipper.defaultInformationstate.DefaultRecord;

/**
 *
 * @author WaterschootJB
 */
public interface Manager {
        
    boolean canProcess();
    
    long timeUntilNextProcess();

    long getInterval(int ms);

    DefaultRecord getIs();

    void process();

    void setInterval(int ms);

    void setIS(DefaultRecord is);
    
    void addTemplateFile(String templatePath);
    
    void setParams(String[] strings, String[][] stringArrays);
    
    void addFunction(String className, String path); //perhaps we only allow ofther stuffs, see simpleexample
    
}
