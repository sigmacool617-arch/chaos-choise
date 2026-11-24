package com.narezany.drill;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Левый клик по блоку с буром в руке. В 1.7.10 это событие приходит только
 * на сервере, поэтому всё делаем здесь: клик отменяем (блок не ломается),
 * а координаты ставим в очередь бурения.
 */
public class DrillHandler {

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
        if (held == null || held.getItem() != Drill.drill) {
            return;
        }

        event.setCanceled(true);

        DrillJobs.start((EntityPlayerMP) player, player.worldObj,
                event.x, event.y, event.z, event.face);
    }
}
