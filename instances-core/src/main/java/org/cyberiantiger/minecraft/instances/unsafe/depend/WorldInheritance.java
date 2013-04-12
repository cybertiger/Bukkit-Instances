/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.unsafe.depend;

import org.cyberiantiger.minecraft.instances.util.Dependency;

/**
 *
 * @author antony
 */
public interface WorldInheritance extends Dependency {
    public void preAddInheritance(String parent, String child);
    public void postAddInheritance(String parent, String child);
    public void preRemoveInheritance(String parent, String child);
    public void postRemoveInheritance(String parent, String child);
}
