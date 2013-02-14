/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

    private static class ReenterTime extends IntProperty {

        @Override
        public String getName() {
            return "reenterTime";
        }

        @Override
        public void set(PortalPair portal, Integer value) {
            portal.setReenterTime(value);
        }

        @Override
        public Integer get(PortalPair portal) {
            return portal.getReenterTime();
        }

        @Override
        public void reset(PortalPair portal) {
            portal.setReenterTime(0);
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
    private static final Map<String, Property> propertyMap = new TreeMap<String, Property>();
    private static void addProperty(Property prop) {
        propertyMap.put(prop.getName().toLowerCase(), prop);
    }

    static {
        addProperty(new EntryPrice());
        addProperty(new EntryItem());
        addProperty(new CreatePrice());
        addProperty(new CreateItem());
        addProperty(new ReenterTime());
        addProperty(new UnloadTime());
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
