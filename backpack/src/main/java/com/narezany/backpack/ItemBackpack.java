package com.narezany.backpack;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Рюкзак: 1 уровень — 9 слотов, 2 — 18, 3 — 27. */
public class ItemBackpack extends Item {

    private final int tier;

    public ItemBackpack(int tier) {
        this.tier = tier;
        setUnlocalizedName("backpack.t" + tier);
        setTextureName("backpack:backpack_" + tier);
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabTools);
    }

    public int getTier() {
        return tier;
    }

    /** Сколько слотов внутри. */
    public int getSlots() {
        return tier * 9;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        // открываем только на сервере: клиент откроет окно по пакету
        if (!world.isRemote && !player.isSneaking()) {
            player.openGui(Backpack.instance, tier, world, 0, 0, 0);
        }
        return stack;
    }
}
