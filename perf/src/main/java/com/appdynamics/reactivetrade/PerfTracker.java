package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.util.JsonUtils;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Verticle;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by trader on 9/18/14.
 */
public class PerfTracker implements Verticle {

    private HttpClient httpClient;
    private int warmups;
    private AtomicInteger count = new AtomicInteger(0);

    private Vertx vertx;
    private Context context;

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

    public Vertx getVertx() {
        return vertx;
    }

    public void init(Vertx vertx, Context context) {
        this.vertx = vertx;
        this.context = context;
    }

    public void start(Future<Void> startFuture) {

        HttpClientOptions opts = new HttpClientOptions();
        opts.setDefaultHost("localhost");
        opts.setDefaultPort(9001);
        vertx.createHttpClient(opts).getNow("/", new Handler<HttpClientResponse>() {
            public void handle(HttpClientResponse httpClientResponse) {
                System.out.println("Whoopee 2!");
            }
        });

        final JsonObject conf = context.config();
        int port = conf.getInteger("externalport");
        long runTime = conf.getInteger("run.time.millis");
        warmups = conf.getInteger("warmups");

        HttpClientOptions opts2 = new HttpClientOptions();
        opts2.setDefaultHost("localhost");
        opts2.setDefaultPort(port);
        httpClient = vertx.createHttpClient(opts2);
        System.out.println("host is " + opts2.getDefaultHost() + ", port is " + opts2.getDefaultPort() + " for cl=" + httpClient);

        vertx.createHttpClient(opts).getNow("/trade/buy/sym/AAPL/qty/100/price/85", new Handler<HttpClientResponse>() {
            public void handle(HttpClientResponse httpClientResponse) {
                System.out.println("Whoopee!");
            }
        });

        try {
            Thread.sleep(20000);
        } catch (Exception e) {}

        JsonObject reqData = nextReq(null);
        while (warmups-- > 0) {
            sendRequest(reqData, true);
            reqData = nextReq(reqData);
            System.out.println("warmup req sent, remaining=" + warmups);
            break;
        }

        try {
            Thread.sleep(20000);
        } catch (Exception e) {}
        if (warmups > 0) System.exit(0);
        long now = System.currentTimeMillis();
        long endTime = now + runTime;
        System.out.println("Warmup done.  Starting at " + (new Date(now)).toString() + ", ending at " + (new Date(endTime)).toString());

        reqData = nextReq(null);
        while (now < endTime) {
            sendRequest(reqData, false);
            reqData = nextReq(reqData);
            now = System.currentTimeMillis();
        }

        System.out.println("There were " + count.get() + " requests");
        System.exit(0);
    }

    public void stop(Future<Void> future) throws Exception {

    }

    private void sendRequest(JsonObject reqData, final boolean isWarmup) {

        String url = getURL(reqData);
        System.out.println("calling getNow for uri " + url + " using cl=" + httpClient);
        try {
            httpClient.getNow(url, new Handler<HttpClientResponse>() {
                public void handle(HttpClientResponse resp) {

                    System.out.println("in outer handler....");
                    resp.bodyHandler(new Handler<Buffer>() {
                        public void handle(Buffer buffer) {
                            System.out.println("handling, warmup=" + isWarmup);
                            if (!isWarmup) {
                                int newCount = count.incrementAndGet();
                                System.out.println("newcount is " + newCount);
                            }
                        }
                    });
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String getURL(JsonObject reqData) {
        String key = reqData.getString(JsonUtils.PERF_KEY);
        if (key.equals(TRADE_PATH)) {
            return TRADE_PATH + "/" + reqData.getString(JsonUtils.OP) + "/sym/" + reqData.getString(JsonUtils.SYMBOL) +
                    "/qty/" + reqData.getInteger(JsonUtils.QTY) + "/price/100.0";
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
}
