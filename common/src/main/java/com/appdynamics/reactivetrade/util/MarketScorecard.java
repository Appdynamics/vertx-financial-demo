package com.appdynamics.reactivetrade.util;

/**
 * Created by trader on 7/31/14.
 */
public abstract class MarketScorecard {

    private double spreadRevenue;
    private double tradeRevenue;
    private double tradeCost;
    private double custSatScore;
    private double fixedFeePerTrade;
    private double perShareCost;
    private double perTradeCostForMarket;
    private double custSatCostPerDroppedTrade;

    protected MarketScorecard(double fixedCostPerTrade,
                              double perShareCost,
                              double perTradeCostForMarket,
                              double custSatCostPerDroppedTrade) {
        spreadRevenue = 0.0;
        this.fixedFeePerTrade = fixedCostPerTrade;
        this.perShareCost = perShareCost;
        this.perTradeCostForMarket = perTradeCostForMarket;
        this.custSatCostPerDroppedTrade = custSatCostPerDroppedTrade;
    }

    public void recordTrade(double spread, int quantity, double customerGoodwill) {
        int spreadProfit = (int) (spread * quantity);
        recordSpreadRevenue(spreadProfit);
        recordTradeCost();
        recordTradeRevenue(quantity);
        adjustCustomerSatisfactionScore((customerGoodwill * quantity) / 1000);
    }

    protected void recordSpreadRevenue(double revenue) {
        addSpreadRevenue(revenue);
    }

    protected synchronized void addSpreadRevenue(double revenue) {
        spreadRevenue += revenue;
    }

    protected abstract void recordTradeRevenue(int quantity);

    protected synchronized void addTradeRevenue(double revenue) {
        tradeRevenue += revenue;
    }

    protected void recordTradeCost() {
        addTradeCost(perTradeCostForMarket);
    }

    protected synchronized void addTradeCost(double cost) {
        tradeCost += cost;
    }

    public void recordDroppedOrder() {
        recordDroppedOrderCost();
    }

    protected void recordDroppedOrderCost() {
        adjustCustomerSatisfactionScore(-(custSatCostPerDroppedTrade));
    }

    protected synchronized void adjustCustomerSatisfactionScore(double delta) {
        custSatScore += delta;
    }

    public synchronized double getSpreadRevenue() {
        double revenue = spreadRevenue;
        spreadRevenue = 0.0;
        return revenue;
    }

    public synchronized double getTradeCost() {
        double cost = tradeCost;
        tradeCost = 0.0;
        return cost;
    }

    public synchronized double getCustomerSatisfactionScore() {
        double score = custSatScore;
        custSatScore = 0.0;
        return score;
    }

    public synchronized double getTradeRevenue() {
        double revenue = tradeRevenue;
        tradeRevenue = 0.0;
        return revenue;
    }

    protected double getPerShareCost() {
        return perShareCost;
    }

    protected double getFixedFeePerTrade() {
        return fixedFeePerTrade;
    }
}
