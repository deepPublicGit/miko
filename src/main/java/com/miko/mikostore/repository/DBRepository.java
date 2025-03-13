package com.miko.mikostore.repository;

import com.miko.mikostore.model.AppList;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;

public class DBRepository{

  private final static  String dbHost = System.getenv().getOrDefault("DB_HOST", "localhost");
  private final static  int dbPort = Integer.parseInt(System.getenv().getOrDefault("DB_PORT", "5432"));
  private final static  String dbName = System.getenv().getOrDefault("DB_NAME", "mydb");
  private final static  String dbUser = System.getenv().getOrDefault("DB_USER", "user");
  private final static  String dbPassword = System.getenv().getOrDefault("DB_PASSWORD", "password");


  public static SqlClient getSqlClient(Vertx vertx) {
    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(dbPort)
      .setHost(dbHost)
      .setDatabase(dbName)
      .setUser(dbUser)
      .setPassword(dbPassword);

    PoolOptions poolOptions = new PoolOptions().setMaxSize(10);

    return PgBuilder
      .client()
      .with(poolOptions)
      .connectingTo(connectOptions)
      .using(vertx)
      .build();
  }
}
