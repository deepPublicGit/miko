package com.miko.mikostore;

import com.miko.mikostore.model.AppList;
import com.miko.mikostore.model.EmailFormat;
import com.miko.mikostore.router.MainRouter;
import io.netty.util.internal.ResourcesUtil;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class MailVerticle extends AbstractVerticle {


  private MailClient mailClient;

  private final static  String apiPassword = System.getenv().getOrDefault("SENDGRID_PWD", "");


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("Starting MailVerticle");

    EventBus eventBus = vertx.eventBus();
    eventBus.consumer("send.email", this::sendEmail);

    MailConfig mailConfig = new MailConfig();
    mailConfig.setHostname("smtp.sendgrid.net");
    mailConfig.setPort(25);
    mailConfig.setUsername("apikey");
    mailConfig.setPassword(apiPassword);

    this.mailClient = MailClient.create(vertx, mailConfig);
    startPromise.complete();
  }

  private void sendEmail(Message<EmailFormat> objectMessage) {
    System.out.println("send.email received! " + objectMessage.body());
//    JsonObject event = new JsonObject(objectMessage.body());
    EmailFormat email = objectMessage.body();
    MailMessage message = new MailMessage();
    message.setFrom(email.getFrom());
    message.setTo(email.getTo());
    message.setSubject(email.getSubject());
    message.setText("");
    message.setHtml(String.format(getHtml(), email.getOptionalDetails().get("appId"),
      email.getOptionalDetails().get("botId"), email.getOptionalDetails().get("status"),
      email.getOptionalDetails().get("timeStamp"), email.getBody()
    ));
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

  private String getHtml() {
    try (InputStream in = getClass().getResourceAsStream("/email_template.html")) {
      if(in != null) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
          return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
      }
    } catch (Exception e) {
      System.out.println("Error reading file: " + e.getMessage());
    }
    return "";
  }

}
