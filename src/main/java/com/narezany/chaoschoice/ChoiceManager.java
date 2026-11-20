package com.narezany.chaoschoice;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.narezany.chaoschoice.network.NetworkHandler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

/**
 * Сердце мода: раз в N секунд предлагает два случайных варианта,
 * держит отсчёт, принимает голос и исполняет выбранное.
 */
public final class ChoiceManager {

    public static final int TICKS_PER_SECOND = 20;

    private static final Random RAND = new Random();

    /** Голос, пришедший из сетевого пакета; обрабатывается в серверном тике. */
    public static volatile String pendingVotePlayer = null;
    public static volatile int pendingVoteIndex = -1;

    private static boolean running = true;
    private static boolean choosing = false;
    private static int ticksLeft = 0;
    private static int pushTimer = 0;
    private static ChaosOption optionA = null;
    private static ChaosOption optionB = null;

    /* ---------------- управление ---------------- */

    public static void reset() {
        running = true;
        choosing = false;
        ticksLeft = seconds(ChaosConfig.firstWaveSeconds);
        pushTimer = 0;
        optionA = null;
        optionB = null;
        pendingVotePlayer = null;
        pendingVoteIndex = -1;
        BlockJobs.reset();
    }

    public static void stop() {
        running = false;
        choosing = false;
        ticksLeft = 0;
        optionA = null;
        optionB = null;
        pushState();
        broadcast("§8[Хаос] §7выключен.");
    }

    public static void forceStart() {
        running = true;
        if (choosing) {
            return;
        }
        startWave();
    }

    public static String status() {
        if (!running) {
            return "§8[Хаос] §7выключен. Включить: §f/chaos start";
        }
        if (choosing) {
            return "§8[Хаос] §7идёт выбор, осталось §f" + secondsLeft() + "с §7| правок мира в очереди: §f"
                    + BlockJobs.pending();
        }
        return "§8[Хаос] §7следующая волна через §f" + secondsLeft() + "с";
    }

    /* ---------------- тик ---------------- */

    public static void serverTick() {
        takeVote();

        if (!running) {
            return;
        }

        List<EntityPlayerMP> online = players();
        if (online.isEmpty()) {
            return;
        }

        if (ticksLeft > 0) {
            ticksLeft--;
        }

        if (pushTimer > 0) {
            pushTimer--;
        }

        if (pushTimer <= 0) {
            pushTimer = TICKS_PER_SECOND;
            pushState();
        }

        if (choosing) {
            if (ticksLeft <= 0) {
                resolveFate();
            }
        } else if (ticksLeft <= 0) {
            startWave();
        }
    }

    private static void takeVote() {
        String voter = pendingVotePlayer;
        int index = pendingVoteIndex;
        if (voter == null) {
            return;
        }
        pendingVotePlayer = null;
        pendingVoteIndex = -1;
        handleVote(voter, index);
    }

    /* ---------------- волна ---------------- */

    private static void startWave() {
        List<EntityPlayerMP> online = players();
        if (online.isEmpty()) {
            ticksLeft = seconds(10);
            return;
        }
        optionA = ChaosRegistry.random();
        optionB = ChaosRegistry.randomOther(optionA);
        choosing = true;
        ticksLeft = seconds(Math.max(5, ChaosConfig.choiceSeconds));
        pushTimer = 0;

        for (int i = 0; i < online.size(); i++) {
            EntityPlayerMP p = online.get(i);
            NetworkHandler.sendOpen(p, optionA.name, optionB.name, secondsLeft());
            p.addChatComponentMessage(new ChatComponentText(
                    "§8[Хаос] §fA§7) §e" + optionA.name + " §8| §fB§7) §e" + optionB.name));
        }
        broadcast("§8[Хаос] §7у тебя §f" + secondsLeft() + "с §7— меню внизу экрана.");
        pushState();
    }

    private static void resolveFate() {
        List<EntityPlayerMP> online = players();
        ChaosOption chosen = (optionA != null && RAND.nextBoolean()) ? optionA : optionB;
        if (chosen == null) {
            endWave();
            return;
        }
        EntityPlayerMP victim = online.isEmpty() ? null : online.get(RAND.nextInt(online.size()));
        broadcast("§8[Хаос] §7время вышло. Судьба выбрала: §e" + chosen.name);
        apply(chosen, victim);
        endWave();
    }

    private static void handleVote(String playerName, int index) {
        if (!choosing || optionA == null || optionB == null) {
            return;
        }
        EntityPlayerMP p = find(playerName);
        if (p == null) {
            return;
        }
        if (index != 0 && index != 1) {
            return;
        }
        ChaosOption chosen = index == 0 ? optionA : optionB;
        broadcast("§8[Хаос] §f" + playerName + " §7выбрал: §e" + chosen.name);
        apply(chosen, p);
        endWave();
    }

    private static void apply(ChaosOption option, EntityPlayerMP player) {
        String result;
        try {
            result = ChaosRunner.run(option, player);
        } catch (Throwable t) {
            t.printStackTrace();
            result = "что-то пошло не так";
        }
        if (result != null && result.length() > 0) {
            broadcast("§8→ §7" + result);
        }
    }

    private static void endWave() {
        choosing = false;
        optionA = null;
        optionB = null;
        ticksLeft = seconds(Math.max(5, ChaosConfig.waveIntervalSeconds));
        pushTimer = 0;
        pushState();
    }

    /* ---------------- игроки и сеть ---------------- */

    public static List<EntityPlayerMP> players() {
        List<EntityPlayerMP> out = new ArrayList<EntityPlayerMP>();
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) {
            return out;
        }
        List<?> raw = server.getConfigurationManager().playerEntityList;
        for (int i = 0; i < raw.size(); i++) {
            Object o = raw.get(i);
            if (o instanceof EntityPlayerMP) {
                out.add((EntityPlayerMP) o);
            }
        }
        return out;
    }

    private static EntityPlayerMP find(String name) {
        List<EntityPlayerMP> online = players();
        for (int i = 0; i < online.size(); i++) {
            if (online.get(i).getCommandSenderName().equalsIgnoreCase(name)) {
                return online.get(i);
            }
        }
        return null;
    }

    public static void broadcast(String message) {
        List<EntityPlayerMP> online = players();
        for (int i = 0; i < online.size(); i++) {
            online.get(i).addChatComponentMessage(new ChatComponentText(message));
        }
    }

    public static void setPendingVote(String playerName, int index) {
        pendingVotePlayer = playerName;
        pendingVoteIndex = index;
    }

    private static void pushState() {
        List<EntityPlayerMP> online = players();
        if (online.isEmpty()) {
            return;
        }
        int sec = secondsLeft();
        String a = (choosing && optionA != null) ? optionA.shortName : "";
        String b = (choosing && optionB != null) ? optionB.shortName : "";
        int state = choosing ? 1 : 0;
        for (int i = 0; i < online.size(); i++) {
            NetworkHandler.sendWave(online.get(i), state, sec, a, b);
        }
    }

    public static int secondsLeft() {
        return (ticksLeft + TICKS_PER_SECOND - 1) / TICKS_PER_SECOND;
    }

    private static int seconds(int value) {
        return Math.max(0, value) * TICKS_PER_SECOND;
    }

    private ChoiceManager() {
    }
}
