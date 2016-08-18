package com.appdynamics.reactivetrade.market;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * <p>A {@link com.appdynamics.reactivetrade.market.MarketDefinition} encapsulates the necessary information to
 * both define and construct a market and associated MarketEngine for
 * the purpose of trading {@link Order}s for a {@link Symbol}.</p>
 * 
 * <p>{@link com.appdynamics.reactivetrade.market.MarketDefinition}s are stored in Coherence Caches named {@link com.appdynamics.reactivetrade.market.MarketDefinition#CACHENAME}
 * using their {@link #getSymbol()} as their keys.</p>
 */
public class MarketDefinition {

	private static final long serialVersionUID = 8508479359875591311L;
	
	/**
	 * <p>The name of the Coherence cache holding the {@link com.appdynamics.reactivetrade.market.MarketDefinition}s.</p>
	 */
	public static final String CACHENAME = "MarketDefinitions";

	/**
	 * <p>The {@link Symbol} used to uniquely identify the market and it's {@link com.appdynamics.reactivetrade.market.MarketDefinition}.</p>
	 */
	private Symbol symbol;

	/**
	 * <p>The user-friendly name for the {@link com.appdynamics.reactivetrade.market.MarketDefinition}.</p>
	 */
	private String name;
	
	/**
	 * <p>Constructs an immutable {@link com.appdynamics.reactivetrade.market.MarketDefinition}.</p>
	 * 
	 * @param symbol The unique {@link Symbol} for the {@link com.appdynamics.reactivetrade.market.MarketDefinition}.
	 * 		  The symbol must be unique within an instance of the trading system.
	 * 
	 * @param name The user-friendly name for the {@link com.appdynamics.reactivetrade.market.MarketDefinition}
	 */
	public MarketDefinition(Symbol symbol, String name) {
		this.symbol = symbol;
		this.name = name;
	}

	/**
	 * <p>The unique {@link Symbol} for the {@link com.appdynamics.reactivetrade.market.MarketDefinition}.</p>
	 * 
	 * @return {@link Symbol}
	 */
	public Symbol getSymbol() {
		return symbol;
	}

	/**
	 * <p>The user-friendly name of the {@link com.appdynamics.reactivetrade.market.MarketDefinition}.</p>
	 * 
	 * @return {@link String}
	 */
	public String getName() {
		return name;
	}

    public int hashCode() {
        int hash = 7;
        hash = hash * 17 + getSymbol().hashCode();
        hash = hash * 17 + getName().hashCode();

        return hash;
    }

    public boolean equals(Object other) {
        try {
            MarketDefinition otherDef = (MarketDefinition) other;
            return getSymbol().equals(otherDef.getSymbol()) && getName().equals(otherDef.getName());
        } catch (ClassCastException cce) {
            return false;
        }
    }

    public String toString() {
        return "MarketDefinition(" + getSymbol() + ", " + hashCode() + ")";
    }
}
