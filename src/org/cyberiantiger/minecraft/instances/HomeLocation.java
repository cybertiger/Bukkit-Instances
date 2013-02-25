/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author antony
 */
public class HomeLocation {
    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final float pitch;
    private final float yaw;

    public HomeLocation(ConfigurationSection home) {
        this(home.getString("world"), home.getDouble("x"), home.getDouble("y"), home.getDouble("z"), (float)home.getDouble("pitch"), (float)home.getDouble("yaw"));
    }

    public HomeLocation(Location location) {
        this(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }

    public HomeLocation(String world, double x, double y, double z, float pitch, float yaw) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public void save(ConfigurationSection section) {
        section.set("world", world);
        section.set("x", x);
        section.set("y", y);
        section.set("z", z);
        section.set("pitch", pitch);
        section.set("yaw", yaw);
    }

    public Location getLocation(Server server) {
        return new Location(server.getWorld(world), x, y, z, yaw, pitch);
    }
}
