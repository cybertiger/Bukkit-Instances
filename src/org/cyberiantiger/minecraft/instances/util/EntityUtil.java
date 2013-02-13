/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.util;

import org.bukkit.entity.EntityType;

/**
 *
 * @author antony
 */
public class EntityUtil {
    private EntityUtil() {
    }
    
    public static String getPrettyName(EntityType type) {
        return type.getName();
    }

}
