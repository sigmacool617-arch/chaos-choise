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
 * Бур — бурит квадрат 3×3 за фиксированные 2.5 секунды.
 * Прочность 3990, за каждый сломанный блок снимается 1 единица (за квадрат 3×3 — 9).
 */
@Mod(modid = Drill.MODID,
        name = "Drill",
        version = Drill.VERSION,
        acceptableRemoteVersions = "*",
        acceptedMinecraftVersions = "[1.7.10]")
public class Drill {

    public static final String MODID = "drill";
    public static final String VERSION = "1.0.0";

    public static Item drill;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        drill = new ItemDrill();
        GameRegistry.registerItem(drill, "drill");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // по схеме со скриншота: три доски, шесть железных слитков, палка
        GameRegistry.addRecipe(new ItemStack(drill),
                "PII",
                "PSI",
                "PII",
                'P', Blocks.planks,
                'I', Items.iron_ingot,
                'S', Items.stick);

        MinecraftForge.EVENT_BUS.register(new DrillHandler());
        FMLCommonHandler.instance().bus().register(new ServerTicker());
    }
}
