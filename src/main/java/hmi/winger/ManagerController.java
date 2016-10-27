package hmi.winger;

import hmi.winger.managers.DefaultManager;
import hmi.winger.managers.Manager;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manager controller for references to all managers and reading the templates
 * 
 * @author Siewart van Wingerden
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
    
    public ManagerController(String managerPath, DefaultRecord is){
        mp = new ManagerParser(is);
        Collection <Manager> newManagers = null;
        newManagers = mp.parseFolder(managerPath);
        managers = new ArrayList<>();
        managers.addAll(newManagers);
    }
    
    public void addManager(Manager m){
        managers.add(m);
    }

    public DefaultRecord getIS(){
        return mp.getIS();
    }
    /**
     * Method that loops through all of the managers and checks if its a manager's turn.
     * Also some extra checks to see if managers run out of sync.
     * Does not terminate and pauses thread, recommended to run in its own Thread.
     */
    public void run(){
        if(managers == null || managers.isEmpty()){
            System.err.println("No managers to run found.");
        }
        while(!managers.isEmpty()){
            long startTime = System.currentTimeMillis();
            long nextTime = Long.MAX_VALUE;
            long longestItTime = 0;
            Manager longManager = null;
            Manager nextManager = null;
            for(int i = 0; i < managers.size(); i++){
                long itStart = System.currentTimeMillis();
                Manager m = managers.get(i);
                if(m.timeUntilNextProcess() < nextTime){
                    if(m.timeUntilNextProcess() <= 0){
                        processManager(m);
                    }
                    if(m.timeUntilNextProcess() < nextTime) // if still next, set as next manager
                    {
                        nextManager = m;
                        nextTime = m.timeUntilNextProcess(); 
                    }
                }
                long itEnd = System.currentTimeMillis();
                if(itEnd - itStart > longestItTime){
                    longestItTime = itEnd - itStart;
                    longManager = m;
                }
            }
            long loopTime = System.currentTimeMillis() - startTime;
            if(loopTime > 50){
                System.out.println("Time it took for looping all managers was over 50ms: " + loopTime);
                if(longManager != null){
                    System.out.println("Most time intensive was: " + longManager.getName() +"(" + longManager.getID() + ") taking :"+ longestItTime + "ms.");
                }
            }
            nextTime -= loopTime;
            if(nextTime <= 0 && nextManager != null){
                processManager(nextManager);
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

    private void processManager(Manager m){
        m.process();
        long pastTime = -m.timeUntilNextProcess();
        if(pastTime > 10000){
            System.out.println("We waited a very long time for " + m.getName() +"(" + m.getID() + ") Does this this process return at all?");
            if(m instanceof DefaultManager){
                System.out.println("Since this manager extends DefaultManager this could also be caused by not calling super.process() or otherwise updating timeUntillNextProcess()");
            }
        }
        if(pastTime > m.getInterval()/2){
            System.out.println("We waited over half of the interval time for " + m.getName() +"(" + m.getID() + ") - ("+ (pastTime) + " out of " + m.getInterval()+")");
        }else if(pastTime > 50){
            System.out.println("We waited over 50 ms too long for " + m.getName() +"(" + m.getID() + "). In total: "+ pastTime + "ms.");
        }
    }
    
}
