/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.depend;

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
import org.bukkit.Sound;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
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
import org.cyberiantiger.minecraft.instances.util.DependencyFactory;

/**
 *
 * @author antony
 */
public class WorldGuardWorldInheritanceFactory extends DependencyFactory<WorldInheritance> {
    private static final String PLUGIN_NAME = "WorldGuard";
    
    public WorldGuardWorldInheritanceFactory(Plugin thisPlugin) {
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

        public WorldGuardWorldInheritance(Plugin plugin) throws NoSuchFieldException {
            this.worldGuard = (WorldGuardPlugin) plugin;
            regionManagers = GlobalRegionManager.class.getDeclaredField("managers");
            regionManagers.setAccessible(true);
        }

        public void addInheritance(String parent, String child) {
            try {
                ConcurrentHashMap<String, RegionManager> map = 
                        (ConcurrentHashMap<String, RegionManager>) regionManagers.get(worldGuard.getGlobalRegionManager());
                RegionManager regionManager = map.get(parent);
                if (regionManager == null) {
                    World world = getThisPlugin().getServer().getWorld(parent);
                    if (world == null) {
                        world = new FakeWorld(parent);
                        regionManager = worldGuard.getGlobalRegionManager().create(world);
                    } else {
                        regionManager = worldGuard.getGlobalRegionManager().create(world);
                    }
                    map.putIfAbsent(parent, regionManager);
                }
                regionManager = map.get(parent);
                map.putIfAbsent(child, regionManager);
            } catch (IllegalArgumentException ex) {
                getThisPlugin().getLogger().log(Level.WARNING, null, ex);
            } catch (IllegalAccessException ex) {
                getThisPlugin().getLogger().log(Level.WARNING, null, ex);
            }
        }

        public void removeInheritance(String parent, String child) {
            try {
                ConcurrentHashMap map = (ConcurrentHashMap) regionManagers.get(worldGuard.getGlobalRegionManager());
                map.remove(child);
            } catch (IllegalArgumentException ex) {
                getThisPlugin().getLogger().log(Level.WARNING, null, ex);
            } catch (IllegalAccessException ex) {
                getThisPlugin().getLogger().log(Level.WARNING, null, ex);
            }
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

        public Block getBlockAt(int i, int i1, int i2) {
            throw new UnsupportedOperationException();
        }

        public Block getBlockAt(Location lctn) {
            throw new UnsupportedOperationException();
        }

        public int getBlockTypeIdAt(int i, int i1, int i2) {
            throw new UnsupportedOperationException();
        }

        public int getBlockTypeIdAt(Location lctn) {
            throw new UnsupportedOperationException();
        }

        public int getHighestBlockYAt(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        public int getHighestBlockYAt(Location lctn) {
            throw new UnsupportedOperationException();
        }

        public Block getHighestBlockAt(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        public Block getHighestBlockAt(Location lctn) {
            throw new UnsupportedOperationException();
        }

        public Chunk getChunkAt(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        public Chunk getChunkAt(Location lctn) {
            throw new UnsupportedOperationException();
        }

        public Chunk getChunkAt(Block block) {
            throw new UnsupportedOperationException();
        }

        public boolean isChunkLoaded(Chunk chunk) {
            throw new UnsupportedOperationException();
        }

        public Chunk[] getLoadedChunks() {
            throw new UnsupportedOperationException();
        }

        public void loadChunk(Chunk chunk) {
            throw new UnsupportedOperationException();
        }

        public boolean isChunkLoaded(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        public boolean isChunkInUse(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        public void loadChunk(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        public boolean loadChunk(int i, int i1, boolean bln) {
            throw new UnsupportedOperationException();
        }

        public boolean unloadChunk(Chunk chunk) {
            throw new UnsupportedOperationException();
        }

        public boolean unloadChunk(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        public boolean unloadChunk(int i, int i1, boolean bln) {
            throw new UnsupportedOperationException();
        }

        public boolean unloadChunk(int i, int i1, boolean bln, boolean bln1) {
            throw new UnsupportedOperationException();
        }

        public boolean unloadChunkRequest(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        public boolean unloadChunkRequest(int i, int i1, boolean bln) {
            throw new UnsupportedOperationException();
        }

        public boolean regenerateChunk(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        public boolean refreshChunk(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        public Item dropItem(Location lctn, ItemStack is) {
            throw new UnsupportedOperationException();
        }

        public Item dropItemNaturally(Location lctn, ItemStack is) {
            throw new UnsupportedOperationException();
        }

        public Arrow spawnArrow(Location lctn, Vector vector, float f, float f1) {
            throw new UnsupportedOperationException();
        }

        public boolean generateTree(Location lctn, TreeType tt) {
            throw new UnsupportedOperationException();
        }

        public boolean generateTree(Location lctn, TreeType tt, BlockChangeDelegate bcd) {
            throw new UnsupportedOperationException();
        }

        public Entity spawnEntity(Location lctn, EntityType et) {
            throw new UnsupportedOperationException();
        }

        public LivingEntity spawnCreature(Location lctn, EntityType et) {
            throw new UnsupportedOperationException();
        }

        public LivingEntity spawnCreature(Location lctn, CreatureType ct) {
            throw new UnsupportedOperationException();
        }

        public LightningStrike strikeLightning(Location lctn) {
            throw new UnsupportedOperationException();
        }

        public LightningStrike strikeLightningEffect(Location lctn) {
            throw new UnsupportedOperationException();
        }

        public List<Entity> getEntities() {
            throw new UnsupportedOperationException();
        }

        public List<LivingEntity> getLivingEntities() {
            throw new UnsupportedOperationException();
        }

        public <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... types) {
            throw new UnsupportedOperationException();
        }

        public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> type) {
            throw new UnsupportedOperationException();
        }

        public Collection<Entity> getEntitiesByClasses(Class<?>... types) {
            throw new UnsupportedOperationException();
        }

        public List<Player> getPlayers() {
            throw new UnsupportedOperationException();
        }

        public String getName() {
            return name;
        }

        public UUID getUID() {
            throw new UnsupportedOperationException();
        }

        public Location getSpawnLocation() {
            throw new UnsupportedOperationException();
        }

        public boolean setSpawnLocation(int i, int i1, int i2) {
            throw new UnsupportedOperationException();
        }

        public long getTime() {
            throw new UnsupportedOperationException();
        }

        public void setTime(long l) {
            throw new UnsupportedOperationException();
        }

        public long getFullTime() {
            throw new UnsupportedOperationException();
        }

        public void setFullTime(long l) {
            throw new UnsupportedOperationException();
        }

        public boolean hasStorm() {
            throw new UnsupportedOperationException();
        }

        public void setStorm(boolean bln) {
            throw new UnsupportedOperationException();
        }

        public int getWeatherDuration() {
            throw new UnsupportedOperationException();
        }

        public void setWeatherDuration(int i) {
            throw new UnsupportedOperationException();
        }

        public boolean isThundering() {
            throw new UnsupportedOperationException();
        }

        public void setThundering(boolean bln) {
            throw new UnsupportedOperationException();
        }

        public int getThunderDuration() {
            throw new UnsupportedOperationException();
        }

        public void setThunderDuration(int i) {
            throw new UnsupportedOperationException();
        }

        public boolean createExplosion(double d, double d1, double d2, float f) {
            throw new UnsupportedOperationException();
        }

        public boolean createExplosion(double d, double d1, double d2, float f, boolean bln) {
            throw new UnsupportedOperationException();
        }

        public boolean createExplosion(double d, double d1, double d2, float f, boolean bln, boolean bln1) {
            throw new UnsupportedOperationException();
        }

        public boolean createExplosion(Location lctn, float f) {
            throw new UnsupportedOperationException();
        }

        public boolean createExplosion(Location lctn, float f, boolean bln) {
            throw new UnsupportedOperationException();
        }

        public Environment getEnvironment() {
            throw new UnsupportedOperationException();
        }

        public long getSeed() {
            throw new UnsupportedOperationException();
        }

        public boolean getPVP() {
            throw new UnsupportedOperationException();
        }

        public void setPVP(boolean bln) {
            throw new UnsupportedOperationException();
        }

        public ChunkGenerator getGenerator() {
            throw new UnsupportedOperationException();
        }

        public void save() {
            throw new UnsupportedOperationException();
        }

        public List<BlockPopulator> getPopulators() {
            throw new UnsupportedOperationException();
        }

        public <T extends Entity> T spawn(Location lctn, Class<T> type) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        public FallingBlock spawnFallingBlock(Location lctn, Material mtrl, byte b) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        public FallingBlock spawnFallingBlock(Location lctn, int i, byte b) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        public void playEffect(Location lctn, Effect effect, int i) {
            throw new UnsupportedOperationException();
        }

        public void playEffect(Location lctn, Effect effect, int i, int i1) {
            throw new UnsupportedOperationException();
        }

        public <T> void playEffect(Location lctn, Effect effect, T t) {
            throw new UnsupportedOperationException();
        }

        public <T> void playEffect(Location lctn, Effect effect, T t, int i) {
            throw new UnsupportedOperationException();
        }

        public ChunkSnapshot getEmptyChunkSnapshot(int i, int i1, boolean bln, boolean bln1) {
            throw new UnsupportedOperationException();
        }

        public void setSpawnFlags(boolean bln, boolean bln1) {
            throw new UnsupportedOperationException();
        }

        public boolean getAllowAnimals() {
            throw new UnsupportedOperationException();
        }

        public boolean getAllowMonsters() {
            throw new UnsupportedOperationException();
        }

        public Biome getBiome(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        public void setBiome(int i, int i1, Biome biome) {
            throw new UnsupportedOperationException();
        }

        public double getTemperature(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        public double getHumidity(int i, int i1) {
            throw new UnsupportedOperationException();
        }

        public int getMaxHeight() {
            throw new UnsupportedOperationException();
        }

        public int getSeaLevel() {
            throw new UnsupportedOperationException();
        }

        public boolean getKeepSpawnInMemory() {
            throw new UnsupportedOperationException();
        }

        public void setKeepSpawnInMemory(boolean bln) {
            throw new UnsupportedOperationException();
        }

        public boolean isAutoSave() {
            throw new UnsupportedOperationException();
        }

        public void setAutoSave(boolean bln) {
            throw new UnsupportedOperationException();
        }

        public void setDifficulty(Difficulty dfclt) {
            throw new UnsupportedOperationException();
        }

        public Difficulty getDifficulty() {
            throw new UnsupportedOperationException();
        }

        public File getWorldFolder() {
            throw new UnsupportedOperationException();
        }

        public WorldType getWorldType() {
            throw new UnsupportedOperationException();
        }

        public boolean canGenerateStructures() {
            throw new UnsupportedOperationException();
        }

        public long getTicksPerAnimalSpawns() {
            throw new UnsupportedOperationException();
        }

        public void setTicksPerAnimalSpawns(int i) {
            throw new UnsupportedOperationException();
        }

        public long getTicksPerMonsterSpawns() {
            throw new UnsupportedOperationException();
        }

        public void setTicksPerMonsterSpawns(int i) {
            throw new UnsupportedOperationException();
        }

        public int getMonsterSpawnLimit() {
            throw new UnsupportedOperationException();
        }

        public void setMonsterSpawnLimit(int i) {
            throw new UnsupportedOperationException();
        }

        public int getAnimalSpawnLimit() {
            throw new UnsupportedOperationException();
        }

        public void setAnimalSpawnLimit(int i) {
            throw new UnsupportedOperationException();
        }

        public int getWaterAnimalSpawnLimit() {
            throw new UnsupportedOperationException();
        }

        public void setWaterAnimalSpawnLimit(int i) {
            throw new UnsupportedOperationException();
        }

        public int getAmbientSpawnLimit() {
            throw new UnsupportedOperationException();
        }

        public void setAmbientSpawnLimit(int i) {
            throw new UnsupportedOperationException();
        }

        public void playSound(Location lctn, Sound sound, float f, float f1) {
            throw new UnsupportedOperationException();
        }

        public String[] getGameRules() {
            throw new UnsupportedOperationException();
        }

        public String getGameRuleValue(String string) {
            throw new UnsupportedOperationException();
        }

        public boolean setGameRuleValue(String string, String string1) {
            throw new UnsupportedOperationException();
        }

        public boolean isGameRule(String string) {
            throw new UnsupportedOperationException();
        }

        public void sendPluginMessage(Plugin plugin, String string, byte[] bytes) {
            throw new UnsupportedOperationException();
        }

        public Set<String> getListeningPluginChannels() {
            throw new UnsupportedOperationException();
        }

        public void setMetadata(String string, MetadataValue mv) {
            throw new UnsupportedOperationException();
        }

        public List<MetadataValue> getMetadata(String string) {
            throw new UnsupportedOperationException();
        }

        public boolean hasMetadata(String string) {
            throw new UnsupportedOperationException();
        }

        public void removeMetadata(String string, Plugin plugin) {
            throw new UnsupportedOperationException();
        }
    }
    
}
