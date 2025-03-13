package com.miko.mikostore.service;

import com.miko.mikostore.model.AppList;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScheduleService {

  private final SqlClient client;
  HashMap<Integer, List<AppList>> localCache; // Move to Redis

  public ScheduleService(SqlClient sqlClient) {
      client = sqlClient;
      localCache = new HashMap<>();
//      testSqlQuery(client);
  }

  private final String SCHEDULE_QUERY = """
    WITH BOT_STATUS AS (SELECT APP_ID, DATE_UPDATED FROM MIKO.STATUS WHERE BOT_ID = $1)
    SELECT A.* FROM MIKO.APP_LIST A LEFT JOIN BOT_STATUS B ON A.APP_ID=B.APP_ID AND A.DATE_UPDATED>B.DATE_UPDATED
    """;

  public void getSchedule(int botId, RoutingContext context) {
//    System.out.println("getSchedule: " +  client.toString());
    int appId = Integer.parseInt(context.get("appId"));
    if(localCache.containsKey(botId) && (appId + 1) < localCache.get(botId).size()) {
      context.response().end(Json.encodePrettily(localCache.get(botId).get(appId + 1)));
    } else {
      client.preparedQuery(SCHEDULE_QUERY).execute(Tuple.of(botId)).onComplete(ar -> {
        if (ar.succeeded()) {
          RowSet<Row> result = ar.result();

          for(Row row : result) {
            System.out.println("Got " + row.toString());
            if(!localCache.containsKey(botId)) {
              localCache.put(botId, new ArrayList<>());
            }
            localCache.get(botId).add(getApp(row));
          }
          context.response().end(Json.encodePrettily(localCache.get(botId).get(0)));
        }
      });
    }
  }

  private AppList getApp(Row row) {
    AppList appList = new AppList();
    appList.setAppId(String.valueOf(row.getValue(0)));
    appList.setAppName(row.getString("app_name"));
    appList.setAppUrl(row.getString("app_url"));
    appList.setVersion(String.valueOf(row.getValue("version")));
    return appList;
  }

  private static void testSqlQuery(SqlClient client) {

    client
      .query("SELECT * FROM miko.app_list WHERE app_id in (1,2,3,4,5)")
      .execute()
      .onComplete(ar -> {
        if (ar.succeeded()) {
          RowSet<Row> result = ar.result();
          System.out.println("Got " + result.size() + " rows ");
          for(Row row : result) {
            System.out.println("Got " + row.toString());
          }
        } else {
          System.out.println("Failure: " + ar.cause().getMessage());
        }
//        client.close();
      });
  }

}
