package com.chektek.websocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A minimal WebSocket server implementation using raw Java NIO.
 * Implements RFC 6455 WebSocket protocol.
 */
public abstract class WebSocketServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServer.class);
	private static final String WEBSOCKET_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

	private final int port;
	private final Map<SocketChannel, WebSocketConnection> connections = new ConcurrentHashMap<>();
	private final AtomicBoolean running = new AtomicBoolean(false);
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	private ServerSocketChannel serverChannel;
	private Selector selector;

	public WebSocketServer(int port) {
		this.port = port;
	}

	public void start() throws IOException {
		if (running.getAndSet(true)) {
			return;
		}

		selector = Selector.open();
		serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);
		serverChannel.socket().setReuseAddress(true);
		serverChannel.socket().bind(new InetSocketAddress(port));
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);

		executor.submit(this::runLoop);
		onStart();
	}

	public void stop() throws IOException {
		running.set(false);

		for (WebSocketConnection conn : connections.values()) {
			try {
				conn.close();
			} catch (IOException e) {
				LOGGER.warn("Error closing connection: " + e.getMessage());
			}
		}
		connections.clear();

		if (selector != null) {
			selector.wakeup();
			selector.close();
		}
		if (serverChannel != null) {
			serverChannel.close();
		}
		executor.shutdown();
	}

	public int getPort() {
		return port;
	}

	public void broadcast(String message) {
		for (WebSocketConnection conn : connections.values()) {
			if (conn.isHandshakeComplete()) {
				try {
					conn.send(message);
				} catch (IOException e) {
					LOGGER.warn("Error broadcasting to client: " + e.getMessage());
				}
			}
		}
	}

	public Set<WebSocketConnection> getConnections() {
		return Set.copyOf(connections.values());
	}

	private void runLoop() {
		while (running.get()) {
			try {
				selector.select(1000);
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

				while (keys.hasNext()) {
					SelectionKey key = keys.next();
					keys.remove();

					if (!key.isValid()) {
						continue;
					}

					if (key.isAcceptable()) {
						accept(key);
					} else if (key.isReadable()) {
						read(key);
					}
				}
			} catch (IOException e) {
				if (running.get()) {
					LOGGER.error("Error in server loop: " + e.getMessage());
				}
			}
		}
	}

	private void accept(SelectionKey key) throws IOException {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel client = server.accept();
		client.configureBlocking(false);
		client.register(selector, SelectionKey.OP_READ);

		WebSocketConnection conn = new WebSocketConnection(client);
		connections.put(client, conn);
		LOGGER.info("Client connected: " + client.getRemoteAddress());
	}

	private void read(SelectionKey key) {
		SocketChannel client = (SocketChannel) key.channel();
		WebSocketConnection conn = connections.get(client);

		if (conn == null) {
			return;
		}

		ByteBuffer buffer = ByteBuffer.allocate(8192);
		int bytesRead;

		try {
			bytesRead = client.read(buffer);
		} catch (IOException e) {
			closeConnection(client, conn);
			return;
		}

		if (bytesRead == -1) {
			closeConnection(client, conn);
			return;
		}

		buffer.flip();
		byte[] data = new byte[buffer.remaining()];
		buffer.get(data);

		try {
			if (!conn.isHandshakeComplete()) {
				handleHandshake(conn, new String(data));
			} else {
				handleFrame(conn, data);
			}
		} catch (Exception e) {
			LOGGER.error("Error handling data: " + e.getMessage());
			onError(conn, e);
			closeConnection(client, conn);
		}
	}

	private void handleHandshake(WebSocketConnection conn, String request) throws IOException {
		String key = extractWebSocketKey(request);
		if (key == null) {
			throw new IOException("Invalid WebSocket handshake: missing Sec-WebSocket-Key");
		}

		String acceptKey = generateAcceptKey(key);
		String response = "HTTP/1.1 101 Switching Protocols\r\n" +
				"Upgrade: websocket\r\n" +
				"Connection: Upgrade\r\n" +
				"Sec-WebSocket-Accept: " + acceptKey + "\r\n\r\n";

		conn.getChannel().write(ByteBuffer.wrap(response.getBytes()));
		conn.setHandshakeComplete(true);
		onOpen(conn);
	}

	private String extractWebSocketKey(String request) {
		for (String line : request.split("\r\n")) {
			if (line.toLowerCase().startsWith("sec-websocket-key:")) {
				return line.substring(18).trim();
			}
		}
		return null;
	}

	private String generateAcceptKey(String key) {
		try {
			String combined = key + WEBSOCKET_GUID;
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[] hash = digest.digest(combined.getBytes());
			return Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-1 not available", e);
		}
	}

	private void handleFrame(WebSocketConnection conn, byte[] data) throws IOException {
		if (data.length < 2) {
			return;
		}

		int offset = 0;
		while (offset < data.length) {
			int frameStart = offset;

			if (data.length - offset < 2)
				break;

			int firstByte = data[offset] & 0xFF;
			int secondByte = data[offset + 1] & 0xFF;
			offset += 2;

			int opcode = firstByte & 0x0F;
			boolean masked = (secondByte & 0x80) != 0;
			int payloadLength = secondByte & 0x7F;

			if (payloadLength == 126) {
				if (data.length - offset < 2)
					break;
				payloadLength = ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
				offset += 2;
			} else if (payloadLength == 127) {
				if (data.length - offset < 8)
					break;
				payloadLength = 0;
				for (int i = 0; i < 8; i++) {
					payloadLength = (payloadLength << 8) | (data[offset + i] & 0xFF);
				}
				offset += 8;
			}

			byte[] maskKey = null;
			if (masked) {
				if (data.length - offset < 4)
					break;
				maskKey = new byte[4];
				System.arraycopy(data, offset, maskKey, 0, 4);
				offset += 4;
			}

			if (data.length - offset < payloadLength)
				break;

			byte[] payload = new byte[payloadLength];
			System.arraycopy(data, offset, payload, 0, payloadLength);
			offset += payloadLength;

			if (masked && maskKey != null) {
				for (int i = 0; i < payload.length; i++) {
					payload[i] ^= maskKey[i % 4];
				}
			}

			switch (opcode) {
				case 0x01: // Text frame
					onMessage(conn, new String(payload));
					break;
				case 0x08: // Close frame
					closeConnection(conn.getChannel(), conn);
					return;
				case 0x09: // Ping frame
					sendPong(conn, payload);
					break;
				case 0x0A: // Pong frame
					// Ignore pongs
					break;
			}
		}
	}

	private void sendPong(WebSocketConnection conn, byte[] payload) throws IOException {
		byte[] frame = createFrame(0x0A, payload);
		conn.getChannel().write(ByteBuffer.wrap(frame));
	}

	static byte[] createFrame(int opcode, byte[] payload) {
		int payloadLength = payload.length;
		byte[] frame;

		if (payloadLength <= 125) {
			frame = new byte[2 + payloadLength];
			frame[1] = (byte) payloadLength;
		} else if (payloadLength <= 65535) {
			frame = new byte[4 + payloadLength];
			frame[1] = 126;
			frame[2] = (byte) (payloadLength >> 8);
			frame[3] = (byte) payloadLength;
		} else {
			frame = new byte[10 + payloadLength];
			frame[1] = 127;
			for (int i = 0; i < 8; i++) {
				frame[9 - i] = (byte) (payloadLength >> (8 * i));
			}
		}

		frame[0] = (byte) (0x80 | opcode); // FIN + opcode
		System.arraycopy(payload, 0, frame, frame.length - payloadLength, payloadLength);
		return frame;
	}

	private void closeConnection(SocketChannel client, WebSocketConnection conn) {
		connections.remove(client);
		try {
			client.close();
		} catch (IOException e) {
			// Ignore
		}
		onClose(conn);
	}

	// Abstract methods for subclasses to implement
	public abstract void onStart();

	public abstract void onOpen(WebSocketConnection conn);

	public abstract void onClose(WebSocketConnection conn);

	public abstract void onMessage(WebSocketConnection conn, String message);

	public abstract void onError(WebSocketConnection conn, Exception ex);
}
