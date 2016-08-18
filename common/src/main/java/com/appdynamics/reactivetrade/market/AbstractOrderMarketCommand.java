package com.appdynamics.reactivetrade.market;

import java.util.Date;

/**
 * <p>An abstract base implementation of a {@link MarketCommand} that uses {@link Order}s.</p>
 * 
 * <p>Copyright (c) 2007. All Rights Reserved. Oracle Corporation.</p>
 * 
 * @author Brian Oliver (brian.oliver@oracle.com)
 */
public abstract class AbstractOrderMarketCommand extends AbstractMarketCommand {
	
	/**
	 * <p>The {@link Order} on which this {@link MarketCommand} will operate.</p>
	 */
	private Order order;
	
	
	/**
	 * <p>Constructor for an {@link Order} based {@link MarketCommand}.</p>
	 * 
	 * @param order
	 */
	public AbstractOrderMarketCommand(Order order) {
		super();
		assert order != null;		
		this.order = order;
	}
	
	
	/**
	 * <p>The {@link Symbol} on which this {@link MarketCommand} will operate.</p>
	 */
	public Symbol getSymbol() {
		return order.getSymbol();
	}
	
	
	/**
	 * <p>The {@link Order} on which this {@link MarketCommand} will operate.</p>
	 * 
	 * @return {@link Order}
	 */
	public Order getOrder() {
		return order;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%s{symbol=%s, at=%s, order=%s}", getClass().getName(), getSymbol(), new Date(getTimeCreated()), getOrder()); 
	}
}
