package com.narezany.titanium;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;

/** Титановая руда. Ломается алмазной киркой и выше, выпадает сама собой. */
public class BlockTitaniumOre extends Block {

    public BlockTitaniumOre() {
        super(Material.rock);
        setBlockName("titanium.ore");
        setBlockTextureName("titanium:titanium_ore");
        setHardness(4.5F);
        setResistance(12.0F);
        setStepSound(Block.soundTypeStone);
        setCreativeTab(CreativeTabs.tabBlock);
        setHarvestLevel("pickaxe", 3);
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }
}
