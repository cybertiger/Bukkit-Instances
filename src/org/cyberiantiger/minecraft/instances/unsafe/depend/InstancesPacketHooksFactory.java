/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.depend;

import org.cyberiantiger.minecraft.instances.unsafe.PacketHooks;
import org.bukkit.plugin.Plugin;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.util.DependencyFactory;
import org.cyberiantiger.minecraft.unsafe.CBShim;

/**
 *
 * @author antony
 */
public class InstancesPacketHooksFactory extends DependencyFactory<PacketHooks> {

    public InstancesPacketHooksFactory(Plugin instances) {
        super(instances, instances.getName());
    }

    @Override
    public Class<PacketHooks> getInterfaceClass() {
        return PacketHooks.class;
    }

    @Override
    protected PacketHooks createInterface(Plugin plugin) throws Exception {
        Instances instances = (Instances) plugin;
        return CBShim.createShim(PacketHooks.class, plugin, plugin, instances.getEditCommandInCreative());
    }
}
