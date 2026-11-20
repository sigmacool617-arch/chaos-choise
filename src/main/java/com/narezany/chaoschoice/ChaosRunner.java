package com.narezany.chaoschoice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/** Исполнение действий варианта на сервере. */
public final class ChaosRunner {

    private static final Random RAND = new Random();

    public static String run(ChaosOption option, EntityPlayerMP chooser) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < option.parts.length; i++) {
            String text;
            try {
                text = apply(option.parts[i], chooser);
            } catch (Throwable t) {
                t.printStackTrace();
                text = null;
            }
            if (text != null && text.length() > 0) {
                if (sb.length() > 0) {
                    sb.append(" · ");
                }
                sb.append(text);
            }
        }
        return sb.toString();
    }

    private static List<EntityPlayerMP> targets(EntityPlayerMP chooser) {
        if (chooser != null) {
            List<EntityPlayerMP> one = new ArrayList<EntityPlayerMP>();
            one.add(chooser);
            return one;
        }
        return ChoiceManager.players();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static String apply(ChaosAction a, EntityPlayerMP chooser) {
        switch (a.kind) {
            case KILL:
            case MONSTERS: {
                int count = 0;
                WorldServer[] worlds = MinecraftServer.getServer().worldServers;
                for (int w = 0; w < worlds.length; w++) {
                    List copy = new ArrayList(worlds[w].loadedEntityList);
                    for (int i = 0; i < copy.size(); i++) {
                        Entity e = (Entity) copy.get(i);
                        if (e == null || e.isDead || e instanceof EntityPlayer) {
                            continue;
                        }
                        if (a.kind == ChaosAction.Kind.MONSTERS) {
                            if (e instanceof IMob) {
                                e.setDead();
                                count++;
                            }
                            continue;
                        }
                        String id = EntityList.getEntityString(e);
                        if (id == null) {
                            continue;
                        }
                        for (int k = 0; k < a.ids.length; k++) {
                            if (a.ids[k].equalsIgnoreCase(id)) {
                                e.setDead();
                                count++;
                                break;
                            }
                        }
                    }
                }
                return count > 0 ? "мобов убрано: " + count : "мобов не нашлось";
            }
            case WIPE: {
                int count = 0;
                List<EntityPlayerMP> players = targets(chooser);
                for (int i = 0; i < players.size(); i++) {
                    count += wipeInventory(players.get(i), a.ids);
                }
                return count > 0 ? "предметов убрано: " + count : "предметов не нашлось";
            }
            case GIVE: {
                List<EntityPlayerMP> players = targets(chooser);
                for (int i = 0; i < players.size(); i++) {
                    EntityPlayerMP p = players.get(i);
                    cmd("give " + p.getCommandSenderName() + " " + a.ids[0] + " " + a.amount);
                }
                return a.label.length() > 0 ? "+" + a.label : "+предмет";
            }
            case EFFECT: {
                List<EntityPlayerMP> players = targets(chooser);
                for (int i = 0; i < players.size(); i++) {
                    EntityPlayerMP p = players.get(i);
                    cmd("effect " + p.getCommandSenderName() + " " + a.amount + " " + a.seconds + " "
                            + a.amplifier);
                }
                return a.label;
            }
            case XP: {
                List<EntityPlayerMP> players = targets(chooser);
                for (int i = 0; i < players.size(); i++) {
                    EntityPlayerMP p = players.get(i);
                    cmd("xp " + a.amount + " " + p.getCommandSenderName());
                }
                return "+" + a.amount + " опыта";
            }
            case HURT: {
                List<EntityPlayerMP> players = targets(chooser);
                for (int i = 0; i < players.size(); i++) {
                    players.get(i).attackEntityFrom(DamageSource.generic, a.amount * 2);
                }
                return "−" + a.amount + " сердца";
            }
            case HEALTH: {
                List<EntityPlayerMP> players = targets(chooser);
                for (int i = 0; i < players.size(); i++) {
                    players.get(i).setHealth(a.amount);
                }
                return "здоровье: " + (a.amount / 2) + " сердца";
            }
            case HEAL: {
                List<EntityPlayerMP> players = targets(chooser);
                for (int i = 0; i < players.size(); i++) {
                    EntityPlayerMP p = players.get(i);
                    p.setHealth(p.getMaxHealth());
                }
                return "здоровье полное";
            }
            case SPAWN: {
                int spawned = 0;
                List<EntityPlayerMP> players = targets(chooser);
                for (int i = 0; i < players.size(); i++) {
                    EntityPlayerMP p = players.get(i);
                    for (int n = 0; n < a.amount; n++) {
                        double ang = RAND.nextDouble() * Math.PI * 2;
                        double dist = 3 + RAND.nextDouble() * 4;
                        cmd("summon " + a.mob + " " + fmt(p.posX + Math.cos(ang) * dist) + " "
                                + fmt(p.posY + 1) + " " + fmt(p.posZ + Math.sin(ang) * dist));
                        spawned++;
                    }
                }
                return spawned > 0 ? a.label + ": " + spawned : "мобы не влезли";
            }
            case NIGHT: {
                cmd("time set 18000");
                return "наступает ночь";
            }
            case STORM: {
                cmd("weather thunder " + a.seconds);
                return "гроза " + a.seconds + "с";
            }
            case LIGHTNING: {
                List<EntityPlayerMP> players = targets(chooser);
                for (int i = 0; i < players.size(); i++) {
                    EntityPlayerMP p = players.get(i);
                    cmd("summon LightningBolt " + fmt(p.posX) + " " + fmt(p.posY) + " "
                            + fmt(p.posZ));
                }
                return "молния!";
            }
            case UP: {
                List<EntityPlayerMP> players = targets(chooser);
                for (int i = 0; i < players.size(); i++) {
                    EntityPlayerMP p = players.get(i);
                    cmd("tp " + p.getCommandSenderName() + " " + fmt(p.posX) + " "
                            + fmt(p.posY + a.amount) + " " + fmt(p.posZ));
                }
                return "вверх на " + a.amount;
            }
            case RANDOM_TP: {
                List<EntityPlayerMP> players = targets(chooser);
                for (int i = 0; i < players.size(); i++) {
                    EntityPlayerMP p = players.get(i);
                    double ang = RAND.nextDouble() * Math.PI * 2;
                    double dist = 16 + RAND.nextDouble() * a.radius;
                    int tx = MathHelper.floor_double(p.posX + Math.cos(ang) * dist);
                    int tz = MathHelper.floor_double(p.posZ + Math.sin(ang) * dist);
                    int ty = safeY(p.worldObj, tx, MathHelper.floor_double(p.posY), tz);
                    cmd("tp " + p.getCommandSenderName() + " " + fmt(tx + 0.5D) + " " + ty + " "
                            + fmt(tz + 0.5D));
                }
                return "телепорт в случайную точку";
            }
            case PIT: {
                List<EntityPlayerMP> players = targets(chooser);
                for (int i = 0; i < players.size(); i++) {
                    EntityPlayerMP p = players.get(i);
                    World w = p.worldObj;
                    int x = MathHelper.floor_double(p.posX);
                    int y = MathHelper.floor_double(p.posY);
                    int z = MathHelper.floor_double(p.posZ);
                    for (int ax = -1; ax <= 1; ax++) {
                        for (int az = -1; az <= 1; az++) {
                            for (int d = 1; d <= a.depth; d++) {
                                if (w.blockExists(x + ax, y - d, z + az)) {
                                    w.setBlock(x + ax, y - d, z + az, Blocks.air, 0, 2);
                                }
                            }
                        }
                    }
                }
                return "провал под ногами";
            }
            case FIRE: {
                int made = 0;
                List<EntityPlayerMP> players = targets(chooser);
                for (int i = 0; i < players.size(); i++) {
                    EntityPlayerMP p = players.get(i);
                    World w = p.worldObj;
                    int x = MathHelper.floor_double(p.posX);
                    int y = MathHelper.floor_double(p.posY);
                    int z = MathHelper.floor_double(p.posZ);
                    for (int n = 0; n < a.amount; n++) {
                        int px = x + RAND.nextInt(9) - 4;
                        int pz = z + RAND.nextInt(9) - 4;
                        if (w.isAirBlock(px, y, pz)) {
                            w.setBlock(px, y, pz, Blocks.fire, 0, 2);
                            made++;
                        }
                    }
                }
                return made > 0 ? "поджогов: " + made : "поджигать было нечего";
            }
            case DROP_ALL: {
                int moved = 0;
                List<EntityPlayerMP> players = targets(chooser);
                for (int i = 0; i < players.size(); i++) {
                    EntityPlayerMP p = players.get(i);
                    InventoryPlayer inv = p.inventory;
                    for (int slot = 0; slot < inv.mainInventory.length; slot++) {
                        ItemStack s = inv.mainInventory[slot];
                        if (s == null) {
                            continue;
                        }
                        p.entityDropItem(s, 0.5F);
                        inv.mainInventory[slot] = null;
                        moved++;
                    }
                    p.inventoryContainer.detectAndSendChanges();
                }
                return moved > 0 ? "выпало стаков: " + moved : "выбрасывать было нечего";
            }
            case SHUFFLE: {
                List<EntityPlayerMP> players = targets(chooser);
                for (int i = 0; i < players.size(); i++) {
                    shuffleInventory(players.get(i));
                }
                return "инвентарь перемешан";
            }
            case BREAK: {
                int broken = 0;
                List<EntityPlayerMP> players = targets(chooser);
                for (int i = 0; i < players.size(); i++) {
                    EntityPlayerMP p = players.get(i);
                    InventoryPlayer inv = p.inventory;
                    for (int slot = 0; slot < inv.mainInventory.length; slot++) {
                        broken += breakStack(inv.mainInventory, slot);
                    }
                    for (int slot = 0; slot < inv.armorInventory.length; slot++) {
                        broken += breakStack(inv.armorInventory, slot);
                    }
                    p.inventoryContainer.detectAndSendChanges();
                }
                return broken > 0 ? "испорчено вещей: " + broken : "ломать было нечего";
            }
            case BLOCKS: {
                List<EntityPlayerMP> players = targets(chooser);
                for (int i = 0; i < players.size(); i++) {
                    EntityPlayerMP p = players.get(i);
                    int x = MathHelper.floor_double(p.posX);
                    int y = MathHelper.floor_double(p.posY);
                    int z = MathHelper.floor_double(p.posZ);
                    BlockJobs.add(new BlockJob(p.worldObj, x, y, z, a.radius, a.fromBlocks,
                            a.toBlock, a.label));
                }
                return a.label;
            }
            default:
                return "";
        }
    }

    /* ---------------- помощники ---------------- */

    private static int breakStack(ItemStack[] stacks, int slot) {
        ItemStack s = stacks[slot];
        if (s != null && s.getMaxDamage() > 0 && s.getItemDamage() < s.getMaxDamage()) {
            s.setItemDamage(s.getMaxDamage());
            return 1;
        }
        return 0;
    }

    private static int wipeInventory(EntityPlayerMP p, String[] ids) {
        int count = 0;
        InventoryPlayer inv = p.inventory;
        for (int i = 0; i < inv.mainInventory.length; i++) {
            ItemStack s = inv.mainInventory[i];
            if (s != null && matches(s, ids)) {
                count += s.stackSize;
                inv.mainInventory[i] = null;
            }
        }
        for (int i = 0; i < inv.armorInventory.length; i++) {
            ItemStack s = inv.armorInventory[i];
            if (s != null && matches(s, ids)) {
                count += s.stackSize;
                inv.armorInventory[i] = null;
            }
        }
        if (count > 0) {
            p.inventoryContainer.detectAndSendChanges();
        }
        return count;
    }

    private static boolean matches(ItemStack stack, String[] ids) {
        Object name = Item.itemRegistry.getNameForObject(stack.getItem());
        if (!(name instanceof String)) {
            return false;
        }
        String id = (String) name;
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals(id)) {
                return true;
            }
        }
        return false;
    }

    private static void shuffleInventory(EntityPlayerMP p) {
        InventoryPlayer inv = p.inventory;
        List<ItemStack> all = new ArrayList<ItemStack>();
        for (int i = 0; i < inv.mainInventory.length; i++) {
            if (inv.mainInventory[i] != null) {
                all.add(inv.mainInventory[i]);
                inv.mainInventory[i] = null;
            }
        }
        Collections.shuffle(all, RAND);
        for (int i = 0; i < all.size(); i++) {
            inv.mainInventory[i] = all.get(i);
        }
        p.inventoryContainer.detectAndSendChanges();
    }

    private static int safeY(World w, int x, int y, int z) {
        for (int yy = y + 24; yy > y - 40; yy--) {
            if (!w.blockExists(x, yy, z)) {
                continue;
            }
            Block b = w.getBlock(x, yy, z);
            if (b != Blocks.air && b != Blocks.water && b != Blocks.flowing_water) {
                return yy + 1;
            }
        }
        return y + 1;
    }

    private static void cmd(String command) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            server.getCommandManager().executeCommand(server, command);
        }
    }

    private static String fmt(double v) {
        return String.format(Locale.US, "%.2f", Double.valueOf(v));
    }

    private ChaosRunner() {
    }
}
