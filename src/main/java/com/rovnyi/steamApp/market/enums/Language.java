package com.rovnyi.steamApp.market.enums;

/**
 * Enum containing all languages supported by Steam
 */
public enum Language {
    ENGLISH("english"),
    NORWEGIAN("norwegian"),
    ARABIC("arabic"),
    BULGARIAN("bulgarian"),
    CZECH("czech"),
    DANISH("danish"),
    DUTCH("dutch"),
    FINNISH("finnish"),
    FRENCH("french"),
    GERMAN("german"),
    GREEK("greek"),
    HUNGARIAN("hungarian"),
    ITALIAN("italian"),
    JAPANESE("japanese"),
    KOREAN("korean"),
    POLISH("polish"),
    PORTUGUESE("portuguese"),
    ROMANIAN("romanian"),
    RUSSIAN("russian"),
    SPANISH("spanish"),
    SWEDISH("swedish"),
    THAI("thai"),
    TURKISH("turkish"),
    UKRAINIAN("ukrainian"),
    VIETNAMESE("vietnamese");

    private final String language;

    Language(String language){
        this.language = language;
    }

    public String getLanguage(){
        return this.language;
    }
}