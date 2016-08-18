package com.appdynamics.reactivetrade.market;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by trader on 7/25/14.
 */
public class TradedSecurities {

    private static final Set<String> SECURITIES = new HashSet<String>();

    static {
        SECURITIES.add("ORCL");
        SECURITIES.add("AAPL");
        SECURITIES.add("MSFT");
        SECURITIES.add("GOOG");
        SECURITIES.add("FB");
        SECURITIES.add("APPD");
        SECURITIES.add("XOM");
        SECURITIES.add("BAC");
        SECURITIES.add("BRKB");
        SECURITIES.add("IBM");
        SECURITIES.add("ADBE");
    }

    public static String validate(String symbol) {

        String result = null;

        if (symbol != null) {
            String canonical = symbol.trim().toUpperCase();
            if (SECURITIES.contains(canonical)) {
                result = canonical;
            }
        }

        return result;
    }
}
