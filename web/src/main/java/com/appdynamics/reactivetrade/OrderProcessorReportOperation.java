package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.util.JsonUtils;
import com.appdynamics.reactivetrade.util.PageUtils;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by trader on 3/17/15.
 */
public abstract class OrderProcessorReportOperation extends WebRequestOperation {

    protected OrderProcessorReportOperation(HttpClient httpClient, String dispatcherPath) {
        super(httpClient, dispatcherPath);
    }

    protected void processOrders(JsonObject json) {
        JsonArray array = getOrdersArray(json);
        PageUtils ut = new PageUtils();
        ut.pageBegin();
        writePageCommon(ut);
        ut.out("<hr/>");

        if (array != null && array.size() > 0) {
            ut.out("<table border=\"1\">");
            ut.out("<tr>");
            ut.out("<th>ID</th><th>Symbol</th><th>Price</th><th>Quantity</th><th>Buy/Sell</th><th>Timestamp</th>");
            ut.out("</tr>");

            for (Iterator it = array.iterator(); it.hasNext(); ) {
                JsonObject obj = (JsonObject) it.next();
                ut.out("<tr><td>");
                ut.out(obj.getString(JsonUtils.ID));
                ut.out("</td><td>");
                ut.out(obj.getString(JsonUtils.SYMBOL));
                ut.out("</td><td>");
                ut.out(String.valueOf(obj.getDouble(JsonUtils.PRICE)));
                ut.out("</td><td>");
                ut.out(String.valueOf(obj.getInteger(JsonUtils.QTY)));
                ut.out("</td><td>");
                ut.out(String.valueOf(obj.getString(JsonUtils.SIDE)));
                ut.out("</td><td>");
                ut.out(new Date(obj.getLong(JsonUtils.TSTAMP).longValue()).toString());
                ut.out("</td></tr>");
            }
            ut.out("<table>");
        } else {
            ut.out("<i>" + getMessage() + "</i>");
        }
        ut.pageEnd();
        ut.writeFinal(getResponse());
    }

    protected abstract String getMessage();

    protected abstract JsonArray getOrdersArray(JsonObject container);
}
