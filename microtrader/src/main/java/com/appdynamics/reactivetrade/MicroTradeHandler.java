package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.market.*;
import com.appdynamics.reactivetrade.persist.DBManager;
import com.appdynamics.reactivetrade.util.*;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Verticle;

import java.util.Set;

public class MicroTradeHandler implements Verticle {

    private MarketUtils mu;
    private int histSize;
    private CommonParameters params;
    private Vertx vertx;

    public Vertx getVertx() {
        return vertx;
    }

    public void init(Vertx vertx, Context context) {
        this.vertx = vertx;
    }

    public void start(Future<Void> startFuture) {

        final JsonObject conf = getVertx().getOrCreateContext().config();
        histSize = conf.getInteger("orderhistorysize");
        params = new CommonParameters("micro", conf);

        mu = new MarketUtils(new MicroTradeScorecard(params.getFixedFeePerTrade(),
                                                     params.getPerShareFee(),
                                                     params.getPerTradeCostForMarket(),
                                                     params.getCustSatCostPerDroppedTrade()),
                             params.getSpread());

        vertx.eventBus().consumer(conf.getString("micro.eventbus.address"), new Handler<Message<JsonObject>>() {
            public void handle(Message<JsonObject> msg) {
                JsonObject result = handleJsonMessage(msg);
                msg.reply(result);
            }
        });
        System.out.println(conf.getString("boot.msg.prefix") + ": MicroTradeHandler");    }

    public void stop(Future<Void> future) throws Exception {

    }

    private JsonObject handleJsonMessage(Message<JsonObject> msg) {
        JsonObject json = msg.body();
        String op = json.getString(JsonUtils.OP);
        JsonObject result = null;
        if (op.equals(JsonUtils.OP_BUY)) {
            MarketEngine forSymbol = mu.getMarketEngine("ME", json.getString(JsonUtils.SYMBOL), histSize);
            Order order = JsonUtils.orderFromJson(json);
            AcceptOrder marketCommand = new AcceptOrder(order);
            forSymbol.queueCommand(marketCommand);
            result = json;
            DBManager.getInstance("MicroTrader").doMicrotradeBuy();
        } else if (op.equals(JsonUtils.OP_SELL)) {
            MarketEngine forSymbol = mu.getMarketEngine("ME", json.getString(JsonUtils.SYMBOL), histSize);
            Order order = JsonUtils.orderFromJson(json);
            AcceptOrder marketCommand = new AcceptOrder(order);
            forSymbol.queueCommand(marketCommand);
            result = json;
            DBManager.getInstance("MicroTrader").doMicrotradeSell();
        } else if (op.equals(JsonUtils.OP_FILLS)) {
            result = getFills();
        } else if (op.equals(JsonUtils.OP_OPEN_ORDERS)) {
            result = getOpenOrders();
        } else if (op.equals(JsonUtils.OP_CLOSED_ORDERS)) {
            result = getClosedOrders();
        } else if (op.equals(JsonUtils.OP_SCORECARD)) {
            result = getScorecard();
        }

        return result;
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
}