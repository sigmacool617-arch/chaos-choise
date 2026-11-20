package com.narezany.chaoschoice;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.World;

/**
 * Очередь правок мира. Чтобы не лагало, блоки меняются порциями
 * (ChaosConfig.blockBudgetPerTick за тик) и только в загруженных чанках.
 */
public final class BlockJobs {

    private static final List<Job> JOBS = new ArrayList<Job>();

    private static final int Y_UP = 12;
    private static final int Y_DOWN = 8;

    public static void add(BlockJob spec) {
        JOBS.add(new Job(spec));
    }

    public static void reset() {
        JOBS.clear();
    }

    public static int pending() {
        return JOBS.size();
    }

    public static void tickWorld(World world) {
        int budget = ChaosConfig.blockBudgetPerTick;
        if (budget < 100) {
            budget = 100;
        }
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
            if (JOBS.get(i).spec.world == world) {
                return JOBS.get(i);
            }
        }
        return null;
    }

    private static final class Job {

        final BlockJob spec;
        final int r;
        int dx;
        int dy;
        int dz;
        boolean done;

        Job(BlockJob spec) {
            this.spec = spec;
            this.r = Math.max(2, Math.min(32, spec.radius));
            this.dx = -r;
            this.dy = -Y_DOWN;
            this.dz = -r - 1;
        }

        private void advance() {
            dz++;
            if (dz > r) {
                dz = -r;
                dx++;
            }
            if (dx > r) {
                dx = -r;
                dy++;
            }
            if (dy > Y_UP) {
                done = true;
            }
        }

        int step(int budget) {
            while (budget > 0) {
                advance();
                if (done) {
                    break;
                }
                budget--;
                int wx = spec.x + dx;
                int wy = spec.y + dy;
                int wz = spec.z + dz;
                if (!spec.world.blockExists(wx, wy, wz)) {
                    continue;
                }
                Block block = spec.world.getBlock(wx, wy, wz);
                int id = Block.getIdFromBlock(block);
                for (int k = 0; k < spec.fromBlocks.length; k++) {
                    if (spec.fromBlocks[k] == id) {
                        spec.world.setBlock(wx, wy, wz, Block.getBlockById(spec.toBlock), 0, 2);
                        break;
                    }
                }
            }
            return budget;
        }
    }

    private BlockJobs() {
    }
}
