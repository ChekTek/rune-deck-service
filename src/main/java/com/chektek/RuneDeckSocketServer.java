package com.chektek;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chektek.payload.Payload;
import com.google.gson.Gson;

public class RuneDeckSocketServer extends WebSocketServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(RuneDeckSocketServer.class);

	private final Gson GSON = new Gson();
	private PayloadCache payloadCache = PayloadCache.getInstance();

	public RuneDeckSocketServer(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
	}

	public void broadcast(Payload payload) {
		String payloadJSON = this.GSON.toJson(payload);
		super.broadcast(payloadJSON);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		LOGGER.info("Client connected: " + conn.getRemoteSocketAddress());
		payloadCache.clearCache();
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		payloadCache.clearCache();
		LOGGER.info("RuneDeckSocketServer closed connection");
	}

	@Override
	public void onMessage(WebSocket conn, String messageString) {

		try {
			Message message = this.GSON.fromJson(messageString, Message.class);

			if (message.messageType.equals("clearCache")) {
				payloadCache.clearCache();
			}

		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
		}

	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		payloadCache.clearCache();
		LOGGER.error(ex.getMessage());
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a specific
			// websocket
		}
	}

	@Override
	public void onStart() {
		payloadCache.clearCache();
		this.setConnectionLostTimeout(60);
		LOGGER.info("RuneDeckSocketServer started on port: " + this.getPort());
	}

}
