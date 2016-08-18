package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.market.*;
import com.appdynamics.reactivetrade.persist.DBManager;
import com.appdynamics.reactivetrade.util.CommonParameters;
import com.appdynamics.reactivetrade.util.JsonUtils;
import com.appdynamics.reactivetrade.util.MarketUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Verticle;

import java.util.Set;

/**
 * Created by trader on 7/28/14.
 */
public class PremierClientHandler implements Verticle {

    private MarketUtils mu;
    private int histSize;
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
        params = new CommonParameters("premier", conf);

        mu = new MarketUtils(new PremierClientsScorecard(params.getFixedFeePerTrade(),
                                                         params.getPerShareFee(),
                                                         params.getPerTradeCostForMarket(),
                                                         params.getCustSatCostPerDroppedTrade()),
                             params.getSpread());

        vertx.eventBus().consumer(conf.getString("premier.eventbus.address"), new Handler<Message<JsonObject>>() {
            public void handle(Message<JsonObject> msg) {
                handleJsonMessage(msg);
            }
        });
        System.out.println(conf.getString("boot.msg.prefix") + ": PremierClientTradeHandler");
    }

    public void stop(Future<Void> future) throws Exception {

    }

    private void handleJsonMessage(Message<JsonObject> msg) {
        JsonObject json = msg.body();
        String op = json.getString("op");
        JsonObject result = null;
        if (op.equals(JsonUtils.OP_BUY)) {
            MarketEngine forSymbol = mu.getMarketEngine("PC", json.getString(JsonUtils.SYMBOL), histSize);
            Order order = JsonUtils.orderFromJson(json);
            AcceptOrder marketCommand = new AcceptOrder(order);
            forSymbol.queueCommand(marketCommand);
            DBManager.getInstance("Premier").doPremierClientBuy();
            result = json;
        } else if (op.equals(JsonUtils.OP_SELL)) {
            MarketEngine forSymbol = mu.getMarketEngine("PC", json.getString(JsonUtils.SYMBOL), histSize);
            Order order = JsonUtils.orderFromJson(json);
            AcceptOrder marketCommand = new AcceptOrder(order);
            forSymbol.queueCommand(marketCommand);
            DBManager.getInstance("Premier").doPremierClientSell();
            result = json;
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
}
