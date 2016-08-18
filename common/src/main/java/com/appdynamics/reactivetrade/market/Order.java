package com.appdynamics.reactivetrade.market;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * <p>This class represents an {@link com.appdynamics.reactivetrade.market.Order} in a Market to either buy or sell
 * a specified quantity of {@link Symbol} at a specified price.</p>
 * 
 * <p>With exception to the order state attributes timePlaced and isClosed, attributes in this class are immutable.
 * If you wish to change and order, you must cancel an existing order and then create a new one.</p>
 * 
 */
public class Order implements Comparable<Order> {
	
	private static final long serialVersionUID = 872102760361872571L;

	/**
	 * <p>The globally unique identity for the {@link com.appdynamics.reactivetrade.market.Order}.</p>
	 */
	private String id;
	
	/**
	 * <p>The {@link Symbol} for the {@link com.appdynamics.reactivetrade.market.Order}
	 */
	private Symbol symbol;
	
	/**
	 * <p>The time since the EPOC when the {@link com.appdynamics.reactivetrade.market.Order} was placed.</p>
	 */
	private long timePlaced;
	
	/**
	 * <p>The Side indicating the type of {@link com.appdynamics.reactivetrade.market.Order}.</p>
	 */
	private Side side;

	/**
	 * <p>The price requested for the {@link com.appdynamics.reactivetrade.market.Order}.</p>
	 */
	private double price;

	/**
	 * <p>The quantity requested for the {@link com.appdynamics.reactivetrade.market.Order}.</p>
	 */
	private int quantity;

	/**
	 * <p>Is the {@link com.appdynamics.reactivetrade.market.Order} closed?  Only open (not closed) {@link com.appdynamics.reactivetrade.market.Order}s may be processed.</p>
	 */
	private boolean isClosed;

	/**
	 * <p>Constructs an open {@link com.appdynamics.reactivetrade.market.Order} with a unique identity and a
	 * timePlaced attribute that is equivalent to the system time.</p>
	 * 
	 * <p>NOTE: The timePlaced may be updated when the {@link com.appdynamics.reactivetrade.market.Order} is
	 * actually placed.</p>
	 * 
	 * @param symbol
	 * @param side
	 * @param price
	 * @param quantity
	 */
	public Order(Symbol symbol, String id, Side side, double price, int quantity) {
		this.id = id;
		this.timePlaced = System.currentTimeMillis();
		this.symbol = symbol;
		this.side = side;
		this.price = price;
		this.quantity = quantity;
	}

	/**
	 * <p>The globally unique identity of the {@link com.appdynamics.reactivetrade.market.Order}.</p>
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * <p>The {@link Symbol} for the {@link com.appdynamics.reactivetrade.market.Order}.</p>
	 * 
	 * @return {@link Symbol}
	 */
	public Symbol getSymbol() {
		return symbol;
	}

	/**
	 * <p>The {@link Side} of the {@link com.appdynamics.reactivetrade.market.Order}.</p>
	 * 
	 * @return {@link Side}
	 */
	public Side getSide() {
		return side;
	}

	/**
	 * <p>The requested price for the {@link com.appdynamics.reactivetrade.market.Order}.</p>
	 * 
	 * @return double
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * <p>The requested quantity for the {@link com.appdynamics.reactivetrade.market.Order}.</p>
	 * 
	 * @return int
	 */
	public int getQuantity() {
		return quantity;
	}
	
	/**
	 * <p>The time since the EPOC that the {@link com.appdynamics.reactivetrade.market.Order} was placed.</p>
	 * 
	 * @return long
	 */
	public long getTimePlaced() {
		return timePlaced;
	}

	/**
	 * <p>Sets the time since the EPOC that the {@link com.appdynamics.reactivetrade.market.Order} was placed.</p>
	 * 
	 * <p>NOTE: This should only be called by the appropriate PlaceOrderProcessor.</p>
	 * 
	 * @param timePlaced
	 */
	public void setTimePlaced(long timePlaced) {
		this.timePlaced = timePlaced;
	}

	/**
	 * @return boolean
	 */
	public boolean isClosed() {
		return isClosed;
	}

	/**
	 * <p>Signifies that the {@link com.appdynamics.reactivetrade.market.Order} is now closed.</p>
	 * 
	 * <p>NOTE: This should only be called by the appropriate CloseProcessor.</p>
	 * 
	 */
	public void close() {
		this.isClosed = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("Order{id=%s, symbol=%s, side=%s, price=%f, quantity=%d, hash=%d}",
                                getId(), getSymbol(), getSide(), getPrice(), getQuantity(), hashCode());
	}

    public int hashCode() {
        int hash = Order.class.hashCode();
        hash = hash * 17 + id.hashCode();

        return hash;
    }

    public boolean equals(Object other) {
        try {
            Order otherOrder = (Order) other;
            return hashCode() == otherOrder.hashCode();
        } catch (ClassCastException cce) {
            return false;
        }
    }

    @Override
    public int compareTo(Order o) {
        int result = getSymbol().toString().compareTo(o.getSymbol().toString());
        if (result == 0) {
            result = getSide().compareTo(o.getSide());
            if (result == 0) {
                result = getQuantity() - o.getQuantity();
                if (result == 0) {
                    result = (int) (getTimePlaced() - o.getTimePlaced());
                }
            }
        }

        return result;
    }
}
