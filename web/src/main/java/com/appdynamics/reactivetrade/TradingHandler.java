package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.persist.DBManager;
import com.appdynamics.reactivetrade.util.JsonUtils;
import com.appdynamics.reactivetrade.util.PageUtils;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Verticle;

import java.util.*;

public class TradingHandler implements Verticle {

    private static final String TRADE_PATH = "/trade";
    private static final String FILLS_PATH = "/fills";
    private static final String LOGIN_PATH = "/login";
    private static final String LOGOUT_PATH = "/logout";
    private static final String OPEN_ORDERS_PATH = "/orders/open";
    private static final String CLOSED_ORDERS_PATH = "/orders/closed";
    private static final String SCORECARD_PATH = "/scorecard";

    private String dispatcherHost;
    private int dispatcherPort;
    private String dispatcherPath;
    private HttpClient httpClient;
    private Vertx vertx;

    private static final Map<String, WebRequestOperation> OPERATIONS = new HashMap<String, WebRequestOperation>();

    public void start(Future<Void> future) {

        final JsonObject conf = getVertx().getOrCreateContext().config();
        int port = conf.getInteger("externalport");
        dispatcherHost = conf.getString("delegate.host");
        dispatcherPort = conf.getInteger("delegate.port").intValue();
        dispatcherPath = conf.getString("delegate.path");
        HttpClientOptions opts = new HttpClientOptions();
        opts.setDefaultHost(dispatcherHost);
        opts.setDefaultPort(dispatcherPort);
        httpClient = vertx.createHttpClient(opts);

        OPERATIONS.put(JsonUtils.OP_BUY, new TradeOperation(httpClient, dispatcherPath));
        OPERATIONS.put(JsonUtils.OP_SELL, new TradeOperation(httpClient, dispatcherPath));
        OPERATIONS.put(JsonUtils.OP_CLOSED_ORDERS, new ClosedOrdersReportOperation(httpClient, dispatcherPath));
        OPERATIONS.put(JsonUtils.OP_OPEN_ORDERS, new OpenOrdersReportOperation(httpClient, dispatcherPath));
        OPERATIONS.put(JsonUtils.OP_FILLS, new FillsReportOperation(httpClient, dispatcherPath));
        OPERATIONS.put(JsonUtils.OP_SCORECARD, new ScorecardReportOperation(httpClient, dispatcherPath));

        HttpServerOptions serverOpts = new HttpServerOptions();
        serverOpts.setTcpKeepAlive(true);
        serverOpts.setReuseAddress(true);
        vertx.createHttpServer(serverOpts).requestHandler(new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                int status = 200;
                JsonObject reqJson = JsonUtils.userReqToJson(req);
                if (reqJson != null) {
                    String op = reqJson.getString(JsonUtils.OP);
                    if (op != null) {
                        WebRequestOperation oper = OPERATIONS.get(op);
                        if (oper != null) {
                            oper.setResponse(req.response());
                            oper.setPayload(reqJson);
                            oper.execute();
                        } else {
                            if (op.equals(JsonUtils.OP_HOMEPAGE)) {
                                doHomePage(req);
                            } else if (op.equals(JsonUtils.OP_LOGIN)) {
                                doLogin(req);
                            } else if (op.equals(JsonUtils.OP_LOGOUT)) {
                                doLogout(req);
                            } else {
                                status = write404(req);
                            }
                        }
                    } else {
                        status = write404(req);
                    }
                } else {
                    status = write404(req);
                }

                req.response().setStatusCode(status);
            }
        }).listen(port);
    }

    private int write404(HttpServerRequest req) {
        req.response().setStatusMessage("Not found");
        PageUtils ut = new PageUtils();
        ut.pageBegin();
        ut.out("<p>404: not found.</p>");
        ut.pageEnd();
        ut.writeFinal(req.response());

        return 404;
    }

    private void doLogin(HttpServerRequest req) {
        DBManager.getInstance("AuthDB-").doLogin();
        PageUtils ut = new PageUtils();
        ut.pageBegin();
        ut.out("<p><h4>Login successful.</h4></p>");
        ut.pageEnd();
        ut.writeFinal(req.response());
    }

    private void doLogout(HttpServerRequest req) {
        DBManager.getInstance("AuthDB-").doLogout();
        PageUtils ut = new PageUtils();
        ut.pageBegin();
        ut.out("<p>You are now logged out.</p>");
        ut.pageEnd();
        ut.writeFinal(req.response());
    }

    private void doHomePage(HttpServerRequest req) {

        PageUtils ut = new PageUtils();
        ut.pageBegin();
        ut.out("<p><h4>Custom trade</h4></p>");
        ut.out("<hr/>");
        ut.out("<form action=\"" + TRADE_PATH + "\" formmethod=\"get\">");
        ut.out("<table>");
        ut.out("<tr>");
        ut.out("<td>Symbol: <input type=\"text\" name=\"s\"/></td>");
        ut.out("<td><select name=\"c\" required=\"required\"/>");
        ut.out("<option value=\"buy\">Buy</option>");
        ut.out("<option value=\"sell\">Sell</option>");
        ut.out("</select></td>");
        ut.out("<td>Qty: <input type=\"number\" name=\"a\"/></td>");
        ut.out("<td>Price: <input type=\"number\" name=\"p\"/></td>");
        ut.out("<td><input type=\"submit\" value=\"Execute\"/>");
        ut.out("</tr></table>");

        ut.out("</form>");
        ut.out("<p><h4>Quick trades</h4></p>");
        ut.out("<hr/>");

        ut.out("<table>");
        ut.out("<tr><td>Buy 100 ORCL at $41</td><td><a href=\"" + TRADE_PATH+ "/buy/sym/ORCL/qty/100/price/41\">Execute</a>");
        ut.out("<tr><td>Buy 1000 ORCL at $41</td><td><a href=\"" + TRADE_PATH+ "/buy/sym/ORCL/qty/1000/price/41\">Execute</a>");
        ut.out("<tr><td>Sell 100 ORCL at $39</td><td><a href=\"" + TRADE_PATH+ "/sell/sym/ORCL/qty/100/price/39\">Execute</a>");
        ut.out("<tr><td>Sell 1000 ORCL at $39</td><td><a href=\"" + TRADE_PATH+ "/sell/sym/ORCL/qty/1000/price/39\">Execute</a>");
        ut.out("<tr><td>Buy 100 AAPL at $551</td><td><a href=\"" + TRADE_PATH+ "/buy/sym/AAPL/qty/100/price/551\">Execute</a>");
        ut.out("<tr><td>Buy 1000 AAPL at $551</td><td><a href=\"" + TRADE_PATH+ "/buy/sym/AAPL/qty/1000/price/551\">Execute</a>");
        ut.out("<tr><td>Sell 100 AAPL at $549</td><td><a href=\"" + TRADE_PATH+ "/sell/sym/AAPL/qty/100/price/549\">Execute</a>");
        ut.out("<tr><td>Sell 1000 AAPL at $549</td><td><a href=\"" + TRADE_PATH+ "/sell/sym/AAPL/qty/1000/price/549\">Execute</a>");
        ut.out("<tr><td>Buy 100 GOOG at $1201</td><td><a href=\"" + TRADE_PATH+ "/buy/sym/GOOG/qty/100/price/1201\">Execute</a>");
        ut.out("<tr><td>Buy 1000 GOOG at $1201</td><td><a href=\"" + TRADE_PATH+ "/buy/sym/GOOG/qty/1000/price/1201\">Execute</a>");
        ut.out("<tr><td>Sell 100 GOOG at $1199</td><td><a href=\"" + TRADE_PATH+ "/sell/sym/GOOG/qty/100/price/1199\">Execute</a>");
        ut.out("<tr><td>Sell 1000 GOOG at $1199</td><td><a href=\"" + TRADE_PATH+ "/sell/sym/GOOG/qty/1000/price/1199\">Execute</a>");
        ut.out("<tr><td>Buy 100 FB at $71</td><td><a href=\"" + TRADE_PATH+ "/buy/sym/FB/qty/100/price/71\">Execute</a>");
        ut.out("<tr><td>Buy 1000 FB at $71</td><td><a href=\"" + TRADE_PATH+ "/buy/sym/FB/qty/1000/price/71\">Execute</a>");
        ut.out("<tr><td>Sell 100 FB at $69</td><td><a href=\"" + TRADE_PATH+ "/sell/sym/FB/qty/100/price/69\">Execute</a>");
        ut.out("<tr><td>Sell 1000 FB at $69</td><td><a href=\"" + TRADE_PATH+ "/sell/sym/FB/qty/1000/price/69\">Execute</a>");
        ut.out("<tr><td>Buy 100 MSFT at $41</td><td><a href=\"" + TRADE_PATH+ "/buy/sym/MSFT/qty/100/price/41\">Execute</a>");
        ut.out("<tr><td>Buy 1000 MSFT at $41</td><td><a href=\"" + TRADE_PATH+ "/buy/sym/MSFT/qty/1000/price/41\">Execute</a>");
        ut.out("<tr><td>Sell 100 MSFT at $39</td><td><a href=\"" + TRADE_PATH+ "/sell/sym/MSFT/qty/100/price/39\">Execute</a>");
        ut.out("<tr><td>Sell 1000 MSFT at $39</td><td><a href=\"" + TRADE_PATH+ "/sell/sym/MSFT/qty/1000/price/39\">Execute</a>");
        ut.out("</table>");

        ut.out("<hr/>");
        ut.out("<p><h4>View Fills from partially-filled orders</h4></p>");
        ut.out("<form action=\"" + FILLS_PATH + "\" formmethod=\"get\">");
        ut.out("<p><input type=\"submit\" value=\"Show\"/></p>");
        ut.out("</form>");

        ut.out("<hr/>");
        ut.out("<p><h4>View Open Orders</h4></p>");
        ut.out("<form action=\"" + OPEN_ORDERS_PATH + "\" formmethod=\"get\">");
        ut.out("<p><input type=\"submit\" value=\"Show\"/></p>");
        ut.out("</form>");

        ut.out("<hr/>");
        ut.out("<p><h4>View Recently Closed Orders</h4></p>");
        ut.out("<form action=\"" + CLOSED_ORDERS_PATH + "\" formmethod=\"get\">");
        ut.out("<p><input type=\"submit\" value=\"Show\"/></p>");
        ut.out("</form>");

        ut.out("<hr/>");
        ut.out("<p><h4>Administration</h4></p>");
        ut.out("<form action=\"" + "/" + LOGOUT_PATH + "\" formmethod=\"get\">");
        ut.out("<p><input type=\"submit\" value=\"Logout\"/></p>");
        ut.out("</form>");

        ut.pageEnd();
        ut.writeFinal(req.response());
    }

    public Vertx getVertx() {
        return vertx;
    }

    public void init(Vertx vertx, Context context) {
        this.vertx = vertx;
    }

    public void stop(Future<Void> future) throws Exception {

    }
}