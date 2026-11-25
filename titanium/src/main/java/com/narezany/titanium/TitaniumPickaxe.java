package com.narezany.titanium;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemPickaxe;

/** Кирка из титана: конструктор у ванильной кирки protected, поэтому свой класс. */
public class TitaniumPickaxe extends ItemPickaxe {

    public TitaniumPickaxe() {
        super(Titanium.TOOL_MATERIAL);
        setCreativeTab(CreativeTabs.tabTools);
    }
}
