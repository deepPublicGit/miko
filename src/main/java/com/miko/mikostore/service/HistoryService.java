package com.miko.mikostore.service;

import com.miko.mikostore.model.EmailFormat;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.time.Instant;
import java.util.HashMap;

public class HistoryService {

  private final SqlClient client;

  private final String HISTOR_INSERT_QUERY = """
    INSERT INTO MIKO.HISTORY_STATUS (BOT_ID, APP_ID, STATUS, DATE_UPDATED) VALUES ($1, $2, $3, $4)
    """;

  public HistoryService(SqlClient sqlClient) {
    client = sqlClient;
  }

  public Integer getStatusFromCache(String status) {
    return statusCache.get(status);
  }

  public void updateStatus(int botId, int appId, String status, EventBus eventBus, RoutingContext context) {
    if (getStatusFromCache(status) == null)
        context.response().setStatusCode(400).end("Invalid Status!");
    else {
      int newStatus = getStatusFromCache(status);
      if(newStatus == 3) {
        String key = botId + "_" + appId;
        errorCache.put(key, errorCache.getOrDefault(key, 0) + 1);
        if(errorCache.get(key) == 3) {
          sendEmail(eventBus, context);
          context.response().setStatusCode(500).end("Internal Server Error!");
        }
      }
      client.preparedQuery(STATUS_QUERY)
        .execute(Tuple.of(newStatus, botId, appId))
        .onComplete(ar -> {
        if (ar.succeeded()) {
          eventBus.send("send.historical", context);
          context.response().setStatusCode(200).end();
        }
      });
    }
  }


  private static void sendEmail(EventBus emailEventBus, RoutingContext context) {
    emailEventBus
      .request("send.email", getEmailFormat(context))
      .onComplete(ar -> {
        if (ar.succeeded()) {
          System.out.printf("Email Sent: %s\n", ar.result().body());
        } else {
          System.out.printf("Email failed: %s\n", ar.cause().getMessage());
        }
      });
  }

}
