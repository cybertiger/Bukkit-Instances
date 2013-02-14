/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances;

import com.fernferret.allpay.multiverse.GenericBank;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.cyberiantiger.minecraft.instances.unsafe.InstanceTools;
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
            if (pair.getCreateOrEntryPrice() > 0.0D && instances.getCore() != null) {
                GenericBank bank = instances.getCore().getBank();
                if (!bank.hasEnough(player, pair.getCreateOrEntryPrice(), -1)) {
                    e.setCancelled(true);
                    return;
                }
                bank.take(player, pair.getCreateOrEntryPrice(), -1);
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

            instance = new Instance(sourceWorld.getName(), world.getName());

            party.addInstance(instance);

            instances.getLogger().info("Created instance: " + instance);
        } else {
            if (pair.getEntryPrice() > 0.0D && instances.getCore() != null) {
                GenericBank bank = instances.getCore().getBank();
                if (!bank.hasEnough(player, pair.getEntryPrice(), -1)) {
                    e.setCancelled(true);
                    return;
                }
                bank.take(player, pair.getEntryPrice(), -1);
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
