package com.rovnyi.steamApp.enums;

/**
 * Enum containing the currencies of countries currently supported by this API
 */
public enum CurrencyCode {
    USD(1),      // United States Dollar
    GBP(2),      // British Pound
    EUR(3),      // Euro
    RUB(5),      // Russian Ruble
    BRL(7),      // Brazilian Real
    JPY(8),      // Japanese Yen
    NOK(9),      // Norwegian Krone
    AUD(12),     // Australian Dollar
    CAD(20),     // Canadian Dollar
    PLN(21),     // Polish Zloty
    UAH(18),     // Ukrainian Hryvnia
    TRY(17),     // Turkish Lira
    CNY(23),     // Chinese Yuan
    INR(24),     // Indian Rupee
    MXN(26);     // Mexican Peso

    private final int id;

    CurrencyCode(int id){
        this.id = id;
    }

    public int getCode(){
        return this.id;
    }
}