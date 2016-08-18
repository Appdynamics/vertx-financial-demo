package com.appdynamics.reactivetrade.util;

import com.appdynamics.reactivetrade.market.*;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by trader on 7/25/14.
 */
public class JsonUtils {

    public static final String OP = "op";
    public static final String SYMBOL = "symbol";
    public static final String TSTAMP = "timestamp";
    public static final String QTY = "quantity";
    public static final String PRICE = "price";
    public static final String BUYPRICE = "buyprice";
    public static final String SELLPRICE = "sellprice";
    public static final String ID = "id";
    public static final String CONTENTS = "contents";
    public static final String BUYID = "buyId";
    public static final String SELLID = "sellId";
    public static final String SIDE = "side";
    public static final String RESULT = "result";
    public static final String REQ_PATH = "reqpath";
    public static final String PERF_KEY = "perfKey";
    public static final String SPREAD_REVENUE = "spreadRevenue";
    public static final String TRADE_REVENUE = "tradeRevenue";
    public static final String TRADE_COST = "tradeCost";
    public static final String CUST_SAT_SCORE = "droppedOrderCost";

    public static final String OP_HOMEPAGE = "homepage";
    public static final String OP_BUY = "buy";
    public static final String OP_SELL = "sell";
    public static final String OP_LOGIN = "login";
    public static final String OP_LOGOUT = "logout";
    public static final String OP_FILLS = "fills";
    public static final String OP_OPEN_ORDERS = "openorders";
    public static final String OP_CLOSED_ORDERS = "closedorders";
    public static final String OP_SCORECARD = "scorecard";

    public static JsonObject userReqToJson(HttpServerRequest req) {

        JsonObject result = null;

        // Looking for a path matching "/trade/{buy|sell}/sym/<symbol>/qty/<quantity>/price/<price>"
        String[] splitPath = req.path().split("/");
        if (splitPath != null) {
            if (splitPath.length == 9) {

                // buy/sell
                String buySell = null;
                String lcOp = splitPath[2].toLowerCase();
                if (lcOp.equals("buy") || lcOp.equals("sell")) {
                    buySell = lcOp;
                }

                // symbol
                String sym = splitPath[4].toUpperCase();

                // quantity
                int qty = -1;
                try {
                    qty = Integer.parseInt(splitPath[6]);
                } catch (NumberFormatException nfe) {
                }

                // price
                double price = -1.0;
                try {
                    price = Double.parseDouble(splitPath[8]);
                } catch (NumberFormatException nfe) {
                }

                if (buySell != null && sym != null && qty > 0 && price > 0.0) {
                    result = newWithId();
                    result.put(OP, buySell);
                    result.put(SYMBOL, sym);
                    result.put(TSTAMP, System.currentTimeMillis());
                    result.put(QTY, qty);
                    result.put(PRICE, price);
                }

            } else if (splitPath.length == 3) {

                String cmd = splitPath[1].toLowerCase();
                if (cmd.equals("orders")) {
                    String subCmd = splitPath[2].toLowerCase();
                    if (subCmd.equals("open")) {
                        result = newWithId();
                        result.put(OP, OP_OPEN_ORDERS);
                    } else if (subCmd.equals("closed")) {
                        result = newWithId();
                        result.put(OP, OP_CLOSED_ORDERS);
                    }
                }

            } else if (splitPath.length == 2) {

                String cmd = splitPath[1].toLowerCase();
                if (cmd.equals("fills")) {
                    result = newWithId();
                    result.put(OP, OP_FILLS);
                } else if (cmd.equals("scorecard")) {
                    result = newWithId();
                    result.put(OP, OP_SCORECARD);
                } else if (cmd.equals("login")) {
                    result = new JsonObject();
                    result.put(OP, OP_LOGIN);
                } else if (cmd.equals("logout")) {
                    result = new JsonObject();
                    result.put(OP, OP_LOGOUT);
                }
            } else if (splitPath.length == 0) {
                result = new JsonObject();
                result.put(OP, OP_HOMEPAGE);
            } else {
                for (int i = 0; i < splitPath.length; ++i) {
                    System.out.println(splitPath[i]);
                }
            }
        }

        return result;
    }

