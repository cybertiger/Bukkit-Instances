/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

/**
 *
 * @author antony
 */
public class Party implements Comparable<Party> {

    private final String name;
    private Player leader;
    private List<Player> members = new ArrayList<Player>();
    private List<Player> invites = new ArrayList<Player>();
    // Map of source world name -> instance.
    private Map<String, Instance> sourceMap = new HashMap<String,Instance>();
    // Map of instance world name -> instance.
    private Map<String, Instance> instanceMap = new HashMap<String,Instance>();

    public Party(String name, Player leader) {
        this.name = name;
        this.leader = leader;
        members.add(leader);
    }

    public String getName() {
        return name;
    }

    public Player getLeader() {
        return leader;
    }

    public void setLeader(Player leader) {
        this.leader = leader;
    }

    public List<Player> getMembers() {
        return members;
    }

    public List<Player> getInvites() {
        return invites;
    }

    public Instance getInstanceFromSourceWorld(String world) {
        return sourceMap.get(world);
    }

    public Instance getInstanceFromInstanceWorld(String world) {
        return instanceMap.get(world);
    }

    public void addInstance(Instance instance) {
        sourceMap.put(instance.getSourceWorld(), instance);
        instanceMap.put(instance.getInstance(), instance);
    }

    public void removeInstance(Instance instance) {
        sourceMap.remove(instance.getSourceWorld());
        instanceMap.remove(instance.getInstance());
    }

    public Collection<Instance> getInstances() {
        return sourceMap.values();
    }

    public Instance getInstance(Player player) {
        return instanceMap.get(player.getWorld().getName());
    }

    public void chat(Instances instances, Player player, String msg) {
        sendAll(instances.getPartyNamePrefix() + getName() + ' ' + player.getDisplayName() + instances.getPartyNameSuffix() + ' ' + msg);
    }

    public void emote(Instances instances, Player player, String msg) {
        sendAll(instances.getPartyNamePrefix() + getName() + instances.getPartyNameSuffix() + ' ' + player.getDisplayName() + ' ' + msg);
    }

    public void message(Instances instances, String msg) {
        sendAll(instances.getPartyNamePrefix() + getName() + instances.getPartyNameSuffix() + ' ' + msg);
    }

    public void sendAll(String s) {
        for (Player p : members) {
            p.sendMessage(s);
        }
    }

    public void sendAll(String s, Player exclude) {
        for (Player p : members) {
            if (!exclude.equals(p)) {
                p.sendMessage(s);
            }
        }
    }

    public int compareTo(Party o) {
        return name.compareTo(o.name);
    }
}
