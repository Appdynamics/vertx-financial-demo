package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.util.JsonUtils;
import com.appdynamics.reactivetrade.util.PageUtils;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;

/**
 * Created by trader on 3/17/15.
 */
public class ScorecardReportOperation extends WebRequestOperation {

    public ScorecardReportOperation(HttpClient httpClient, String dispatcherPath) {
        super(httpClient, dispatcherPath);
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    protected void handleJson(JsonObject json) {
        PageUtils ut = new PageUtils();
        ut.pageBegin();
        writePageCommon(ut);
        ut.out("<hr/>");

        ut.out("<table border=\"1\">");
        ut.out("<tr>");
        ut.out("<th>Gross revenue</th><th>Costs</th><th>Net Profit</th><th>Customer Satisfaction Score</th>");
        ut.out("</tr>");

        ut.out("<tr><td>");
        writeRevenue(ut, json.getDouble(JsonUtils.TRADE_REVENUE));
        ut.out("</td><td>");
        writeCost(ut, json.getDouble(JsonUtils.TRADE_COST));
        ut.out("</td><td>");
        writeProfit(ut, json.getDouble(JsonUtils.TRADE_REVENUE).doubleValue() - json.getDouble(JsonUtils.TRADE_COST).doubleValue());
        ut.out("</td><td>");
        writeCustSatScore(ut, json.getInteger(JsonUtils.CUST_SAT_SCORE));
        ut.out("</td></tr>");

        ut.pageEnd();
        ut.writeFinal(getResponse());
    }

    private void writeRevenue(PageUtils ut, Number revenue) {
        ut.out(String.valueOf(revenue));
    }

    private void writeCost(PageUtils ut, Number cost) {
        ut.out(String.valueOf(cost));
    }

    private void writeProfit(PageUtils ut, double profit) {
        ut.out(String.valueOf(profit));
    }

    private void writeCustSatScore(PageUtils ut, Number score) {
        ut.out(String.valueOf(score));
    }
}
