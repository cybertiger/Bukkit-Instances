/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.cyberiantiger.minecraft.Facing;


/**
 *
 * @author antony
 */
public class PortalPair implements Comparable<PortalPair> {
    private final InstanceEntrancePortal enter;
    private final InstanceDestinationPortal destination;
    private final String name;
    private double entryPrice;
    private ItemStack entryItem;
    private double createPrice;
    private ItemStack createItem;
    private int unloadTime;
    private int recreateTime;
    private World.Environment environment;
    private Difficulty difficulty;
    private String defaultParty;
    private int maxPlayers;
    private int maxInstances;
    // Last time a player created a new instance of this portal's dungeon.
    private Map<String, Long> lastCreate = new HashMap<String, Long>();

    public PortalPair(String name, InstanceEntrancePortal enter, InstanceDestinationPortal destination, World.Environment environment, Difficulty difficulty) {
        this(name, enter, destination, 0, 0, null, null, 0, 0, environment, difficulty, null, null, null, 0, 0);
    };

    public PortalPair(String name, InstanceEntrancePortal enter, InstanceDestinationPortal destination, double entryPrice, double createPrice, ItemStack entryItem, ItemStack createItem, int unloadTime, int recreateTime, World.Environment environment, Difficulty difficulty, String defaultParty, Facing entranceFacing, Facing destinationFacing, int maxPlayers, int maxInstances) {
        this.name = name;
        this.enter = enter;
        this.destination = destination;
        this.entryPrice = entryPrice;
        this.entryItem = entryItem;
        this.createPrice = createPrice;
        this.createItem = createItem;
        this.unloadTime = unloadTime;
        this.recreateTime = recreateTime;
        this.environment = environment;
        this.difficulty = difficulty;
        this.defaultParty = defaultParty;
        this.maxPlayers = maxPlayers;
        this.maxInstances = maxInstances;
        enter.setFacing(entranceFacing);
        destination.setFacing(destinationFacing);
        enter.setPortalPair(this);
        destination.setPortalPair(this);
    }

    public Map<String, Long> getLastCreate() {
        return lastCreate;
    }

    public double getCreateOrEntryPrice() {
        return createPrice > 0.0D ? createPrice : entryPrice;
    }

    public ItemStack getCreateOrEntryItem() {
        return createItem == null ? entryItem : createItem;
    }

    public ItemStack getCreateItem() {
        return createItem;
    }

    public void setCreateItem(ItemStack createItem) {
        this.createItem = createItem;
    }

    public double getCreatePrice() {
        return createPrice;
    }

    public void setCreatePrice(double createPrice) {
        this.createPrice = createPrice;
    }

    public ItemStack getEntryItem() {
        return entryItem;
    }

    public void setEntryItem(ItemStack entryItem) {
        this.entryItem = entryItem;
    }

    public double getEntryPrice() {
        return entryPrice;
    }

    public void setEntryPrice(double entryPrice) {
        this.entryPrice = entryPrice;
    }

    public int getRecreateTime() {
        return recreateTime;
    }

    public void setRecreateTime(int reenterTime) {
        this.recreateTime = reenterTime;
    }

    public int getUnloadTime() {
        return unloadTime;
    }

    public void setUnloadTime(int unloadTime) {
        this.unloadTime = unloadTime;
    }

    public String getName() {
        return name;
    }

    public InstanceDestinationPortal getDestination() {
        return destination;
    }

    public InstanceEntrancePortal getEnter() {
        return enter;
    }

    public World.Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(World.Environment environment) {
        this.environment = environment;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getDefaultParty() {
        return defaultParty;
    }

    public void setDefaultParty(String defaultParty) {
        this.defaultParty = defaultParty;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMaxInstances() {
        return maxInstances;
    }

    public void setMaxInstances(int maxInstances) {
        this.maxInstances = maxInstances;
    }
    
    public int compareTo(PortalPair o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append(name);
        ret.append(" entrance: ");
        ret.append(enter.getCuboid().toString());
        ret.append(" destination: ");
        ret.append(destination.getCuboid().toString());
        return ret.toString();
    }
}
