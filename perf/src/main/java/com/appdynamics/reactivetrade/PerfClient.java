package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.util.JsonUtils;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VoidHandler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Verticle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by trader on 9/19/14.
 */
public class PerfClient implements Verticle, Handler<HttpClientResponse> {

    private HttpClient client;

    private long start;

    private int count = 0;

    private JsonObject reqData;
    private Vertx vertx;

    // This determines the degree of pipelining
    private static final int CREDITS_BATCH = 100;
    private static final int THRESHOLD = 80;

    // Number of connections to create
    private static final int MAX_CONNS = 10;

    private int requestCredits = CREDITS_BATCH;

    private static final int[] QUANTITIES = { 25, 100, 400, 2000, 8000, 50000, 1000000 };

    private static final String TRADE_PATH = "/trade";
    private static final String FILLS_PATH = "/fills";
    private static final String OPEN_ORDERS_PATH = "/orders/open";
    private static final String CLOSED_ORDERS_PATH = "/orders/closed";
    private static final String LOGIN_PATH = "/login";
    private static final String LOGOUT_PATH = "/logout";

    private static final Map<String, String> SYMBOLS_AND_NEXT;

    static {
        SYMBOLS_AND_NEXT = new HashMap<String, String>();

        /* Symbols:
            AAPL, ADBE, BAC, BRKB, FB, GOOG, IBM, MSFT, ORCL, XOM
        */

        SYMBOLS_AND_NEXT.put("AAPL", "ADBE");
        SYMBOLS_AND_NEXT.put("ADBE", "BAC");
        SYMBOLS_AND_NEXT.put("BAC", "BRKB");
        SYMBOLS_AND_NEXT.put("BRKB", "FB");
        SYMBOLS_AND_NEXT.put("FB", "GOOG");
        SYMBOLS_AND_NEXT.put("GOOG", "IBM");
        SYMBOLS_AND_NEXT.put("IBM", "MSFT");
        SYMBOLS_AND_NEXT.put("MSFT", "ORCL");
        SYMBOLS_AND_NEXT.put("ORCL", "XOM");
    }

    public void handle(HttpClientResponse response) {
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Invalid response");
        }
        response.endHandler(new VoidHandler() {
            public void handle() {
                count++;

                if (count % 100 == 0) {
                    System.out.println(count + " and counting.....");
                }

                if (count >= 5000) {
                    requestCredits = 0;

                    System.out.println("Done in " + (System.currentTimeMillis() - start) + " milliseconds.");
                    System.exit(0);
                } else {
                    makeRequest();
                }
            }
        });
    }

    public void start(Future<Void> startFuture) {
        HttpClientOptions opts = new HttpClientOptions();
        opts.setDefaultHost("localhost");
        opts.setDefaultPort(9001);
        opts.setMaxPoolSize(MAX_CONNS);
        client = vertx.createHttpClient(opts);
        reqData = null;
        makeRequest();
        makeRequest();
        makeRequest();
        makeRequest();
        makeRequest();
        makeRequest();
        makeRequest();
        makeRequest();
        makeRequest();
    }

    public void stop(Future<Void> future) throws Exception {

    }

    private void makeRequest() {
        if (start == 0) {
            start = System.currentTimeMillis();
        }

        reqData = nextReq(reqData);
        client.getNow(getURL(reqData), this);
    }

    private String getURL(JsonObject reqData) {
        String key = reqData.getString(JsonUtils.PERF_KEY);
        if (key.equals(TRADE_PATH)) {
            return TRADE_PATH + "/" + reqData.getString(JsonUtils.OP) + "/sym/" + reqData.getString(JsonUtils.SYMBOL) +
                    "/qty/" + reqData.getDouble(JsonUtils.QTY) + "/price/100.0";
        } else {
            return key;
        }
    }

    private JsonObject nextReq(JsonObject currReq) {

        if (currReq == null) {
            return getTradeJson("AAPL", QUANTITIES[0], JsonUtils.OP_BUY);
        } else {
            String key = currReq.getString(JsonUtils.PERF_KEY);
            if (key.equals(TRADE_PATH)) {
                String op = currReq.getString(JsonUtils.OP);
                if (op.equals(JsonUtils.OP_BUY)) {
                    return getTradeJson(currReq.getString(JsonUtils.SYMBOL), currReq.getInteger(JsonUtils.QTY), JsonUtils.OP_SELL);
                } else {
                    int nShares = currReq.getInteger(JsonUtils.QTY);
                    int nextShares = getNextShares(nShares);
                    if (nextShares > 0) {
                        return getTradeJson(currReq.getString(JsonUtils.SYMBOL), nextShares, JsonUtils.OP_BUY);
                    } else {
                        String nextSym = SYMBOLS_AND_NEXT.get(currReq.getString(JsonUtils.SYMBOL));
                        if (nextSym == null) {
                            return getFillsJson();
                        } else {
                            return getTradeJson(nextSym, QUANTITIES[0], JsonUtils.OP_BUY);
                        }
                    }
                }
            } else if (key.equals(FILLS_PATH)) {
                return getOpenOrdersJson();
            } else if (key.equals(OPEN_ORDERS_PATH)) {
                return getClosedOrdersJson();
            } else if (key.equals(CLOSED_ORDERS_PATH)) {
                return getLoginJson();
            } else if (key.equals(LOGIN_PATH)) {
                return getLogoutJson();
            } else {
                // start over
                return getTradeJson("AAPL", QUANTITIES[0], JsonUtils.OP_BUY);
            }
        }
    }

    private JsonObject getTradeJson(String ticker, int nShares, String op) {
        JsonObject result = new JsonObject();
        result.put(JsonUtils.PERF_KEY, TRADE_PATH);
        result.put(JsonUtils.SYMBOL, ticker);
        result.put(JsonUtils.QTY, nShares);
        result.put(JsonUtils.OP, op);

        return result;
    }

    private JsonObject getFillsJson() {
        JsonObject result = new JsonObject();
        result.put(JsonUtils.PERF_KEY, FILLS_PATH);

        return result;
    }

    private JsonObject getOpenOrdersJson() {
        JsonObject result = new JsonObject();
        result.put(JsonUtils.PERF_KEY, OPEN_ORDERS_PATH);

        return result;
    }

    private JsonObject getClosedOrdersJson() {
        JsonObject result = new JsonObject();
        result.put(JsonUtils.PERF_KEY, CLOSED_ORDERS_PATH);

        return result;
    }

    private JsonObject getLoginJson() {
        JsonObject result = new JsonObject();
        result.put(JsonUtils.PERF_KEY, LOGIN_PATH);

        return result;
    }

    private JsonObject getLogoutJson() {
        JsonObject result = new JsonObject();
        result.put(JsonUtils.PERF_KEY, LOGOUT_PATH);

        return result;
    }

    private int getNextShares(int seed) {
        int i = 0;
        while (i < QUANTITIES.length && seed >= QUANTITIES[i]) {
            i++;
        }

        if (i == QUANTITIES.length) {
            return -1;
        } else {
            return QUANTITIES[i];
        }
    }

    public Vertx getVertx() {
        return vertx;
    }

    public void init(Vertx vertx, Context context) {
        this.vertx = vertx;
    }
}