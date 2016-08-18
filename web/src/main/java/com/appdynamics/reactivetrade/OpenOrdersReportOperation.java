package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.util.JsonUtils;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by trader on 3/17/15.
 */
public class OpenOrdersReportOperation extends OrderProcessorReportOperation {

    public OpenOrdersReportOperation(HttpClient httpClient, String dispatcherPath) {
        super(httpClient, dispatcherPath);
    }

    @Override
    protected String getMessage() {
        return "No open orders found";
    }

    @Override
    protected JsonArray getOrdersArray(JsonObject container) {
        return getOpenOrdersArray(container);
    }

    private JsonArray getOpenOrdersArray(JsonObject container) {
        return JsonUtils.getArray(container);
    }

    @Override
    public String getDisplayName() {
        return "OpenOrders";
    }

    @Override
    public void handleJson(JsonObject json) {
        processOrders(json);
    }
}
