package com.narezany.chaoschoice.client;

/** То, что клиент помнит о текущей волне. */
public final class ClientState {

    /** 0 — до волны N секунд, 1 — идёт выбор. */
    public static int state = 0;
    public static int seconds = 0;
    public static String shortA = "";
    public static String shortB = "";

    public static boolean menuOpen = false;
    public static String nameA = "";
    public static String nameB = "";
    /** Через сколько тиков снова показать меню (0 — сразу). */
    public static int reopen = 160;
    /** Просьба закрыть меню: пакеты приходят в сетевом потоке. */
    public static boolean closeRequested = false;

    private ClientState() {
    }
}
