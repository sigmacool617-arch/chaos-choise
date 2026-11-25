package com.narezany.drill;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

/**
 * Бур. Настоящая кирка: ломает как обычно, с полоской прогресса и дропом,
 * но скорость подобрана так, чтобы любой блок ломался одинаково долго.
 */
public class ItemDrill extends ItemPickaxe {

    /** Сколько тиков занимает ломание любого блока. */
    private final float digTicks;

    /** Урон по мобам. */
    private final float attackDamage;

    /**
     * @param unlocalized  "drill.iron" / "drill.gold" / "drill.diamond"
     * @param durability   прочность
     * @param digTicks     время ломания в тиках (20 тиков = 1 секунда)
     * @param attackDamage урон
     * @param texture      "drill:iron_drill" и т.п.
     */
    public ItemDrill(String unlocalized, int durability, float digTicks, float attackDamage, String texture) {
        super(ToolMaterial.EMERALD);
        this.digTicks = digTicks;
        this.attackDamage = attackDamage;
        setUnlocalizedName(unlocalized);
        setTextureName(texture);
        setMaxStackSize(1);
        setMaxDamage(durability);
        setCreativeTab(CreativeTabs.tabTools);
    }

    /**
     * Ваниль считает прогресс как speed / твёрдость / 30 за тик.
     * Значит время = твёрдость * 30 / speed. Подставляем speed = твёрдость * 30 / digTicks,
     * чтобы время всегда было одинаковым.
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
        return hardness * 30.0F / digTicks;
    }

    /** Урон бура по мобам. */
    @Override
    public float getDamageVsEntity(Entity target) {
        return attackDamage;
    }

    /** Полоска прочности видна всегда, а не только после первой царапины. */
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }
}
