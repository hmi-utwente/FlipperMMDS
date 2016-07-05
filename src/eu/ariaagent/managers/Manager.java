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
    
    /**
     * Method for calculating the time until the manager needs to check its template again.
     * @return the time.
     */
    long timeUntilNextProcess();

    /**
     * Returns the interval of the manager
     * @return the interval
     */
    long getInterval();
    
    /**
     * Returns the information state the manager can read from/update to
     * @return the information state
     */
    DefaultRecord getIS();
    
    /**
     * Executes the manager's corresponding templates
     */
    void process();

    /**
     * Sets the interval of the manager
     * @param ms, time in milliseconds 
     */
    void setInterval(int ms);

    /**
     * Sets the information state of the manager
     * @param is, the information state
     */
    void setIS(DefaultRecord is);
    
    /**
     * Add a template file from which the manager must read rules
     * @param templatePath, the filepath of the the template
     */
    void addTemplateFile(String templatePath);
    
    /**
     * Parses parameters of a custom class and calls them.
     * @param strings, the calls
     * @param stringArrays, the lists of arguments
     */
    void setParams(String[] strings, String[][] stringArrays);
    
    /**
     * Add a custom Java function to the manager
     * @param functionInstance, functions to call from the manager
     */
    void addFunction(Object functionInstance);
    
}
