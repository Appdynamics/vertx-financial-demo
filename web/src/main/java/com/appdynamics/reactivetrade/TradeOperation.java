package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.persist.DBManager;
import com.appdynamics.reactivetrade.util.JsonUtils;
import com.appdynamics.reactivetrade.util.PageUtils;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;

/**
 * Created by trader on 3/17/15.
 */
public class TradeOperation extends WebRequestOperation {

    public TradeOperation(HttpClient httpClient, String dispatcherPath) {
        super(httpClient, dispatcherPath);
    }

    @Override
    public String getDisplayName() {
        return "Trade";
    }

    private void storeTrade(JsonObject json) {
        DBManager.getInstance("TradeLog-").logTrade(json);
    }

    public void handleJson(JsonObject json) {
        storeTrade(json);
        PageUtils ut = new PageUtils();
        ut.pageBegin();
        ut.out("Executed trade: op=");
        ut.out(json.getString(JsonUtils.OP));
        ut.out(", sym=");
        ut.out(json.getString(JsonUtils.SYMBOL));
        ut.out(", qty=");
        ut.out(String.valueOf(json.getInteger(JsonUtils.QTY)));
        ut.out("<br/>");
        writePageCommon(ut);
        ut.pageEnd();
        ut.writeFinal(getResponse());
    }
}
