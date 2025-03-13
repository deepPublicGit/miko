package com.miko.mikostore.service;

import com.miko.mikostore.model.EmailFormat;
import com.miko.mikostore.model.StatusModel;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;

public class HistoryService {

  private final SqlClient client;

  private final String HISTORY_INSERT_QUERY = """
    INSERT INTO MIKO.HISTORY_STATUS (BOT_ID, APP_ID, STATUS, DATE_UPDATED) VALUES ($1, $2, $3, $4)
    """;

  public HistoryService(SqlClient sqlClient) {
    client = sqlClient;
  }

  public void insertStatus(StatusModel statusModel) {
//    System.out.println("Client + " + client);

    client.preparedQuery(HISTORY_INSERT_QUERY)
      .execute(Tuple.of(statusModel.getBotId(), statusModel.getAppId(),
        statusModel.getStatusEnum(), LocalDateTime.parse(statusModel.getDateUpdated())))
      .onComplete(ar -> {
        if (ar.succeeded()) {
          System.out.println("Status inserted! Bot ID: " + statusModel.getBotId());
        } else {
          ar.cause().printStackTrace();
          System.out.println("Status insertion failed! Bot ID: " + statusModel.getBotId());
        }
      });
  }

}
