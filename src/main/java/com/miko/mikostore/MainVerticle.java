package com.miko.mikostore;

import com.miko.mikostore.repository.DBRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;


public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(new MailVerticle());
    vertx.deployVerticle(new HistoricalVerticle());
    vertx.deployVerticle(new RouterVerticle());
  }
}
