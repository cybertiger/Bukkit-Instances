/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances;

/**
 *
 * @author antony
 */
public class Session {

    private Selection current = new Selection();
    private Selection entrance;
    private Selection destination;

    public Selection getCurrent() {
        return current;
    }

    public Selection getEntrance() {
        return entrance;
    }

    public void setEntrance() {
        entrance = (Selection) current.clone();
    }

    public Selection getDestination() {
        return destination;
    }

    public void setDestination() {
        destination = (Selection) current.clone();
    }

    public void clear() {
        destination = null;
        entrance = null;
    }

}
