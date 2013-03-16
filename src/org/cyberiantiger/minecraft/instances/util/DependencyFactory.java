/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.util;

import java.util.logging.Level;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author antony
 */
public abstract class DependencyFactory<T extends Dependency> {

    private final Plugin thisPlugin;
    private final String plugin;
    private boolean cannotLoad = false;
    private T dependency;

    public DependencyFactory(Plugin thisPlugin, String plugin) {
        this.thisPlugin = thisPlugin;
        this.plugin = plugin;
    }

    public final String getPlugin() {
        return plugin;
    }

    public final T getDependecy() {
        if (!thisPlugin.isEnabled()) {
            dependency = null;
            return null;
        }
        Plugin depPlugin;
        if (plugin.equals(thisPlugin.getName()))  {
            depPlugin = thisPlugin;
        } else {
            depPlugin = thisPlugin.getServer().getPluginManager().getPlugin(plugin);
            if (depPlugin == null || !depPlugin.isEnabled()) {
                dependency = null;
                return null;
            }
            if (dependency != null) {
                if (dependency.getPlugin() != depPlugin) {
                    dependency = null;
                } else {
                    return dependency;
                }
            }
            if (cannotLoad) {
                return dependency;
            }
        }
        try {
            dependency = createInterface(depPlugin);
        } catch (Throwable ex) {
            cannotLoad = true;
            thisPlugin.getLogger().log(Level.WARNING,
                    "Error loading dependecy interface " + getInterfaceClass().getName() + " for plugin " + plugin, ex);
        }
        return dependency;
    }

    public abstract Class<T> getInterfaceClass();

    protected abstract T createInterface(Plugin plugin) throws Exception;
}
