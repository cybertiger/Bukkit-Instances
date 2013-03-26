/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bukkit.Difficulty;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cyberiantiger.minecraft.instances.Facing;
import org.cyberiantiger.minecraft.instances.Instances;
import org.cyberiantiger.minecraft.instances.PortalPair;

/**
 *
 * @author antony
 */
public class ModifyPortal extends AbstractCommand {

    private enum Action {

        SHOW {

            public List<String> invoke(Instances instances, PortalPair portal, Property property, CommandSender sender, String[] args) {
                if (property == null) {
                    List<String> ret = new ArrayList<String>();
                    for (Map.Entry<String, Property> e : propertyMap.entrySet()) {
                        ret.add(portal.getName() + '.' + e.getValue().getName() + " = " + e.getValue().get(portal));
                    }
                    return ret;
                } else {
                    return msg(portal.getName() + '.' + property.getName() + " = " + property.get(portal));
                }
            }
        },
        SET {

            public List<String> invoke(Instances instances, PortalPair portal, Property property, CommandSender sender, String[] args) {
                if (property == null) {
                    throw new InvocationException("You must specify a property name with set.");
                }
                Object value = property.parse(instances, sender, args);
                if (value == null) {
                    return null;
                }
                property.set(portal, value);
                return msg("Set " + portal.getName() + '.' + property.getName() + " = " + value);
            }
        },
        RESET {

            public List<String> invoke(Instances instances, PortalPair portal, Property property, CommandSender sender, String[] args) {
                if (property == null) {
                    throw new InvocationException("You must specify a property name with remove.");
                }
                property.reset(portal);
                return msg("Reset " + portal.getName() + '.' + property.getName() + " to " + property.get(portal));
            }
        };

        public abstract List<String> invoke(Instances instances, PortalPair portal, Property property, CommandSender sender, String[] args);
    }

    private abstract static class Property<T> {

        public abstract String getName();

        public abstract void set(PortalPair portal, T value);

        public abstract T get(PortalPair portal);

        public abstract void reset(PortalPair portal);

        public abstract T parse(Instances instances, CommandSender sender, String[] args);
    }

    private abstract static class DoubleProperty extends Property<Double> {

        public Double parse(Instances instances, CommandSender sender, String[] args) {
            if (args.length != 1) {
                throw new InvocationException("You must specify a double value for this property.");
            }
            try {
                return Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                throw new InvocationException("Not a valid currency ammount: " + args[0]);
            }
        }
    }

    private abstract static class ItemStackProperty extends Property<ItemStack> {

        public ItemStack parse(Instances instances, CommandSender sender, String[] args) {
            if (SenderType.getSenderType(sender) != SenderType.PLAYER) {
                throw new NotAvailableException("You must be a player to set item type properties.");
            }
            if (args.length != 0) {
                throw new InvocationException("You must be holding the item to set for this property.");
            }
            Player player = (Player) sender;
            ItemStack value = player.getItemInHand();
            if (value == null) {
                throw new InvocationException("You must be holding an item to set as this property.");
            }
            return value;
        }
    }

    private abstract static class IntProperty extends Property<Integer> {

        public Integer parse(Instances instances, CommandSender sender, String[] args) {
            if (args.length != 1) {
                throw new InvocationException("You must specify an integer value for this property.");
            }
            try {
                return Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                throw new InvocationException("Not a valid currency ammount: " + args[0]);
            }
        }
    }

    private abstract static class StringProperty extends Property<String> {
        public String parse(Instances instances, CommandSender sender, String[] args) {
            if (args.length != 1) {
                throw new InvocationException("You must specify a string value for this property.");
            }
            return args[0];
        }
    }

