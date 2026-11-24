package com.narezany.fastbuild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

/**
 * Точки выделения по игрокам и постановка задания на заполнение.
 * Клик приходит из сетевого потока, поэтому сначала в очередь, а разбор — в тике сервера.
 */
public final class SelectionStore {

    /** Больше этого не заполняем: 32x32x32. */
    public static final int MAX_VOLUME = 32768;

    public static class Sel {
        public int dim = 0;
        public boolean has1 = false;
        public boolean has2 = false;
        public int x1, y1, z1;
        public int x2, y2, z2;

        public int minX() { return Math.min(x1, x2); }
        public int minY() { return Math.min(y1, y2); }
        public int minZ() { return Math.min(z1, z2); }
        public int maxX() { return Math.max(x1, x2); }
        public int maxY() { return Math.max(y1, y2); }
        public int maxZ() { return Math.max(z1, z2); }

        public int volume() {
            return (maxX() - minX() + 1) * (maxY() - minY() + 1) * (maxZ() - minZ() + 1);
        }

        public void clear() {
            has1 = false;
            has2 = false;
        }
    }

    private static class Queued {
        final String player;
        final int x, y, z;

        Queued(String player, int x, int y, int z) {
            this.player = player;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private static final Map<String, Sel> MAP = new HashMap<String, Sel>();
    private static final Queue<Queued> QUEUE = new ConcurrentLinkedQueue<Queued>();

    /* ---------------- очередь из сетевого потока ---------------- */

    public static void queueSelect(String player, int x, int y, int z) {
        QUEUE.add(new Queued(player, x, y, z));
    }

    public static void processQueue() {
        Queued q;
        while ((q = QUEUE.poll()) != null) {
            EntityPlayerMP player = find(q.player);
            if (player != null) {
                select(player, q.x, q.y, q.z);
            }
        }
    }

    /* ---------------- выбор точек ---------------- */

    private static void select(EntityPlayerMP player, int x, int y, int z) {
        Sel sel = get(player.getCommandSenderName());

        if (sel.has1 && !sel.has2 && sel.dim == player.dimension) {
            sel.x2 = x;
            sel.y2 = y;
            sel.z2 = z;
            sel.has2 = true;
            msg(player, "§6[Стройка] §fТочка 2: §e" + x + " " + y + " " + z
                    + " §7— блоков: §f" + sel.volume()
                    + " §8(ПКМ по блоку — заполнить, Shift+ПКМ — сброс)");
            return;
        }

        sel.dim = player.dimension;
        sel.x1 = x;
        sel.y1 = y;
        sel.z1 = z;
        sel.has1 = true;
        sel.has2 = false;
        msg(player, "§6[Стройка] §fТочка 1: §e" + x + " " + y + " " + z
                + " §8(теперь ЛКМ по второму углу)");
    }

    /* ---------------- заполнение ---------------- */

    public static void fill(EntityPlayerMP player, World world, int x, int y, int z) {
        String name = player.getCommandSenderName();
        Sel sel = get(name);

        if (player.isSneaking()) {
            sel.clear();
            msg(player, "§6[Стройка] §7выделение сброшено.");
            return;
        }

        if (!sel.has1 || !sel.has2) {
            msg(player, "§6[Стройка] §7сначала выбери две точки левым кликом.");
            return;
        }
        if (sel.dim != player.dimension) {
            sel.clear();
            msg(player, "§6[Стройка] §7точки в другом измерении — выделение сброшено.");
            return;
        }

        Block block = world.getBlock(x, y, z);
        if (block == null || block == Blocks.air) {
            msg(player, "§6[Стройка] §7воздухом не заполнить, ткни в обычный блок.");
            return;
        }

        int minY = Math.max(0, sel.minY());
        int maxY = Math.min(255, sel.maxY());
        if (maxY < minY) {
            msg(player, "§6[Стройка] §7выделение вне мира.");
            return;
        }

        int volume = (sel.maxX() - sel.minX() + 1) * (maxY - minY + 1) * (sel.maxZ() - sel.minZ() + 1);
        if (volume > MAX_VOLUME) {
            msg(player, "§6[Стройка] §cслишком много: §f" + volume + " §cблоков, максимум §f"
                    + MAX_VOLUME + "§c.");
            return;
        }

        int meta = world.getBlockMetadata(x, y, z);

        Item item = Item.getItemFromBlock(block);
        if (item == null) {
            msg(player, "§6[Стройка] §7у этого блока нет предмета — нечем платить.");
            return;
        }

        int itemDamage = block.damageDropped(meta);
        boolean creative = player.capabilities != null && player.capabilities.isCreativeMode;

        if (!creative) {
            int have = count(player, item, itemDamage);
            if (have < volume) {
                msg(player, "§6[Стройка] §cне хватает: нужно §f" + volume
                        + " §cштук, в инвентаре §f" + have + "§c.");
                return;
            }
            if (!consume(player, item, itemDamage, volume)) {
                msg(player, "§6[Стройка] §7не удалось списать блоки — ничего не строю.");
                return;
            }
        }

        FillJobs.add(world, sel.minX(), minY, sel.minZ(), sel.maxX(), maxY, sel.maxZ(), block, meta);
        msg(player, "§6[Стройка] §7заполняю §f" + volume + " §7блоков: §f" + block.getLocalizedName()
                + (creative ? "" : " §8(списано: " + volume + ")"));

        sel.clear();
    }

    /* ---------------- оплата материалом ---------------- */

    /** Сколько подходящих предметов в инвентаре (без запаски/края сетки). */
    private static int count(EntityPlayerMP player, Item item, int damage) {
        ItemStack[] slots = player.inventory.mainInventory;
        int sum = 0;
        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = slots[i];
            if (matches(stack, item, damage)) {
                sum += stack.stackSize;
            }
        }
        return sum;
    }

