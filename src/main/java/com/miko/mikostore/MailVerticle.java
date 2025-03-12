package com.miko.mikostore;

import com.miko.mikostore.model.AppList;
import com.miko.mikostore.router.MainRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;

public class MailVerticle extends AbstractVerticle {


  private MailClient mailClient;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("Starting MailVerticle");
    EventBus eventBus = vertx.eventBus();
    eventBus.consumer("send.email", this::sendEmail);
    MailConfig mailConfig = new MailConfig();
    mailConfig.setHostname("smtp.sendgrid.net");
    mailConfig.setPort(25);

    mailConfig.setHostname(config().getString("mail.host"));
    mailConfig.setPort(config().getInteger("mail.port"));
//    mailConfig.setStarttls(StartTLSOptions.REQUIRED);
    mailConfig.setUsername(config().getString("mail.username"));
    mailConfig.setPassword(config().getString("mail.password"));

    this.mailClient = MailClient.create(vertx, mailConfig);
    startPromise.complete();
  }

  private void sendEmail(Message<String> objectMessage) {
    System.out.println("send.email recieved! " + objectMessage.body());
    JsonObject event = new JsonObject(objectMessage.body());

    MailMessage message = new MailMessage();
    message.setFrom(event.getString("from"));
    message.setTo(event.getString("to"));
    message.setCc(event.getString("cc"));
    message.setSubject(event.getString("subject"));
    message.setText(event.getString("body"));
    message.setHtml(String.format("%s <a href=\"http://vertx.io\">vertx.io</a>", event.getString("body")));
    System.out.println("Email" + message);
    mailClient.sendMail(message, mailResultAsyncResult -> {
      if (mailResultAsyncResult.succeeded()) {
        System.out.println("Mail sent");
        objectMessage.reply("Mail sent!");
      } else {
        System.out.println("Mail not sent");
        objectMessage.fail(0, mailResultAsyncResult.cause().getMessage());
      }
    });
  }

}
