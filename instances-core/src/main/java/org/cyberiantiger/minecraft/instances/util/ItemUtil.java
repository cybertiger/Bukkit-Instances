/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.util;

import org.bukkit.Material;

/**
 *
 * @author antony
 */
public final class ItemUtil {
    private ItemUtil() {
    }
    
    public static String prettyName(Material m) {
        // Could do better.
        String tmp = m.name();
        tmp = tmp.toLowerCase();
        tmp = tmp.replace('_', ' ');
        return tmp;
    }

}
