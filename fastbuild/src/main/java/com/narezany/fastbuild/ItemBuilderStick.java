package com.narezany.fastbuild;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Сама палочка: правый клик по блоку заполняет выделение. */
public class ItemBuilderStick extends Item {

    public ItemBuilderStick() {
        setUnlocalizedName("fastbuild.builder_stick");
        setTextureName("fastbuild:builder_stick");
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                             int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        if (!(player instanceof EntityPlayerMP)) {
            return true;
        }
        SelectionStore.fill((EntityPlayerMP) player, world, x, y, z);
        return true;
    }
}
