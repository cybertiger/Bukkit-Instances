/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances;

import org.bukkit.Location;

/**
 *
 * @author antony
 */
public final class Coord implements Comparable<Coord> {

    public static final Coord ZERO = new Coord(0, 0, 0);
    private final int x;
    private final int y;
    private final int z;

    public Coord(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Coord(Location loc) {
        this(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Coord other = (Coord) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        if (this.z != other.z) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.x;
        hash = 89 * hash + this.y;
        hash = 89 * hash + this.z;
        return hash;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Coord translate(int x, int y, int z) {
        return new Coord(this.x + x, this.y + y, this.z + z);
    }

    public Coord translate(Coord offset) {
        return translate(offset.getX(), offset.getY(), offset.getZ());
    }

    @Override
    public String toString() {
        return "<" + x + ',' + y + ',' + z + '>';
    }

    public int compareTo(Coord coord) {
        if (this.y != coord.y) {
            return this.y - coord.y;
        } else if (this.z != coord.y) {
            return this.z - coord.z;
        } else if (this.x != coord.x) {
            return this.x - coord.x;
        }
        return 0;
    }

    public static Coord fromLocation(Location location) {
        return new Coord(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
