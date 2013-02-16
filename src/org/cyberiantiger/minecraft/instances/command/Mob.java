/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.command;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.cyberiantiger.minecraft.instances.Instances;

/**
 *
 * @author antony
 */
public class Mob extends AbstractCommand {

    public Mob() {
        super(SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, Player player, String[] args) {
        if (args.length != 1 && args.length != 2) {
            return null;
        }
        String mob = args[0];
        int count = 1;
        if (args.length == 2) {
            try {
                count = Integer.parseInt(args[1]);
            } catch (NumberFormatException nfe) {
                throw new InvocationException("Not a number: " + args[1]);
            }
            if (count < 1 || count > 256) {
                throw new InvocationException("Try spawning a sensible number.");
            }
        }
        EntitySpawner spawner = getSpawner(args[0]);
        if (spawner == null) {
            throw new InvocationException("Not a valid entity: " + args[0]);
        }
        Block b = player.getTargetBlock(null, 200);
        if (b == null) {
            throw new InvocationException("You are not looking at anything.");
        }
        Location spawnLoc = b.getLocation();
        spawnLoc.setY(spawnLoc.getBlockY() + 1);
        for (int i = 0; i < count; i++) {
            spawner.spawnEntity(spawnLoc);
        }
        return msg("You spawned " + count + " " + spawner.getName() + ".");
    }

    private EntitySpawner getSpawner(String entity) {
        if ("wither_skeleton".equalsIgnoreCase(entity)) {
            return new WitherSkeletonSpawner();
        }
        EntityType type = EntityType.fromName(entity);
        if (type == null || !type.isAlive()) {
            return null;
        }
        return new DefaultEntitySpawner(type);
    }

    private interface EntitySpawner {

        public void spawnEntity(Location loc);

        public String getName();
    }

    private class DefaultEntitySpawner implements EntitySpawner {

        private final EntityType type;

        public DefaultEntitySpawner(EntityType type) {
            this.type = type;
        }

        public void spawnEntity(Location loc) {
            Entity e = loc.getWorld().spawnEntity(loc, type);
            if (e instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) e;
                living.setCanPickupItems(true);
            }
        }

        public String getName() {
            return type.getName();
        }
    }

    private class WitherSkeletonSpawner implements EntitySpawner {

        public void spawnEntity(Location loc) {
            Skeleton e = (Skeleton) loc.getWorld().spawnEntity(loc, EntityType.SKELETON);
            e.setSkeletonType(SkeletonType.WITHER);
            e.setCanPickupItems(true);
        }

        public String getName() {
            return "wither skeleton";
        }
    }
}
