package com.narezany.backpack;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/** Слот рюкзака: рюкзак в рюкзак не кладётся. */
public class SlotBackpack extends Slot {

    public SlotBackpack(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return !(stack.getItem() instanceof ItemBackpack);
    }
}
