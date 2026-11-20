package com.narezany.chaoschoice;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Список вариантов для Minecraft 1.7.10.
 * Набор предметов и мобов подогнан под 1.7.10: без незерита, элитр и трезубца.
 */
public final class ChaosRegistry {

    public static final List<ChaosOption> OPTIONS = new ArrayList<ChaosOption>();
    private static final Random RAND = new Random();

    private static final int[] WATER = { 8, 9 };
    private static final int[] GLASS_BLOCKS = { 20, 95, 102, 160 };
    private static final int[] TORCH = { 50, 75, 76 };
    private static final int[] LEAVES = { 18, 161 };
    private static final int[] GRASS = { 2, 31, 175 };
    private static final int[] SAND = { 12 };
    private static final int[] LOGS = { 17, 162 };

    static {
        /* ---------- мобы ---------- */
        add("Удалить всех куриц", "курицы", ChaosAction.kill("Chicken"));
        add("Удалить всех коров", "коровы", ChaosAction.kill("Cow", "MushroomCow"));
        add("Удалить всех свиней", "свиньи", ChaosAction.kill("Pig"));
        add("Удалить всех овец", "овцы", ChaosAction.kill("Sheep"));
        add("Удалить всех зомби", "зомби", ChaosAction.kill("Zombie", "PigZombie"));
        add("Удалить всех криперов", "криперы", ChaosAction.kill("Creeper"));
        add("Удалить всех скелетов", "скелеты", ChaosAction.kill("Skeleton", "WitherSkeleton"));
        add("Удалить всех пауков", "пауки", ChaosAction.kill("Spider", "CaveSpider"));
        add("Удалить всех крестьян", "крестьяне", ChaosAction.kill("Villager"));
        add("Удалить всех волков", "волки", ChaosAction.kill("Wolf"));
        add("Удалить всех котов", "коты", ChaosAction.kill("Ozelot", "Ocelot"));
        add("Удалить всех лошадей", "лошади", ChaosAction.kill("EntityHorse"));
        add("Удалить всех железных големов", "големы", ChaosAction.kill("VillagerGolem", "IronGolem"));
        add("Удалить всех монстров", "монстры", ChaosAction.monsters());

        /* ---------- предметы ---------- */
        add("Удалить все алмазы", "алмазы",
                ChaosAction.wipe("minecraft:diamond", "minecraft:diamond_block"));
        add("Удалить все изумруды", "изумруды",
                ChaosAction.wipe("minecraft:emerald", "minecraft:emerald_block"));
        add("Удалить всё золото", "золото",
                ChaosAction.wipe("minecraft:gold_ingot", "minecraft:gold_block", "minecraft:gold_nugget"));
        add("Удалить всё железо", "железо",
                ChaosAction.wipe("minecraft:iron_ingot", "minecraft:iron_block"));
        add("Удалить весь уголь", "уголь",
                ChaosAction.wipe("minecraft:coal"));
        add("Удалить весь ТНТ", "ТНТ",
                ChaosAction.wipe("minecraft:tnt"));
        add("Удалить все факелы", "факелы",
                ChaosAction.wipe("minecraft:torch"));
        add("Удалить все яблоки", "яблоки",
                ChaosAction.wipe("minecraft:apple", "minecraft:golden_apple"));
        add("Удалить весь хлеб", "хлеб",
                ChaosAction.wipe("minecraft:bread"));
        add("Удалить все стрелы", "стрелы",
                ChaosAction.wipe("minecraft:arrow"));
        add("Удалить все книги", "книги",
                ChaosAction.wipe("minecraft:book", "minecraft:writable_book", "minecraft:written_book",
                        "minecraft:enchanted_book"));
        add("Удалить всю древесину", "дерево",
                ChaosAction.wipe("minecraft:log", "minecraft:log2"));
        add("Удалить весь булыжник", "булыжник",
                ChaosAction.wipe("minecraft:cobblestone"));
        add("Удалить весь песок", "песок",
                ChaosAction.wipe("minecraft:sand"));
        add("Удалить все мечи", "мечи",
                ChaosAction.wipe("minecraft:wooden_sword", "minecraft:stone_sword",
                        "minecraft:iron_sword", "minecraft:golden_sword", "minecraft:diamond_sword"));
        add("Удалить всю броню", "броня",
                ChaosAction.wipe("minecraft:leather_helmet", "minecraft:leather_chestplate",
                        "minecraft:leather_leggings", "minecraft:leather_boots",
                        "minecraft:chainmail_helmet", "minecraft:chainmail_chestplate",
                        "minecraft:chainmail_leggings", "minecraft:chainmail_boots",
                        "minecraft:iron_helmet", "minecraft:iron_chestplate",
                        "minecraft:iron_leggings", "minecraft:iron_boots",
                        "minecraft:golden_helmet", "minecraft:golden_chestplate",
                        "minecraft:golden_leggings", "minecraft:golden_boots",
                        "minecraft:diamond_helmet", "minecraft:diamond_chestplate",
                        "minecraft:diamond_leggings", "minecraft:diamond_boots"));
        add("Удалить все вёдра", "вёдра",
                ChaosAction.wipe("minecraft:bucket", "minecraft:water_bucket",
                        "minecraft:lava_bucket", "minecraft:milk_bucket"));
        add("Удалить все кровати", "кровати",
                ChaosAction.wipe("minecraft:bed"));
        add("Удалить все луки", "луки",
                ChaosAction.wipe("minecraft:bow"));
        add("Удалить всё стекло", "стекло",
                ChaosAction.wipe("minecraft:glass", "minecraft:stained_glass",
                        "minecraft:glass_pane", "minecraft:stained_glass_pane"));
        add("Удалить всю землю", "земля",
                ChaosAction.wipe("minecraft:dirt", "minecraft:grass"));
        add("Удалить весь редстоун", "редстоун",
                ChaosAction.wipe("minecraft:redstone", "minecraft:redstone_block",
                        "minecraft:repeater", "minecraft:comparator"));
        add("Удалить все удочки", "удочки",
                ChaosAction.wipe("minecraft:fishing_rod"));

        /* ---------- плюс с подвохом ---------- */
        add("Получить 1 алмаз — но вся вода вокруг исчезает", "алмаз/нет воды",
                ChaosAction.give("minecraft:diamond", 1, "1 алмаз"),
                ChaosAction.blocks(WATER, 0, "вода испаряется"));
        add("Получить 2 алмаза — но инвентарь перемешивается", "2 алмаза/микс",
                ChaosAction.give("minecraft:diamond", 2, "2 алмаза"),
                ChaosAction.shuffle());
        add("Получить 3 изумруда — но инструменты ломаются", "изумруд/хлам",
                ChaosAction.give("minecraft:emerald", 3, "3 изумруда"),
                ChaosAction.breakTools());
        add("Получить 3 золотых яблока — но рядом 4 крипера", "яблоки/криперы",
                ChaosAction.give("minecraft:golden_apple", 3, "3 золотых яблока"),
                ChaosAction.spawn("Creeper", 4, "4 крипера"));
        add("Получить 30 опыта — но наступает ночь", "опыт/ночь",
                ChaosAction.xp(30),
                ChaosAction.night());
        add("Получить 16 хлеба — но слепота на 30 секунд", "хлеб/слепота",
                ChaosAction.give("minecraft:bread", 16, "16 хлеба"),
                ChaosAction.effect(15, 30, 0, "слепота 30с"));
        add("Получить 64 стрелы — но потерять 4 сердца", "стрелы/урон",
                ChaosAction.give("minecraft:arrow", 64, "64 стрелы"),
                ChaosAction.hurt(4));
        add("Получить алмазный меч — но здоровья 2 сердца", "меч/2 сердца",
                ChaosAction.give("minecraft:diamond_sword", 1, "алмазный меч"),
                ChaosAction.health(4));
        add("Ускорение II на 2 минуты — но рядом 6 зомби", "скорость/зомби",
                ChaosAction.effect(1, 120, 1, "ускорение II"),
                ChaosAction.spawn("Zombie", 6, "6 зомби"));
        add("Регенерация 30 секунд — но голод на минуту", "реген/голод",
                ChaosAction.effect(10, 30, 1, "регенерация"),
                ChaosAction.effect(17, 60, 1, "голод"));
        add("Ночное зрение 3 минуты — но всё стекло исчезает", "зрение/стекло",
                ChaosAction.effect(16, 180, 0, "ночное зрение"),
                ChaosAction.blocks(GLASS_BLOCKS, 0, "стекло исчезает"));
        add("Сила II на 2 минуты — но факелы гаснут", "сила/тьма",
                ChaosAction.effect(5, 120, 1, "сила II"),
                ChaosAction.blocks(TORCH, 0, "факелы гаснут"));
        add("Полное здоровье — но инвентарь перемешивается", "хил/микс",
                ChaosAction.heal(),
                ChaosAction.shuffle());
        add("Получить 32 факела — но трава становится землёй", "факелы/земля",
                ChaosAction.give("minecraft:torch", 32, "32 факела"),
                ChaosAction.blocks(GRASS, 3, "трава чернеет"));
        add("Получить 8 стейков — но всё железо исчезает", "стейки/–железо",
                ChaosAction.give("minecraft:cooked_beef", 8, "8 стейков"),
                ChaosAction.wipe("minecraft:iron_ingot", "minecraft:iron_block"));
        add("Получить 5 изумрудов — но все алмазы исчезают", "изумруд/–алмаз",
                ChaosAction.give("minecraft:emerald", 5, "5 изумрудов"),
                ChaosAction.wipe("minecraft:diamond", "minecraft:diamond_block"));
        add("Получить 16 досок — но песок становится стеклом", "доски/стекло",
                ChaosAction.give("minecraft:planks", 16, "16 досок"),
                ChaosAction.blocks(SAND, 20, "песок стекленеет"));
        add("Получить 3 бутылочки опыта — но несёт на 50 блоков вверх", "опыт/полёт",
                ChaosAction.give("minecraft:experience_bottle", 3, "3 бутылочки опыта"),
                ChaosAction.up(50));
        add("Огнестойкость 3 минуты — но под ногами вспыхивает огонь", "огонь/пожар",
                ChaosAction.effect(12, 180, 0, "огнестойкость"),
                ChaosAction.fire(1));
        add("Получить 16 ТНТ — но гроза на 2 минуты", "ТНТ/гроза",
                ChaosAction.give("minecraft:tnt", 16, "16 ТНТ"),
                ChaosAction.storm(120));
        add("Получить 1 алмаз — но всё добро падает на землю", "алмаз/дроп",
                ChaosAction.give("minecraft:diamond", 1, "1 алмаз"),
                ChaosAction.dropAll());
        add("Получить 2 эндер-жемчуга — но тебя телепортирует незнамо куда", "жемчуг/телепорт",
                ChaosAction.give("minecraft:ender_pearl", 2, "2 эндер-жемчуга"),
                ChaosAction.randomTp(48));
        add("Получить 4 кости — но рядом 3 скелета", "кости/скелеты",
                ChaosAction.give("minecraft:bone", 4, "4 кости"),
                ChaosAction.spawn("Skeleton", 3, "3 скелета"));
        add("Получить 16 пороха — но рядом 3 крипера", "порох/криперы",
                ChaosAction.give("minecraft:gunpowder", 16, "16 пороха"),
                ChaosAction.spawn("Creeper", 3, "3 крипера"));
        add("Получить 10 обсидиана — но вокруг всё горит", "обсидиан/пожар",
                ChaosAction.give("minecraft:obsidian", 10, "10 обсидиана"),
                ChaosAction.fire(6));
        add("Получить 8 досок — но вся древесина вокруг исчезает", "доски/–дерево",
                ChaosAction.give("minecraft:planks", 8, "8 досок"),
                ChaosAction.blocks(LOGS, 0, "деревья исчезают"));
        add("Получить 1 алмаз — но ночь и слепота", "алмаз/тьма",
                ChaosAction.give("minecraft:diamond", 1, "1 алмаз"),
                ChaosAction.night(),
                ChaosAction.effect(15, 15, 0, "слепота 15с"));
        add("Получить 64 стрелы — но все луки исчезают", "стрелы/–луки",
                ChaosAction.give("minecraft:arrow", 64, "64 стрелы"),
                ChaosAction.wipe("minecraft:bow"));
        add("Получить 8 хлеба — но голод и слабость", "хлеб/слабость",
                ChaosAction.give("minecraft:bread", 8, "8 хлеба"),
                ChaosAction.effect(17, 45, 0, "голод"),
                ChaosAction.effect(18, 45, 0, "слабость"));
        add("Получить 5 опыта — но ночь и 5 зомби", "опыт/зомби",
                ChaosAction.xp(5),
                ChaosAction.night(),
                ChaosAction.spawn("Zombie", 5, "5 зомби"));
        add("Получить 1 алмаз — но все коровы исчезают", "алмаз/–коровы",
                ChaosAction.give("minecraft:diamond", 1, "1 алмаз"),
                ChaosAction.kill("Cow", "MushroomCow"));
        add("Получить золотое яблоко — но молния и пожар", "яблоко/огонь",
                ChaosAction.give("minecraft:golden_apple", 1, "золотое яблоко"),
                ChaosAction.lightning(),
                ChaosAction.fire(4));
        add("Получить 8 глаз эндера — но листья облетают", "эндер/листья",
                ChaosAction.give("minecraft:ender_eye", 8, "8 глаз эндера"),
                ChaosAction.blocks(LEAVES, 0, "листья облетают"));
        add("Получить 32 факела — но весь песок исчезает", "факелы/–песок",
                ChaosAction.give("minecraft:torch", 32, "32 факела"),
                ChaosAction.wipe("minecraft:sand"));
    }

    private static void add(String name, String shortName, ChaosAction... parts) {
        OPTIONS.add(new ChaosOption(name, shortName, parts));
    }

    public static ChaosOption random() {
        return OPTIONS.get(RAND.nextInt(OPTIONS.size()));
    }

    public static ChaosOption randomOther(ChaosOption not) {
        ChaosOption other = not;
        for (int i = 0; i < 8 && other == not; i++) {
            other = random();
        }
        return other == not ? random() : other;
    }

    private ChaosRegistry() {
    }
}
