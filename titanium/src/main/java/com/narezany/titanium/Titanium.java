package com.narezany.titanium;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemSpade;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;

/**
 * Титан: руда только на высоте 9, плитки, слитки, броня и инструменты.
 */
@Mod(modid = Titanium.MODID,
        name = "Titanium",
        version = Titanium.VERSION,
        acceptableRemoteVersions = "*",
        acceptedMinecraftVersions = "[1.7.10]")
public class Titanium {

    public static final String MODID = "titanium";
    public static final String VERSION = "1.0.0";

    /** 7096 прочности у всех инструментов. */
    public static Item.ToolMaterial TOOL_MATERIAL;

    /** 11720 прочности у каждого элемента брони. */
    public static Item.ArmorMaterial ARMOR_MATERIAL;

    public static final int ARMOR_DURABILITY = 11720;
    public static final int TOOL_DURABILITY = 7096;

    public static Block ore;
    public static Item plate;
    public static Item ingot;

    public static Item sword;
    public static Item pickaxe;
    public static Item axe;
    public static Item shovel;

    public static Item helmet;
    public static Item chestplate;
    public static Item leggings;
    public static Item boots;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        TOOL_MATERIAL = EnumHelper.addToolMaterial("TITANIUM", 4, TOOL_DURABILITY, 14.0F, 5.0F, 22);
        ARMOR_MATERIAL = EnumHelper.addArmorMaterial("TITANIUM", 40, new int[]{3, 8, 6, 3}, 22);

        ore = new BlockTitaniumOre();
        GameRegistry.registerBlock(ore, "titanium_ore");

        plate = new Item()
                .setUnlocalizedName("titanium.plate")
                .setTextureName("titanium:titanium_plate")
                .setCreativeTab(CreativeTabs.tabMaterials);
        ingot = new Item()
                .setUnlocalizedName("titanium.ingot")
                .setTextureName("titanium:titanium_ingot")
                .setCreativeTab(CreativeTabs.tabMaterials);
        GameRegistry.registerItem(plate, "titanium_plate");
        GameRegistry.registerItem(ingot, "titanium_ingot");

        sword = new ItemSword(TOOL_MATERIAL)
                .setUnlocalizedName("titanium.sword")
                .setTextureName("titanium:titanium_sword");
        pickaxe = new TitaniumPickaxe()
                .setUnlocalizedName("titanium.pickaxe")
                .setTextureName("titanium:titanium_pickaxe");
        axe = new TitaniumAxe()
                .setUnlocalizedName("titanium.axe")
                .setTextureName("titanium:titanium_axe");
        shovel = new ItemSpade(TOOL_MATERIAL)
                .setUnlocalizedName("titanium.shovel")
                .setTextureName("titanium:titanium_shovel");

        pickaxe.setHarvestLevel("pickaxe", 4);
        axe.setHarvestLevel("axe", 4);
        shovel.setHarvestLevel("shovel", 4);

        GameRegistry.registerItem(sword, "titanium_sword");
        GameRegistry.registerItem(pickaxe, "titanium_pickaxe");
        GameRegistry.registerItem(axe, "titanium_axe");
        GameRegistry.registerItem(shovel, "titanium_shovel");

        helmet = new TitaniumArmor(0);
        chestplate = new TitaniumArmor(1);
        leggings = new TitaniumArmor(2);
        boots = new TitaniumArmor(3);

        GameRegistry.registerItem(helmet, "titanium_helmet");
        GameRegistry.registerItem(chestplate, "titanium_chestplate");
        GameRegistry.registerItem(leggings, "titanium_leggings");
        GameRegistry.registerItem(boots, "titanium_boots");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // руда -> плитка в печке
        GameRegistry.addSmelting(ore, new ItemStack(plate), 1.0F);
        // три плитки в любом порядке -> слиток
        GameRegistry.addShapelessRecipe(new ItemStack(ingot), plate, plate, plate);

        GameRegistry.addRecipe(new ItemStack(sword),
                "I",
                "I",
                "S",
                'I', ingot, 'S', Items.stick);
        GameRegistry.addRecipe(new ItemStack(pickaxe),
                "III",
                " S ",
                " S ",
                'I', ingot, 'S', Items.stick);
        GameRegistry.addRecipe(new ItemStack(axe),
                "II",
                "IS",
                " S",
                'I', ingot, 'S', Items.stick);
        GameRegistry.addRecipe(new ItemStack(shovel),
                "I",
                "S",
                "S",
                'I', ingot, 'S', Items.stick);

        GameRegistry.addRecipe(new ItemStack(helmet),
                "III",
                "I I",
                'I', ingot);
        GameRegistry.addRecipe(new ItemStack(chestplate),
                "I I",
                "III",
                "III",
                'I', ingot);
        GameRegistry.addRecipe(new ItemStack(leggings),
                "III",
                "I I",
                "I I",
                'I', ingot);
        GameRegistry.addRecipe(new ItemStack(boots),
                "I I",
                "I I",
                'I', ingot);

        GameRegistry.registerWorldGenerator(new TitaniumWorldGen(), 0);
        MinecraftForge.EVENT_BUS.register(new TitaniumArmorHandler());
    }
}
