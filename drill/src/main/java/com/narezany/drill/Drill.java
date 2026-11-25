package com.narezany.drill;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

/**
 * Три бура, которые ломают квадрат 3×3 за фиксированное время:
 * железный — 2.5 с, золотой — 0.2 с, алмазный — 0.5 с.
 */
@Mod(modid = Drill.MODID,
        name = "Drill",
        version = Drill.VERSION,
        acceptableRemoteVersions = "*",
        acceptedMinecraftVersions = "[1.7.10]")
public class Drill {

    public static final String MODID = "drill";
    public static final String VERSION = "1.2.0";

    /* 20 тиков = 1 секунда */
    public static final float TICKS_IRON = 50.0F;    // 2.5 с
    public static final float TICKS_GOLD = 4.0F;     // 0.2 с
    public static final float TICKS_DIAMOND = 10.0F; // 0.5 с

    public static Item drillIron;
    public static Item drillGold;
    public static Item drillDiamond;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        drillIron = new ItemDrill("drill.iron", 3990, TICKS_IRON, 3.0F, "drill:iron_drill");
        drillGold = new ItemDrill("drill.gold", 666, TICKS_GOLD, 6.0F, "drill:gold_drill");
        drillDiamond = new ItemDrill("drill.diamond", 19310, TICKS_DIAMOND, 9.0F, "drill:diamond_drill");

        // железный оставляем под старым id, чтобы уже скрафченные буры не пропали
        GameRegistry.registerItem(drillIron, "drill");
        GameRegistry.registerItem(drillGold, "drill_gold");
        GameRegistry.registerItem(drillDiamond, "drill_diamond");
    }

    /** Любой из трёх буров. */
    public static boolean isDrill(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        return item == drillIron || item == drillGold || item == drillDiamond;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // схема одна и та же: 3 доски, 6 слитков/алмазов, палка
        GameRegistry.addRecipe(new ItemStack(drillIron),
                "PII",
                "PSI",
                "PII",
                'P', Blocks.planks,
                'I', Items.iron_ingot,
                'S', Items.stick);

        GameRegistry.addRecipe(new ItemStack(drillGold),
                "PGG",
                "PSG",
                "PGG",
                'P', Blocks.planks,
                'G', Items.gold_ingot,
                'S', Items.stick);

        GameRegistry.addRecipe(new ItemStack(drillDiamond),
                "PDD",
                "PSD",
                "PDD",
                'P', Blocks.planks,
                'D', Items.diamond,
                'S', Items.stick);

        MinecraftForge.EVENT_BUS.register(new DrillHandler());
        FMLCommonHandler.instance().bus().register(new ServerTicker());
    }
}
