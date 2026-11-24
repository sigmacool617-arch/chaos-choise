package com.narezany.drill;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/** Сам бур. Ломает не он сам, а задание в DrillJobs — так время всегда одно и то же. */
public class ItemDrill extends Item {

    /** Прочность бура. */
    public static final int DURABILITY = 3990;

    public ItemDrill() {
        setUnlocalizedName("drill");
        setTextureName("drill:drill");
        setMaxStackSize(1);
        setMaxDamage(DURABILITY);
        setCreativeTab(CreativeTabs.tabTools);
    }
}
