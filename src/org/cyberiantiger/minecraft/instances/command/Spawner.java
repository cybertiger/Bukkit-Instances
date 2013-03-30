/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.nbt.CompoundTag;
import org.cyberiantiger.minecraft.nbt.DoubleTag;
import org.cyberiantiger.minecraft.nbt.FloatTag;
import org.cyberiantiger.minecraft.nbt.ListTag;
import org.cyberiantiger.minecraft.nbt.TagType;
import org.cyberiantiger.minecraft.unsafe.NBTTools;

/**
 *
 * @author antony
 */
public class Spawner extends AbstractCommand {

    private static Map<Character, Flag> flagMap = new HashMap<Character, Flag>();

    private enum ArmourSlot {

        HAND,
        FEET,
        LEGS,
        BODY,
        HEAD;
    }

    private enum Flag {

        ADD_MOB('a', true),
        DROPCHANCE('d', true),
        MAX_ENTITIES('e', true),
        POTION_EFFECTS('f', true),
        HEALTH('h', true),
        INFO('i', false),
        ITEM('I', true),
        LOOT('l', true),
        POWERED('L', true),
        MIN_DELAY('m', true),
        MAX_DELAY('M', true),
        PLAYER_RANGE('p', true),
        POSITION('P', true),
        SPAWN_RANGE('r', true),
        REMOVE('R', true),
        SPAWN_COUNT('s', true),
        SELECT('S', true),
        SKELETON_TYPE('t', true),
        PERSIST('T', true),
        INVULNERABLE('v', true),
        MOTION('V', true),
        WEIGHT('w', true),
        SIZE('z', true);
        private char letter;
        private boolean hasArg;

        private Flag(char letter, boolean hasArg) {
            this.letter = letter;
            this.hasArg = hasArg;
        }

        public char getLetter() {
            return letter;
        }

        public boolean hasArg() {
            return hasArg;
        }
    }

    static {
        for (Flag f : Flag.values()) {
            flagMap.put(f.getLetter(), f);
        }
    }

    public Spawner() {
        super(SenderType.PLAYER);
    }

