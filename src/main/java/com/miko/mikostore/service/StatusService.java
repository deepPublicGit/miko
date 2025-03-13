package com.miko.mikostore.service;

import com.miko.mikostore.model.EmailFormat;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

public class StatusService {

  private final SqlClient client;

  private final HashMap<String, Integer> statusCache; // Should be in Redis
  private final HashMap<String, Integer> errorCache; // Should be in Redis
  private final static String toEmail = System.getenv().getOrDefault("EMAIL_TO", "pradeepkg41199@gmail.com");

  private final String STATUS_QUERY = """
    UPDATE MIKO.STATUS SET STATUS = $1 WHERE BOT_ID = $2 AND APP_ID = $3
    """;

  public StatusService(SqlClient sqlClient) {
    client = sqlClient;
    statusCache = new HashMap<>();
    errorCache = new HashMap<>();
    statusCache.put("SCHEDULED", 0);
    statusCache.put("PICKEDUP", 1);
    statusCache.put("COMPLETED", 2);
    statusCache.put("ERROR", 3);
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

  private static String getEmailFormat(RoutingContext context) {
    EmailFormat emailFormat = new EmailFormat();
    emailFormat.setFrom("pradeepkg41199@gmail.com");
    emailFormat.setTo(toEmail);
    emailFormat.setSubject("Error Event");
    emailFormat.setBody(context.body().asString());

    HashMap<String, String> errorDetails = new HashMap<>();
    errorDetails.put("appId", context.pathParam("appId"));
    errorDetails.put("botId", context.request().getParam("botId"));
    errorDetails.put("status", context.request().getParam("status"));
    errorDetails.put("timeStamp", Instant.now().toString());
    emailFormat.setOptionalDetails(errorDetails);

    return Json.encode(emailFormat);
  }
}
