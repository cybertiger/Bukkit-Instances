/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_4_R1.Chunk;
import net.minecraft.server.v1_4_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_4_R1.ChunkSection;
import net.minecraft.server.v1_4_R1.EmptyChunk;
import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityTracker;
import net.minecraft.server.v1_4_R1.EntityTypes;
import net.minecraft.server.v1_4_R1.ExceptionWorldConflict;
import net.minecraft.server.v1_4_R1.IChunkLoader;
import net.minecraft.server.v1_4_R1.IDataManager;
import net.minecraft.server.v1_4_R1.IWorldAccess;
import net.minecraft.server.v1_4_R1.MethodProfiler;
import net.minecraft.server.v1_4_R1.MinecraftServer;
import net.minecraft.server.v1_4_R1.NBTTagCompound;
import net.minecraft.server.v1_4_R1.NibbleArray;
import net.minecraft.server.v1_4_R1.PlayerFileData;
import net.minecraft.server.v1_4_R1.TileEntity;
import net.minecraft.server.v1_4_R1.WorldData;
import net.minecraft.server.v1_4_R1.WorldManager;
import net.minecraft.server.v1_4_R1.WorldProvider;
import net.minecraft.server.v1_4_R1.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_4_R1.CraftServer;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public final class InstanceTools {

    public static org.bukkit.World createInstance(Instances instances, World sourceWorld) {
        MinecraftServer console = ((CraftServer) instances.getServer()).getServer();
        CraftWorld bukkitWorld = (CraftWorld) sourceWorld;
        WorldServer mcWorld = bukkitWorld.getHandle();

        int i = 0;
        while (instances.getServer().getWorld(sourceWorld.getName() + '-' + i) != null) {
            i++;
        }

        String instanceName = sourceWorld.getName() + '-' + i;

        IDataManager dataManager =
                new InstanceDataManager(mcWorld.getDataManager(), instanceName);

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

        World.Environment env = bukkitWorld.getEnvironment();
        ChunkGenerator generator = bukkitWorld.getGenerator();

        WorldServer instanceWorld = new WorldServer(console, dataManager, instanceName, dimension, null, profiler, env, generator);

        instanceWorld.worldMaps = console.worlds.get(0).worldMaps;
        instanceWorld.tracker = new EntityTracker(instanceWorld);
        instanceWorld.addIWorldAccess((IWorldAccess) new WorldManager(console, instanceWorld));
        instanceWorld.difficulty = mcWorld.difficulty;
        instanceWorld.setSpawnFlags(true, true); // ?????
        console.worlds.add(instanceWorld);

        if (generator != null) {
            instanceWorld.getWorld().getPopulators().addAll(generator.getDefaultPopulators(instanceWorld.getWorld()));
        }

        instances.getServer().getPluginManager().callEvent(new WorldInitEvent(instanceWorld.getWorld()));
        instances.getServer().getPluginManager().callEvent(new WorldLoadEvent(instanceWorld.getWorld()));

        return instanceWorld.getWorld();
    }

    private static class InstanceDataManager implements IDataManager {

        private final IDataManager sourceWorld;
        private final String world;
        private final UUID uid;
        private final WorldData worldData;

        public InstanceDataManager(IDataManager sourceWorld, String world) {
            this.sourceWorld = sourceWorld;
            this.world = world;
            this.uid = UUID.randomUUID();
            // Clone and store a copy of WorldData.
            NBTTagCompound tmp = new NBTTagCompound();
            sourceWorld.getWorldData().a(tmp);
            this.worldData = new WorldData(tmp);
        }

        public WorldData getWorldData() {
            return worldData;
        }

        public void checkSession() throws ExceptionWorldConflict {
            // Share the lock on the world we're instancing.
            sourceWorld.checkSession();
        }

        public IChunkLoader createChunkLoader(WorldProvider wp) {
            return new InstanceChunkLoader(sourceWorld.createChunkLoader(wp));
        }

        public void saveWorldData(WorldData wd, NBTTagCompound nbttc) {
            // NOOP.
        }

        public void saveWorldData(WorldData wd) {
            // NOOP.
        }

        public PlayerFileData getPlayerFileData() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void a() {
            // This looks like it's supposed to sync to disc.
            // Noop
        }

        public File getDataFile(String string) {
            System.err.println("InstanceDataManager.getDataFile(" + string + ")");
            // new Exception().printStackTrace();
            return sourceWorld.getDataFile(string);
        }

        public String g() {
            // Worldname.
            return world;
        }

        public UUID getUUID() {
            return uid;
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
            try {
                if (cacheChunk == null) {
                    Chunk originalChunk = source.a(world, i, j);
                    cacheChunk = cloneChunk(world, originalChunk);
                } else {
                    initChunk(cacheChunk);
                }
            } catch (NoSuchFieldException ex) {
                Logger.getLogger(InstanceTools.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(InstanceTools.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(InstanceTools.class.getName()).log(Level.SEVERE, null, ex);
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

    private static Chunk cloneChunk(net.minecraft.server.v1_4_R1.World world, Chunk original) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        // Optimization.
        if (original == null) {
            return null;
        }
        if (original instanceof EmptyChunk) {
            return new EmptyChunk(world, original.x, original.z);
        }
        // Shite, now we have to do things the hard way.
        Chunk clone = new Chunk(world, original.x, original.z);
        Field f;

        // clone.sections
        f = Chunk.class.getDeclaredField("sections");
        f.setAccessible(true);
        ChunkSection[] originalSection = (ChunkSection[]) f.get(original);
        ChunkSection[] cloneSection = (ChunkSection[]) f.get(clone);

        for (int i = 0; i < cloneSection.length; i++) {
            cloneSection[i] = cloneChunkSection(originalSection[i]);
        }

        // clone.s
        f = Chunk.class.getDeclaredField("s");
        f.setAccessible(true);
        byte[] originalData = (byte[]) f.get(original);
        byte[] cloneData = (byte[]) f.get(clone);
        System.arraycopy(originalData, 0, cloneData, 0, cloneData.length);
        // clone.b
        System.arraycopy(original.b, 0, clone.b, 0, clone.b.length);
        // clone.c
        System.arraycopy(original.c, 0, clone.c, 0, clone.c.length);
        // clone.d
        clone.d = original.d;
        // clone.world = world - Set in constructor.
        // clone.heightMap
        System.arraycopy(original.heightMap, 0, clone.heightMap, 0, clone.heightMap.length);
        // clone.x - set in constructor.
        // clone.z - set in constructor.
        // clone.t
        f = Chunk.class.getDeclaredField("t");
        f.setAccessible(true);
        f.set(clone, f.get(original));
        // clone.tileEntities
        for (TileEntity e : (Collection<TileEntity>) original.tileEntities.values()) {
            NBTTagCompound tmp = new NBTTagCompound();
            e.b(tmp);
            TileEntity cloneTile = TileEntity.c(tmp);
            clone.a(cloneTile);
        }

        // clone.entitySlices
        for (List l : original.entitySlices) {
            for (Entity e : (List<Entity>) l) {
                NBTTagCompound tmp = new NBTTagCompound();
                if (e.c(tmp)) {
                    Entity cloneEntity = EntityTypes.a(tmp, world);
                    clone.a(cloneEntity);
                }
            }
        }

        // clone.done
        clone.done = original.done;

        // clone.l
        clone.l = original.l;

        // clone.m
        clone.m = original.m;

        // clone.n
        clone.n = original.n;

        // clone.seenByPlayer
        clone.seenByPlayer = original.seenByPlayer;

        // clone.p
        clone.p = original.p;

        // clone.u
        f = Chunk.class.getDeclaredField("u");
        f.setAccessible(true);
        f.set(clone, f.get(original));

        // clone.q
        f = Chunk.class.getDeclaredField("q");
        f.setAccessible(true);
        f.set(clone, f.get(original));

        // clone.bukkitChunk - Set in constructor.

        // clone.mustSave
        clone.mustSave = original.mustSave;

        return clone;
    }

    private static ChunkSection cloneChunkSection(ChunkSection original) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        if (original == null) {
            return null;
        }
        byte[] originalBlockIds = original.g();
        byte[] cloneBlockIds = new byte[originalBlockIds.length];
        System.arraycopy(originalBlockIds, 0, cloneBlockIds, 0, originalBlockIds.length);
        NibbleArray originalExtBlockIds = original.i();
        byte[] cloneExtBlockIds;
        if (originalExtBlockIds != null) {
            cloneExtBlockIds = new byte[originalExtBlockIds.a.length];
            System.arraycopy(originalExtBlockIds.a, 0, cloneExtBlockIds, 0, originalExtBlockIds.a.length);
        } else {
            cloneExtBlockIds = null;
        }
        NibbleArray originalBlockData = original.j();
        NibbleArray originalBlockLight = original.k();
        NibbleArray originalSkyLight = original.l();

        ChunkSection clone = new ChunkSection(original.d(), originalSkyLight != null, cloneBlockIds, cloneExtBlockIds);

        System.arraycopy(originalBlockData.a, 0, clone.j().a, 0, originalBlockData.a.length);
        System.arraycopy(originalBlockLight.a, 0, clone.k().a, 0, originalBlockLight.a.length);
        if (originalSkyLight != null) {
            System.arraycopy(originalSkyLight.a, 0, clone.l().a, 0, originalSkyLight.a.length);
        }

        Field f;

        f = ChunkSection.class.getDeclaredField("nonEmptyBlockCount");
        f.setAccessible(true);
        f.set(clone, f.get(original));

        f = ChunkSection.class.getDeclaredField("tickingBlockCount");
        f.setAccessible(true);
        f.set(clone, f.get(original));

        return clone;
    }
}
