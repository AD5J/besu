package tech.pegasys.pantheon.tests.acceptance.dsl.pubsub;

import static org.assertj.core.api.Assertions.assertThat;

import tech.pegasys.pantheon.tests.acceptance.dsl.WaitUtils;
import tech.pegasys.pantheon.tests.acceptance.dsl.node.PantheonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;

public class WebSocketConnection {

  private final RequestOptions options;
  private final ConcurrentLinkedDeque<SubscriptionEvent> subscriptionEvents;

  private volatile String error;
  private volatile boolean receivedResponse;
  private volatile JsonRpcSuccessEvent latestEvent;
  private volatile WebSocket connection;

  public WebSocketConnection(final Vertx vertx, final PantheonNode node) {
    if (!node.jsonRpcWebSocketPort().isPresent()) {
      throw new IllegalStateException(
          "Can't start websocket connection for node with RPC disabled");
    }
    subscriptionEvents = new ConcurrentLinkedDeque<>();
    options = new RequestOptions();
    options.setPort(node.jsonRpcWebSocketPort().get());
    options.setHost(node.getHost());

    connect(vertx);
  }

  public JsonRpcSuccessEvent subscribe(final String params) {
    resetLatestResult();
    return send(
        String.format("{\"id\": 1, \"method\": \"eth_subscribe\", \"params\": [\"%s\"]}", params));
  }

  public JsonRpcSuccessEvent unsubscribe(final Subscription subscription) {
    resetLatestResult();
    return send(
        String.format(
            "{\"id\": 2, \"method\": \"eth_unsubscribe\", \"params\": [\"%s\"]}", subscription));
  }

  private JsonRpcSuccessEvent send(final String json) {

    connection.writeBinaryMessage(Buffer.buffer(json));

    WaitUtils.waitFor(() -> assertThat(receivedResponse).isEqualTo(true));

    assertThat(latestEvent)
        .as(
            "Expecting a JSON-RPC success response to message: %s, instead received: %s",
            json, error)
        .isNotNull();

    return latestEvent;
  }

  private void connect(final Vertx vertx) {
    vertx
        .createHttpClient(new HttpClientOptions())
        .websocket(
            options,
            websocket -> {
              webSocketConnection(websocket);

              websocket.handler(
                  data -> {
                    try {
                      final WebSocketEvent eventType = Json.decodeValue(data, WebSocketEvent.class);

                      if (eventType.isSubscription()) {
                        success(Json.decodeValue(data, SubscriptionEvent.class));
                      } else {
                        success(Json.decodeValue(data, JsonRpcSuccessEvent.class));
                      }

                    } catch (final DecodeException e) {
                      error(data.toString());
                    }
                  });
            });

    WaitUtils.waitFor(() -> assertThat(connection).isNotNull());
  }

  private void webSocketConnection(final WebSocket connection) {
    this.connection = connection;
  }

  private void resetLatestResult() {
    this.receivedResponse = false;
    this.error = null;
    this.latestEvent = null;
  }

  private void error(final String response) {
    this.receivedResponse = true;
    this.error = response;
  }

  private void success(final JsonRpcSuccessEvent result) {
    this.receivedResponse = true;
    this.latestEvent = result;
  }

  private void success(final SubscriptionEvent result) {
    this.receivedResponse = true;
    this.subscriptionEvents.add(result);
  }

  public List<SubscriptionEvent> getSubscriptionEvents() {
    return new ArrayList<>(subscriptionEvents);
  }
}
