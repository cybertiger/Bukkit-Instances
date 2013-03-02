/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.unsafe.inventories;

import com.onarandombox.multiverseinventories.api.profile.WorldGroupProfile;

/**
 *
 * @author antony
 */
public class MultiverseInventories implements Inventories {

    private final com.onarandombox.multiverseinventories.api.Inventories inventories;

    public MultiverseInventories(com.onarandombox.multiverseinventories.api.Inventories inventories) {
        this.inventories = inventories;
    }

    public void addShare(String source, String destination) {
        if (inventories.isEnabled()) {
            for (WorldGroupProfile profile : inventories.getGroupManager().getGroupsForWorld(source)) {
                profile.addWorld(destination);
            }
        }
    }

    public void removeShare(String source, String destination) {
        if (inventories.isEnabled()) {
            for (WorldGroupProfile profile : inventories.getGroupManager().getGroupsForWorld(source)) {
                profile.removeWorld(destination);
            }
        }
    }
}
