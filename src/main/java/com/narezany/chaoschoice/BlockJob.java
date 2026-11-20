package com.narezany.chaoschoice;

import net.minecraft.world.World;

/** Описание отложенной правки мира: заменить блоки в кубе вокруг точки. */
public class BlockJob {

    public final World world;
    public final int x;
    public final int y;
    public final int z;
    public final int radius;
    public final int[] fromBlocks;
    public final int toBlock;
    public final String label;

    public BlockJob(World world, int x, int y, int z, int radius,
                    int[] fromBlocks, int toBlock, String label) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.fromBlocks = fromBlocks;
        this.toBlock = toBlock;
        this.label = label;
    }
}
