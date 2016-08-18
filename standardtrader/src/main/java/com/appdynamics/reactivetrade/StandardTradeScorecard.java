package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.util.MarketScorecard;

/**
 * Created by trader on 3/11/15.
 */
public class StandardTradeScorecard extends MarketScorecard {

    public StandardTradeScorecard(double fixedCostPerTrade,
                                  double perShareCost,
                                  double perTradeCostForMarket,
                                  double custSatCostPerDroppedTrade) {
        super(fixedCostPerTrade, perShareCost, perTradeCostForMarket, custSatCostPerDroppedTrade);
    }

    @Override
    protected synchronized void recordTradeRevenue(int quantity) {
        double shareBasedRevenue = 0.0;
        int subjectToFees = quantity - 1000;
        if (subjectToFees > 0) {
            shareBasedRevenue += (getPerShareCost() * subjectToFees);
        }

        addTradeRevenue(getFixedFeePerTrade() + shareBasedRevenue);
    }
}
