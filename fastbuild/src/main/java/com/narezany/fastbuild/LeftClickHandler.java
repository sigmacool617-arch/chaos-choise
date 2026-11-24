package com.narezany.fastbuild;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Левый клик по блоку с палочкой в руке: блок не ломается, а координаты
 * становятся точкой выделения.
 *
 * В 1.7.10 это событие приходит ТОЛЬКО на сервере (ItemInWorldManager.onBlockClicked),
 * на клиенте его не бывает — поэтому всё делаем здесь, без пакетов.
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
        if (player == null || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null || held.getItem() != FastBuild.builderStick) {
            return;
        }

        // блок не ломаем: сервер сам пришлёт клиенту его обратно
        event.setCanceled(true);

        long now = player.worldObj.getTotalWorldTime();
        if (now - lastClick < 6) {
            return;
        }
        lastClick = now;

        SelectionStore.queueSelect(player.getCommandSenderName(), event.x, event.y, event.z);
    }
}
