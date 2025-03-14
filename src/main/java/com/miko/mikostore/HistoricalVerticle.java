package com.miko.mikostore;

import com.miko.mikostore.model.EmailFormat;
import com.miko.mikostore.model.StatusModel;
import com.miko.mikostore.repository.DBRepository;
import com.miko.mikostore.service.HistoryService;
import com.miko.mikostore.service.ScheduleService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.web.RoutingContext;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class HistoricalVerticle extends AbstractVerticle {

  private static HistoryService historyService;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("Starting HistoricalVerticle");
    historyService = new HistoryService(DBRepository.getSqlClient(vertx));
    EventBus eventBus = vertx.eventBus();

    eventBus.consumer("send.historical", this::saveHistory);

    startPromise.complete();
  }

  private void saveHistory(Message<String> objectMessage) {
    System.out.println("send.historical received! " + objectMessage.body());
    JsonObject event = new JsonObject(objectMessage.body());

    StatusModel statusModel = new StatusModel();
    statusModel.setStatus(event.getString("status"));
    statusModel.setStatusEnum(event.getInteger("statusEnum"));
    statusModel.setAppId(event.getInteger("appId"));
    statusModel.setBotId(event.getInteger("botId"));
    statusModel.setDateUpdated(event.getString("dateUpdated"));
    historyService.insertStatus(statusModel);
  }

}
