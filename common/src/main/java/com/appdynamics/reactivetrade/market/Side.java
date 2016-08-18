package com.appdynamics.reactivetrade.market;

/**
 * <p>An enumeration to represent the side (buy or sell) of an {@link com.bsbank.demo.exchange.Order}.</p>
 * 
 * <p>Copyright (c) 2007. All Rights Reserved. Oracle Corporation.</p>
 * 
 * @author Brian Oliver (brian.oliver@oracle.com)
 */
public enum Side {

	/**
	 * <p>Indicates a request to buy.</p>
	 */
	Buy, 
	
	
	/**
	 * <p>Indicates a request to sell.</p>
	 */
	Sell;
	
	
	/**
	 * <p>Returns the opposite of a side.
	 * 
	 * @return Buy for Sell.  Sell for Buy
	 */
	public Side opposite() {
		return this == Side.Buy ? Side.Sell : Side.Buy;
	}
}
