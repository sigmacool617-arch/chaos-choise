package com.narezany.drill;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;

/**
 * Бур. Настоящая кирка: ломает как обычно, с полоской прогресса и дропом,
 * но скорость подобрана так, чтобы любой блок ломался ровно 2.5 секунды.
 */
public class ItemDrill extends ItemPickaxe {

    /** Прочность бура. */
    public static final int DURABILITY = 3990;

    /** Сколько тиков занимает ломание: 50 тиков = 2.5 секунды. */
    public static final float DIG_TICKS = 50.0F;

    public ItemDrill() {
        super(ToolMaterial.EMERALD);
        setUnlocalizedName("drill");
        setTextureName("drill:drill");
        setMaxStackSize(1);
        setMaxDamage(DURABILITY);
        setCreativeTab(CreativeTabs.tabTools);
    }

    /**
     * Ваниль считает прогресс как speed / твёрдость / 30 за тик.
     * Значит время = твёрдость * 30 / speed. Подставляем speed = твёрдость * 30 / 50,
     * чтобы всегда выходило 50 тиков.
     */
    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        if (block == null) {
            return 1.0F;
        }
        float hardness;
        try {
            hardness = block.getBlockHardness(null, 0, 0, 0);
        } catch (Throwable t) {
            hardness = 1.0F;
        }
        if (hardness <= 0.0F) {
            // блоки, которые ломаются мгновенно (трава, факелы), не трогаем
            return 1.0F;
        }
        return hardness * 30.0F / DIG_TICKS;
    }

    /** Полоска прочности видна всегда, а не только после первой царапины. */
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }
}
