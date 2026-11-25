package com.narezany.titanium;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

/**
 * Жила титановой руды из четырёх блоков, только на высоте 9
 * и только в обычном мире. Шанс маленький: примерно один чанк из восьми.
 */
public class TitaniumWorldGen implements IWorldGenerator {

    private static final double CHANCE = 0.12D;
    private static final int HEIGHT = 9;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
                         IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.dimensionId != 0) {
            return;
        }
        if (random.nextDouble() >= CHANCE) {
            return;
        }

        int x = chunkX * 16 + random.nextInt(16);
        int z = chunkZ * 16 + random.nextInt(16);

        place(world, x, z);

        int[][] offsets = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int i = 0; i < offsets.length; i++) {
            int j = random.nextInt(offsets.length);
            int[] tmp = offsets[i];
            offsets[i] = offsets[j];
            offsets[j] = tmp;
        }
        for (int i = 0; i < 3; i++) {
            place(world, x + offsets[i][0], z + offsets[i][1]);
        }
    }

    private void place(World world, int x, int z) {
        if (!world.blockExists(x, HEIGHT, z)) {
            return;
        }
        Block block = world.getBlock(x, HEIGHT, z);
        if (block != Blocks.stone) {
            return;
        }
        world.setBlock(x, HEIGHT, z, Titanium.ore, 0, 2);
    }
}
