package com.appdynamics.reactivetrade.util;

import com.appdynamics.reactivetrade.market.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by trader on 7/25/14.
 */
public class MarketUtils {

    private MarketScorecard scorecard;
    private double spreadThreshold;
    private final Map<String, MarketEngine> engines;

    public MarketUtils(MarketScorecard scorecard, double spreadThreshold) {
        this.scorecard = scorecard;
        this.spreadThreshold = spreadThreshold;
        engines = new HashMap<String, MarketEngine>();
    }

    public MarketEngine getMarketEngine(String pfx, String symbol, int historySize) {
        MarketEngine result = null;
        String canonicalSymbol = TradedSecurities.validate(symbol);
        if (canonicalSymbol != null) {
            synchronized (engines) {
                result = engines.get(canonicalSymbol);
                if (result == null) {
                    Symbol marketSymbol = new Symbol(canonicalSymbol);
                    MarketDefinition def = new MarketDefinition(marketSymbol, "Market for " + canonicalSymbol);
                    result = new MarketEngine(pfx, marketSymbol, historySize, scorecard, spreadThreshold);
                    engines.put(symbol, result);
                    result.start(def);
                }
            }
        }

        return result;
    }

    public MarketScorecard getScorecard() {
        return scorecard;
    }

    public Set<Fill> getFills() {
        Set<Fill> result = new TreeSet<Fill>();
        synchronized (engines) {
            for (MarketEngine engine : engines.values()) {
                result.addAll(engine.getFills());
            }
        }

        return result;
    }

    public Set<Order> getOpenOrders() {
        Set<Order> result = new TreeSet<Order>();
        synchronized (engines) {
            for (MarketEngine engine : engines.values()) {
                result.addAll(engine.getOpenOrders());
            }
        }

        return result;
    }

    public Set<Order> getClosedOrders() {
        Set<Order> result = new TreeSet<Order>();
        synchronized (engines) {
            for (MarketEngine engine : engines.values()) {
                result.addAll(engine.getClosedOrders());
            }
        }

        return result;
    }
}
