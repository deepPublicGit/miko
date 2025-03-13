package com.miko.mikostore;

import com.miko.mikostore.model.EmailFormat;
import com.miko.mikostore.model.StatusModel;
import com.miko.mikostore.service.HistoryService;
import com.miko.mikostore.service.ScheduleService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.mail.MailMessage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class HistoricalVerticle extends AbstractVerticle {

  private static HistoryService historyService;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("Starting HistoricalVerticle");
    historyService = new HistoryService()
    EventBus eventBus = vertx.eventBus();

    eventBus.consumer("send.historical", this::saveHistory);

    startPromise.complete();
  }

  private void saveHistory(Message<StatusModel> objectMessage) {
    System.out.println("send.email received! " + objectMessage.body());
    StatusModel email = objectMessage.body();

  }

}