    @Override
    public List<String> execute(Instances instances, Player player, String[] args) {
        if (args.length == 0) {
            return null;
        }
        NBTTools nbtTools = instances.getNBTTools();
        Block b;
        if ("create".equals(args[0])) {
            // Shitty hack to get the looked at entity.
            Vector n1 = player.getLocation().toVector();
            Vector dir = player.getLocation().getDirection();
            LivingEntity nearest = null;
            double nearestDistance = Double.MAX_VALUE;
            for (Entity e : player.getNearbyEntities(100, 100, 100)) {
                if (e instanceof LivingEntity) {
                    Vector n0 = e.getLocation().toVector();
                    double distance = -n1.clone().subtract(n0).dot(dir) / dir.lengthSquared();
                    if (distance > 0) { // In front of the player, not behind.
                        double lineDist = Math.sqrt(n1.distanceSquared(n0) - distance * distance);
                        if (lineDist < 1) { // Tollerance value of within 3 blocks of the line.
                            if (distance < nearestDistance) {
                                nearestDistance = distance;
                                nearest = (LivingEntity) e;
                            }
                        }
                    }
                }
            }
            if (nearest == null || !nearest.hasLineOfSight(player)) {
                throw new InvocationException("You need to be looking at a living entity to use this.");
            }
            if (!nearest.getType().isSpawnable()) {
                // Just in case a player tries to turn another player into a monster spawner.
                throw new InvocationException(nearest.getType().name() + " cannot be spawned");
            }
            b = nearest.getLocation().getBlock();
            b.setType(Material.MOB_SPAWNER);
            CompoundTag entity = nbtTools.readEntity(nearest);
            entity.remove("Pos");
            entity.remove("Motion");
            CompoundTag spawner = nbtTools.readTileEntity(b);
            CompoundTag spawn = createSpawn(nearest.getType(), 100);
            spawn.getCompound("Properties").getValue().putAll(entity.getValue());
            spawner.setCompound("SpawnData");
            spawner.getCompound("SpawnData").getValue().putAll(spawn.getValue());
            spawner.setString("EntityId", nearest.getType().getName());
            spawner.setList("SpawnPotentials", new ListTag("SpawnPotentials", TagType.COMPOUND, new CompoundTag[]{spawn}));
            nbtTools.writeTileEntity(b, spawner);
            args = shift(args, 1);
        } else {
            b = player.getTargetBlock(null, 200);
        }
        if (b.getType() != Material.MOB_SPAWNER) {
            throw new InvocationException("You are not looking at a mob spawner.");
        }

        List<String> ret = new ArrayList<String>();
        boolean modified = false;

        CompoundTag tileEntity = nbtTools.readTileEntity(b);
        ListTag spawnPotentials = tileEntity.getList("SpawnPotentials");
        CompoundTag spawn;
        if (spawnPotentials == null) {
            spawn = createSpawn(EntityType.fromName(tileEntity.getString("EntityId")), 100);
            CompoundTag[] spawns = new CompoundTag[]{spawn};
            spawnPotentials = new ListTag("SpawnPotentials", TagType.COMPOUND, spawns);
            tileEntity.setList("SpawnPotentials", spawnPotentials);
        } else {
            spawn = (CompoundTag) spawnPotentials.getValue()[0];
        }
        setSpawnTimeDefaults(tileEntity);
        Flag flag = null;
        for (String a : args) {

            if (flag == null) {
                if (a.startsWith("--")) {
                    try {
                        flag = Flag.valueOf(a.substring(2).toUpperCase().replace('-', '_'));
                    } catch (IllegalArgumentException e) {
                        throw new InvocationException("Unknown flag: " + a);
                    }
                } else if (a.startsWith("-")) {
                    if (a.length() != 2) {
                        throw new InvocationException("Unknown flag: " + a);
                    }
                    flag = flagMap.get(a.charAt(1));
                    if (flag == null) {
                        throw new InvocationException("Unknown flag: " + a);
                    }
                } else {
                    EntityType type = EntityType.fromName(a);
                    if (type == null) {
                        throw new InvocationException("Not a valid entity: " + a);
                    }
                    if (!type.isAlive()) {
                        throw new InvocationException("Not a living entity: " + a);
                    }
                    spawn.setString("Type", type.getName());
                    tileEntity.setString("EntityId", type.getName());
                    tileEntity.setCompound("SpawnData");
                    tileEntity.getCompound("SpawnData").getValue().putAll(spawn.getValue());
                    modified = true;
                }
                if (flag != null && !flag.hasArg()) {
                    switch (flag) {
                        case INFO:
                            ret.add("Minimum delay: " + tileEntity.getShort("MinSpawnDelay"));
                            ret.add("Maximum delay: " + tileEntity.getShort("MaxSpawnDelay"));
                            ret.add("Spawn count: " + tileEntity.getShort("SpawnCount"));
                            ret.add("Spawn range: " + tileEntity.getShort("SpawnRange"));
                            ret.add("Player range: " + tileEntity.getShort("RequiredPlayerRange"));
                            ret.add("Max entities: " + tileEntity.getShort("MaxNearbyEntities"));
                            ret.add("Monsters:");
                            CompoundTag[] tags = (CompoundTag[]) spawnPotentials.getValue();
                            for (int i = 0; i < tags.length; i++) {
                                ret.add("    " + i + " : " + tags[i].getString("Type") + "(" + tags[i].getInt("Weight") + ")");
                            }
                    }
                    flag = null;
                }
            } else {
                switch (flag) {
                    case MIN_DELAY:
                        try {
                            short delay = Short.valueOf(a);
                            if (delay <= 0) {
                                throw new InvocationException("Minimum delay must be a positive number.");
                            }
                            tileEntity.setShort("MinSpawnDelay", delay);
                            modified = true;
                        } catch (NumberFormatException e) {
                            throw new InvocationException("Minimum delay must be a number.");
                        }
                        break;
                    case MAX_DELAY:
                        try {
                            short delay = Short.valueOf(a);
                            if (delay <= 0) {
                                throw new InvocationException("Maximum delay must be a positive number.");
                            }
                            tileEntity.setShort("MaxSpawnDelay", delay);
                            modified = true;
                        } catch (NumberFormatException e) {
                            throw new InvocationException("Maximum delay must be a number.");
                        }
                        break;
                    case SPAWN_COUNT:
                        try {
                            short count = Short.valueOf(a);
                            if (count <= 0) {
                                throw new InvocationException("Spawn count must be a positive number.");
                            }
                            tileEntity.setShort("SpawnCount", count);
                            modified = true;
                        } catch (NumberFormatException e) {
                            throw new InvocationException("Spawn count must be a number.");
                        }
                        break;
                    case SPAWN_RANGE:
                        try {
                            short range = Short.valueOf(a);
                            if (range <= 0) {
                                throw new InvocationException("Spawn range must be a positive number.");
                            }
                            modified = true;
                            tileEntity.setShort("SpawnRange", range);
                        } catch (NumberFormatException e) {
                            throw new InvocationException("Spawn range must be a number.");
                        }
                        break;
                    case PLAYER_RANGE:
                        try {
                            short range = Short.valueOf(a);
                            if (range <= 0) {
                                throw new InvocationException("Player range must be a positive number.");
                            }
                            modified = true;
                            tileEntity.setShort("RequiredPlayerRange", range);
                        } catch (NumberFormatException e) {
                            throw new InvocationException("Player range must be a number.");
                        }
                        break;
                    case MAX_ENTITIES:
                        try {
                            short max = Short.valueOf(a);
                            if (max <= 0) {
                                throw new InvocationException("Maximum entities must be a positive number.");
                            }
                            modified = true;
                            tileEntity.setShort("MaxNearbyEntities", max);
                        } catch (NumberFormatException e) {
                            throw new InvocationException("Maximum entities must be a number.");
                        }
                        break;
                    case WEIGHT:
                        try {
                            int weight = Integer.valueOf(a);
                            if (weight <= 0) {
                                throw new InvocationException("Weight must be a positive number.");
                            }
                            modified = true;
                            spawn.setInt("Weight", weight);
                        } catch (NumberFormatException e) {
                            throw new InvocationException("Weight must be a number.");
                        }
                        break;
                    case SELECT:
                        try {
                            int mob = Integer.valueOf(a);
                            if (mob < 0 || mob >= spawnPotentials.getValue().length) {
                                throw new InvocationException("No spawn numbered: " + mob);
                            }
                            spawn = (CompoundTag) spawnPotentials.getValue()[mob];
                        } catch (NumberFormatException e) {
                            throw new InvocationException("Must select spawned mob by number.");
                        }
                        break;
                    case ADD_MOB:
                        EntityType type = EntityType.fromName(a);
                        if (type == null) {
                            throw new InvocationException("Not a valid entity: " + a);
                        }
                        if (!type.isAlive()) {
                            throw new InvocationException("Not a living entity: " + a);
                        }
                        spawn = createSpawn(type, 100);
                        spawnPotentials.add(spawn);
                        modified = true;
                        break;
                    case REMOVE:
                        try {
                            int idx = Integer.valueOf(a);
                            if (idx < 0 || idx >= spawnPotentials.getValue().length) {
                                throw new InvocationException("No spawn numbered: " + idx);
                            }
                            if (spawnPotentials.getValue().length == 1) {
                                throw new InvocationException("You cannot remove the last monster spawn");
                            }
                            spawnPotentials.remove(idx);
                            spawn = (CompoundTag) spawnPotentials.getValue()[0];
                            modified = true;
                        } catch (NumberFormatException e) {
                            throw new InvocationException("Must select spawned mob by number.");
                        }
                        break;
                    case HEALTH:
                        try {
                            int i = Integer.parseInt(a);
                            if (i > 65535 || i < 0) {
                                throw new InvocationException("Must be between 0 and 65535");
                            }
                            getProperties(spawn).setShort("Health", (short) i);
                            modified = true;
                        } catch (NumberFormatException e) {
                            throw new InvocationException("Health must be a number.");
                        }
                        break;
                    case LOOT:
                        boolean loot = Boolean.valueOf(a);
                        getProperties(spawn).setByte("CanPickUpLoot", loot ? (byte) 1 : 0);
                        modified = true;
                        break;
                    case INVULNERABLE:
                        boolean invulnerable = Boolean.valueOf(a);
                        getProperties(spawn).setByte("Invulnerable", invulnerable ? (byte) 1 : 0);
                        modified = true;
                        break;
                    case POWERED:
                        boolean powered = Boolean.valueOf(a);
                        getProperties(spawn).setByte("powered", powered ? (byte) 1 : 0);
                        modified = true;
                        break;
                    case PERSIST:
                        boolean persist = Boolean.valueOf(a);
                        getProperties(spawn).setByte("PersistenceRequired", persist ? (byte) 1 : 0);
                    case SKELETON_TYPE:
                        boolean wither;
                        if ("normal".equals(a)) {
                            wither = false;
                        } else if ("wither".equals(a)) {
                            wither = true;
                        } else {
                            throw new InvocationException("Skeleton type must be wither or normal.");
                        }
                        getProperties(spawn).setByte("SkeletonType", wither ? (byte) 1 : 0);
                        modified = true;
                        break;
                    case SIZE:
                        try {
                            CompoundTag properties = getProperties(spawn);
                            if ("none".equals(a)) {
                                if (properties.containsKey("Size")) {
                                    properties.remove("Size");
                                    modified = true;
                                }
                            } else {
                                int size = Integer.valueOf(a);
                                properties.setInt("Size", size);
                                modified = true;
                            }
                        } catch (NumberFormatException e) {
                            throw new InvocationException("Size must be a number");
                        }
                        break;
                    case POSITION:
                        try {
                            CompoundTag tag = getProperties(spawn);
                            if ("none".equals(a)) {
                                if (tag.containsKey("Pos")) {
                                    tag.remove("Pos");
                                    modified = true;
                                }
                            } else {
                                String[] values = a.split(",");
                                if (values.length != 3) {
                                    throw new InvocationException("Position must contain 3 numbers separated by , e.g. 1,2,3.");
                                }
                                double x = Double.valueOf(values[0]);
                                double y = Double.valueOf(values[1]);
                                double z = Double.valueOf(values[2]);
                                ListTag list = tag.getList("Pos");
                                if (list == null) {
                                    DoubleTag[] pos = new DoubleTag[3];
                                    list = new ListTag("Pos", TagType.DOUBLE, pos);
                                    tag.setList("Pos", list);
                                }
                                DoubleTag[] pos = (DoubleTag[]) list.getValue();
                                pos[0] = new DoubleTag(null, x);
                                pos[1] = new DoubleTag(null, y);
                                pos[2] = new DoubleTag(null, z);
                                modified = true;
                            }
                        } catch (NumberFormatException e) {
                            throw new InvocationException("Position elements must be valid numbers.");
                        }
                        break;
                    case MOTION:
                        try {
                            CompoundTag tag = getProperties(spawn);
                            if ("none".equals(a)) {
                                if (tag.containsKey("Motion")) {
                                    tag.remove("Motion");
                                    modified = true;
                                }
                            } else {
                                String[] values = a.split(",");
                                if (values.length != 3) {
                                    throw new InvocationException("Motion must contain 3 numbers separated by , e.g. 1,2,3.");
                                }
                                double x = Double.valueOf(values[0]);
                                double y = Double.valueOf(values[1]);
                                double z = Double.valueOf(values[2]);
                                ListTag list = tag.getList("Motion");
                                if (list == null) {
                                    DoubleTag[] pos = new DoubleTag[3];
                                    list = new ListTag("Motion", TagType.DOUBLE, pos);
                                    tag.setList("Motion", list);
                                }
                                DoubleTag[] pos = (DoubleTag[]) list.getValue();
                                pos[0] = new DoubleTag(null, x);
                                pos[1] = new DoubleTag(null, y);
                                pos[2] = new DoubleTag(null, z);
                                modified = true;
                            }
                        } catch (NumberFormatException e) {
                            throw new InvocationException("Motion elements must be valid numbers.");
                        }
                        break;
                    case ITEM:
                        try {
                            ArmourSlot slot = ArmourSlot.valueOf(a.toUpperCase());
                            setEquipmentDefaults(spawn);
                            ListTag equipment = getEquipment(spawn);
                            ItemStack stack = player.getItemInHand();
                            if (stack == null) {
                                equipment.getValue()[slot.ordinal()] = new CompoundTag();
                                modified = true;
                            } else {
                                CompoundTag tag = nbtTools.readItemStack(stack);
                                if (tag != null) {
                                    equipment.getValue()[slot.ordinal()] = tag;
                                    modified = true;
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            throw new InvocationException("Armour slot must be one of hand, feet, legs, body or head.");
                        }
                        break;
                    case DROPCHANCE:
                        try {
                            int idx = a.indexOf(':');
                            if (idx == -1) {
                                throw new InvocationException("Drop chance must be <slot>:<chance> e.g. hand:0.5.");
                            }
                            ArmourSlot slot = ArmourSlot.valueOf(a.substring(0, idx).toUpperCase());
                            String chanceVal = a.substring(idx + 1);
                            float chance;
                            if ("always".equals(chanceVal)) {
                                chance = 2.0f;
                            } else {
                                chance = Float.valueOf(a.substring(idx + 1));
                                if (chance < 0.0 || chance > 1.0) {
                                    throw new InvocationException("Drop chance must be between 0.0 and 1.0.");
                                }
                            }
                            setEquipmentDefaults(spawn);
                            ListTag chances = getDropChance(spawn);
                            chances.getValue()[slot.ordinal()] = new FloatTag(null, chance);
                            modified = true;
                        } catch (NumberFormatException e) {
                            throw new InvocationException("Drop chance must be a number between 0.0 and 1.0.");
                        } catch (IllegalArgumentException e) {
                            throw new InvocationException("Armour slot must be one of hand, feet, legs, body or head.");
                        }
                        break;
                    case POTION_EFFECTS:
                        try {
                            int duration = Integer.parseInt(a);
                            if (duration < 0) {
                                throw new InvocationException("Duration must be positive.");
                            }
                            CompoundTag playerTag = nbtTools.readEntity(player);
                            ListTag effects = playerTag.getList("ActiveEffects");
                            if (effects == null) {
                                getProperties(spawn).remove("ActiveEffects");
                            } else {
                                for (CompoundTag effect : (CompoundTag[]) effects.getValue()) {
                                    effect.setInt("Duration", duration);
                                }
                                getProperties(spawn).setList("ActiveEffects", effects);
                            }
                            modified = true;
                        } catch (NumberFormatException e) {
                            throw new InvocationException("You must specify a duration.");
                        }
                        break;

                }
                flag = null;
            }
        }
        if (modified) {
            nbtTools.writeTileEntity(b, tileEntity);
            ret.add("Spawner modified.");
        }
        return ret;
    }

    private void setSpawnTimeDefaults(CompoundTag tileEntity) {
        if (!tileEntity.containsKey("MinSpawnDelay")) {
            tileEntity.setShort("MinSpawnDelay", (short) 200);
        }
        if (!tileEntity.containsKey("MaxSpawnDelay")) {
            tileEntity.setShort("MaxSpawnDelay", (short) 800);
        }
        if (!tileEntity.containsKey("SpawnCount")) {
            tileEntity.setShort("SpawnCount", (short) 4);
        }
    }

    private CompoundTag getProperties(CompoundTag spawn) {
        return spawn.getCompound("Properties");
    }

    private ListTag getEquipment(CompoundTag spawn) {
        return getProperties(spawn).getList("Equipment");
    }

    private ListTag getDropChance(CompoundTag spawn) {
        return getProperties(spawn).getList("DropChances");
    }

    private void setEquipmentDefaults(CompoundTag spawn) {
        CompoundTag properties = spawn.getCompound("Properties");
        CompoundTag[] armour = new CompoundTag[5];
        FloatTag[] dropChance = new FloatTag[5];
        for (int i = 0; i < armour.length; i++) {
            armour[i] = new CompoundTag();
            dropChance[i] = new FloatTag(null, 0.05f);
        }
        if (!properties.containsKey("Equipment")) {
            properties.setList("Equipment", new ListTag("Equipment", TagType.COMPOUND, armour));
        }
        if (!properties.containsKey("DropChances")) {
            properties.setList("DropChances", new ListTag("DropChances", TagType.FLOAT, dropChance));
        }
    }

    private CompoundTag createSpawn(EntityType type, int weight) {
        CompoundTag spawn = new CompoundTag();
        spawn.setInt("Weight", weight);
        spawn.setCompound("Properties");
        spawn.setString("Type", type.getName());
        return spawn;
    }
}
