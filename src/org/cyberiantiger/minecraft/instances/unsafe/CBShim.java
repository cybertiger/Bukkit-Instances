/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;

/**
 *
 * @author antony
 */
public class CBShim {

    private static final String INSTANCES_PACKAGE = CBShim.class.getPackage().getName();
    private static final String CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit";
    private static final Map<Class<?>,Object> cache = new HashMap<Class<?>, Object>();

    public static <T> T getShim(Class<T> type, Server server) {
        T ret = (T) cache.get(type);
        if (ret != null) {
            return ret;
        }
        Class<?> serverClass = server.getClass();
        while (!serverClass.getPackage().getName().startsWith(CRAFTBUKKIT_PACKAGE) ) {
            serverClass = serverClass.getSuperclass();
            if (serverClass == null) {
                unsupportedVersion(server);
            }
        }
        String pkg  = serverClass.getPackage().getName();
        int i = pkg.lastIndexOf(".");
        if (i == -1) {
            unsupportedVersion(server);
        }
        String childPackage = pkg.substring(i+1);
        String className = INSTANCES_PACKAGE + '.' + childPackage + '.' + type.getSimpleName();
        try {
            Class<T> typeClass = (Class<T>) CBShim.class.getClassLoader().loadClass(className);
            ret = typeClass.newInstance();
            cache.put(type, ret);
            return ret;
        } catch (ClassNotFoundException ex) {
            unsupportedVersion(server);
        } catch (InstantiationException ex) {
            unsupportedVersion(server);
        } catch (IllegalAccessException ex) {
            unsupportedVersion(server);
        }
        // unreached, stupid compiler.
        return null;
    }

    private static void unsupportedVersion(Server server) {
        throw new UnsupportedOperationException("Unsupported CraftBukkit version: " + server.getBukkitVersion());
    }
    
}
