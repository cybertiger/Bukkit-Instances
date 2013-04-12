/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.util;

/**
 *
 * @author antony
 */
public class StringUtil {
    private StringUtil() {}
    public static String error(String msg) {
        return "§4" + msg;
    }
    public static String success(String msg) {
        return "§2" + msg;
    }
}
