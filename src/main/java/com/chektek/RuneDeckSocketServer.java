package com.chektek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chektek.payload.Payload;
import com.chektek.websocket.WebSocketConnection;
import com.chektek.websocket.WebSocketServer;
import com.google.gson.Gson;

public class RuneDeckSocketServer extends WebSocketServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(RuneDeckSocketServer.class);

	private final Gson gson;
	private PayloadCache payloadCache = PayloadCache.getInstance();

	public RuneDeckSocketServer(int port, Gson gson) {
		super(port);
		this.gson = gson;
	}

	public void broadcast(Payload payload) {
		String payloadJSON = this.gson.toJson(payload);
		super.broadcast(payloadJSON);
	}

	@Override
	public void onOpen(WebSocketConnection conn) {
		try {
			LOGGER.info("Client connected: " + conn.getRemoteAddress());
		} catch (Exception e) {
			LOGGER.info("Client connected");
		}
		payloadCache.clearCache();
	}

	@Override
	public void onClose(WebSocketConnection conn) {
		payloadCache.clearCache();
		LOGGER.info("RuneDeckSocketServer closed connection");
	}

	@Override
	public void onMessage(WebSocketConnection conn, String messageString) {
		try {
			Message message = this.gson.fromJson(messageString, Message.class);

			if (message.messageType.equals("clearCache")) {
				payloadCache.clearCache();
			}

		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
		}
	}

	@Override
	public void onError(WebSocketConnection conn, Exception ex) {
		payloadCache.clearCache();
		LOGGER.error(ex.getMessage());
	}

	@Override
	public void onStart() {
		payloadCache.clearCache();
		LOGGER.info("RuneDeckSocketServer started on port: " + this.getPort());
	}

}
