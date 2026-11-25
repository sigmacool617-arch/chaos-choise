package com.narezany.drill;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

/**
 * Квадрат 3×3. Сам блок ломает ваниль (киркой-буром, с прогрессом и дропом),
 * а остальные восемь добиваются на следующем тике через
 * ItemInWorldManager.tryHarvestBlock — с дропом, опытом и износом бура
 * по 1 единице за блок. Итого за квадрат — 9 прочности.
 */
public final class DrillJobs {

    private static final List<Job> JOBS = new ArrayList<Job>();

    /** Пока true, события слома от нашего же добивания игнорируются. */
    private static boolean digging = false;

    public static boolean isDigging() {
        return digging;
    }

    private static final class Job {
        final World world;
        final String player;
        final int dimension;
        final int x, y, z, face;

        Job(EntityPlayerMP player, World world, int x, int y, int z, int face) {
            this.world = world;
            this.player = player.getCommandSenderName();
            this.dimension = player.dimension;
            this.x = x;
            this.y = y;
            this.z = z;
            this.face = face;
        }
    }

    public static void add(EntityPlayerMP player, World world, int x, int y, int z, int face) {
        JOBS.add(new Job(player, world, x, y, z, face));
    }

    public static void tickWorld(World world) {
        if (JOBS.isEmpty()) {
            return;
        }
        for (int i = JOBS.size() - 1; i >= 0; i--) {
            Job job = JOBS.get(i);
            if (job.world != world) {
                continue;
            }
            JOBS.remove(i);

            EntityPlayerMP player = find(job.player);
            if (player == null || player.dimension != job.dimension) {
                continue;
            }
            dig(player, job);
        }
    }

    private static void dig(EntityPlayerMP player, Job job) {
        digging = true;
        try {
            World world = job.world;
            if (job.face == 0 || job.face == 1) {
                // верх/низ: квадрат в плоскости XZ
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx != 0 || dz != 0) {
                            breakBlock(player, world, job.x + dx, job.y, job.z + dz);
                        }
                    }
                }
            } else if (job.face == 2 || job.face == 3) {
                // север/юг: квадрат в плоскости XY
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx != 0 || dy != 0) {
                            breakBlock(player, world, job.x + dx, job.y + dy, job.z);
                        }
                    }
                }
            } else {
                // запад/восток: квадрат в плоскости YZ
                for (int dz = -1; dz <= 1; dz++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dz != 0 || dy != 0) {
                            breakBlock(player, world, job.x, job.y + dy, job.z + dz);
                        }
                    }
                }
            }
        } finally {
            digging = false;
        }
    }

    private static void breakBlock(EntityPlayerMP player, World world, int x, int y, int z) {
        if (y < 0 || y > 255) {
            return;
        }
        // бур сломался — дальше не бурим
        if (!Drill.isDrill(player.getCurrentEquippedItem())) {
            return;
        }
        if (!world.blockExists(x, y, z)) {
            return;
        }

        Block block = world.getBlock(x, y, z);
        if (block == null) {
            return;
        }
        Material material = block.getMaterial();
        if (material == Material.air || material.isLiquid()) {
            // воздух и воду/лаву бур не «ломает» — только тратил бы прочность
            return;
        }
        float hardness;
        try {
            hardness = block.getBlockHardness(world, x, y, z);
        } catch (Throwable t) {
            return;
        }
        if (hardness < 0.0F) {
            // неразрушимое: бедрок, барьер, рамка портала Края и т.п.
            return;
        }

        player.theItemInWorldManager.tryHarvestBlock(x, y, z);
    }

    private static EntityPlayerMP find(String name) {
        List<?> raw = net.minecraft.server.MinecraftServer.getServer() == null
                ? null
                : net.minecraft.server.MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        if (raw == null) {
            return null;
        }
        for (int i = 0; i < raw.size(); i++) {
            Object o = raw.get(i);
            if (o instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) o;
                if (player.getCommandSenderName().equalsIgnoreCase(name)) {
                    return player;
                }
            }
        }
        return null;
    }

    private DrillJobs() {
    }
}
