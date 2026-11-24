package com.narezany.fastbuild;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.World;

/** Заполнение области порциями, чтобы не вешать игру на большом объёме. */
public final class FillJobs {

    /** Сколько блоков ставить за тик. */
    private static final int BUDGET = 1200;

    private static final List<Job> JOBS = new ArrayList<Job>();

    public static void add(World world, int x1, int y1, int z1, int x2, int y2, int z2,
                           Block block, int meta) {
        JOBS.add(new Job(world, x1, y1, z1, x2, y2, z2, block, meta));
    }

    public static int pending() {
        return JOBS.size();
    }

    public static void tickWorld(World world) {
        int budget = BUDGET;
        while (budget > 0 && !JOBS.isEmpty()) {
            Job job = firstFor(world);
            if (job == null) {
                return;
            }
            budget = job.step(budget);
            if (job.done) {
                JOBS.remove(job);
            }
        }
    }

    private static Job firstFor(World world) {
        for (int i = 0; i < JOBS.size(); i++) {
            if (JOBS.get(i).world == world) {
                return JOBS.get(i);
            }
        }
        return null;
    }

    private static final class Job {

        final World world;
        final int minX, minY, minZ, maxX, maxY, maxZ;
        final Block block;
        final int meta;

        int x, y, z;
        boolean done;

        Job(World world, int x1, int y1, int z1, int x2, int y2, int z2,
            Block block, int meta) {
            this.world = world;
            this.minX = Math.min(x1, x2);
            this.minY = Math.min(y1, y2);
            this.minZ = Math.min(z1, z2);
            this.maxX = Math.max(x1, x2);
            this.maxY = Math.max(y1, y2);
            this.maxZ = Math.max(z1, z2);
            this.block = block;
            this.meta = meta;
            this.x = minX;
            this.y = minY;
            this.z = minZ;
        }

        int step(int budget) {
            while (budget > 0) {
                if (done) {
                    break;
                }
                world.setBlock(x, y, z, block, meta, 3);
                budget--;

                z++;
                if (z > maxZ) {
                    z = minZ;
                    x++;
                }
                if (x > maxX) {
                    x = minX;
                    y++;
                }
                if (y > maxY) {
                    done = true;
                }
            }
            return budget;
        }
    }

    private FillJobs() {
    }
}
