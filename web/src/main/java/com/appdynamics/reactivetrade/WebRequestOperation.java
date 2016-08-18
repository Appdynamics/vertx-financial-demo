package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.util.PageUtils;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

/**
 * Created by trader on 3/17/15.
 */
public abstract class WebRequestOperation {

    private HttpClient httpClient;
    private String dispatcherPath;
    private HttpServerResponse response;
    private JsonObject payload;

    protected WebRequestOperation(HttpClient httpClient, String dispatcherPath) {
        this.httpClient = httpClient;
        this.dispatcherPath = dispatcherPath;
    }

    protected String getDispatcherPath() {
        return dispatcherPath;
    }

    public abstract String getDisplayName();

    public void execute() {

        HttpClientRequest req = httpClient.request(HttpMethod.GET, getDispatcherPath(), new Handler<HttpClientResponse>() {
            public void handle(HttpClientResponse resp) {
                resp.bodyHandler(new Handler<Buffer>() {
                    public void handle(Buffer buffer) {
                        JsonObject json = new JsonObject(buffer.getString(0, buffer.length()));
                        handleJson(json);
                    }
                });
            }
        });

        req.putHeader("Content-Type", "text/json; charset=UTF-8");
        req.end(getPayload().toString());
    }

    protected abstract void handleJson(JsonObject json);

    public HttpServerResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServerResponse response) {
        this.response = response;
    }

    public JsonObject getPayload() {
        return payload;
    }

    public void setPayload(JsonObject payload) {
        this.payload = payload;
    }

    protected void writePageCommon(PageUtils ut) {
        ut.out("<h4>Helpful links:</h4>");
        ut.out("<a href=\"/\">Home Page</a><br/>");
        ut.out("<a href=\"/fills\">Fills Report (partially-filled orders)</a><br/>");
        ut.out("<a href=\"/orders/closed\">Recent Closed Orders Report</a><br/>");
        ut.out("<a href=\"/orders/open\">Open Orders Report</a><br/>");
        ut.out("<a href=\"/scorecard\">Scorecard</a><br/>");
        ut.out("<a href=\"/logout\">Logout</a><br/>");
    }
}
