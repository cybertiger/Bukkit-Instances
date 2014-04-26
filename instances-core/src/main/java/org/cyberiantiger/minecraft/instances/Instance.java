/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.instances;

import java.io.File;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author antony
 */
public class Instance {

    private final String sourceWorld;
    private final String instance;
    private final PortalPair portal;
    private final File folder;
    private BukkitTask deleteTask;

    public Instance(PortalPair portal, String sourceWorld, String instance, File folder) {
        this.portal = portal;
        this.sourceWorld = sourceWorld;
        this.instance = instance;
        this.folder = folder;
    }

    public String getSourceWorld() {
        return sourceWorld;
    }

    public String getInstance() {
        return instance;
    }

    public PortalPair getPortal() {
        return portal;
    }

    public File getFolder() {
        return folder;
    }

    public BukkitTask getDeleteTask() {
        return deleteTask;
    }

    public void setDeleteTask(BukkitTask deleteTask) {
        this.deleteTask = deleteTask;
    }

    public void cancelDelete() {
        if (deleteTask != null) {
            deleteTask.cancel();
            deleteTask = null;
        }
    }

    @Override
    public String toString() {
        return sourceWorld + ':' + instance;
    }
}
