/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.depend;

import com.sk89q.worldguard.bukkit.ConfigurationManager;
import com.sk89q.worldguard.bukkit.WorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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
        private final Field regionManagers;
        private final Field configMap;

        public WorldGuardWorldInheritance(Plugin plugin) throws NoSuchFieldException {
            this.worldGuard = (WorldGuardPlugin) plugin;
            regionManagers = GlobalRegionManager.class.getDeclaredField("managers");
            regionManagers.setAccessible(true);
            configMap = ConfigurationManager.class.getDeclaredField("worlds");
            configMap.setAccessible(true);
        }

        public void preAddInheritance(String parent, String child) {
            try {
                World world = getThisPlugin().getServer().getWorld(parent);
                if (world == null) {
                    world = new FakeWorld(parent);
                }
                ConfigurationManager configManager = worldGuard.getGlobalStateManager();
                WorldConfiguration config = configManager.get(world);
                {
                    ConcurrentHashMap<String, WorldConfiguration> map = 
                            (ConcurrentHashMap<String, WorldConfiguration>) configMap.get(configManager);
                    map.put(child, config);
                }
                {
                    ConcurrentHashMap<String, RegionManager> map =
                            (ConcurrentHashMap<String, RegionManager>) regionManagers.get(worldGuard.getGlobalRegionManager());
                    RegionManager regionManager = map.get(parent);
                    if (regionManager == null) {
                        regionManager = worldGuard.getGlobalRegionManager().create(world);
                        map.putIfAbsent(parent, regionManager);
                    }
                    regionManager = map.get(parent);
                    map.putIfAbsent(child, regionManager);
                }
            } catch (IllegalArgumentException ex) {
                getThisPlugin().getLogger().log(Level.WARNING, null, ex);
            } catch (IllegalAccessException ex) {
                getThisPlugin().getLogger().log(Level.WARNING, null, ex);
            }
        }

        public void postAddInheritance(String parent, String child) {
        }

        public void preRemoveInheritance(String parent, String child) {
            try {
                ConcurrentHashMap map = (ConcurrentHashMap) regionManagers.get(worldGuard.getGlobalRegionManager());
                map.remove(child);
                map = (ConcurrentHashMap) configMap.get(worldGuard.getGlobalStateManager());
                map.remove(child);
            } catch (IllegalArgumentException ex) {
                getThisPlugin().getLogger().log(Level.WARNING, null, ex);
            } catch (IllegalAccessException ex) {
                getThisPlugin().getLogger().log(Level.WARNING, null, ex);
            }
        }

        public void postRemoveInheritance(String parent, String child) {
        }

        public Plugin getPlugin() {
            return worldGuard;
        }
    }

    private static class FakeWorld implements World {
        private final String name;

        public FakeWorld(String name) {
            this.name = name;
        }

        @Override
        public Block getBlockAt(int i, int i1, int i2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Block getBlockAt(Location lctn) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getBlockTypeIdAt(int i, int i1, int i2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getBlockTypeIdAt(Location lctn) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getHighestBlockYAt(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getHighestBlockYAt(Location lctn) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Block getHighestBlockAt(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Block getHighestBlockAt(Location lctn) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Chunk getChunkAt(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Chunk getChunkAt(Location lctn) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Chunk getChunkAt(Block block) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isChunkLoaded(Chunk chunk) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Chunk[] getLoadedChunks() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void loadChunk(Chunk chunk) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isChunkLoaded(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isChunkInUse(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void loadChunk(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean loadChunk(int i, int i1, boolean bln) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean unloadChunk(Chunk chunk) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean unloadChunk(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean unloadChunk(int i, int i1, boolean bln) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean unloadChunk(int i, int i1, boolean bln, boolean bln1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean unloadChunkRequest(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean unloadChunkRequest(int i, int i1, boolean bln) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean regenerateChunk(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean refreshChunk(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Item dropItem(Location lctn, ItemStack is) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Item dropItemNaturally(Location lctn, ItemStack is) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Arrow spawnArrow(Location lctn, Vector vector, float f, float f1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean generateTree(Location lctn, TreeType tt) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean generateTree(Location lctn, TreeType tt, BlockChangeDelegate bcd) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Entity spawnEntity(Location lctn, EntityType et) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LightningStrike strikeLightning(Location lctn) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LightningStrike strikeLightningEffect(Location lctn) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Entity> getEntities() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<LivingEntity> getLivingEntities() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... types) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Entity> getEntitiesByClasses(Class<?>... types) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Player> getPlayers() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public UUID getUID() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Location getSpawnLocation() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean setSpawnLocation(int i, int i1, int i2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setTime(long l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getFullTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setFullTime(long l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasStorm() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setStorm(boolean bln) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getWeatherDuration() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setWeatherDuration(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isThundering() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setThundering(boolean bln) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getThunderDuration() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setThunderDuration(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean createExplosion(double d, double d1, double d2, float f) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean createExplosion(double d, double d1, double d2, float f, boolean bln) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean createExplosion(double d, double d1, double d2, float f, boolean bln, boolean bln1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean createExplosion(Location lctn, float f) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean createExplosion(Location lctn, float f, boolean bln) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Environment getEnvironment() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getSeed() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean getPVP() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPVP(boolean bln) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ChunkGenerator getGenerator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void save() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<BlockPopulator> getPopulators() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T extends Entity> T spawn(Location lctn, Class<T> type) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        @Override
        public FallingBlock spawnFallingBlock(Location lctn, Material mtrl, byte b) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        @Override
        public FallingBlock spawnFallingBlock(Location lctn, int i, byte b) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void playEffect(Location lctn, Effect effect, int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void playEffect(Location lctn, Effect effect, int i, int i1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> void playEffect(Location lctn, Effect effect, T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> void playEffect(Location lctn, Effect effect, T t, int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ChunkSnapshot getEmptyChunkSnapshot(int i, int i1, boolean bln, boolean bln1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setSpawnFlags(boolean bln, boolean bln1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean getAllowAnimals() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean getAllowMonsters() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Biome getBiome(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setBiome(int i, int i1, Biome biome) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double getTemperature(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double getHumidity(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getMaxHeight() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getSeaLevel() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean getKeepSpawnInMemory() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setKeepSpawnInMemory(boolean bln) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isAutoSave() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setAutoSave(boolean bln) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDifficulty(Difficulty dfclt) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Difficulty getDifficulty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public File getWorldFolder() {
            throw new UnsupportedOperationException();
        }

        @Override
        public WorldType getWorldType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean canGenerateStructures() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getTicksPerAnimalSpawns() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setTicksPerAnimalSpawns(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getTicksPerMonsterSpawns() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setTicksPerMonsterSpawns(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getMonsterSpawnLimit() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setMonsterSpawnLimit(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getAnimalSpawnLimit() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setAnimalSpawnLimit(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getWaterAnimalSpawnLimit() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setWaterAnimalSpawnLimit(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getAmbientSpawnLimit() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setAmbientSpawnLimit(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void playSound(Location lctn, Sound sound, float f, float f1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String[] getGameRules() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getGameRuleValue(String string) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean setGameRuleValue(String string, String string1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isGameRule(String string) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendPluginMessage(Plugin plugin, String string, byte[] bytes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<String> getListeningPluginChannels() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setMetadata(String string, MetadataValue mv) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<MetadataValue> getMetadata(String string) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasMetadata(String string) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeMetadata(String string, Plugin plugin) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Entity> getNearbyEntities(Location lctn, double d, double d1, double d2) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public WorldBorder getWorldBorder() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void spawnParticle(Particle prtcl, Location lctn, int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void spawnParticle(Particle prtcl, double d, double d1, double d2, int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T> void spawnParticle(Particle prtcl, Location lctn, int i, T t) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T> void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, T t) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T> void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2, T t) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T> void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5, T t) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2, double d3) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5, double d6) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T> void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2, double d3, T t) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T> void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5, double d6, T t) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
}