    /** Списывает ровно amount подходящих предметов. */
    private static boolean consume(EntityPlayerMP player, Item item, int damage, int amount) {
        ItemStack[] slots = player.inventory.mainInventory;
        int left = amount;
        for (int i = 0; i < slots.length && left > 0; i++) {
            ItemStack stack = slots[i];
            if (!matches(stack, item, damage)) {
                continue;
            }
            int take = Math.min(left, stack.stackSize);
            stack.stackSize -= take;
            left -= take;
            if (stack.stackSize <= 0) {
                player.inventory.setInventorySlotContents(i, null);
            }
        }
        return left == 0;
    }

    private static boolean matches(ItemStack stack, Item item, int damage) {
        return stack != null
                && stack.stackSize > 0
                && stack.getItem() == item
                && stack.getItemDamage() == damage;
    }

    /* ---------------- мелочи ---------------- */

    public static Sel get(String player) {
        Sel sel = MAP.get(player);
        if (sel == null) {
            sel = new Sel();
            MAP.put(player, sel);
        }
        return sel;
    }

    private static EntityPlayerMP find(String name) {
        List<EntityPlayerMP> online = players();
        for (int i = 0; i < online.size(); i++) {
            if (online.get(i).getCommandSenderName().equalsIgnoreCase(name)) {
                return online.get(i);
            }
        }
        return null;
    }

    public static List<EntityPlayerMP> players() {
        List<EntityPlayerMP> out = new ArrayList<EntityPlayerMP>();
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) {
            return out;
        }
        List<?> raw = server.getConfigurationManager().playerEntityList;
        for (int i = 0; i < raw.size(); i++) {
            Object o = raw.get(i);
            if (o instanceof EntityPlayerMP) {
                out.add((EntityPlayerMP) o);
            }
        }
        return out;
    }

    private static void msg(EntityPlayerMP player, String text) {
        player.addChatComponentMessage(new ChatComponentText(text));
    }

    private SelectionStore() {
    }
}
