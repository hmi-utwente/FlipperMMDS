package eu.ariaagent.flipper;

import eu.ariaagent.managers.Manager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manager controller for references to all managers and reading the templates
 * 
 * @author WaterschootJB
 */
public class ManagerController {
    
    private final ManagerParser mp;
    private final List <Manager> managers;
    
    /**
     * Construct a manager controller with a collection of managers and a parser for
     * reading the managers from files.
     * @param managerPath, the path where manager files are located.
     */
    public ManagerController(String managerPath){
        mp = new ManagerParser();
        Collection <Manager> newManagers = null;
        newManagers = mp.parseFolder(managerPath);
        managers = new ArrayList<>();
        managers.addAll(newManagers);
    }

    /**
     * Method that loops through all of the managers and checks if it's a manager's turn.
     * Also some extra checks to see if managers run out of sync.
     * Does not terminate
     */
    public void run(){
        if(managers == null || managers.isEmpty()){
            System.err.println("No managers to run found.");
        }
        while(!managers.isEmpty()){
            long startTime = System.currentTimeMillis();
            long nextTime = Long.MAX_VALUE;
            Manager nextManager = null;
            for(int i = 0; i < managers.size(); i++){
                Manager m = managers.get(i);
                if(m.timeUntilNextProcess() < nextTime){
                    nextManager = m;
                    nextTime = m.timeUntilNextProcess(); 
                }
            }
            long loopTime = System.currentTimeMillis() - startTime;
            nextTime -= loopTime;
            if(nextTime <= 0 && nextManager != null){
                nextManager.process();
                if(-nextTime > nextManager.getInterval()/2){
                    System.out.println("We waited over half of the interval time ("+ (-nextTime) + " out of " + nextManager.getInterval()+")");
                }else if(-nextTime > 50){
                    System.out.println("We waited over 50 ms too long. In total: "+ -nextTime + "ms.");
                }                
            }else{
                try {
                    if(nextTime > 0){
                        Thread.sleep(nextTime);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(ManagerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }    
}
