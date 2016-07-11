/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.network;

import eu.ariaagent.managers.Manager;
import eu.ariaagent.managers.SimpleManager;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Stack;

/**
 *
 * @author WaterschootJB
 */
public class StackingManagers {
    
    
    
    public StackingManagers(){    
        Stack<Manager> managers = new Stack();
        Collection paths = new ArrayList();
        paths.add("D:/GitHub/FlipperExample/Example.xml");
        Manager manager1 = new SimpleManager(new DefaultRecord(),50,paths,null);
        Manager manager2 = new SimpleManager(new DefaultRecord(),500,paths,null);
        Manager manager3 = new SimpleManager(new DefaultRecord(),5000,paths,null);
        managers.add(manager1);
        managers.add(manager2);
        managers.add(manager3);
        Comparator mc = new NextManagerComparator();
        while(!managers.isEmpty()){
            managers.sort(mc);
            Manager cm = managers.peek();
            if(cm.timeUntilNextProcess() < 0){
                System.out.println(cm.getInterval() + cm.managerName());
                cm.process();
            }            
        }        
    }
    
    public static void main(String[] args){
        
        StackingManagers sm = new StackingManagers();       
        
        
    }

    private static class NextManagerComparator implements Comparator<Manager> {

        public NextManagerComparator() {
        }

        @Override
        public int compare(Manager m1, Manager m2) {
            return (int) (m2.timeUntilNextProcess()-m1.timeUntilNextProcess());
        }
    }
    
   
  
            
   
    
}
