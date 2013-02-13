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
                    for (Map.Entry<String,Property> e : propertyMap.entrySet()) {
                        ret.add(portal.getName() + '.' + property.getName() + " = " + property.get(portal));
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
                Object value;
                property.set(portal, value = property.parse(instances, sender, args));
                return msg("Set " + portal.getName() + '.' + property.getName() + " = " + value);
            }

        },
        REMOVE {
            public List<String> invoke(Instances instances, PortalPair portal, Property property, CommandSender sender, String[] args) {
                if (property == null) {
                    throw new InvocationException("You must specify a property name with remove.");
                }
                property.remove(portal);
                return msg("Removed " + portal.getName() + '.' + property.getName());
            }
        };

        public abstract List<String> invoke(Instances instances, PortalPair portal, Property property, CommandSender sender, String[] args);
    }

    private abstract static class Property<T> {
        public abstract String getName();
        public abstract void set(PortalPair portal, T value);
        public abstract T get(PortalPair portal);
        public abstract void remove(PortalPair portal);
        public abstract T parse(Instances instances, CommandSender sender, String[] args);
    }
    
    private static final Map<String, Property> propertyMap = new TreeMap<String, Property>();
    static {
        
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
        if (args.length >= 1) {
            try {
                action = Action.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvocationException("You can only show, set or remove properties");
            }
            if (args.length >= 2) {
                prop = propertyMap.get(args[2]);
                if (prop == null) {
                    throw new InvocationException("Unknown property: " + args[2]);
                }
                args = shift(args,2);
            } else {
                args = shift(args,1);
            }
        }

        return action.invoke(instances, portal, prop, sender, args);
    }
}
