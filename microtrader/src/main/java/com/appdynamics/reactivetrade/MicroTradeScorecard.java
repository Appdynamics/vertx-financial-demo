package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.util.MarketScorecard;

/**
 * Created by trader on 3/11/15.
 */
public class MicroTradeScorecard extends MarketScorecard {

    public MicroTradeScorecard(double fixedCostPerTrade,
                               double perShareCost,
                               double perTradeCostForMarket,
                               double custSatCostPerDroppedTrade) {
        super(fixedCostPerTrade, perShareCost, perTradeCostForMarket, custSatCostPerDroppedTrade);
    }

    @Override
    protected void recordTradeRevenue(int quantity) {
        double shareBasedRevenue = 0.0;
        int subjectToFees = quantity - 100;
        if (subjectToFees > 0) {
            shareBasedRevenue += (getPerShareCost() * subjectToFees);
        }

        addTradeRevenue(getFixedFeePerTrade() + shareBasedRevenue);
    }
}
