package org.tongqing;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

/**
 * Created by Tongqing Qiu.
 */
public class RestServer extends Verticle {
    public void start() {

        RouteMatcher rm = new RouteMatcher();
        rm.get("/myapp/myresource", new Handler<HttpServerRequest>() {
            public void handle(final HttpServerRequest req) {
                // sleep 100 ms
                vertx.setTimer(100, new Handler<Long>() {
                    public void handle(Long timerID) {
                        req.response().end("Got it");
                    }
                });
            }
        });

        vertx.createHttpServer().requestHandler(rm).listen(8080);
    }
}
