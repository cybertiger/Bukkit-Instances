/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances;

/**
 *
 * @author antony
 */
public class PortalPair implements Comparable<PortalPair> {
    private final InstanceEntrancePortal enter;
    private final InstanceDestinationPortal destination;
    private final String name;

    public PortalPair(String name, InstanceEntrancePortal enter, InstanceDestinationPortal destination) {
        this.name = name;
        this.enter = enter;
        this.destination = destination;
        enter.setPortalPair(this);
        destination.setPortalPair(this);
    }

    public String getName() {
        return name;
    }

    public InstanceDestinationPortal getDestination() {
        return destination;
    }

    public InstanceEntrancePortal getEnter() {
        return enter;
    }

    public int compareTo(PortalPair o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append(name);
        ret.append(" entrance: ");
        ret.append(enter.getCuboid().toString());
        ret.append(" destination: ");
        ret.append(destination.getCuboid().toString());
        return ret.toString();
    }
}
