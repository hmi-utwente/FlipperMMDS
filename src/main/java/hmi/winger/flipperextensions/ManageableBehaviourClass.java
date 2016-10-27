package hmi.winger.flipperextensions;

import hmi.winger.managers.Manager;
import hmi.flipper.behaviourselection.behaviours.BehaviourClass;


/**
 *
 * @author Siewart
 */
public interface ManageableBehaviourClass extends BehaviourClass{
    Manager getManager();
    void setManager(Manager manager);
}
