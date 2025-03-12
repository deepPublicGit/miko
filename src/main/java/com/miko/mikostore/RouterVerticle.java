package com.miko.mikostore;

import com.miko.mikostore.model.AppList;
import com.miko.mikostore.router.MainRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;

public class RouterVerticle extends AbstractVerticle {


  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    String dbHost = System.getenv().getOrDefault("DB_HOST", "localhost");
    int dbPort = Integer.parseInt(System.getenv().getOrDefault("DB_PORT", "5432"));
    String dbName = System.getenv().getOrDefault("DB_NAME", "mydb");
    String dbUser = System.getenv().getOrDefault("DB_USER", "user");
    String dbPassword = System.getenv().getOrDefault("DB_PASSWORD", "password");
    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(dbPort)
      .setHost(dbHost)
      .setDatabase(dbName)
      .setUser(dbUser)
      .setPassword(dbPassword);
    System.out.println("Props: " + connectOptions.toString());

    PoolOptions poolOptions = new PoolOptions().setMaxSize(10);

    SqlClient client = PgBuilder
      .client()
      .with(poolOptions)
      .connectingTo(connectOptions)
      .using(vertx)
      .build();



    vertx.createHttpServer().requestHandler(MainRouter.createRouter(vertx)).listen(8888)
      .onComplete(http -> {
      if (http.succeeded()) {
        client
          .query("SELECT * FROM miko.app_list WHERE app_id in (1,2,3,4,5)")
          .execute()
          .onComplete(ar -> {
            if (ar.succeeded()) {
              RowSet<Row> result = ar.result();
              System.out.println("Got " + result.size() + " rows ");
              for(Row row : result) {
                System.out.println("Got " + getApp(row));

              }
            } else {
              System.out.println("Failure: " + ar.cause().getMessage());
            }

            client.close();
          });
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  private String getApp(Row row) {
    AppList appList = new AppList();
    appList.setAppId(String.valueOf(row.getValue(0)));
    appList.setAppName(row.getString("app_name"));
    appList.setAppUrl(row.getString("app_url"));
    appList.setVersion(String.valueOf(row.getValue("version")));
    return appList.toString();
  }
}
