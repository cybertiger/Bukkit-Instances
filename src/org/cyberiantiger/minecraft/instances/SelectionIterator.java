package org.cyberiantiger.minecraft.instances;

import org.bukkit.block.Block;

public interface SelectionIterator {

    public void block(int x, int y, int z, Block block);
}
