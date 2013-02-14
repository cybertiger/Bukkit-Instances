/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.cyberiantiger.minecraft.instances.unsafe.InstanceTools;
import org.cyberiantiger.minecraft.instances.unsafe.bank.Bank;
import org.cyberiantiger.minecraft.instances.util.ItemUtil;
import org.cyberiantiger.minecraft.instances.util.StringUtil;

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
            player.sendMessage(StringUtil.error("You must be in a party to enter the dungeon."));
            e.setCancelled(true);
            return;
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

        Instance instance = party.getInstanceFromSourceWorld(destination.getCuboid().getWorld());

        World world;
        if (instance == null) {
            if (pair.getCreateOrEntryPrice() > 0.0D) {
                Bank bank = instances.getBank();
                if (!bank.deduct(player, pair.getCreateOrEntryPrice())) {
                    player.sendMessage(StringUtil.error("An fee of " + pair.getCreateOrEntryPrice() + " rem is required."));
                    e.setCancelled(true);
                    return;
                }
                player.sendMessage(StringUtil.success("Your bank account has been deducted " + pair.getCreateOrEntryPrice() + " rem"));

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
            World sourceWorld = instances.getServer().getWorld(destination.getCuboid().getWorld());
            if (sourceWorld == null) {
                player.sendMessage(StringUtil.error("Portal does not connect anywhere."));
                e.setCancelled(true);
                return;
            }
            world = InstanceTools.createInstance(instances, sourceWorld);
            if (world == null) {
                player.sendMessage(StringUtil.error("Could not create instance world."));
                e.setCancelled(true);
                return;
            }

            instances.getPermissions().addInheritance(sourceWorld.getName(), world.getName());
            instances.getInventories().addShare(sourceWorld.getName(), world.getName());

            instance = new Instance(sourceWorld.getName(), world.getName());

            party.addInstance(instance);

            instances.getLogger().info("Created instance: " + instance);
        } else {
            if (pair.getEntryPrice() > 0.0D) {
                Bank bank = instances.getBank();
                if (!bank.deduct(player, pair.getEntryPrice())) {
                    player.sendMessage(StringUtil.error("An fee of " + pair.getEntryPrice() + " rem is required."));
                    e.setCancelled(true);
                    return;
                }
                player.sendMessage(StringUtil.success("Your bank account has been deducted " + pair.getEntryPrice() + " rem"));
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

        destination.teleport(e.getPlayer(), world);
    }

    @Override
    protected void onLeave(Instances instances, PlayerMoveEvent e) {
        // NOOP
    }

    // Teleports a player TO this portal.
    protected void teleport(Player player) {
        super.teleport(player, player.getServer().getWorld(getCuboid().getWorld()));
    }
}
