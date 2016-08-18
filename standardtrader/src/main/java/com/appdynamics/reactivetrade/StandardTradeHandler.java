package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.market.*;
import com.appdynamics.reactivetrade.persist.DBManager;
import com.appdynamics.reactivetrade.util.*;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Verticle;

import java.util.Set;

public class StandardTradeHandler implements Verticle {

    private MarketUtils mu;
    private int histSize;
    private int premierThreshold;
    private String premierEvtBusAddr;
    private CommonParameters params;
    private Vertx vertx;
    private Context context;

    public Vertx getVertx() {
        return vertx;
    }

    public void init(Vertx vertx, Context context) {
        this.vertx = vertx;
        this.context = context;
    }

    public void start(Future<Void> startFuture) {

        final JsonObject conf = context.config();
        histSize = conf.getInteger("orderhistorysize");
        premierEvtBusAddr = conf.getString("premier.eventbus.address");
        params = new CommonParameters("standard", conf);
        mu = new MarketUtils(new StandardTradeScorecard(params.getFixedFeePerTrade(),
                                                        params.getPerShareFee(),
                                                        params.getPerTradeCostForMarket(),
                                                        params.getCustSatCostPerDroppedTrade()),
                             params.getSpread());


        Number thresholdVal = conf.getInteger("premier.ordersize.threshold");
        try {
            premierThreshold = thresholdVal.intValue();
        } catch (Exception e) {
            e.printStackTrace();
            premierThreshold = 10000;
        }

        vertx.eventBus().consumer(conf.getString("standard.eventbus.address"), new Handler<Message<JsonObject>>() {
            public void handle(Message<JsonObject> msg) {
                handleJsonMessage(msg);
            }
        });
        System.out.println(conf.getString("boot.msg.prefix") + ": StandardTradeHandler");
    }

    public void stop(Future<Void> future) throws Exception {

    }

    private void handleJsonMessage(Message<JsonObject> msg) {

        JsonObject json = msg.body();
        String op = json.getString(JsonUtils.OP);
        JsonObject result = null;

        if (op.equals(JsonUtils.OP_BUY) || op.equals(JsonUtils.OP_SELL)) {
            result = dispatch(msg);
        } else if (op.equals(JsonUtils.OP_FILLS)) {
            result = getFills();
        } else if (op.equals(JsonUtils.OP_OPEN_ORDERS)) {
            result = getOpenOrders();
        } else if (op.equals(JsonUtils.OP_CLOSED_ORDERS)) {
            result = getClosedOrders();
        } else if (op.equals(JsonUtils.OP_SCORECARD)) {
            result = getScorecard();
        }

        if (result != null) {
            msg.reply(result);
        }
    }

    private JsonObject getFills() {
        Set<Fill> allFills = mu.getFills();
        return JsonUtils.jsonFromFills(allFills);
    }

    private JsonObject getOpenOrders() {
        Set<Order> allOpenOrders = mu.getOpenOrders();
        return JsonUtils.jsonFromOrders(allOpenOrders, true);
    }

    private JsonObject getClosedOrders() {
        Set<Order> allClosedOrders = mu.getClosedOrders();
        return JsonUtils.jsonFromOrders(allClosedOrders, false);
    }

    private JsonObject getScorecard() {
        return JsonUtils.jsonFromScorecard(mu.getScorecard());
    }

    private JsonObject dispatch(Message<JsonObject> msg) {
        JsonObject order = msg.body();
        int qty = order.getInteger(JsonUtils.QTY).intValue();

        JsonObject result;
        if (qty < premierThreshold) {
            result = dispatchNormalTrade(order);
        } else {
            result = dispatchPremierTrade(msg);
        }

        return result;
    }

    private JsonObject dispatchNormalTrade(JsonObject json) {
        JsonObject result = json;

        MarketEngine forSymbol = mu.getMarketEngine("ST", json.getString(JsonUtils.SYMBOL), histSize);
        Order order = JsonUtils.orderFromJson(json);
        AcceptOrder marketCommand = new AcceptOrder(order);
        forSymbol.queueCommand(marketCommand);

        if (json.getString(JsonUtils.OP).equals(JsonUtils.OP_BUY)) {
            DBManager.getInstance("StandardTrade").doStandardtradeBuy();
        } else {
            DBManager.getInstance("StandardTrade").doStandardtradeSell();
        }

        return result;
    }

    private JsonObject dispatchPremierTrade(final Message<JsonObject> origMessage) {
        JsonObject json = origMessage.body();
        vertx.eventBus().send(premierEvtBusAddr, json, new Handler<AsyncResult<Message<JsonObject>>>() {
            public void handle(AsyncResult<Message<JsonObject>> asyncResult) {
                Message<JsonObject> replyMessage = asyncResult.result();
                origMessage.reply(replyMessage.body());
            }
        });

        return null;
    }
}