package com.narezany.backpack;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Серверная часть GUI: контейнер рюкзака. */
public class CommonProxy implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        int slot = x;
        ItemStack held = null;
        if (slot >= 0 && slot < player.inventory.mainInventory.length) {
            held = player.inventory.mainInventory[slot];
        }
        if (held == null || !(held.getItem() instanceof ItemBackpack)) {
            slot = player.inventory.currentItem;
            held = player.getCurrentEquippedItem();
        }
        if (held == null || !(held.getItem() instanceof ItemBackpack)) {
            return null;
        }
        return new ContainerBackpack(player, held, slot);
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }
}
