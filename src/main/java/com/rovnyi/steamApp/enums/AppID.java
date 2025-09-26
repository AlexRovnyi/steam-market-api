package com.rovnyi.steamApp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enum containing the Steam's App ID of games currently supported by this API
 */
public enum AppID {
    STEAM_COMMUNITY(753),
    COUNTER_STRIKE_2(730),
    RUST(252490),
    DOTA_2(570),
    PUBG(578080),
    TEAM_FORTRESS_2(440),
    BANANA(2923300),
    DONT_STARVE_TOGETHER(322330),
    EGG(2784840),
    UNTURNED(304930);

    private final int id;

    AppID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public static Optional<AppID> getEnum(int value) {
        return Arrays.stream(values())
                .filter(appID -> appID.id == value)
                .findFirst();
    }
}