package com.narezany.chaoschoice;

import com.narezany.chaoschoice.network.NetworkHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

/**
 * Chaos Choice — раз в минуту игра предлагает два случайных «хаоса»,
 * отсчёт и меню появляются внизу экрана.
 */
@Mod(modid = ChaosChoice.MODID,
        name = "Chaos Choice",
        version = ChaosChoice.VERSION,
        acceptableRemoteVersions = "*",
        acceptedMinecraftVersions = "[1.7.10]")
public class ChaosChoice {

    public static final String MODID = "chaoschoice";
    public static final String VERSION = "1.0.0";

    @Mod.Instance(MODID)
    public static ChaosChoice instance;

    @SidedProxy(clientSide = "com.narezany.chaoschoice.client.ClientProxy",
            serverSide = "com.narezany.chaoschoice.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ChaosConfig.load(event);
        NetworkHandler.init();
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new ServerTicker());
        proxy.init();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        ChoiceManager.reset();
        event.registerServerCommand(new CommandChaos());
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        ChoiceManager.stop();
    }
}
