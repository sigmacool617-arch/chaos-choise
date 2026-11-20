package com.narezany.chaoschoice;

/** Серверная половина: всё, что связано с экраном, живёт в клиентском прокси. */
public class CommonProxy {

    public void preInit() {
    }

    public void init() {
    }

    /** Пришло состояние волны (отсчёт). На сервере ничего не делаем. */
    public void onWave(int state, int seconds, String shortA, String shortB) {
    }

    /** Открыть меню выбора игроку. На сервере ничего не делаем. */
    public void onOpen(String nameA, String nameB, int seconds) {
    }
}
