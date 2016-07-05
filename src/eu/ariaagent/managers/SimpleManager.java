/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.managers;

import javax.validation.constraints.NotNull;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import java.util.Collection;

/**
 *
 * @author WaterschootJB
 */
public class SimpleManager extends DefaultManager{
    
    private Collection <String> paths;
    private Collection <Object> functions;
    
    public SimpleManager(DefaultRecord is, long interval, @NotNull Collection <String> paths, Collection <Object> functions){
        super(is,interval);
        this.paths = paths;
        this.functions = functions;
        for(String p : paths){
            super.tc.processTemplateFile(p);
        }
        if(functions != null){
            for(Object f : functions){
                super.tc.addFunction(f);
            }
        }        
    } 

    @Override
    public void addFunction(String className, String path) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
