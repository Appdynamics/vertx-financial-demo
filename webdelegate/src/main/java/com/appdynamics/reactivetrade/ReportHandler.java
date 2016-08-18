package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.util.JsonUtils;
import com.appdynamics.reactivetrade.util.PageUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Sneaky little handler/dispatcher, which fires off the same request to a list of recipients and merges the results together.
 *
 * Created by trader on 7/28/14.
 */
public class ReportHandler implements Handler<AsyncResult<Message<JsonObject>>> {

    private HttpServerRequest req;
    private JsonObject userReq;
    private JsonObject results;
    private int nReplies;

    public ReportHandler(HttpServerRequest req, JsonObject userReq, int nReplies) {
        this.req = req;
        this.userReq = userReq;
        this.nReplies = nReplies;
        this.results = new JsonObject();
        results.put("array", new JsonArray());
    }

    public void handle(AsyncResult<Message<JsonObject>> asyncResult) {
        Message<JsonObject> msg = asyncResult.result();
        JsonObject result = msg.body();
        JsonUtils.mergeArray(results, result);

        synchronized (this) {
            nReplies--;

            if (nReplies == 0) {
                // Reply to original sender
                PageUtils ut = new PageUtils();
                ut.writeJSON(req, results);
            }
        }
    }
}
