package com.example.story_teller;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TtsWebsocket {
    private static final String TAG = "TtsWebsocket";
    private static final String API_URL = "wss://openspeech.bytedance.com/api/v1/tts/ws_binary";
    private static final String ACCESS_TOKEN = "onYaEKVb5IFO7r1Q7yFMz9YawZCSw27t";
    private final Context context;
    private final Gson gson = new Gson();
    private TtsWebSocketClient client;

    public interface TtsCallback {
        void onTtsStart();

        void onTtsComplete();

        void onTtsError(String error);
    }

    private TtsCallback ttsCallback;

    public void setTtsCallback(TtsCallback callback) {
        this.ttsCallback = callback;
    }

    public TtsWebsocket(TtsRequest request, Context context) {
        this.context = context;
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer; " + ACCESS_TOKEN);
        client = new TtsWebSocketClient(URI.create(API_URL), headers, request);
    }

    public void execute() {
        client.connect();
    }

    private class TtsWebSocketClient extends WebSocketClient {
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        private final TtsRequest request;
        private MediaPlayer mediaPlayer;

        public TtsWebSocketClient(URI serverUri, Map<String, String> headers, TtsRequest request) {
            super(serverUri, headers);
            this.request = request;
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.d(TAG, "WebSocket Connected");
            try {
                String json = gson.toJson(request);
                byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
                byte[] header = { 0x11, 0x10, 0x10, 0x00 };
                ByteBuffer requestByte = ByteBuffer.allocate(8 + jsonBytes.length);
                requestByte.put(header).putInt(jsonBytes.length).put(jsonBytes);
                send(requestByte.array());
            } catch (Exception e) {
                Log.e(TAG, "Error sending request", e);
                close();
            }
        }

        @Override
        public void onMessage(ByteBuffer bytes) {
            try {
                int protocolVersion = (bytes.get(0) & 0xff) >> 4;
                int headerSize = bytes.get(0) & 0x0f;
                int messageType = (bytes.get(1) & 0xff) >> 4;
                int messageTypeSpecificFlags = bytes.get(1) & 0x0f;
                int serializationMethod = (bytes.get(2) & 0xff) >> 4;
                int messageCompression = bytes.get(2) & 0x0f;
                int reserved = bytes.get(3) & 0xff;
                bytes.position(headerSize * 4);

                byte[] fourByte = new byte[4];
                if (messageType == 11) {
                    // Audio-only server response
                    Log.d(TAG, "Received audio-only response");
                    if (messageTypeSpecificFlags == 0) {
                        // Ack without audio data
                        return;
                    }

                    bytes.get(fourByte, 0, 4);
                    int sequenceNumber = new BigInteger(fourByte).intValue();
                    bytes.get(fourByte, 0, 4);
                    int payloadSize = new BigInteger(fourByte).intValue();
                    byte[] payload = new byte[payloadSize];
                    bytes.get(payload, 0, payloadSize);
                    buffer.write(payload);

                    if (sequenceNumber < 0) {
                        // 收到最后一段数据
                        String audioFile = context.getCacheDir() + "/tts_" + System.currentTimeMillis() + ".mp3";
                        try (FileOutputStream fos = new FileOutputStream(audioFile)) {
                            fos.write(buffer.toByteArray());
                            playAudio(audioFile);
                        }
                    }
                } else if (messageType == 15) {
                    // Error message from server
                    bytes.get(fourByte, 0, 4);
                    int code = new BigInteger(fourByte).intValue();
                    bytes.get(fourByte, 0, 4);
                    int messageSize = new BigInteger(fourByte).intValue();
                    byte[] messageBytes = new byte[messageSize];
                    bytes.get(messageBytes, 0, messageSize);
                    String errorMessage = new String(messageBytes, StandardCharsets.UTF_8);
                    Log.e(TAG, "Server error: " + errorMessage);
                    if (ttsCallback != null) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            ttsCallback.onTtsError(errorMessage);
                        });
                    }
                    close();
                } else {
                    Log.w(TAG, "Received unknown message type: " + messageType);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing message", e);
                close();
            }
        }

        private void playAudio(String audioFile) {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(audioFile);
                mediaPlayer.prepare();

                if (ttsCallback != null) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        ttsCallback.onTtsStart();
                    });
                }

                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> {
                    mp.release();
                    new File(audioFile).delete();
                    if (ttsCallback != null) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            ttsCallback.onTtsComplete();
                        });
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error playing audio", e);
                if (ttsCallback != null) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        ttsCallback.onTtsError(e.getMessage());
                    });
                }
            }
        }

        @Override
        public void onMessage(String message) {
            Log.d(TAG, "Received text message: " + message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.d(TAG, "Connection closed: " + reason);
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

        @Override
        public void onError(Exception ex) {
            Log.e(TAG, "WebSocket error", ex);
            close();
        }
    }
}