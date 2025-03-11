package com.miko.mikostore;

import com.miko.mikostore.router.MainRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {


  @Override
  public void start(Promise<Void> startPromise) throws Exception {


    vertx.createHttpServer().requestHandler(MainRouter.createRouter(vertx)).listen(8888)
      .onComplete(http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}
