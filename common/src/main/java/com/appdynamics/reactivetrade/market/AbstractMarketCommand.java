package com.appdynamics.reactivetrade.market;

/**
 * <p>An abstract base implementation of a {@link MarketCommand}.</p>
 * 
 * <p>Copyright (c) 2007. All Rights Reserved. Oracle Corporation.</p>
 * 
 * @author Brian Oliver (brian.oliver@oracle.com)
 */
public abstract class AbstractMarketCommand implements MarketCommand {

	/**
	 * <p>The time since the EPOC when this {@link MarketCommand} was created.</p>
	 */
	private long timeCreated;
	
	
	/**
	 * <p>Default Constructor.</p>
	 */
	public AbstractMarketCommand() {
		this.timeCreated = System.currentTimeMillis();
	}
	
	
	/**
	 * <p>The time since the EPOC when the {@link MarketCommand} was created.</p>
	 * 
	 * @return long
	 */
	public long getTimeCreated() {
		return timeCreated;
	}
	
}
