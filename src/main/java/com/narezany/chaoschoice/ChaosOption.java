package com.narezany.chaoschoice;

/** Вариант выбора: имя, короткое имя для полоски внизу и список действий. */
public class ChaosOption {

    public final String name;
    public final String shortName;
    public final ChaosAction[] parts;

    public ChaosOption(String name, String shortName, ChaosAction... parts) {
        this.name = name;
        this.shortName = shortName;
        this.parts = parts;
    }
}
