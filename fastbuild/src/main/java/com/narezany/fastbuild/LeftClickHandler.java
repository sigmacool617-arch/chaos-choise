package com.narezany.fastbuild;

import com.narezany.fastbuild.network.NetworkHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Левый клик по блоку с палочкой в руке: блок не ломается, а координаты
 * уходят на сервер как точка выделения.
 */
public class LeftClickHandler {

    /** Не чаще одного клика за 6 тиков (иначе удержание ЛКМ спамит точки). */
    private static long lastClick = 0;

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        if (event.action != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            return;
        }
        EntityPlayer player = event.entityPlayer;
        if (player == null || player.worldObj == null || !player.worldObj.isRemote) {
            return;
        }
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null || held.getItem() != FastBuild.builderStick) {
            return;
        }

        event.setCanceled(true);

        long now = player.worldObj.getTotalWorldTime();
        if (now - lastClick < 6) {
            return;
        }
        lastClick = now;

        NetworkHandler.sendSelect(event.x, event.y, event.z);
    }
}
