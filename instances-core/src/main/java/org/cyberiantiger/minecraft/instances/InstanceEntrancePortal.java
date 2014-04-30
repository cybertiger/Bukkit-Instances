/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.cyberiantiger.minecraft.Cuboid;
import org.cyberiantiger.minecraft.instances.unsafe.depend.Bank;
import org.cyberiantiger.minecraft.instances.util.ItemUtil;
import org.cyberiantiger.minecraft.instances.util.StringUtil;
import org.cyberiantiger.minecraft.instances.util.TimeUtil;

/**
 *
 * @author antony
 */
public class InstanceEntrancePortal extends Portal {

    private PortalPair pair;

    public InstanceEntrancePortal(Cuboid cuboid) {
        super(cuboid);
    }

    public void setPortalPair(PortalPair pair) {
        this.pair = pair;
    }

    public PortalPair getPortalPair() {
        return pair;
    }

    @Override
    protected void onEnter(Instances instances, PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Party party = instances.getParty(player);
        if (party == null) {
            String defaultParty = getPortalPair().getDefaultParty();
            if (defaultParty == null) {
                player.sendMessage(StringUtil.error("You must be in a party to enter the dungeon."));
                e.setCancelled(true);
                return;
            }
            party = instances.getParty(defaultParty);
            if (party == null) {
                party = instances.partyCreate(defaultParty, null);
            } 
            instances.partyAdd(party, player);
            party.emote(instances, player, " has joined.");
        }

        if (pair == null) {
            player.sendMessage(StringUtil.error("Portal does not connect anywhere."));
            e.setCancelled(true);
            return;
        }

        InstanceDestinationPortal destination = getPortalPair().getDestination();
        if (destination == null) {
            player.sendMessage(StringUtil.error("Portal does not connect anywhere."));
            e.setCancelled(true);
            return;
        }

        long now = player.getWorld().getFullTime();

        Instance instance = party.getInstanceFromSourceWorld(destination.getCuboid().getWorld());

        World world;
        if (instance == null) {
            if (pair.getRecreateTime() > 0 && pair.getLastCreate().containsKey(player.getName())) {
                int recreateTime = pair.getRecreateTime();
                long lastCreate = pair.getLastCreate().get(player.getName());
                if (lastCreate + recreateTime > now) {
                    player.sendMessage(StringUtil.error("You cannot recreate this dungeon for another " + TimeUtil.format(lastCreate + recreateTime - now) + "."));
                    e.setCancelled(true);
                    return;
                }
            }
            if (pair.getMaxPlayers() > 0 && pair.getMaxPlayers() > party.getMembers().size()) {
                player.sendMessage(StringUtil.error("Your party is too big for this dungeon."));
                e.setCancelled(true);
                return;
            }
            if (pair.getMaxInstances() > 0 && pair.getMaxInstances() <= instances.getInstances(pair).size()) {
                player.sendMessage(StringUtil.error("Maximum number of dungeons for this portal has been reached."));
                e.setCancelled(true);
                return;
            }
            // XXX: If price and item are set, then code will charge entry price then deny entry.
            if (pair.getCreateOrEntryPrice() > 0.0D) {
                try {
                    Bank bank = instances.getBank();
                    if (!bank.deduct(player, pair.getCreateOrEntryPrice())) {
                        player.sendMessage(StringUtil.error("An fee of " + pair.getCreateOrEntryPrice() + " rem is required."));
                        e.setCancelled(true);
                        return;
                    }
                    player.sendMessage(StringUtil.success("Your bank account has been deducted " + pair.getCreateOrEntryPrice() + " rem"));
                } catch (UnsupportedOperationException ex) {
                    instances.getLogger().warning("No economy support and portals have prices.");
                }
            }
            if (pair.getCreateOrEntryItem() != null) {
                ItemStack itemInHand = player.getItemInHand();
                ItemStack required = pair.getCreateOrEntryItem();
                if (itemInHand == null || !required.isSimilar(itemInHand) || required.getAmount() > itemInHand.getAmount()) {
                    player.sendMessage(StringUtil.error("An offering of " + required.getAmount() + " " + ItemUtil.prettyName(required.getType()) + " is required."));
                    e.setCancelled(true);
                    return;
                }
                if (required.getAmount() == itemInHand.getAmount()) {
                    player.setItemInHand(null);
                } else {
                    itemInHand.setAmount(itemInHand.getAmount() - required.getAmount());
                }
                player.sendMessage(StringUtil.success("Your offering of " + required.getAmount() + " " + ItemUtil.prettyName(required.getType()) + " has been accepted."));
            }

            world = instances.createInstance(party, player, pair);
        } else {
            if (pair.getEntryPrice() > 0.0D) {
                try {
                    Bank bank = instances.getBank();
                    if (!bank.deduct(player, pair.getEntryPrice())) {
                        player.sendMessage(StringUtil.error("An fee of " + pair.getEntryPrice() + " rem is required."));
                        e.setCancelled(true);
                        return;
                    }
                    player.sendMessage(StringUtil.success("Your bank account has been deducted " + pair.getEntryPrice() + " rem"));
                } catch (UnsupportedOperationException ex) {
                    instances.getLogger().warning("No economy support and portals have prices.");
                }
            }
            if (pair.getEntryItem() != null) {
                ItemStack itemInHand = player.getItemInHand();
                ItemStack required = pair.getEntryItem();
                if (itemInHand == null || !required.isSimilar(itemInHand) || required.getAmount() > itemInHand.getAmount()) {
                    player.sendMessage(StringUtil.error("An offering of " + required.getAmount() + " " + ItemUtil.prettyName(required.getType()) + " is required."));
                    e.setCancelled(true);
                    return;
                }
                if (required.getAmount() == itemInHand.getAmount()) {
                    player.setItemInHand(null);
                } else {
                    itemInHand.setAmount(itemInHand.getAmount() - required.getAmount());
                }
                player.sendMessage(StringUtil.success("Your offering of " + required.getAmount() + " " + ItemUtil.prettyName(required.getType()) + " has been accepted."));
            }
            world = instances.getServer().getWorld(instance.getInstance());
        }

        instances.setLastEnterPortal(player, this);
        destination.teleport(instances, e.getPlayer(), world);
    }

    @Override
    protected void onLeave(Instances instances, PlayerMoveEvent e) {
        // NOOP
    }

    // Teleports a player TO this portal.
    protected void teleport(Instances instances, Player player) {
        super.teleport(instances, player, player.getServer().getWorld(getCuboid().getWorld()));
    }

    public boolean isDestination() {
        return false;
    }
}
