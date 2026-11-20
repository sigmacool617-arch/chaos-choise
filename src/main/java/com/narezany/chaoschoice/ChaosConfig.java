package com.narezany.chaoschoice;

import java.io.File;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/** Настройки мода: config/ChaosChoice.cfg */
public final class ChaosConfig {

    public static int firstWaveSeconds = 15;
    public static int waveIntervalSeconds = 60;
    public static int choiceSeconds = 60;
    public static int blockBudgetPerTick = 1500;

    public static void load(FMLPreInitializationEvent event) {
        File file = event.getSuggestedConfigurationFile();
        Configuration cfg = new Configuration(file);
        try {
            cfg.load();

            Property first = cfg.get("timing", "firstWaveSeconds", firstWaveSeconds,
                    "Пауза до первой волны, сек");
            Property interval = cfg.get("timing", "waveIntervalSeconds", waveIntervalSeconds,
                    "Пауза после волны (отсчёт новой), сек");
            Property choice = cfg.get("timing", "choiceSeconds", choiceSeconds,
                    "Сколько секунд даётся на выбор");
            Property budget = cfg.get("world", "blockBudgetPerTick", blockBudgetPerTick,
                    "Сколько блоков мира менять за тик");

            firstWaveSeconds = Math.max(1, first.getInt(firstWaveSeconds));
            waveIntervalSeconds = Math.max(1, interval.getInt(waveIntervalSeconds));
            choiceSeconds = Math.max(5, choice.getInt(choiceSeconds));
            blockBudgetPerTick = Math.max(100, budget.getInt(blockBudgetPerTick));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cfg.hasChanged()) {
                cfg.save();
            }
        }
    }

    private ChaosConfig() {
    }
}