    private abstract static class FacingProperty extends Property<Facing> {
        @Override
        public Facing parse(Instances instances, CommandSender sender, String[] args) {
            if (args.length == 1) {
                if ("north".equalsIgnoreCase(args[0])) {
                    return Facing.NORTH;
                } else if("east".equalsIgnoreCase(args[0])) {
                    return Facing.EAST;
                } else if("south".equalsIgnoreCase(args[0])) {
                    return Facing.SOUTH;
                } else if("west".equalsIgnoreCase(args[0])) {
                    return Facing.WEST;
                } else {
                    throw new InvocationException("Invalid argument, must be north/south/east/west");
                }
            } else if (args.length == 0) {
                if (sender instanceof Player) {
                    return new Facing(((Player)sender).getLocation());
                } else {
                    throw new NotAvailableException("Only players can set facing to their current facing.");
                }
            } else {
                throw new InvocationException("Setting a facing accepts 0 or 1 arguments only.");
            }
        }
    }

    private static class DifficultyProperty extends Property<Difficulty> {
        public Difficulty parse(Instances instances, CommandSender sender, String[] args) {
            if (args.length != 1) {
                throw new InvocationException("You must specify a difficulty value for this property");
            }
            try {
                return Difficulty.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvocationException("Difficulty must be peaceful, easy, normal or hard");
            }
        }

        @Override
        public String getName() {
            return "difficulty";
        }

        @Override
        public void set(PortalPair portal, Difficulty value) {
            portal.setDifficulty(value);
        }

        @Override
        public Difficulty get(PortalPair portal) {
            return portal.getDifficulty();
        }

        @Override
        public void reset(PortalPair portal) {
            portal.setDifficulty(Difficulty.NORMAL);
        }
    }

    private static class EntryPrice extends DoubleProperty {

        @Override
        public String getName() {
            return "entryPrice";
        }

        @Override
        public void set(PortalPair portal, Double value) {
            portal.setEntryPrice(value);
        }

        @Override
        public Double get(PortalPair portal) {
            return portal.getEntryPrice();
        }

        @Override
        public void reset(PortalPair portal) {
            portal.setEntryPrice(0.0D);
        }
    }

    private static class CreatePrice extends DoubleProperty {

        @Override
        public String getName() {
            return "createPrice";
        }

        @Override
        public void set(PortalPair portal, Double value) {
            portal.setCreatePrice(value);
        }

        @Override
        public Double get(PortalPair portal) {
            return portal.getCreatePrice();
        }

        @Override
        public void reset(PortalPair portal) {
            portal.setCreatePrice(0.0D);
        }

    }

    private static class EntryItem extends ItemStackProperty {

        @Override
        public String getName() {
            return "entryItem";
        }

        @Override
        public void set(PortalPair portal, ItemStack value) {
            portal.setEntryItem(value);
        }

        @Override
        public ItemStack get(PortalPair portal) {
            return portal.getEntryItem();
        }

        @Override
        public void reset(PortalPair portal) {
            portal.setEntryItem(null);
        }

    }

    private static class CreateItem extends ItemStackProperty {

        @Override
        public String getName() {
            return "createItem";
        }

        @Override
        public void set(PortalPair portal, ItemStack value) {
            portal.setCreateItem(value);
        }

        @Override
        public ItemStack get(PortalPair portal) {
            return portal.getCreateItem();
        }

        @Override
        public void reset(PortalPair portal) {
            portal.setCreateItem(null);
        }

    }

    private static class RecreateTime extends IntProperty {

        @Override
        public String getName() {
            return "recreateTime";
        }

        @Override
        public void set(PortalPair portal, Integer value) {
            portal.setRecreateTime(value);
        }

        @Override
        public Integer get(PortalPair portal) {
            return portal.getRecreateTime();
        }

        @Override
        public void reset(PortalPair portal) {
            portal.setRecreateTime(0);
        }

    }

    private static class UnloadTime extends IntProperty {

        @Override
        public String getName() {
            return "unloadTime";
        }

        @Override
        public void set(PortalPair portal, Integer value) {
            portal.setUnloadTime(value);
        }

        @Override
        public Integer get(PortalPair portal) {
            return portal.getUnloadTime();
        }

