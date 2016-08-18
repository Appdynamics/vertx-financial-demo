package com.appdynamics.reactivetrade;

import com.appdynamics.reactivetrade.util.GenericScorecard;
import com.appdynamics.reactivetrade.util.JsonUtils;
import com.appdynamics.reactivetrade.util.MarketScorecard;
import com.appdynamics.reactivetrade.util.PageUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

/**
 * Created by trader on 3/11/15.
 */
public class ScorecardHandler implements Handler<AsyncResult<Message<JsonObject>>> {

    private HttpServerRequest req;
    private JsonObject userReq;
    private GenericScorecard results;
    private int nReplies;

    public ScorecardHandler(HttpServerRequest req, int nReplies) {
        this.req = req;
        this.nReplies = nReplies;
        this.results = new GenericScorecard();
    }

    public void handle(AsyncResult<Message<JsonObject>> asyncResult) {
        Message<JsonObject> msg = asyncResult.result();
        MarketScorecard result = JsonUtils.scorecardFromJson(msg.body());
        results.subsume(result);

        synchronized (this) {
            nReplies--;
        }

        if (nReplies == 0) {
            // Reply to original sender
            JsonObject replyObj = JsonUtils.jsonFromScorecard(results);
            PageUtils ut = new PageUtils();
            ut.writeJSON(req, replyObj);
        }
    }
}
