/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe;

import org.bukkit.plugin.Plugin;

/**
 *
 * @author antony
 */
public interface PacketHooks {

    public void configure(Plugin plugin, boolean editCommandInCreative);

    public void setInstalled(boolean b);
    
}
