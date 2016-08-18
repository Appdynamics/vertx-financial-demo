package com.appdynamics.reactivetrade.market;

/**
 * <p>A {@link MarketCommand} encapsulates a unit of work that a
 * MarketEngine and market may perform.</p>
 * 
 * <p>Typical examples include; accepting a new order, canceling and 
 * order or accepting an existing fill (during a system recovery).</p>
 */

public interface MarketCommand {

	/**
	 * <p>The {@link Symbol} on which the {@link MarketCommand} must be executed.</p>
	 * 
	 * @return {@link Symbol}
	 */
	public Symbol getSymbol();
	
	/**
	 * <p>The time since the EPOC when the {@link MarketCommand} was created.</p>
	 * 
	 * @return long
	 */
	public long getTimeCreated();
}
