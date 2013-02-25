/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_4_R1.Chunk;
import net.minecraft.server.v1_4_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_4_R1.ChunkRegionLoader;
import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityHuman;
import net.minecraft.server.v1_4_R1.EntityTracker;
import net.minecraft.server.v1_4_R1.ExceptionWorldConflict;
import net.minecraft.server.v1_4_R1.IChunkLoader;
import net.minecraft.server.v1_4_R1.IDataManager;
import net.minecraft.server.v1_4_R1.IWorldAccess;
import net.minecraft.server.v1_4_R1.MethodProfiler;
import net.minecraft.server.v1_4_R1.MinecraftServer;
import net.minecraft.server.v1_4_R1.NBTTagCompound;
import net.minecraft.server.v1_4_R1.PlayerFileData;
import net.minecraft.server.v1_4_R1.TileEntity;
import net.minecraft.server.v1_4_R1.WorldData;
import net.minecraft.server.v1_4_R1.WorldManager;
import net.minecraft.server.v1_4_R1.WorldProvider;
import net.minecraft.server.v1_4_R1.WorldServer;
import net.minecraft.server.v1_4_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_4_R1.WorldProviderHell;
import net.minecraft.server.v1_4_R1.WorldProviderTheEnd;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_4_R1.CraftServer;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.cyberiantiger.minecraft.instances.Coord;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.PortalPair;
import org.cyberiantiger.minecraft.instances.generator.VoidGenerator;

/**
 *
 * @author antony
 */
public final class InstanceTools {

    public static org.bukkit.World createInstance(Instances instances, PortalPair portal, String sourceWorld) {
        File dataFolder = new File(instances.getServer().getWorldContainer(), sourceWorld);
        if (!dataFolder.isDirectory()) {
            instances.getLogger().info("Failed to create instance, could not find data folder " + dataFolder.getAbsolutePath() + " for world " + sourceWorld);
            return null;
        }

        MinecraftServer console = ((CraftServer) instances.getServer()).getServer();
        if (console == null) {
            instances.getLogger().info("Failed to create instance, could not locate console object.");
            return null;
        }

        // CraftWorld bukkitWorld = (CraftWorld) sourceWorld;
        // WorldServer mcWorld = bukkitWorld.getHandle();

        int i = 0;
        while (instances.getServer().getWorld(sourceWorld + '-' + i) != null) {
            i++;
        }

        String instanceName = sourceWorld + '-' + i;

        IDataManager dataManager =
                new InstanceDataManager(instances, dataFolder, instanceName);

        // XXX: Copy paste from craftbukkit.
        int dimension = 10 + console.worlds.size();

        boolean used = false;
        do {
            for (WorldServer server : console.worlds) {
                used = server.dimension == dimension;
                if (used) {
                    dimension++;
                    break;
                }
            }
        } while (used);

        MethodProfiler profiler = console.methodProfiler;

        WorldData wd = dataManager.getWorldData();

        World.Environment env;

        switch (wd.j()) {
            case 0:
                env = World.Environment.NORMAL;
            case -1:
                env = World.Environment.NETHER;
            case 1:
                env = World.Environment.THE_END;
            default:
                env = World.Environment.NORMAL;
        }

        ChunkGenerator generator = new VoidGenerator(Biome.PLAINS, new Coord(wd.c(),wd.d(),wd.e()));

        WorldServer instanceWorld = new WorldServer(console, dataManager, instanceName, dimension, null, profiler, env, generator);

        instanceWorld.worldMaps = console.worlds.get(0).worldMaps;
        instanceWorld.tracker = new EntityTracker(instanceWorld);
        instanceWorld.addIWorldAccess((IWorldAccess) new WorldManager(console, instanceWorld));
        instanceWorld.difficulty = portal.getDifficulty().getValue();
        console.worlds.add(instanceWorld);

        if (generator != null) {
            instanceWorld.getWorld().getPopulators().addAll(generator.getDefaultPopulators(instanceWorld.getWorld()));
        }

        instances.getServer().getPluginManager().callEvent(new WorldInitEvent(instanceWorld.getWorld()));
        instances.getServer().getPluginManager().callEvent(new WorldLoadEvent(instanceWorld.getWorld()));

        return instanceWorld.getWorld();
    }

    private static class InstanceDataManager implements IDataManager, PlayerFileData {

        private final Instances instances;
        private final File dataFolder;
        private final String world;
        private final UUID uid;
        private WorldData worldData;

