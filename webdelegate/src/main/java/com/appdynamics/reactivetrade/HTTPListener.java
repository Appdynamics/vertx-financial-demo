package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.util.JsonUtils;
import com.appdynamics.reactivetrade.util.PageUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Verticle;

public class HTTPListener implements Verticle {

    private String microTradeEvtAddress;
    private String standardTradeEvtAddress;
    private String premierClientsEvtAddress;
    private double microThreshold;
    private Vertx vertx;

    public Vertx getVertx() {
        return vertx;
    }

    public void init(Vertx vertx, Context context) {
        this.vertx = vertx;
    }

    public void start(Future<Void> startFuture) {

        JsonObject conf = getVertx().getOrCreateContext().config();
        microTradeEvtAddress = conf.getString("micro.eventbus.address");
        standardTradeEvtAddress = conf.getString("standard.eventbus.address");
        premierClientsEvtAddress = conf.getString("premier.eventbus.address");

        Double thresholdVal = conf.getDouble("micro.ordervalue.threshold");
        try {
            microThreshold = thresholdVal.doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
            microThreshold = 30000;
        }

        int httpPort = conf.getInteger("delegate.port");
        HttpServerOptions serverOpts = new HttpServerOptions();
        serverOpts.setTcpKeepAlive(true);
        serverOpts.setReuseAddress(true);
        getVertx().createHttpServer(serverOpts).requestHandler(new Handler<HttpServerRequest>() {
            public void handle(final HttpServerRequest req) {
                req.bodyHandler(new Handler<Buffer>() {
                    public void handle(Buffer buffer) {
                        JsonObject userReq = new JsonObject(buffer.getString(0, buffer.length()));
                        if (userReq == null) {
                            JsonObject result = failedRequest(req);
                            PageUtils ut = new PageUtils();
                            ut.writeJSON(req, result);
                        } else {
                            String op = userReq.getString(JsonUtils.OP);
                            if (op.equals(JsonUtils.OP_BUY) || op.equals(JsonUtils.OP_SELL)) {
                                dispatch(req, userReq);
                            } else if (op.equals(JsonUtils.OP_SCORECARD)) {
                                ScorecardHandler sHandler = new ScorecardHandler(req, 3);
                                getVertx().eventBus().send(microTradeEvtAddress, userReq, sHandler);
                                getVertx().eventBus().send(standardTradeEvtAddress, userReq, sHandler);
                                getVertx().eventBus().send(premierClientsEvtAddress, userReq, sHandler);
                            } else {
                                ReportHandler rHandler = new ReportHandler(req, userReq, 3);
                                getVertx().eventBus().send(microTradeEvtAddress, userReq, rHandler);
                                getVertx().eventBus().send(standardTradeEvtAddress, userReq, rHandler);
                                getVertx().eventBus().send(premierClientsEvtAddress, userReq, rHandler);
                            }
                        }
                    }
                });
            }
        }).listen(httpPort);
    }

    private JsonObject failedRequest(HttpServerRequest req) {
        JsonObject result = new JsonObject();
        result.put(JsonUtils.RESULT, "failure");
        result.put(JsonUtils.REQ_PATH, req.path());
        result.put(JsonUtils.TSTAMP, System.currentTimeMillis());

        return result;
    }

    private void dispatch(final HttpServerRequest req, JsonObject order) {
        int qty = order.getInteger("quantity").intValue();
        double price = order.getDouble("price").doubleValue();
        if (qty * price < microThreshold) {
            dispatchToEvtBus(req, microTradeEvtAddress, order);
        } else {
            dispatchToEvtBus(req, standardTradeEvtAddress, order);
        }
    }

    private void dispatchToEvtBus(final HttpServerRequest req, String address, final JsonObject order) {
        getVertx().eventBus().send(address, order, new Handler<AsyncResult<Message<JsonObject>>>() {
            public void handle(AsyncResult<Message<JsonObject>> asyncResult) {
                PageUtils ut = new PageUtils();
                Message<JsonObject> message = asyncResult.result();
                ut.writeJSON(req, message.body());
            }
        });
    }

    public void stop(Future<Void> stopFuture) {

    }
}