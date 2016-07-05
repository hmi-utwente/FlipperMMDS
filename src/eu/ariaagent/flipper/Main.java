package eu.ariaagent.flipper;
/**
 * Main class for initializing the MMDS, with a path to where dialogue managers are located.
 * @author WaterschootJB
 */
public class Main {
    
    /**
     * @param args the command line arguments, of which one can be the path to where the managers are located.
     */
    public static void main( String args[] )
    {
        String managerPath = System.getProperty("managerpath",null);
        if(managerPath == null){
            System.getProperty("user.dir");
        }
        else{
            System.err.print("Make sure you initialize your application from an existing path!");
            return;
        }
        ManagerController dialogueManagementSystem = new ManagerController(managerPath);
    }   
}
