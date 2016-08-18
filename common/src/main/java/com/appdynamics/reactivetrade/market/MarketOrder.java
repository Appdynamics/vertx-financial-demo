package com.appdynamics.reactivetrade.market;

import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

/**
 * <p>A {@link com.appdynamics.reactivetrade.market.MarketOrder} represents the <i>internal mutable state</i> of
 * an open {@link Order} with in a market. That is, for each
 * open {@link Order} in a market there should be at most one
 * {@link com.appdynamics.reactivetrade.market.MarketOrder}.</p>
 * 
 * <p>{@link com.appdynamics.reactivetrade.market.MarketOrder}s are used by markets to keep track of
 * the open {@link Order}s and perform matching to produce Fills.</p>
 * 
 * <p><strong>NOTE:</strong>These objects <strong>are not</strong> placed
 * into Coherence Caches (unlike {@link Order}s) but are used to
 * internally represent the state of {@link Order}s within a Market.</p>
 */
public class MarketOrder implements Comparable<MarketOrder>{

	private Order order;
	private int remainingQuantity;  //an invariant of the remaining quantity (corresponding Order quantity - sum(fills qualities for the said order))
	private TreeSet<Fill> fills;

    private static final long ONE_HOUR = 1000 * 60 * 60;
    private static final long EXPIRATION_TIME = ONE_HOUR * 1;
	
	/**
	 * <p>Constructs a new {@link com.appdynamics.reactivetrade.market.MarketOrder} based on it's associated {@link Order}.</p>
	 * @param order
	 */
	public MarketOrder(Order order) {
		assert order != null;
		assert !order.isClosed();
		this.order = order;
		this.remainingQuantity = order.getQuantity();
		this.fills = new TreeSet<Fill>();
	}

    public Order getOrder() {
        return order;
    }

	/**
	 * <p>The globally unique identity of the {@link com.appdynamics.reactivetrade.market.MarketOrder}.</p>
	 * 
	 * @return {@link String}
	 */
	public String getId() {
		return order.getId();
	}

	/**
	 * <p>The {@link Symbol} for the {@link com.appdynamics.reactivetrade.market.MarketOrder}.</p>
	 * 
	 * @return {@link Symbol}
	 */
	public Symbol getSymbol() {
		return order.getSymbol();
	}
	
	/**
	 * <p>The time since the EPOC that the {@link com.appdynamics.reactivetrade.market.MarketOrder} was placed.</p>
	 * 
	 * @return long
	 */
	public long getTimePlaced() {
		return order.getTimePlaced();
	}

	/**
	 * <p>The {@link Side} of the {@link Order}.</p>
	 * 
	 * @return {@link Side}
	 */
	public Side getSide() {
		return order.getSide();
	}

	/**
	 * <p>The requested price for the {@link com.appdynamics.reactivetrade.market.MarketOrder}.</p>
	 * 
	 * @return long
	 */
	public double getPrice() {
		return order.getPrice();
	}

	/**
	 * <p>The remaining quantity for the open {@link com.appdynamics.reactivetrade.market.MarketOrder}.</p>
	 * 
	 * @return long
	 */
	public int getRemainingQuantity() {
		return remainingQuantity;
	}
	
	/**
	 * <p>Determine if the {@link com.appdynamics.reactivetrade.market.MarketOrder} is completely filled.</p>
	 * 
	 * <p>An {@link Order} is filled when there is nothing left to trade.</p>
	 * 
	 * @return boolean
	 */
	public boolean isFilled() {
		return remainingQuantity <= 0;
	}
	
	/**
	 * <p>We consider that two {@link com.appdynamics.reactivetrade.market.MarketOrder}s are equal if they have the same identity.</p>
	 */
	@Override
	public boolean equals(Object other) {
		return other != null &&
			   (this == other ||
				(other instanceof MarketOrder && ((MarketOrder)other).getId().equals(this.getId())));	   
	}

	/**
	 * <p>The default mechanism for comparing and prioritizing {@link com.appdynamics.reactivetrade.market.MarketOrder}s.</p>
	 * 
	 * <p>When two orders have the same price, they are then ordered by that which occurred
	 * first in time.  Buy prices are prioritized in descending order, Sells are prioritized in ascending order.</p>
	 * 
	 * <p>This could be overwritten to provide custom order prioritization.</p>
	 */
	public int compareTo(MarketOrder other) {
		
		double priceDifference = this.getPrice() - other.getPrice();
		if (Math.abs(priceDifference) <= 0.009) {
			
			long timeDifference = this.getTimePlaced() - other.getTimePlaced();
			return timeDifference == 0 ? 0 : timeDifference < 0 ? -1 : 1;		
			
		} else {
			
			int result = priceDifference < 0 ? -1 : +1;
			return this.getSide() == Side.Buy ? -result : result;
		}
	}

    /**
     * Make all orders expire after a fixed period
     *
     * @return
     */
    public boolean isExpired() {
        long now = System.currentTimeMillis();
        long orderTime = getTimePlaced();

        return now - orderTime > EXPIRATION_TIME;
    }

    /**
	 * <p>Adds the specified {@link Fill} to the {@link com.appdynamics.reactivetrade.market.MarketOrder}, appropriately
	 * adjusting the remaining quantity if the {@link Fill} is not already
	 * associated with the {@link com.appdynamics.reactivetrade.market.MarketOrder}.</p>
	 * 
	 * @param fill
	 * @return true if the specified Fill is new (not already known by the {@link com.appdynamics.reactivetrade.market.MarketOrder}), otherwise false.
	 */
	public boolean addFill(Fill fill) {
		assert fill != null;
		assert fill.getSymbol().equals(this.getSymbol());
		assert fill.isForOrderId(getId());

		if (fills.contains(fill)) {
			return false;
		} else {
			fills.add(fill);
			remainingQuantity -= fill.getQuantity();
			return true;
		}
	}

    public Set<Fill> getFills() {
        return fills;
    }

    public String toString() {
        return "[MarketOrder: sym=" + getSymbol().toString() + ", side=" + getSide() + ", q=" + getRemainingQuantity() + ", p=" + getPrice() + ", filled=" + isFilled() + "]";
    }
}
