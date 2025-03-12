package com.miko.mikostore.router;

import com.miko.mikostore.model.AppList;
import com.miko.mikostore.model.EmailFormat;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class MainRouter {

  private static EventBus emailEventBus;

  public static Router createRouter(Vertx vertx) {
    emailEventBus = vertx.eventBus();

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.route("/api/status/:id").handler(MainRouter::validateId);

    router.get("/api/schedule").handler(MainRouter::getSchedule);
    router.get("/api/sendEmail").handler(MainRouter::sendEmail);

    router.put("/api/status/:id").handler(MainRouter::updateStatus);
    router.route("/*").handler(routingContext -> routingContext.response()
      .setStatusCode(404)
      .end("No Route Found"));

    return router;
  }

  private static void sendEmail(RoutingContext routingContext) {
    emailEventBus
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

  private static void updateStatus(RoutingContext routingContext) {

  }


  private static void getSchedule(RoutingContext routingContext) {
    routingContext.response()
      .setStatusCode(200)
      .end(Json.encode(getDummyAppList()));
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
    emailFormat.setTo("yolo@yolo.com");
    emailFormat.setFrom("yolo@yolo.com");
    emailFormat.setSubject("YOLO");
    emailFormat.setBody(getDummyAppList().toString());
    emailFormat.setCc("yolo@yolo.com");
    return Json.encode(emailFormat);
  }

  private static void validateId(RoutingContext ctx) {

  }
}
