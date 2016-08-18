package com.appdynamics.reactivetrade.market;

import com.appdynamics.reactivetrade.util.MarketScorecard;

import java.util.*;

public class SimpleMarket {

    private Symbol symbol;

	/**
	 * <p>The ordered collection of {@link MarketOrder}s on the {@link Side#Buy} side of the market.</p>
	 */
	private TreeSet<MarketOrder> buySide;
	
	/**
	 * <p>The ordered collection of {@link MarketOrder}s on the {@link Side#Sell} side of the market.</p>
	 */
	private TreeSet<MarketOrder> sellSide;

	/**
	 * <p>A map of all {@link MarketOrder} currently managed by the market.</p>
	 */
	private Map<String, MarketOrder> marketOrders;

    private MarketScorecard scorecard;
    private double spreadThreshold;

	/**
	 * <p>Standard Constructor for a {@link com.appdynamics.reactivetrade.market.SimpleMarket}.</p>
	 */
	public SimpleMarket(Symbol symbol, MarketScorecard scorecard, double spreadThreshold) {

        this.symbol = symbol;

        buySide = new TreeSet<MarketOrder>();
		sellSide = new TreeSet<MarketOrder>();
		marketOrders = new HashMap<String, MarketOrder>();
        this.scorecard = scorecard;
        this.spreadThreshold = spreadThreshold;
	}

    public Symbol getSymbol() {
        return symbol;
    }

    private Set<MarketOrder>[] copyOrders() {
        Set<MarketOrder>[] result = new Set[2];
        synchronized (buySide) {
            result[0] = new TreeSet<MarketOrder>(buySide);
        }

        synchronized (sellSide) {
            result[1] = new TreeSet<MarketOrder>(sellSide);
        }

        return result;
    }

    private Set<MarketOrder> getBuySideFromCopies(Set<MarketOrder>[] copies) {
        return copies[0];
    }

    private Set<MarketOrder> getSellSideFromCopies(Set<MarketOrder>[] copies) {
        return copies[1];
    }

    public Set<Fill> getFills() {

        Set<Fill> result = new TreeSet<Fill>();
        Set<MarketOrder>[] copies = copyOrders();

        for (MarketOrder order : getBuySideFromCopies(copies)) {
            if (!order.isFilled()) {
                result.addAll(order.getFills());
            }
        }

        for (MarketOrder order : getSellSideFromCopies(copies)) {
            if (!order.isFilled()) {
                result.addAll(order.getFills());
            }
        }

        return result;
    }

    public Set<Order> getOpenOrders() {

        Set<Order> result = new TreeSet<Order>();
        Set<MarketOrder>[] copies = copyOrders();

        for (MarketOrder marketOrder : getBuySideFromCopies(copies)) {
            if (!marketOrder.getOrder().isClosed()) {
                result.add(marketOrder.getOrder());
            }
        }

        for (MarketOrder marketOrder : getSellSideFromCopies(copies)) {
            if (!marketOrder.getOrder().isClosed()) {
                result.add(marketOrder.getOrder());
            }
        }

        return result;
    }

