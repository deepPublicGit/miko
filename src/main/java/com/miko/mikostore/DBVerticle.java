package com.miko.mikostore;

import com.miko.mikostore.model.AppList;
import com.miko.mikostore.router.MainRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.pgclient.impl.PgPoolOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;

public class DBVerticle extends AbstractVerticle {


  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(5432)
      .setHost("localhost")
      .setDatabase("test-db")
      .setUser("user")
      .setPassword("password");

    PoolOptions poolOptions = new PoolOptions().setMaxSize(10);

    SqlClient client = PgBuilder
      .client()
      .with(poolOptions)
      .connectingTo(connectOptions)
      .using(vertx)
      .build();

    client
      .query("SELECT * FROM app_list WHERE app_id='app1'")
      .execute()
      .onComplete(ar -> {
        if (ar.succeeded()) {
          RowSet<Row> result = ar.result();
          System.out.println("Got " + result.size() + " rows ");
          System.out.println("Got " + result.next());
        } else {
          System.out.println("Failure: " + ar.cause().getMessage());
        }

        client.close();
      });
  }
}
