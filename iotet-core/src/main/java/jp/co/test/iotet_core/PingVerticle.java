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
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

/*
This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 */
public class PingVerticle extends Verticle {

  public void start() {
	final Logger logger = container.logger();
	
	
	container.deployModule("com.bloidonia.jdbc-persistor-v2.1", new Handler<AsyncResult<String>>() {
		@Override
		public void handle(AsyncResult<String> msg) {
			logger.info("connected!");
		}
	});

	JsonObject config = container.config();
	config.putString("address", "com.bloidonia.jdbcpersistor");
	config.putString("url", "jdbc:mysql://localhost:3306/vertx_test");
	config.putString("username", "root");
	config.putString("password", "");
	
	
	EventBus eventBus = vertx.eventBus();
	
	JsonObject obj = new JsonObject()
		.putString("action", "select")
		.putString("stmt", "select * from test");

	logger.info("send msg");
	eventBus.send("com.bloidonia.jdbcpersistor", obj, new Handler<Message>() {
		@Override
		public void handle(Message msg) {
			logger.info("response!");
			logger.info(msg.body());
		}
	});

    vertx.eventBus().registerHandler("save-user", new Handler<Message<String>>() {
      @Override
      public void handle(Message<String> message) {
        message.reply("pong!");
        container.logger().info("Sent back pong");
      }
    });

    container.logger().info("PingVerticle started");

  }
}
