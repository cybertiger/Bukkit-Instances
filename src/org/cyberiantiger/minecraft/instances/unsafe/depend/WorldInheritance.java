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
    public void addInheritance(String parent, String child);
    public void removeInheritance(String parent, String child);
}
