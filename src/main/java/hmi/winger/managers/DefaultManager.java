package hmi.winger.managers;

import hmi.flipper.behaviourselection.TemplateController;

import hmi.flipper.behaviourselection.template.behaviours.BehaviourClassProvider;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import java.util.Map;
import hmi.winger.flipperextensions.ManageableBehaviourClass;
import hmi.winger.flipperextensions.ManageableFunction;

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
   
    public DefaultManager(DefaultRecord is){
        this.is = is;
        this.tc = new TemplateController();
        this.previousTime = System.currentTimeMillis();
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
    public void addFunction(ManageableFunction functionInstance) {
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
    public void addGlobalBehaviour(String className, ManageableBehaviourClass instance)
    {
        BehaviourClassProvider.addBehaviour(className, instance);
    }
}