        public InstanceDataManager(Instances instances, File dataFolder, String world) {
            this.instances = instances;
            this.dataFolder = dataFolder;
            this.world = world;
            this.uid = UUID.randomUUID();

            File level = new File(dataFolder, "level.dat");

            WorldData worldData = null;

            if (level.exists()) {
                try {
                    NBTTagCompound tag = NBTCompressedStreamTools.a(new FileInputStream(level));
                    worldData = new WorldData(tag.getCompound("Data"));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(InstanceTools.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (worldData == null) {
                level = new File(dataFolder, "level.dat_old");
                if (level.exists()) {
                    try {
                        NBTTagCompound tag = NBTCompressedStreamTools.a(new FileInputStream(level));
                        worldData = new WorldData(tag.getCompound("Data"));
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(InstanceTools.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            this.worldData = worldData;
        }

        public WorldData getWorldData() {
            return worldData;
        }

        public void checkSession() throws ExceptionWorldConflict {
            // NOOP, this is an in memory world.
        }

        public IChunkLoader createChunkLoader(WorldProvider wp) {
            File chunkDir;
            if (wp instanceof WorldProviderHell) {
                chunkDir = new File(dataFolder, "DIM-1");
            } else if (wp instanceof WorldProviderTheEnd) {
                chunkDir = new File(dataFolder, "DIM1");
            } else {
                chunkDir = dataFolder;
            }
            ChunkRegionLoader sourceLoader = new ChunkRegionLoader(chunkDir);
            return new InstanceChunkLoader(sourceLoader);
        }

        public void saveWorldData(WorldData wd, NBTTagCompound nbttc) {
            this.worldData = wd;
        }

        public void saveWorldData(WorldData wd) {
            this.worldData = wd;
        }

        public PlayerFileData getPlayerFileData() {
            return this;
        }

        public void a() {
            // This looks like it's supposed to sync to disc.
            // Noop
        }

        public File getDataFile(String string) {
            instances.getLogger().warning("Data file: " + string + " requested for instanced world " + world);
            // new Exception().printStackTrace();
            return new File(this.dataFolder, string + ".dat");
        }

        public String g() {
            // Worldname.
            return world;
        }

        public UUID getUUID() {
            return uid;
        }

        public void save(EntityHuman eh) {
            instances.getLogger().warning("Warning: was asked to save player " + eh.getName() + " for instanced world " + world);
        }

        public void load(EntityHuman eh) {
            instances.getLogger().warning("Warning: was asked to load player " + eh.getName() + " for instanced world " + world);
        }

        public String[] getSeenPlayers() {
            return new String[0];
        }
    }

    public static final class InstanceChunkLoader implements IChunkLoader {

        private final IChunkLoader source;
        private final Map<ChunkCoordIntPair, Chunk> chunkCache = new HashMap<ChunkCoordIntPair, Chunk>();

        public InstanceChunkLoader(IChunkLoader source) {
            this.source = source;
        }

        public Chunk a(net.minecraft.server.v1_4_R1.World world, int i, int j) {
            Chunk cacheChunk = chunkCache.get(new ChunkCoordIntPair(i, j));
            if (cacheChunk == null) {
                cacheChunk = source.a(world, i, j);
            } else {
                initChunk(cacheChunk);
            }
            return cacheChunk;
        }

        public void a(net.minecraft.server.v1_4_R1.World world, Chunk chunk) {
            chunkCache.put(chunk.l(), chunk);
        }

        public void b(net.minecraft.server.v1_4_R1.World world, Chunk chunk) {
            // NOOP
        }

        public void a() {
            // Probably a NOOP
            //System.err.println("InstanceChunkLoader.a()");
            //Logger.getLogger(CreateInstance.class.getName()).log(Level.SEVERE, null, new Exception());
        }

        public void b() {
            // Probably a NOOP
            //System.err.println("InstanceChunkLoader.a()");
            //Logger.getLogger(CreateInstance.class.getName()).log(Level.SEVERE, null, new Exception());
        }

        private void initChunk(Chunk cacheChunk) {
            List<Entity> entities = new ArrayList<Entity>();
            List<TileEntity> tileEntities = new ArrayList<TileEntity>();
            for (List<Entity> l : cacheChunk.entitySlices) {
                entities.addAll(l);
                l.clear();
            }
            tileEntities.addAll(cacheChunk.tileEntities.values());
            for (Entity e : entities) {
                cacheChunk.a(e);
            }

            for (TileEntity e : tileEntities) {
                cacheChunk.a(e);
            }
        }
    }
}
