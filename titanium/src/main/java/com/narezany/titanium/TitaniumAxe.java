package com.narezany.titanium;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemAxe;

/** Топор из титана. */
public class TitaniumAxe extends ItemAxe {

    public TitaniumAxe() {
        super(Titanium.TOOL_MATERIAL);
        setCreativeTab(CreativeTabs.tabTools);
    }
}
