package com.narezany.drill;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

/**
 * Ловит два момента, оба приходят только на сервере:
 * левый клик (запоминаем грань, по которой бьют) и слом блока (ставим квадрат 3×3).
 * Само ломание делает ваниль — с прогрессом, дропом и износом бура.
 */
public class DrillHandler {

    /** Сколько тиков помним грань клика. */
    private static final long CLICK_TTL = 200;

    private static final Map<String, Click> CLICKS = new HashMap<String, Click>();

    private static final class Click {
        int x, y, z, face;
        long born;
    }

    @SubscribeEvent
    public void onClick(PlayerInteractEvent event) {
        if (event.action != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            return;
        }
        EntityPlayer player = event.entityPlayer;
        if (player == null || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }
        if (heldDrill(player) == null) {
            return;
        }

        Click click = new Click();
        click.x = event.x;
        click.y = event.y;
        click.z = event.z;
        click.face = event.face;
        click.born = player.worldObj.getTotalWorldTime();
        CLICKS.put(player.getCommandSenderName(), click);
    }

    @SubscribeEvent
    public void onBroken(BlockEvent.BreakEvent event) {
        // это мы сами добиваем квадрат — второй квадрат не запускаем
        if (DrillJobs.isDigging()) {
            return;
        }
        EntityPlayer player = event.getPlayer();
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        if (event.world == null || event.world.isRemote) {
            return;
        }
        ItemStack held = heldDrill(player);
        if (held == null) {
            return;
        }

        int face = faceFor(player, event.x, event.y, event.z);
        DrillJobs.add((EntityPlayerMP) player, event.world, event.x, event.y, event.z, face);
        CLICKS.remove(player.getCommandSenderName());
    }

    /* ---------------- вспомогательное ---------------- */

    private static ItemStack heldDrill(EntityPlayer player) {
        ItemStack held = player.getCurrentEquippedItem();
        return Drill.isDrill(held) ? held : null;
    }

    /** Грань, по которой кликнули; если клик не запомнился — смотрим, куда смотрит игрок. */
    private static int faceFor(EntityPlayer player, int x, int y, int z) {
        Click click = CLICKS.get(player.getCommandSenderName());
        if (click != null
                && click.x == x && click.y == y && click.z == z
                && player.worldObj.getTotalWorldTime() - click.born <= CLICK_TTL) {
            return click.face;
        }
        return faceFromLook(player);
    }

    /**
     * Грань из направления взгляда: 0/1 — квадрат в плоскости XZ,
     * 2/3 — плоскость XY, 4/5 — плоскость YZ.
     */
    private static int faceFromLook(EntityPlayer player) {
        Vec3 look = player.getLookVec();
        double ax = Math.abs(look.xCoord);
        double ay = Math.abs(look.yCoord);
        double az = Math.abs(look.zCoord);
        if (ay >= ax && ay >= az) {
            return look.yCoord > 0.0D ? 0 : 1;
        }
        if (ax >= az) {
            return look.xCoord > 0.0D ? 4 : 5;
        }
        return look.zCoord > 0.0D ? 2 : 3;
    }
}
