/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.util;

import eu.ariaagent.managers.Manager;

/**
 *
 * @author Siewart
 */
public interface ManageableFunction {
    Manager getManager();
    void setManager(Manager manager);
}
