/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.unsafe.permissions;

/**
 *
 * @author antony
 */
public interface Permissions {
    public void addInheritance(String parent, String child);
    public void removeInheritance(String parent, String child);
}
