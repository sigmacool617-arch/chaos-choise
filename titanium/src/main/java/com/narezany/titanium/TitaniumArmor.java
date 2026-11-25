package com.narezany.titanium;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * Титановая броня: 11720 прочности у каждого элемента.
 * Защита: по очкам как у алмазной, плюс каждый надетый элемент
 * срезает 12.5% входящего урона — полный сет даёт ровно в два раза
 * меньше урона, чем полный алмазный.
 */
public class TitaniumArmor extends ItemArmor {

    public TitaniumArmor(int armorType) {
        super(Titanium.ARMOR_MATERIAL, 3, armorType);
        setMaxDamage(Titanium.ARMOR_DURABILITY);
        switch (armorType) {
            case 0:
                setUnlocalizedName("titanium.helmet");
                setTextureName("titanium:titanium_helmet");
                break;
            case 1:
                setUnlocalizedName("titanium.chestplate");
                setTextureName("titanium:titanium_chestplate");
                break;
            case 2:
                setUnlocalizedName("titanium.leggings");
                setTextureName("titanium:titanium_leggings");
                break;
            default:
                setUnlocalizedName("titanium.boots");
                setTextureName("titanium:titanium_boots");
                break;
        }
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return "textures/models/armor/titanium_layer_" + (slot == 2 ? 2 : 1) + ".png";
    }
}
