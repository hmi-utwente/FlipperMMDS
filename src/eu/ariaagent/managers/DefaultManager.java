/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.managers;

import hmi.flipper.behaviourselection.TemplateController;
import hmi.flipper.behaviourselection.template.behaviours.BehaviourClassProvider;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.constraints.NotNull;

/**
 *
 * @author WaterschootJB
 */
public abstract class DefaultManager implements Manager
{
    
    protected long interval;
    protected long previousTime;
    protected DefaultRecord is;
    protected TemplateController tc;
    protected String name;
    protected String id;
   
    public DefaultManager(@NotNull DefaultRecord is, long interval){
        this.is = is;
        this.tc = new TemplateController();
        this.interval = interval;
        this.previousTime = System.currentTimeMillis();
    }
    
    public DefaultManager(DefaultRecord is){
        this(is,500);
    }
    
    /**
     * Determines if the manager's time is already up.
     * @return true if it is, false otherwise.
     */
    private boolean canProcess(){
        return System.currentTimeMillis() >= previousTime + interval;
    }
    
    @Override
    public long timeUntilNextProcess(){
        return(previousTime + interval - System.currentTimeMillis());
    }
    
    @Override
    public void process(){ 
        if (canProcess()){
            previousTime = System.currentTimeMillis(); 
            tc.checkTemplates(is);
        }
    }
    
    @Override
    public void setInterval(int ms){
        this.interval = ms;
    }
    
    @Override
    public long getInterval(){
        return this.interval;
    }
    
    @Override
    public DefaultRecord getIS() {
        return is;
    }

    @Override
    public void setIS(DefaultRecord is) {
        this.is = is;
    }
    

    
    @Override
    public void addTemplateFile(String templatePath){
        tc.processTemplateFile(templatePath);
    }
    
    @Override
    public void addFunction(Object functionInstance) {
        tc.addFunction(functionInstance);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public void setID(String id) {
        this.id = id;
    }

    @Override
    public void setParams(Map<String, String> namedValues, Map<String, String[]> namedLists) {
        //Do nothing
    }
    
    @Override
    public void addBehaviourClasses(String jarPath)
    {
        System.out.println("AddBehaviourClasses:"+jarPath);
        URL[] urls = null;
        try {
            urls = new URL[]{new URL("jar:file:"+jarPath+"!/")};
            BehaviourClassProvider.addExternalClasses(urls);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DefaultManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
