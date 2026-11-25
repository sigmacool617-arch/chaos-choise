package com.narezany.titanium;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/** Каждый надетый элемент титановой брони срезает 12.5% урона. */
public class TitaniumArmorHandler {

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) {
            return;
        }
        if (event.source == null || event.source.isUnblockable()) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        ItemStack[] armor = player.inventory.armorInventory;
        int pieces = 0;
        for (int i = 0; i < armor.length; i++) {
            if (armor[i] != null && armor[i].getItem() instanceof TitaniumArmor) {
                pieces++;
            }
        }
        if (pieces > 0) {
            event.ammount *= 1.0F - 0.125F * pieces;
        }
    }
}
