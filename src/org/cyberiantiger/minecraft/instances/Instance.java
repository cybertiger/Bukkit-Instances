/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances;

import java.util.UUID;

/**
 *
 * @author antony
 */
public class Instance {

    private final String sourceWorld;
    private final String instance;

    public Instance(String sourceWorld, String instance) {
        this.sourceWorld = sourceWorld;
        this.instance = instance;
    }

    public String getSourceWorld() {
        return sourceWorld;
    }

    public String getInstance() {
        return instance;
    }

    public String toString() {
        return sourceWorld + ':' + instance;
    }
}
