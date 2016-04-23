/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.depend;

import com.sk89q.worldguard.bukkit.ConfigurationManager;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.managers.RegionContainerImpl;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.util.Normal;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.util.DependencyFactory;

/**
 *
 * @author antony
 */
public class WorldGuardWorldInheritanceFactory extends DependencyFactory<Instances, WorldInheritance> {
    private static final String PLUGIN_NAME = "WorldGuard";
    
    public WorldGuardWorldInheritanceFactory(Instances thisPlugin) {
        super(thisPlugin, PLUGIN_NAME);
    }

    @Override
    public Class<WorldInheritance> getInterfaceClass() {
        return WorldInheritance.class;
    }

    @Override
    protected WorldInheritance createInterface(Plugin plugin) throws Exception {
        return new WorldGuardWorldInheritance(plugin);
    }

    private final class WorldGuardWorldInheritance implements WorldInheritance {
        private static final String EXCUSE = "Worldguard was not open to patches to make this possible without horrible hacks.";
        private final WorldGuardPlugin worldGuard;
        private final Field regionContainerContainer;
        private final Field regionContainerImplMapping;

        public WorldGuardWorldInheritance(Plugin plugin) throws NoSuchFieldException {
            this.worldGuard = (WorldGuardPlugin) plugin;
            this.regionContainerContainer = RegionContainer.class.getDeclaredField("container");
            this.regionContainerContainer.setAccessible(true);
            this.regionContainerImplMapping = RegionContainerImpl.class.getDeclaredField("mapping");
            this.regionContainerImplMapping.setAccessible(true);
        }

        public void preAddInheritance(String parent, String child) {
            try {
                RegionContainer regionContainer = worldGuard.getRegionContainer();
                RegionContainerImpl regionContainerImpl = (RegionContainerImpl) regionContainerContainer.get(regionContainer);
                RegionManager parentRegionManager = regionContainerImpl.get(parent);
                Normal childNormal = Normal.normal(child);
                ConcurrentMap<Normal, RegionManager> mapping = (ConcurrentMap<Normal, RegionManager>) regionContainerImplMapping.get(regionContainerImpl);
                mapping.putIfAbsent(childNormal, parentRegionManager);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                getThisPlugin().getLogger().log(Level.WARNING, null, ex);
            }
        }

        public void postAddInheritance(String parent, String child) {
        }

        public void preRemoveInheritance(String parent, String child) {
            try {
                RegionContainer regionContainer = worldGuard.getRegionContainer();
                RegionContainerImpl regionContainerImpl = (RegionContainerImpl) regionContainerContainer.get(regionContainer);
                RegionManager parentRegionManager = regionContainerImpl.get(parent);
                Normal childNormal = Normal.normal(child);
                ConcurrentMap<Normal, RegionManager> mapping = (ConcurrentMap<Normal, RegionManager>) regionContainerImplMapping.get(regionContainerImpl);
                if (mapping.get(childNormal) == parentRegionManager) {
                    // Dont force save
                    mapping.remove(childNormal);
                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                getThisPlugin().getLogger().log(Level.WARNING, null, ex);
            }
        }

        public void postRemoveInheritance(String parent, String child) {
        }

        public Plugin getPlugin() {
            return worldGuard;
        }
    }
}
