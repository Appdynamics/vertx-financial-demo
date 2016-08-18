package com.appdynamics.reactivetrade.util;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

/**
 * Created by trader on 7/24/14.
 */
public class PageUtils {

    private StringBuilder content;

    public PageUtils() {
        content = new StringBuilder();
    }

    public void out(String data) {
        content.append(data);
        content.append("\r\n");
    }

    public void pageBegin() {
        content.append("<html><head><h3>Financial Demo</h3></head><body>\r\n");
    }

    public void pageEnd() {
        content.append("</body></html>\r\n");
    }

    public void writeFinal(HttpServerResponse response) {
        response.headers().set("Content-Type", "text/html; charset=UTF-8");
        response.headers().add("Content-Length", String.valueOf(content.length()));
        response.write(content.toString());
        response.setStatusCode(200);
        response.end();
    }

    public void writeJSON(HttpServerRequest req, JsonObject json) {
        req.response().headers().set("Content-Type", "text/json; charset=UTF-8");
        String jsonString = json.toString();
        req.response().headers().add("Content-Length", String.valueOf(jsonString.length()));
        req.response().write(jsonString.toString());
        req.response().write("\r\n");
        req.response().end();
    }
}
