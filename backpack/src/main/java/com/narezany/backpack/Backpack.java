package com.narezany.backpack;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Рюкзаки трёх уровней: 9, 18 и 27 слотов.
 */
@Mod(modid = Backpack.MODID,
        name = "Backpack",
        version = Backpack.VERSION,
        acceptableRemoteVersions = "*",
        acceptedMinecraftVersions = "[1.7.10]")
public class Backpack {

    public static final String MODID = "backpack";
    public static final String VERSION = "1.0.1";

    @Mod.Instance(MODID)
    public static Backpack instance;

    @SidedProxy(clientSide = "com.narezany.backpack.ClientProxy",
            serverSide = "com.narezany.backpack.CommonProxy")
    public static CommonProxy proxy;

    public static Item backpackLeather;
    public static Item backpackIron;
    public static Item backpackDiamond;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        backpackLeather = new ItemBackpack(1);
        backpackIron = new ItemBackpack(2);
        backpackDiamond = new ItemBackpack(3);

        GameRegistry.registerItem(backpackLeather, "backpack_leather");
        GameRegistry.registerItem(backpackIron, "backpack_iron");
        GameRegistry.registerItem(backpackDiamond, "backpack_diamond");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

        // 1 уровень: шесть кожаных туник и три нитки
        GameRegistry.addRecipe(new ItemStack(backpackLeather),
                "LLL",
                "SSS",
                "LLL",
                'L', Items.leather_chestplate,
                'S', Items.string);

        // 2 уровень: первый рюкзак в центре, вокруг восемь железных слитков
        GameRegistry.addRecipe(new ItemStack(backpackIron),
                "III",
                "IBI",
                "III",
                'I', Items.iron_ingot,
                'B', backpackLeather);

        // 3 уровень: второй в центре, слева три алмаза, кожа и четыре железа
        GameRegistry.addRecipe(new ItemStack(backpackDiamond),
                "DLI",
                "DBI",
                "DII",
                'D', Items.diamond,
                'L', Items.leather,
                'I', Items.iron_ingot,
                'B', backpackIron);
    }
}
