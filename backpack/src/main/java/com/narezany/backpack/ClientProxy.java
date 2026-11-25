package com.narezany.backpack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Клиентская часть GUI: само окно рюкзака. */
public class ClientProxy extends CommonProxy {

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null || !(held.getItem() instanceof ItemBackpack)) {
            return null;
        }
        return new GuiBackpack(new ContainerBackpack(player, held));
    }
}
