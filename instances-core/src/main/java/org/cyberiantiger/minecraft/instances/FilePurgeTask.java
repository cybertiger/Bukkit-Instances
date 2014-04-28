/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.scheduler.BukkitRunnable;
import org.cyberiantiger.minecraft.util.FileUtils;

/**
 *
 * @author antony
 */
class FilePurgeTask extends BukkitRunnable {
    // Maybe not hardcode this.
    public static final long PURGE_INTERVAL = 20 * 60; // 60 seconds.

    private final Instances plugin;

    public FilePurgeTask(Instances plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // May need to worry about synchronized access later.
        // For now, this is never modified by anyone except at startup.
        Set<File> worldSaves = new HashSet<File>();
        List<File> toPurge = new ArrayList<File>();
        synchronized (plugin.INSTANCE_LOCK) {
            for (List<Instance> instances : plugin.getInstances().values()) {
                for (Instance instance : instances) {
                    File folder = instance.getFolder();
                    try {
                        worldSaves.add(folder.getCanonicalFile());
                    } catch (IOException ex) {
                        worldSaves.add(folder.getAbsoluteFile());
                    }
                }
            }
            File parentDir = plugin.getInstanceWorldContainer();
            try {
                parentDir = parentDir.getCanonicalFile();
            } catch (IOException ioe) {
                parentDir = parentDir.getAbsoluteFile();
            }
            File[] files = parentDir.listFiles();
            if (files != null) {
                for (File f : parentDir.listFiles()) {
                    if (f.isDirectory() && !worldSaves.contains(f)) {
                        toPurge.add(f);
                    }
                }
            }
        }
        for (File file : toPurge) {
            if (worldSaves.contains(file)) {
                plugin.getLogger().log(Level.INFO, "Skipping active instance save dir: {0}", file);
            } else if (file.isDirectory()) {
                try {
                    if (!FileUtils.deleteRecursively(file)) {
                        plugin.getLogger().log(Level.WARNING, "Failed to fully delete unused instance save directory: {0}", file);
                    } else {
                        plugin.getLogger().log(Level.INFO, "Deleted unused instance save directory: {0}", file);
                    }
                }  catch (IOException ioe) {
                    // Never thrown, will be removed from method declaration in next release.
                }
            }
        }
    }

    public synchronized void start() {
        // You'd think there was a way to find out if we're actually currently scheduled.
        // but no, that shit's too complicated for us plugin devs.
        try {
            cancel();
        } catch (IllegalStateException e) {
            // srsly bukkit.
        }
        runTaskTimerAsynchronously(plugin, PURGE_INTERVAL, PURGE_INTERVAL);
    }

    public synchronized void stop() {
        try {
            cancel();
        } catch (IllegalStateException e) {
        }
    }
}
