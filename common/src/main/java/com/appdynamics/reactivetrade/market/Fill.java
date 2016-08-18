package com.appdynamics.reactivetrade.market;

import java.util.Date;

/**
 * <p>This class represents an immutable agreement between a buyer (a buy {@link Order})
 * and a seller (a sell {@link Order}) for an agreed quantity of a {@link Symbol}.</p>
 *
 * <p>The agreement can be made as long as the buy price is greater than or equal to the
 * sell price.  In the case where the agreed-upon buy price is greater than the agreed-upon
 * sell price, a fill can be offered for a better price than the one agreed upon, and the
 * market can take some of the delta as profit..</p>
 *
 * <p>The unique identity of a {@link com.appdynamics.reactivetrade.market.Fill} is the composition of the buyer {@link Order#getId()} and
 * the seller {@link Order#getId()}.</p>
 *
 * This class does not represent anything that implies an entire order is closed out.  An order can be partially
 * filled by one Fill class, and can be closed out by one or more Fills.  In a dynamic market with volatile
 * prices, a single order is typically serviced by multiple fills.
 */
public class Fill implements Comparable<Fill> {

	/**
	 * <p>The identity of a {@link com.appdynamics.reactivetrade.market.Fill} is the transient combination of the
	 * buy and sell {@link Order#getId()}s.</p>
	 */
	private transient String id;
	
	/**
	 * <p>The {@link Symbol} for the {@link com.appdynamics.reactivetrade.market.Fill}
	 */	
	private Symbol symbol;

	/**
	 * <p>The time since the EPOC when this {@link com.appdynamics.reactivetrade.market.Fill} was created.</p>
	 */
	private long timeCreated;

	/**
	 * <p>The globally unique identify of the buy {@link Order} of which this {@link com.appdynamics.reactivetrade.market.Fill} is associated.</p>
	 */
	private String buyOrderId;

	/**
	 * <p>The globally unique identify of the sell {@link Order} of which this {@link com.appdynamics.reactivetrade.market.Fill} is associated.</p>
	 */
	private String sellOrderId;
	
	/**
	 * <p>The agreed buy prices for the {@link com.appdynamics.reactivetrade.market.Fill}.</p>
	 */
	private double buyPrice;
    private double sellPrice;

	/**
	 * <p>The agreed quantity for the {@link com.appdynamics.reactivetrade.market.Fill}.</p>
	 */
	private int quantity;

    public Fill(Symbol symbol, String buyOrderId, String sellOrderId, double buyPrice, double sellPrice, int quantity) {
        this.symbol = symbol;
        this.timeCreated = System.currentTimeMillis();
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
    }

	/**
	 * <p>The globally unique identity of the {@link com.appdynamics.reactivetrade.market.Fill}.</p>
	 * 
	 * @return {@link String}
	 */
	public String getId() {
		if (id == null) {
            id = buyOrderId.toString() + sellOrderId.toString();
        }

		return id;
	}

	/**
	 * <p>The {@link Symbol} for the {@link com.appdynamics.reactivetrade.market.Fill}.</p>
	 * 
	 * @return {@link Symbol}
	 */
	public Symbol getSymbol() {
		return symbol;
	}

	public String getBuyOrderId() {
		return buyOrderId;
	}

	public String getSellOrderId() {
		return sellOrderId;
	}
	
	/**
	 * <p>The agreed prices for the {@link com.appdynamics.reactivetrade.market.Fill}.</p>
	 * 
	 * @return double
	 */
	public double getBuyPrice() {
		return buyPrice;
	}

    public double getSellPrice() {
        return sellPrice;
    }

    public double getProfit() {
        return buyPrice - sellPrice;
    }

	/**
	 * <p>The agreed quantity for the {@link com.appdynamics.reactivetrade.market.Fill}.</p>
	 * 
	 * @return int
	 */
	public int getQuantity() {
		return quantity;
	}
	
	/**
	 * <p>The time since the EPOC when the {@link com.appdynamics.reactivetrade.market.Fill} was created.</p>
	 * 
	 * @return long
	 */
	public long getTimeCreated() {
		return timeCreated;
	}
	
	/**
	 * <p>Determines if the specified orderId is either the buy or sell orderId 
	 * referred to by this {@link com.appdynamics.reactivetrade.market.Fill}.</p>
	 * 
	 * @param orderId
	 * 
	 * @return boolean
	 */
	public boolean isForOrderId(String orderId) {
		return buyOrderId.equals(orderId) || sellOrderId.equals(orderId);
	}

    /**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object other) {
		return other != null &&
		 	   ((this == other) ||
		 	    (other instanceof Fill && ((Fill)other).getId().equals(this.getId())));
	}

    @Override
    public int compareTo(Fill fill) {
        if (fill != null) {
            int compVal = fill.getBuyOrderId().compareTo(getBuyOrderId());
            if (compVal == 0) {
                compVal = fill.getSellOrderId().compareTo(getSellOrderId());
            }

            return compVal;
        } else {
            return 1;
        }
    }
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return getId().hashCode();
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("Fill{symbol=%s, at=%s, buyPrice=%e, sellPrice=%e, quantity=%d;, hash=%d}",
						     getSymbol(), new Date(getTimeCreated()), 
						     getBuyPrice(), getSellPrice(), getQuantity(), hashCode());
	}
}
