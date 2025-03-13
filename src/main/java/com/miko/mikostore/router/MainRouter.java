package com.miko.mikostore.router;

import com.miko.mikostore.model.AppList;
import com.miko.mikostore.model.EmailFormat;
import com.miko.mikostore.model.StatusModel;
import com.miko.mikostore.service.ScheduleService;
import com.miko.mikostore.service.StatusService;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.SqlClient;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class MainRouter {

  private static EventBus eventBus;
  private static ScheduleService scheduleService;
  private static StatusService statusService;

  public static Router createRouter(Vertx vertx, SqlClient sqlClient) {
    eventBus = vertx.eventBus();
//    SqlClient sqlClient = (SqlClient) vertx.sharedData().getLocalMap("db").get("sqlClient");
    scheduleService = new ScheduleService(sqlClient);
    statusService = new StatusService(sqlClient);

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.route("/api/status/:botId").handler(MainRouter::validateStatusRoute);
    router.route("/api/schedule/:botId").handler(MainRouter::validateBotId);

    router.get("/api/schedule/:botId").handler(MainRouter::getSchedule);
    router.put("/api/status/:botId").handler(MainRouter::updateStatus);
    router.get("/api/sendEmail").handler(MainRouter::sendEmail);

    router.route("/*").handler(routingContext -> routingContext.response()
      .setStatusCode(404)
      .end("No Route Found"));

    return router;
  }

  private static void validateBotId(RoutingContext routingContext) {
    try {
      routingContext.put("botId", Integer.parseInt(routingContext.pathParam("botId")));
      routingContext.next();
    } catch (NumberFormatException e) {
      routingContext.response()
        .setStatusCode(400)
        .end("Bad Request: Invalid BotId");
    }
  }

  private static void validateStatusRoute(RoutingContext routingContext) {
    try {
      routingContext.put("botId", Integer.parseInt(routingContext.pathParam("botId")));
      routingContext.put("appId", Integer.parseInt(routingContext.request().getParam("appId")));
      routingContext.put("status", routingContext.request().getParam("status"));
      routingContext.next();
    } catch (NumberFormatException e) {
      routingContext.response()
        .setStatusCode(400)
        .end("Bad Request: Invalid request params!");
    }
  }


  private static void getSchedule(RoutingContext routingContext) {
    int botId = routingContext.get("botId");
    scheduleService.getSchedule(botId, routingContext);
  }


  private static void updateStatus(RoutingContext routingContext) {
    int botId = routingContext.get("botId");
    String status = routingContext.get("status");
    int appId = routingContext.get("appId");
    StatusModel statusModel = new StatusModel();
    statusModel.setStatus(status);
    statusModel.setBotId(botId);
    statusModel.setAppId(appId);
    statusModel.setDateUpdated(LocalDateTime.now(ZoneId.of("UTC")).toString());
    statusService.updateStatus(statusModel, eventBus, routingContext);
  }

  private static void sendEmail(RoutingContext routingContext) {
    eventBus
      .request("send.email", getEmailHeaders())
      .onComplete(ar -> {
        if (ar.succeeded()) {
          System.out.printf("Email sent: %s\n", ar.result().body());
          routingContext.response().setStatusCode(200).end();
        } else {
          System.out.printf("Email failed: %s\n", ar.cause().getMessage());
          routingContext.response().setStatusCode(500).end("Internal Server Error");
        }
      });
  }

  private static AppList getDummyAppList() {
    AppList appList = new AppList();
    appList.setAppName("App 1");
    appList.setAppUrl("https://mikostore.com/app/app1");
    appList.setVersion("2.4");
    appList.setDateAdded(LocalDateTime.now(ZoneId.of("UTC")).toString());
    appList.setDateUpdated(LocalDateTime.now(ZoneId.of("UTC")).toString());
    appList.setAppId(appList.getAppName() + appList.getDateAdded());
    return appList;
  }

  private static String getEmailHeaders() {
    EmailFormat emailFormat = new EmailFormat();
    emailFormat.setFrom("pradeepkg41199@gmail.com");
    emailFormat.setTo("pg903@snu.edu.in");
    emailFormat.setSubject("Error Event - App Installation");
    emailFormat.setBody(getDummyAppList().toString());
    return Json.encode(emailFormat);
  }

}
