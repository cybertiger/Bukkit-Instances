/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances;

import org.cyberiantiger.minecraft.Cuboid;

/**
 *
 * @author antony
 */
public class Session {

    private Selection current = new Selection();
    private Cuboid entrance;
    private Cuboid destination;

    public Selection getCurrent() {
        return current;
    }

    public Cuboid getEntrance() {
        return entrance;
    }

    public void setEntrance(Cuboid entrance) {
        this.entrance = entrance;
    }

    public Cuboid getDestination() {
        return destination;
    }

    public void setDestination(Cuboid destination) {
        this.destination = destination;
    }

    public void clear() {
        destination = null;
        entrance = null;
    }

}
