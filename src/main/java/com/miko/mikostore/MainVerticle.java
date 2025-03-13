package com.miko.mikostore;

import com.miko.mikostore.repository.DBRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;


public class MainVerticle extends AbstractVerticle {


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(new MailVerticle());
//    vertx.deployVerticle(new DBRepository());
    vertx.deployVerticle(new RouterVerticle());
/*    Future<String> mailDeploy =

    if (mailDeploy.isComplete()){
      Future<String> routerDeploy = );
      if (routerDeploy.isComplete()){
        System.out.println("Mail & Router Deployed");
        startPromise.complete();
      } else {
        System.out.println("Mail & Router Failed");

        startPromise.fail(routerDeploy.cause());
      }
    }
    else if (mailDeploy.failed()){
      startPromise.fail(mailDeploy.cause());
    }*/
  }

}