        @Override
        public void reset(PortalPair portal) {
            portal.setUnloadTime(0);
        }

    }

    private static class MaxPlayersProperty extends IntProperty {

        @Override
        public String getName() {
            return "maxPlayers";
        }

        @Override
        public void set(PortalPair portal, Integer value) {
            portal.setMaxPlayers(value);
        }

        @Override
        public Integer get(PortalPair portal) {
            return portal.getMaxPlayers();
        }

        @Override
        public void reset(PortalPair portal) {
            portal.setMaxPlayers(0);
        }
    }

    private static class MaxInstancesProperty extends IntProperty {

        @Override
        public String getName() {
            return "maxInstances";
        }

        @Override
        public void set(PortalPair portal, Integer value) {
            portal.setMaxInstances(value);
        }

        @Override
        public Integer get(PortalPair portal) {
            return portal.getMaxInstances();
        }

        @Override
        public void reset(PortalPair portal) {
            portal.setMaxInstances(0);
        }
        
    }

    private static class DefaultParty extends StringProperty {
        @Override
        public String getName() {
            return "defaultParty";
        }

        @Override
        public void set(PortalPair portal, String value) {
            portal.setDefaultParty(value);
        }

        @Override
        public String get(PortalPair portal) {
            return portal.getDefaultParty();
        }

        @Override
        public void reset(PortalPair portal) {
            portal.setDefaultParty(null);
        }
    }

    private static class EntranceFacing extends FacingProperty {

        @Override
        public String getName() {
            return "entrance.facing";
        }

        @Override
        public void set(PortalPair portal, Facing value) {
            portal.getEnter().setFacing(value);
        }

        @Override
        public Facing get(PortalPair portal) {
            return portal.getEnter().getFacing();
        }

        @Override
        public void reset(PortalPair portal) {
            portal.getEnter().setFacing(null);
        }
    }
    private static class DestinationFacing extends FacingProperty {

        @Override
        public String getName() {
            return "destination.facing";
        }

        @Override
        public void set(PortalPair portal, Facing value) {
            portal.getDestination().setFacing(value);
        }

        @Override
        public Facing get(PortalPair portal) {
            return portal.getDestination().getFacing();
        }

        @Override
        public void reset(PortalPair portal) {
            portal.getDestination().setFacing(null);
        }
    }
    private static final Map<String, Property> propertyMap = new TreeMap<String, Property>();
    private static void addProperty(Property prop) {
        propertyMap.put(prop.getName().toLowerCase(), prop);
    }

    static {
        addProperty(new EntryPrice());
        addProperty(new EntryItem());
        addProperty(new CreatePrice());
        addProperty(new CreateItem());
        addProperty(new RecreateTime());
        addProperty(new UnloadTime());
        addProperty(new DifficultyProperty());
        addProperty(new DefaultParty());
        addProperty(new EntranceFacing());
        addProperty(new DestinationFacing());
        addProperty(new MaxPlayersProperty());
        addProperty(new MaxInstancesProperty());
    }

    @Override
    public List<String> execute(Instances instances, CommandSender sender, String[] args) {
        if (args.length == 0) {
            return null;
        }
        PortalPair portal = instances.getPortalPair(args[0]);
        if (portal == null) {
            throw new InvocationException("No portal named: " + args[0] + ".");
        }

        Action action = Action.SHOW;
        Property prop = null;
        if (args.length >= 2) {
            try {
                action = Action.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvocationException("You can only show, set or reset properties");
            }
            if (args.length >= 3) {
                prop = propertyMap.get(args[2].toLowerCase());
                if (prop == null) {
                    throw new InvocationException("Unknown property: " + args[2]);
                }
                args = shift(args, 3);
            } else {
                args = shift(args, 2);
            }
        } else {
            args = shift(args, 1);

        }

        return action.invoke(instances, portal, prop, sender, args);
    }
}
