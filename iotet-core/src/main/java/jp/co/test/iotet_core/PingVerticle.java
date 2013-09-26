package jp.co.test.iotet_core;
/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

/*
This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 */
public class PingVerticle extends Verticle {

  public void start() {
	final Logger logger = container.logger();
	

	JsonObject jdbcConfig = new JsonObject()
		.putString("address", "jdbc.test")
		.putString("url", "jdbc:mysql://localhost:3306/vertx_test")
		.putString("username", "root")
		.putString("driver", "com.mysql.jdbc.Driver")
		.putString("password", "");
	container.deployModule("com.bloidonia~mod-jdbc-persistor~2.1", jdbcConfig, new Handler<AsyncResult<String>>() {
		@Override
		public void handle(AsyncResult<String> msg) {
			logger.info(msg.succeeded());
			logger.info(msg.result());
			logger.info("connected!");
		}
	});
	
	HttpServer httpServer = vertx.createHttpServer();
	httpServer.requestHandler(new Handler<HttpServerRequest>() {
		@Override
		public void handle(HttpServerRequest request) {
			EventBus eventBus = vertx.eventBus();

			
			if(request.path().equals("/test")) {
				logger.info("okkkkkkkkkkkkkkkkkkkkkkkk");
			} else if(request.path().equals("/save")) {
				JsonObject obj = new JsonObject()
				.putString("action", "select")
				.putString("stmt", "select * from test");

				logger.info("send query");
				eventBus.send("jdbc.test", obj, new Handler<Message>() {
					@Override
					public void handle(Message msg) {
						logger.info("response!");
						logger.info(msg.body());
					}
				});
			}
			
			request.response().end();
		}
	}).listen(1234, "localhost");

	
	



    container.logger().info("PingVerticle started");

  }
}
