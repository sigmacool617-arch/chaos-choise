package com.narezany.chaoschoice;

/**
 * Одно действие внутри варианта «хаоса».
 * Действия собираются фабриками ниже, а исполняются в ChaosRunner.
 */
public class ChaosAction {

    public enum Kind {
        KILL, MONSTERS, WIPE, GIVE, EFFECT, HURT, HEALTH, HEAL, XP,
        SPAWN, NIGHT, STORM, LIGHTNING, UP, RANDOM_TP, PIT, FIRE,
        DROP_ALL, SHUFFLE, BREAK, BLOCKS
    }

    public Kind kind;
    public String[] ids = new String[0];
    public int[] fromBlocks = new int[0];
    public int toBlock = 0;
    public String label = "";
    public int amount = 1;
    public int radius = 16;
    public int seconds = 0;
    public int amplifier = 0;
    public int depth = 3;
    public String mob = "";

    private ChaosAction(Kind kind) {
        this.kind = kind;
    }

    /* ---------- мобы ---------- */

    public static ChaosAction kill(String... ids) {
        ChaosAction a = new ChaosAction(Kind.KILL);
        a.ids = ids;
        return a;
    }

    public static ChaosAction monsters() {
        return new ChaosAction(Kind.MONSTERS);
    }

    /* ---------- предметы ---------- */

    public static ChaosAction wipe(String... ids) {
        ChaosAction a = new ChaosAction(Kind.WIPE);
        a.ids = ids;
        return a;
    }

    public static ChaosAction give(String id, int amount, String label) {
        ChaosAction a = new ChaosAction(Kind.GIVE);
        a.ids = new String[] { id };
        a.amount = amount;
        a.label = label;
        return a;
    }

    /* ---------- игрок ---------- */

    public static ChaosAction effect(int id, int seconds, int amplifier, String label) {
        ChaosAction a = new ChaosAction(Kind.EFFECT);
        a.amount = id;
        a.seconds = seconds;
        a.amplifier = amplifier;
        a.label = label;
        return a;
    }

    public static ChaosAction xp(int amount) {
        ChaosAction a = new ChaosAction(Kind.XP);
        a.amount = amount;
        return a;
    }

    public static ChaosAction hurt(int hearts) {
        ChaosAction a = new ChaosAction(Kind.HURT);
        a.amount = hearts;
        return a;
    }

    public static ChaosAction health(int hp) {
        ChaosAction a = new ChaosAction(Kind.HEALTH);
        a.amount = hp;
        return a;
    }

    public static ChaosAction heal() {
        return new ChaosAction(Kind.HEAL);
    }

    public static ChaosAction up(int blocks) {
        ChaosAction a = new ChaosAction(Kind.UP);
        a.amount = blocks;
        return a;
    }

    public static ChaosAction randomTp(int radius) {
        ChaosAction a = new ChaosAction(Kind.RANDOM_TP);
        a.radius = radius;
        return a;
    }

    public static ChaosAction dropAll() {
        return new ChaosAction(Kind.DROP_ALL);
    }

    public static ChaosAction shuffle() {
        return new ChaosAction(Kind.SHUFFLE);
    }

    public static ChaosAction breakTools() {
        return new ChaosAction(Kind.BREAK);
    }

    /* ---------- мир ---------- */

    public static ChaosAction spawn(String mob, int count, String label) {
        ChaosAction a = new ChaosAction(Kind.SPAWN);
        a.mob = mob;
        a.amount = count;
        a.label = label;
        return a;
    }

    public static ChaosAction night() {
        return new ChaosAction(Kind.NIGHT);
    }

    public static ChaosAction storm(int seconds) {
        ChaosAction a = new ChaosAction(Kind.STORM);
        a.seconds = seconds;
        return a;
    }

    public static ChaosAction lightning() {
        return new ChaosAction(Kind.LIGHTNING);
    }

    public static ChaosAction pit(int depth) {
        ChaosAction a = new ChaosAction(Kind.PIT);
        a.depth = depth;
        return a;
    }

    public static ChaosAction fire(int count) {
        ChaosAction a = new ChaosAction(Kind.FIRE);
        a.amount = count;
        return a;
    }

    public static ChaosAction blocks(int[] from, int to, String label) {
        ChaosAction a = new ChaosAction(Kind.BLOCKS);
        a.fromBlocks = from;
        a.toBlock = to;
        a.label = label;
        return a;
    }
}
