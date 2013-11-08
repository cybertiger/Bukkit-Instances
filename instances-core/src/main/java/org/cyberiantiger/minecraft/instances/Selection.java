package org.cyberiantiger.minecraft.instances;

import org.bukkit.Location;
import org.bukkit.World;
import org.cyberiantiger.minecraft.Coord;
import org.cyberiantiger.minecraft.Cuboid;

public class Selection implements Cloneable {

    private Location from;
    private Location to;

    public World getWorld() {
        if (!isValid()) {
            return null;
        }
        return from.getWorld();
    }

    public boolean isValid() {
        return from != null && to != null && from.getWorld() == to.getWorld();
    }

    public Location getFrom() {
        return from;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    public Coord getSize() {
        if (!isValid()) {
            return null;
        }

        return new Coord(Math.abs(from.getBlockX() - to.getBlockX()) + 1,
                Math.abs(from.getBlockY() - to.getBlockY()) + 1,
                Math.abs(from.getBlockZ() - to.getBlockZ()) + 1);
    }

    public void iterate(SelectionIterator itr) {
        if (!isValid()) {
            return;
        }
        int x1 = from.getBlockX();
        int x2 = to.getBlockX();
        int y1 = from.getBlockY();
        int y2 = to.getBlockY();
        int z1 = from.getBlockZ();
        int z2 = to.getBlockZ();
        if (x2 < x1) {
            int t = x1;
            x1 = x2;
            x2 = t;
        }
        if (y2 < y1) {
            int t = y1;
            y1 = y2;
            y2 = t;
        }
        if (z2 < z1) {
            int t = z1;
            z1 = z2;
            z2 = t;
        }
        World w = from.getWorld();
        for (int j = 0; j <= y2 - y1; j++) {
            for (int i = 0; i <= x2 - x1; i++) {
                for (int k = 0; k <= z2 - z1; k++) {
                    itr.block(i, j, k, w.getBlockAt(x1 + i, y1 + j, z1 + k));
                }
            }
        }
    }

    public Cuboid getCuboid() {
        return new Cuboid(from, to);
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }
}
