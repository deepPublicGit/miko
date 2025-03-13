package com.miko.mikostore.service;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.SqlClient;

import java.util.HashMap;

public class StatusService {

  private final SqlClient client;
  private final HashMap<String, Integer> statusCache; // Should be in Redis

  public StatusService(SqlClient sqlClient) {
    client = sqlClient;
    statusCache = new HashMap<>();
    statusCache.put("SCHEDULED", 0);
    statusCache.put("PICKEDUP", 1);
    statusCache.put("COMPLETED", 2);
    statusCache.put("ERROR", 3);
  }

  public Integer getStatusFromCache(String status) {
    return statusCache.get(status);
  }
  public void updateStatus(int botId, int appId, String status, RoutingContext routingContext) {
    if (getStatusFromCache(status) == null)
        routingContext.response().setStatusCode(400).end("Invalid Status!");
    else {
      client.close();
    }

  }
}
