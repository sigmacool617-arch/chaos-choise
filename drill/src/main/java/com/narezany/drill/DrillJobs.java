package com.narezany.drill;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

/**
 * Очередь бурения: клик ставит задание, через 2.5 секунды (50 тиков) бур
 * выбивает квадрат 3×3, перпендикулярный той грани, по которой кликнули.
 *
 * Ломает через ItemInWorldManager.tryHarvestBlock — как будто игрок сломал сам:
 * дроп, опыт и износ инструмента считает ваниль (1 прочности за блок).
 */
public final class DrillJobs {

    /** 20 тиков в секунде — 2.5 секунды всегда, чем бы ни бурили. */
    public static final int TICKS = 50;

    /** Дальше этого бурение отменяется (игрок ушёл/телепорт). */
    private static final double MAX_DISTANCE_SQ = 64.0D;

    private static final List<Job> JOBS = new ArrayList<Job>();

    private static final class Job {
        final World world;
        final String player;
        final int dimension;
        final int x, y, z, face;
        int ticks = TICKS;

        Job(World world, EntityPlayerMP player, int x, int y, int z, int face) {
            this.world = world;
            this.player = player.getCommandSenderName();
            this.dimension = player.dimension;
            this.x = x;
            this.y = y;
            this.z = z;
            this.face = face;
        }
    }

    public static void start(EntityPlayerMP player, World world, int x, int y, int z, int face) {
        String name = player.getCommandSenderName();

        // по тому же блоку второй клик ничего не меняет
        for (int i = 0; i < JOBS.size(); i++) {
            Job job = JOBS.get(i);
            if (job.player.equals(name) && job.x == x && job.y == y && job.z == z) {
                return;
            }
        }

        removeFor(name);
        JOBS.add(new Job(world, player, x, y, z, face));

        msg(player, "§6[Бур] §7бурю §f3×3 §7— §f2,5 §7секунды…");
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

            EntityPlayerMP player = find(job.player);
            if (player == null || player.dimension != job.dimension) {
                JOBS.remove(i);
                continue;
            }

            ItemStack held = player.getCurrentEquippedItem();
            if (held == null || held.getItem() != Drill.drill) {
                JOBS.remove(i);
                msg(player, "§6[Бур] §7бурение прервано.");
                continue;
            }

            double dx = player.posX - (job.x + 0.5D);
            double dy = player.posY - (job.y + 0.5D);
            double dz = player.posZ - (job.z + 0.5D);
            if (dx * dx + dy * dy + dz * dz > MAX_DISTANCE_SQ) {
                JOBS.remove(i);
                msg(player, "§6[Бур] §7слишком далеко — бурение отменено.");
                continue;
            }

            job.ticks--;
            if (job.ticks <= 0) {
                JOBS.remove(i);
                dig(player, job);
            }
        }
    }

    /* ---------------- сам квадрат 3×3 ---------------- */

    private static void dig(EntityPlayerMP player, Job job) {
        World world = job.world;

        if (job.face == 0 || job.face == 1) {
            // грань сверху/снизу: квадрат в плоскости XZ
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    breakBlock(player, world, job.x + dx, job.y, job.z + dz);
                }
            }
        } else if (job.face == 2 || job.face == 3) {
            // север/юг: квадрат в плоскости XY
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    breakBlock(player, world, job.x + dx, job.y + dy, job.z);
                }
            }
        } else {
            // запад/восток: квадрат в плоскости YZ
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = -1; dy <= 1; dy++) {
                    breakBlock(player, world, job.x, job.y + dy, job.z + dz);
                }
            }
        }
    }

    private static void breakBlock(EntityPlayerMP player, World world, int x, int y, int z) {
        if (y < 0 || y > 255) {
            return;
        }
        // бур сломался — дальше не бурим
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null || held.getItem() != Drill.drill) {
            return;
        }
        if (!world.blockExists(x, y, z)) {
            return;
        }
        player.theItemInWorldManager.tryHarvestBlock(x, y, z);
    }

    /* ---------------- мелочи ---------------- */

    private static void removeFor(String player) {
        for (int i = JOBS.size() - 1; i >= 0; i--) {
            if (JOBS.get(i).player.equals(player)) {
                JOBS.remove(i);
            }
        }
    }

    private static EntityPlayerMP find(String name) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) {
            return null;
        }
        List<?> raw = server.getConfigurationManager().playerEntityList;
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

    private static void msg(EntityPlayerMP player, String text) {
        player.addChatComponentMessage(new ChatComponentText(text));
    }

    private DrillJobs() {
    }
}
