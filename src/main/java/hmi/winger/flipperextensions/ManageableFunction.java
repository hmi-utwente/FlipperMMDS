package hmi.winger.flipperextensions;

import hmi.winger.managers.Manager;

/**
 *
 * @author Siewart
 */
public interface ManageableFunction {
    Manager getManager();
    void setManager(Manager manager);
}
