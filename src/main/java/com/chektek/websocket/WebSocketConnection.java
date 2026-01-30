package com.chektek.websocket;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Represents a single WebSocket connection to a client.
 */
public class WebSocketConnection {

    private final SocketChannel channel;
    private boolean handshakeComplete = false;

    public WebSocketConnection(SocketChannel channel) {
        this.channel = channel;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public boolean isHandshakeComplete() {
        return handshakeComplete;
    }

    public void setHandshakeComplete(boolean complete) {
        this.handshakeComplete = complete;
    }

    public SocketAddress getRemoteAddress() throws IOException {
        return channel.getRemoteAddress();
    }

    public void send(String message) throws IOException {
        byte[] payload = message.getBytes();
        byte[] frame = WebSocketServer.createFrame(0x01, payload); // 0x01 = text frame
        channel.write(ByteBuffer.wrap(frame));
    }

    public void close() throws IOException {
        // Send close frame
        byte[] closeFrame = WebSocketServer.createFrame(0x08, new byte[0]);
        try {
            channel.write(ByteBuffer.wrap(closeFrame));
        } catch (IOException e) {
            // Ignore - connection may already be closed
        }
        channel.close();
    }

    public boolean isOpen() {
        return channel.isOpen() && handshakeComplete;
    }
}
