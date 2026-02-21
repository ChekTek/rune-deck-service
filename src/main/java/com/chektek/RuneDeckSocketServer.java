package com.chektek;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chektek.payload.Payload;
import com.chektek.payload.PayloadType;
import com.chektek.websocket.WebSocketConnection;
import com.chektek.websocket.WebSocketServer;
import com.google.gson.Gson;

import net.runelite.client.plugins.Plugin;

public class RuneDeckSocketServer extends WebSocketServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(RuneDeckSocketServer.class);

	private final Gson gson;
	private final PluginControlService pluginControlService;

	private PayloadCache payloadCache = PayloadCache.getInstance();

	public RuneDeckSocketServer(int port, Gson gson, PluginControlService pluginControlService) {
		super(port);
		this.gson = gson;
		this.pluginControlService = pluginControlService;
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
		String messageType = null;
		try {
			Message message = this.gson.fromJson(messageString, Message.class);

			if (message == null || message.messageType == null) {
				return;
			}

			messageType = message.messageType;

			if (message.messageType.equals("clearCache")) {
				payloadCache.clearCache();
			}

			if (message.messageType.equals("getPlugins")) {
				broadcastPluginList();
			}

			if (message.messageType.equals("togglePlugin")) {
				if (message.pluginId != null && message.isActive != null) {
					pluginControlService.togglePlugin(message.pluginId, message.isActive);
				} else {
					LOGGER.warn("togglePlugin request missing pluginId or isActive");
				}
			}

		} catch (Exception e) {
			LOGGER.warn("Failed to handle websocket message. rawMessage={}, messageType={}", messageString, messageType, e);
		}
	}

	public void broadcastPluginList() {
		List<PluginSummary> plugins = pluginControlService.getPluginSummaries();
		broadcast(this.gson.toJson(Map.of("type", PayloadType.PLUGINS, "plugins", plugins)));
	}

	public void broadcastPluginChange(Plugin plugin, boolean isActive) {
		PluginSummary pluginSummary = pluginControlService.getPluginSummary(plugin, isActive);
		if (pluginSummary == null) {
			return;
		}

		broadcast(this.gson.toJson(Map.of("type", PayloadType.PLUGIN_CHANGED, "plugin", pluginSummary)));
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
