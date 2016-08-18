package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.util.JsonUtils;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by trader on 3/17/15.
 */
public class ClosedOrdersReportOperation extends OrderProcessorReportOperation {

    public ClosedOrdersReportOperation(HttpClient httpClient, String dispatcherPath) {
        super(httpClient, dispatcherPath);
    }

    @Override
    protected String getMessage() {
        return "No recently-closed orders found";
    }

    @Override
    protected JsonArray getOrdersArray(JsonObject container) {
        return getClosedOrdersArray(container);
    }

    private JsonArray getClosedOrdersArray(JsonObject container) {
        return JsonUtils.getArray(container);
    }

    @Override
    public String getDisplayName() {
        return "ClosedOrders";
    }

    @Override
    public void handleJson(JsonObject json) {
        processOrders(json);
    }
}
