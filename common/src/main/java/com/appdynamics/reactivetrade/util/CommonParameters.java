package com.appdynamics.reactivetrade.util;

import io.vertx.core.json.JsonObject;

/**
 * Created by trader on 3/11/15.
 */
public class CommonParameters {

    private double spread;
    private double fixedFeePerTrade;
    private double perShareFee;
    private double perTradeCostForMarket;
    private double custSatCostPerDroppedTrade;

    public CommonParameters(String prefix, JsonObject appConf) {
        spread = appConf.getDouble(prefix + ".spread.threshold").doubleValue();
        fixedFeePerTrade = appConf.getDouble(prefix + ".trade.fixed.fee").doubleValue();
        perShareFee = appConf.getDouble(prefix + ".per.share.fee").doubleValue();
        perTradeCostForMarket = appConf.getDouble(prefix + ".market.cost.per.trade").doubleValue();
        custSatCostPerDroppedTrade = appConf.getDouble(prefix + ".custsat.negative.perdroppedtrade").doubleValue();
    }

    public double getSpread() {
        return spread;
    }

    public double getFixedFeePerTrade() {
        return fixedFeePerTrade;
    }

    public double getPerShareFee() {
        return perShareFee;
    }

    public double getPerTradeCostForMarket() {
        return perTradeCostForMarket;
    }

    public double getCustSatCostPerDroppedTrade() {
        return custSatCostPerDroppedTrade;
    }
}
