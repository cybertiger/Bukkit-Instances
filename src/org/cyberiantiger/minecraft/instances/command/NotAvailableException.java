/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances.command;

/**
 *
 * @author antony
 */
public class NotAvailableException extends RuntimeException {

    public NotAvailableException() {
        super();
    }

    public NotAvailableException(String format) {
        super(format);
    }
}