    private static String generateId() {
        return Thread.currentThread().getName().replace(' ', '_').replace("vert.x", "vx").replace("thread", "th").replace("eventloop", "el") +
                ":" + String.valueOf(System.currentTimeMillis());
    }

    private static final JsonObject newWithId() {
        JsonObject result = new JsonObject();
        result.put(ID, generateId());
        return result;
    }

    public static Order orderFromJson(JsonObject source) {
        Symbol sym = new Symbol(source.getString(SYMBOL));
        String op = source.getString(OP);
        Side side = (op.equals("buy") ? Side.Buy : Side.Sell);
        double price = source.getDouble(PRICE).doubleValue();
        int qty = source.getInteger(QTY);
        return new Order(sym, source.getString(ID), side, price, qty);
    }

    public static MarketScorecard scorecardFromJson(JsonObject source) {
        GenericScorecard result = new GenericScorecard();
        result.addSpreadRevenue(source.getDouble(SPREAD_REVENUE).doubleValue());
        result.addTradeRevenue(source.getDouble(TRADE_REVENUE).doubleValue());
        result.addTradeCost(source.getDouble(TRADE_COST).doubleValue());
        result.adjustCustomerSatisfactionScore(source.getDouble(CUST_SAT_SCORE).doubleValue());

        return result;
    }

    public static JsonObject jsonFromScorecard(MarketScorecard scorecard) {
        JsonObject result = new JsonObject();
        result.put(SPREAD_REVENUE, scorecard.getSpreadRevenue());
        result.put(TRADE_REVENUE, scorecard.getTradeRevenue());
        result.put(TRADE_COST, scorecard.getTradeCost());
        result.put(CUST_SAT_SCORE, scorecard.getCustomerSatisfactionScore());

        return result;
    }

    public static void mergeArray(JsonObject mergeInto, JsonObject newMergedData) {
        JsonArray toMergeElements = newMergedData.getJsonArray("array");
        if (toMergeElements != null && toMergeElements.size() > 0) {
            synchronized (mergeInto) {
                JsonArray mergeIntoElements = mergeInto.getJsonArray("array");
                for (Iterator it = toMergeElements.iterator(); it.hasNext(); ) {
                    mergeIntoElements.add(it.next());
                }
            }
        }
    }

    public static JsonObject jsonFromFills(Set<Fill> fills) {

        JsonObject result = new JsonObject();
        result.put(CONTENTS, "fills");

        JsonArray array = new JsonArray();
        for (Fill fill : fills) {
            JsonObject element = new JsonObject();
            element.put(SYMBOL, fill.getSymbol().toString());
            element.put(BUYPRICE, fill.getBuyPrice());
            element.put(SELLPRICE, fill.getSellPrice());
            element.put(QTY, fill.getQuantity());
            element.put(TSTAMP, fill.getTimeCreated());
            element.put(BUYID, fill.getBuyOrderId());
            element.put(SELLID, fill.getSellOrderId());

            array.add(element);
        }

        result.put("array", array);

        return result;
    }

    public static JsonObject jsonFromOrders(Set<Order> orders, boolean openOrder) {

        JsonObject result = new JsonObject();
        result.put(CONTENTS, openOrder ? "openorders" : "closedorders");

        JsonArray array = new JsonArray();
        for (Order order : orders) {
            JsonObject element = new JsonObject();
            element.put(ID, order.getId());
            element.put(SYMBOL, order.getSymbol().toString());
            element.put(PRICE, order.getPrice());
            element.put(SIDE, order.getSide().toString());
            element.put(QTY, order.getQuantity());
            element.put(TSTAMP, order.getTimePlaced());

            array.add(element);
        }

        result.put("array", array);

        return result;
    }

    public static JsonArray getArray(JsonObject container) {
        return container.getJsonArray("array");
    }
}
