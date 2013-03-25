/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.depend;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.cyberiantiger.minecraft.instances.Cuboid;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.Selection;
import org.cyberiantiger.minecraft.instances.command.InvocationException;
import org.cyberiantiger.minecraft.instances.util.DependencyFactory;

/**
 *
 * @author antony
 */
public class InstancesCuboidSelectionFactory extends DependencyFactory<CuboidSelection> {

    public InstancesCuboidSelectionFactory(Plugin plugin) {
        super(plugin, plugin.getName());
    }

    @Override
    public Class<CuboidSelection> getInterfaceClass() {
        return CuboidSelection.class;
    }

    @Override
    protected CuboidSelection createInterface(Plugin plugin) throws Exception {
        return new InstancesCuboidSelection(plugin);
    }

    private static class InstancesCuboidSelection implements CuboidSelection {

        private final Instances instances;

        public InstancesCuboidSelection(Plugin plugin) {
            this.instances = (Instances) plugin;
        }

        public Cuboid getCurrentSelection(Player player) throws InvocationException {
            Selection sel = instances.getSelection(player);
            if (sel.isValid()) {
                return sel.getCuboid();
            } else {
                throw new InvocationException("You do not currently have a valid selection.");
            }
        }

        public boolean isNative() {
            return true;
        }

        public Plugin getPlugin() {
            return instances;
        }
    }
}
