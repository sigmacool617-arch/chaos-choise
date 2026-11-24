package com.narezany.fastbuild;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import com.narezany.fastbuild.network.NetworkHandler;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

/**
 * FastBuild — палочка быстрого строительства.
 * ЛКМ по блоку: точка 1, второй ЛКМ: точка 2.
 * ПКМ по блоку: заполнить область между точками материалом этого блока.
 */
@Mod(modid = FastBuild.MODID,
        name = "FastBuild",
        version = FastBuild.VERSION,
        acceptableRemoteVersions = "*",
        acceptedMinecraftVersions = "[1.7.10]")
public class FastBuild {

    public static final String MODID = "fastbuild";
    public static final String VERSION = "1.1.0";

    public static Item builderStick;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        builderStick = new ItemBuilderStick();
        GameRegistry.registerItem(builderStick, "builder_stick");
        NetworkHandler.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // по схеме: 4 железа + 2 палки
        GameRegistry.addRecipe(new ItemStack(builderStick),
                "I I",
                "ISI",
                " S ",
                'I', Items.iron_ingot,
                'S', Items.stick);

        MinecraftForge.EVENT_BUS.register(new LeftClickHandler());
        FMLCommonHandler.instance().bus().register(new ServerTicker());
    }
}
