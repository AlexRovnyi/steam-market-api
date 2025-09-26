package com.rovnyi.steamApp.enums;

/**
 * Enum containing all country codes of countries/languages currently supported by Steam
 */
public enum CountryCode {
    ARABIC("AR"),
    BULGARIAN("BG"),
    CHINESE_SIMPLIFIED("CN"), // China
    CHINESE_TRADITIONAL("TW"), // Taiwan
    CZECH("CZ"),
    DANISH("DK"),
    DUTCH("NL"),
    ENGLISH("EN"),
    FINNISH("FI"),
    FRENCH("FR"),
    GERMAN("DE"),
    GREEK("GR"),
    HUNGARIAN("HU"),
    ITALIAN("IT"),
    JAPANESE("JP"),
    KOREAN("KR"),
    NORWEGIAN("NO"),
    POLISH("PL"),
    PORTUGUESE("PT"),
    PORTUGUESE_BRAZIL("BR"),
    ROMANIAN("RO"),
    RUSSIAN("RU"),
    SPANISH_SPAIN("ES"),
    SPANISH_LATIN_AMERICA("MX"),
    SWEDISH("SE"),
    THAI("TH"),
    TURKISH("TR"),
    UKRAINIAN("UA"),
    VIETNAMESE("VN");

    private final String code;

    CountryCode(String code){
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }
}