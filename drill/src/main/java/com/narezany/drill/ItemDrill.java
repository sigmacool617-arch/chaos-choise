package com.narezany.drill;

import java.util.UUID;

import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

/**
 * Бур. Настоящая кирка: ломает как обычно, с полоской прогресса и дропом,
 * но скорость подобрана так, чтобы любой блок ломался одинаково долго.
 */
public class ItemDrill extends ItemPickaxe {

    /**
     * Свой UUID модификатора урона. Нужен свой, а не ванильный Item.field_111210_e,
     * иначе он совпадёт с «Tool modifier», который ставит ItemTool.
     */
    private static final UUID DAMAGE_UUID = UUID.fromString("2b1d6a44-1a55-4b4e-9d3c-6a0e1f9b7c11");

    /** Сколько тиков занимает ломание любого блока. */
    private final float digTicks;

    /** Урон по мобам, как он показывается в подсказке предмета. */
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

    /**
     * Урон в 1.7.10 задаётся не методом, а модификатором атрибута attackDamage.
     * Карту родителя (ItemTool) чистим, чтобы кирка не добавляла свой урон
     * поверх нашего, и кладём ровно один модификатор.
     */
    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap map = super.getItemAttributeModifiers();
        map.clear();
        map.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
                new AttributeModifier(DAMAGE_UUID, "Drill damage", attackDamage, 0));
        return map;
    }

    /** Полоска прочности видна всегда, а не только после первой царапины. */
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }
}
