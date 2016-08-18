package com.appdynamics.reactivetrade.market;

public class Symbol {

	private static final long serialVersionUID = 5665366766057471886L;
	
	private String symbol;

	public Symbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public boolean equals(Object object) {
		return object != null && 
			   ((object == this) || (object instanceof Symbol && ((Symbol)object).symbol.equals(this.symbol)));
	}

	@Override
	public String toString() {
		return symbol;
	}

	@Override
	public int hashCode() {
		return symbol.hashCode();
	}
}
