package com.appdynamics.reactivetrade.market;

public class AcceptOrder extends AbstractOrderMarketCommand {

	public AcceptOrder(Order order) {
		super(order);
	}
	
    public int hashCode() {
        int hash = 7;
        hash = hash * 17 + getClass().hashCode();
        hash = hash * 17 + getOrder().hashCode();

        return hash;
    }

    public boolean equals(Object other) {
        try {
            AcceptOrder otherAccept = (AcceptOrder) other;
            return otherAccept.getOrder().equals(getOrder());
        } catch (ClassCastException cce) {
            return false;
        }
    }
}
