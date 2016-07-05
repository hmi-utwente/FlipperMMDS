/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.managers;

import hmi.flipper.behaviourselection.TemplateController;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import javax.validation.constraints.NotNull;

/**
 *
 * @author WaterschootJB
 */
public abstract class DefaultManager implements Manager{
    
    protected long interval;
    protected long previousTime;
    protected DefaultRecord is;
    protected TemplateController tc;
   
    public DefaultManager(@NotNull DefaultRecord is, long interval){
        this.is = is;
        this.tc = new TemplateController();
        this.interval = interval;
        this.previousTime = System.currentTimeMillis();
    }
    
    public DefaultManager(DefaultRecord is){
        this(is,500);
    }
    
    @Override
    public boolean canProcess(){
        if (System.currentTimeMillis() > previousTime + interval){
            previousTime = System.currentTimeMillis();
            return(true);
        }
        return(false);
    }
    
    @Override
    public long timeUntilNextProcess(){
        return(previousTime + interval - System.currentTimeMillis());
    }
    
    @Override
    public void process(){
        if(canProcess()){
            tc.checkTemplates(is);
        }
    }
    
    @Override
    public void setInterval(int ms){
        this.interval = ms;
    }
    
    @Override
    public long getInterval(int ms){
        return this.interval;
    }
    
    @Override
    public DefaultRecord getIs() {
        return is;
    }

    @Override
    public void setIS(DefaultRecord is) {
        this.is = is;
    }
    
    @Override
    public void setParams(String[] strings, String[][] stringArrays){
        //do nothing
    }
    
    @Override
    public void addTemplateFile(String templatePath){
        tc.processTemplateFile(templatePath);
    }
}
