/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.selection;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.cyberiantiger.minecraft.instances.Cuboid;
import org.cyberiantiger.minecraft.instances.command.InvocationException;

/**
 *
 * @author antony
 */
public class WorldEditCuboidSelection implements CuboidSelection {

    public static final String PLUGIN_NAME = "WorldEdit";
    private final Logger log;
    private final PluginManager manager;

    public WorldEditCuboidSelection(Logger log, PluginManager manager) {
        this.log = log;
        this.manager = manager;
    }

    protected boolean isPluginEnabled() {
        return manager.isPluginEnabled(PLUGIN_NAME);
    }

    protected WorldEditPlugin getPlugin() {
        return (WorldEditPlugin) manager.getPlugin(PLUGIN_NAME);
    }

    protected WorldEditAPI getAPI() {
        return new WorldEditAPI(getPlugin());
    }

    public Cuboid getCurrentSelection(Player player) {
        if (isPluginEnabled()) {
            WorldEditAPI api = getAPI();
            LocalSession session = api.getSession(player);
            LocalWorld world = session.getSelectionWorld();
            try {
                Region selection = session.getSelection(world);
                if (selection instanceof CuboidRegion) {
                    CuboidRegion cubeSelection = (CuboidRegion) selection;
                    Vector pos1 = cubeSelection.getPos1();
                    Vector pos2 = cubeSelection.getPos2();
                    return new Cuboid(world.getName(), pos1.getBlockX(), pos2.getBlockX(), pos1.getBlockY(), pos2.getBlockY(), pos1.getBlockZ(), pos2.getBlockZ());
                } else {
                    throw new InvocationException("You can only create portals for cuboid regions.");
                }
            } catch (IncompleteRegionException ex) {
                throw new InvocationException(ex.getLocalizedMessage());
            }
        }
        throw new InvocationException("Selection disabled: WorldEdit is disabled.");
    }

    public boolean isNative() {
        return false;
    }
}
