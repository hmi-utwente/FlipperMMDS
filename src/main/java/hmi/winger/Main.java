package hmi.winger;
/**
 * Main class for initialising the Winger instance.
 * @author Siewart van Wingerden
 */
public class Main {
    
    /**
     * @param args the command line arguments, of which one can be the path to where the managers are located.
     */
    public static void main( String args[] )
    {
        
        for (String arg : args) {
            if(arg.equals("-help")){
                System.out.println("-Dmanagerpath: Custom manager xml location e.g.: \"C:\\MyManagers\"'");
            }
        }
        String managerPath = System.getProperty("managerpath", null);
        if(managerPath == null && System.getProperty("user.dir") != null){
            managerPath = System.getProperty("user.dir") + "\\config";
        }
        else if(managerPath == null){
            System.err.println(
                "Make sure you initialize your application " +
                "from an existing path! " +
                "e.g. use 'java FlipperMMDS.jar -Dmanagerpath=\"C:\\MyManagers\"'"
            );
            return;
        }
        ManagerController dialogueManagementSystem = new ManagerController(managerPath);
        dialogueManagementSystem.run();
    }   
}