    private Set<MarketOrder> performMatching(Set<Fill> fillsToCreate) {

        Set<MarketOrder> result = new HashSet<MarketOrder>();

        Set<MarketOrder>[] copies = copyOrders();
        Iterator<MarketOrder> buyMarketOrders = getBuySideFromCopies(copies).iterator();
        Iterator<MarketOrder> sellMarketOrders = getSellSideFromCopies(copies).iterator();
        Set<MarketOrder> expiredOrders = new TreeSet<MarketOrder>();

        //we may only start matching if there is at least one buy and one sell order
        if (buyMarketOrders.hasNext() && sellMarketOrders.hasNext()) {

            //grab the first of each type of order
            MarketOrder buyMarketOrder = buyMarketOrders.next();
            MarketOrder sellMarketOrder = sellMarketOrders.next();

            //exhaustively match orders
            boolean isDone = false;

            while (!isDone && buyMarketOrder.getPrice() >= (sellMarketOrder.getPrice() + spreadThreshold)) {

                boolean fillOrder = true;

                // If the orders aren't expired, we have a match
                if (buyMarketOrder.isExpired()) {
                    expiredOrders.add(buyMarketOrder);
                    buyMarketOrders.remove();
                    if (buyMarketOrders.hasNext()) {
                        fillOrder = false;
                        buyMarketOrder = buyMarketOrders.next();
                    } else {
                        isDone = true;
                    }
                }

                if (sellMarketOrder.isExpired()) {
                    expiredOrders.add(sellMarketOrder);
                    sellMarketOrders.remove();
                    if (sellMarketOrders.hasNext()) {
                        fillOrder = false;
                        sellMarketOrder = sellMarketOrders.next();
                    } else {
                        isDone = true;
                    }
                }

                if (!isDone && fillOrder) {

                    //determine the quantity filled and purchase price
                    int quantityFilled = Math.min(buyMarketOrder.getRemainingQuantity(), sellMarketOrder.getRemainingQuantity());

                    //create the fill

                    double buyPrice = buyMarketOrder.getPrice();
                    double sellPrice = sellMarketOrder.getPrice();
                    double diff = buyPrice - sellPrice;
                    double customerGoodwill = diff / 4;

                    // Take half the delta as profit, the other half gets split into equal price breaks for buyer and seller
                    if (customerGoodwill > 0) {
                        buyPrice -= customerGoodwill;
                        sellPrice += customerGoodwill;
                    }
                    Fill fill = new Fill(getSymbol(), buyMarketOrder.getId(), sellMarketOrder.getId(), buyPrice, sellPrice, quantityFilled);
                    fillsToCreate.add(fill);
                    recordProfit(fill.getProfit(), fill.getQuantity(), customerGoodwill);

                    //add the fill to the orders
                    buyMarketOrder.addFill(fill);
                    sellMarketOrder.addFill(fill);

                    //do we need to close the buy order?
                    //(if so remove the order from the market and note that we have to close it)
                    if (buyMarketOrder.isFilled()) {
                        buyMarketOrders.remove();
                        result.add(buyMarketOrder);

                        //next order (if possible)
                        if (buyMarketOrders.hasNext()) {
                            buyMarketOrder = buyMarketOrders.next();
                        } else {
                            isDone = true;
                        }
                    }

                    //do we need to close the sell order?
                    //(if so remove the order from the market and note that we have to close it)
                    if (sellMarketOrder.isFilled()) {
                        sellMarketOrders.remove();
                        result.add(sellMarketOrder);

                        //next order (if possible)
                        if (sellMarketOrders.hasNext()) {
                            sellMarketOrder = sellMarketOrders.next();
                        } else {
                            isDone = true;
                        }
                    }
                }
            }

            synchronized (buySide) {
                buySide.retainAll(getBuySideFromCopies(copies));
            }

            synchronized (sellSide) {
                sellSide.retainAll(getSellSideFromCopies(copies));
            }
        }

        expireOrders(expiredOrders);
        return result;
    }

    private void expireOrders(Set<MarketOrder> expiredOrders) {
    }

    private void recordProfit(double spread, int quantity, double customerGoodwill) {
        // Profit should take two forms: the profit from the spread, and optionally some per-market fee imposed on trades
        scorecard.recordTrade(spread, quantity, customerGoodwill);
    }

    /**
     * {@inheritDoc}
     */
    public Set<MarketOrder> processFillOrder(Order order) {

        //add the order to the market
        MarketOrder marketOrder = new MarketOrder(order);
        (marketOrder.getSide() == Side.Buy ? buySide : sellSide).add(marketOrder);

        // the collection of fills we must create
        Set<Fill> fillsToCreate = new HashSet<Fill>();

        Set<MarketOrder> result = performMatching(fillsToCreate);

        return result;
    }

    public int hashCode() {
        int hash = symbol.hashCode();
        hash = hash * 17 + SimpleMarket.class.hashCode();
        return hash;
    }

    public boolean equals(Object other) {
        try {
            SimpleMarket otherMarket = (SimpleMarket) other;
            return hashCode() == otherMarket.hashCode();
        } catch (ClassCastException cce) {
            return false;
        }
    }
}
