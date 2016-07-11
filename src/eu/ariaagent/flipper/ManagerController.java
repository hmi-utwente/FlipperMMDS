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
        Collection <Manager> newManagers = mp.parseFolder(managerPath);
        managers = new ArrayList<>();
        managers.addAll(newManagers);
    }

    /**
     * Method that loops through all of the managers and checks if it's a manager's turn.
     * Also some extra checks to see if managers run out of sync.
     */
    public void start(){
        if(managers == null || managers.isEmpty()){
            System.err.println("You fool, you have no managers.");
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
            if(nextTime < 0 && nextManager != null){
                nextManager.process();
                if(-nextTime > nextManager.getInterval()/2){
                    System.out.println("We waited over half of the interval time ("+ (-nextTime) + " out of " + nextManager.getInterval()+")");
                }else if(-nextTime > 10){
                    System.out.println("We waited over 10 ms too long. In total: "+ -nextTime + "ms.");
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
    
    private void startAlternative(){
        Stack <Manager> managers2 = new Stack<>();
        while(!managers2.isEmpty()){
            managers2.sort(new NextManagerComparator());
            Manager m = managers2.peek();
            if(m.timeUntilNextProcess() < 0){
                m = managers2.pop();
                m.process();
                managers2.push(m);
            }
        }
    }

    private static class NextManagerComparator implements Comparator<Manager> {

        public NextManagerComparator() {
        }

        @Override
        public int compare(Manager m1, Manager m2) {
            return (int) (m2.timeUntilNextProcess() - m1.timeUntilNextProcess());
        }
    }
}
