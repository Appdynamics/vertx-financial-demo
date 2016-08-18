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
public class FillsReportOperation extends WebRequestOperation {

    public FillsReportOperation(HttpClient httpClient, String dispatcherPath) {
        super(httpClient, dispatcherPath);
    }

    @Override
    public String getDisplayName() {
        return "Fills";
    }

    @Override
    protected void handleJson(JsonObject json) {
        JsonArray array = getFillsArray(json);
        PageUtils ut = new PageUtils();
        ut.pageBegin();
        writePageCommon(ut);
        ut.out("<hr/>");

        if (array != null && array.size() > 0) {
            ut.out("<table border=\"1\">");
            ut.out("<tr>");
            ut.out("<th>Symbol</th><th>Buy Price</th><th>Sell Price</th><th>Quantity</th><th>Timestamp</th><th>Buy ID</th><th>Sell ID</th>");
            ut.out("</tr>");

            for (Iterator it = array.iterator(); it.hasNext(); ) {
                JsonObject obj = (JsonObject) it.next();
                ut.out("<tr>");
                ut.out("<td>");
                ut.out(obj.getString(JsonUtils.SYMBOL));
                ut.out("</td><td>");
                ut.out(String.valueOf(obj.getDouble(JsonUtils.BUYPRICE)));
                ut.out("</td><td>");
                ut.out(String.valueOf(obj.getDouble(JsonUtils.SELLPRICE)));
                ut.out("</td><td>");
                ut.out(String.valueOf(obj.getInteger(JsonUtils.QTY)));
                ut.out("</td><td>");
                ut.out(new Date(obj.getLong(JsonUtils.TSTAMP).longValue()).toString());
                ut.out("</td><td>");
                ut.out(obj.getString(JsonUtils.BUYID));
                ut.out("</td><td>");
                ut.out(obj.getString(JsonUtils.SELLID));
                ut.out("</td></tr>");
            }
            ut.out("<table>");
        } else {
            ut.out("<i>No open fills</i>");
        }
        ut.pageEnd();
        ut.writeFinal(getResponse());
    }

    private JsonArray getFillsArray(JsonObject json) {
        return JsonUtils.getArray(json);
    }

}
