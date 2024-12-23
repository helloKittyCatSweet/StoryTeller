package com.example.story_teller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import okhttp3.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import android.util.Log;
import android.util.Base64;
import android.content.Context;
import io.flutter.plugin.common.MethodChannel;

public class BigModelNew extends WebSocketListener {
    // 地址与鉴权信息 wss://spark-api-n.xf-yun.com/v1.1/chat 1.5地址
    // 地址与鉴权信息 wss://spark-api-n.xf-yun.com/v3.1/chat 3.0地址
    public static final String hostUrl = "wss://maas-api.cn-huabei-1.xf-yun.com/v1.1/chat";
    public static final String appid = "xxx";
    public static final String apiSecret = "xxx";
    public static final String apiKey = "xxx";
    public static final String patch_id = "xxx"; // 调用微调大模型时必传对应resourceId

    public static List<RoleContent> historyList = new ArrayList<>(); // 对话历史存储集合

    public static String totalAnswer = ""; // 大模型的答案汇总

    public static String NewQuestion = "";

    public static final Gson gson = new Gson();

    // 个性化参数
    private String userId;
    private Boolean wsCloseFlag;
    private Context context;
    private final MethodChannel channel;

    private static Boolean totalFlag = true; // 控制提示用户是否输入
    // 构造函数

    public BigModelNew(String userId, Boolean wsCloseFlag, Context context, MethodChannel channel) {
        this.userId = userId;
        this.wsCloseFlag = wsCloseFlag;
        this.context = context;
        this.channel = channel;
    }

    // 添加发送消息的线程类
    private class MyThread extends Thread {
        private WebSocket webSocket;

        public MyThread(WebSocket webSocket) {
            this.webSocket = webSocket;
        }

        @Override
        public void run() {
            try {
                JSONObject requestJson = new JSONObject();
                String[] arr = new String[1];
                arr[0] = patch_id;

                JSONObject header = new JSONObject();
                header.put("app_id", appid);
                header.put("uid", "1234");
                header.put("patch_id", arr);

                JSONObject parameter = new JSONObject();
                JSONObject chat = new JSONObject();
                chat.put("domain", "xxx"); // 微调模型，固定为patch
                chat.put("temperature", 0.5);
                chat.put("max_tokens", 2048);
                parameter.put("chat", chat);

                JSONObject payload = new JSONObject();
                JSONObject message = new JSONObject();
                JSONArray text = new JSONArray();

                // 历史问答对
                if (historyList.size() > 0) {
                    for (RoleContent tempRoleContent : historyList) {
                        text.add(JSON.toJSON(tempRoleContent));
                    }
                }

                // 最新问题
                RoleContent roleContent = new RoleContent();
                roleContent.role = "user";
                roleContent.content = NewQuestion;
                text.add(JSON.toJSON(roleContent));
                historyList.add(roleContent);

                message.put("text", text);
                payload.put("message", message);

                requestJson.put("header", header);
                requestJson.put("parameter", parameter);
                requestJson.put("payload", payload);

                System.err.println(requestJson);
                webSocket.send(requestJson.toString());
            } catch (Exception e) {
                Log.e("BigModelNew", "Error in MyThread", e);
            }
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        System.out.print("大模型：");
        MyThread myThread = new MyThread(webSocket);
        myThread.start();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            JsonParse myJsonParse = gson.fromJson(text, JsonParse.class);
            if (myJsonParse.header.code != 0) {
                Log.e("BigModelNew", "Error code: " + myJsonParse.header.code + ", sid: " + myJsonParse.header.sid);
                webSocket.close(1000, "");
                return;
            }

            List<Text> textList = myJsonParse.payload.choices.text;
            StringBuilder currentResponse = new StringBuilder();
            for (Text temp : textList) {
                Log.d("BigModelNew", "Response content: " + temp.content);
                currentResponse.append(temp.content);
                totalAnswer = totalAnswer + temp.content;

                // 在主线程上通知 Flutter 端部分响应
                final String response = totalAnswer;
                if (onMessageCallback != null) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        onMessageCallback.onPartialResponse(response);
                    });
                }
            }

            if (myJsonParse.header.status == 2) {
                Log.d("BigModelNew", "Complete response received");
                if (canAddHistory()) {
                    RoleContent roleContent = new RoleContent();
                    roleContent.setRole("assistant");
                    roleContent.setContent(totalAnswer);
                    historyList.add(roleContent);
                } else {
                    historyList.remove(0);
                    RoleContent roleContent = new RoleContent();
                    roleContent.setRole("assistant");
                    roleContent.setContent(totalAnswer);
                    historyList.add(roleContent);
                }

                // 在主线程上通知 Flutter 端完整响应，并启动 TTS
                final String finalResponse = totalAnswer;
                if (onMessageCallback != null) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        onMessageCallback.onCompleteResponse(finalResponse);
                        try {
                            TtsRequest ttsRequest = new TtsRequest();
                            ttsRequest.setText(finalResponse);
                            TtsWebsocket ttsWebsocket = new TtsWebsocket(ttsRequest, context);
                            ttsWebsocket.setTtsCallback(new TtsWebsocket.TtsCallback() {
                                @Override
                                public void onTtsStart() {
                                    channel.invokeMethod("onTtsStart", null);
                                }

                                @Override
                                public void onTtsComplete() {
                                    channel.invokeMethod("onTtsComplete", null);
                                }

                                @Override
                                public void onTtsError(String error) {
                                    channel.invokeMethod("onTtsError", error);
                                }
                            });
                            ttsWebsocket.execute();
                        } catch (Exception e) {
                            Log.e("BigModelNew", "TTS Error: ", e);
                            channel.invokeMethod("onTtsError", e.getMessage());
                        }
                    });
                }

                wsCloseFlag = true;
                totalFlag = true;
            }
        } catch (Exception e) {
            Log.e("BigModelNew", "Error processing message", e);
            webSocket.close(1000, "Error: " + e.getMessage());
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        try {
            if (null != response) {
                int code = response.code();
                System.out.println("onFailure code:" + code);
                System.out.println("onFailure body:" + response.body().string());
                if (101 != code) {
                    System.out.println("connection failed");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 鉴权方法
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {

        String newUrl = hostUrl.toString().replace("ws://", "http://").replace("wss://", "https://");
        URL url = new URL(newUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        // System.err.println(preStr);
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // 使用 Android 的 Base64
        String sha = android.util.Base64.encodeToString(hexDigits, Base64.NO_WRAP);
        // System.err.println(sha);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"",
                apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder()
                .//
                addQueryParameter("authorization",
                        android.util.Base64.encodeToString( // 这里也使用 Android 的 Base64
                                authorization.getBytes(StandardCharsets.UTF_8),
                                Base64.NO_WRAP))
                .//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();

        // System.err.println(httpUrl.toString());
        return httpUrl.toString();
    }

    // 返回的json结果拆解
    class JsonParse {
        Header header;
        Payload payload;
    }

    class Header {
        int code;
        int status;
        String sid;
    }

    class Payload {
        Choices choices;
    }

    class Choices {
        List<Text> text;
    }

    class Text {
        String role;
        String content;
    }

    class RoleContent {
        String role;
        String content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    // 修改 sendMessage 方法，使用异步方式
    public void sendMessage(String message) {
        new Thread(() -> {
            try {
                NewQuestion = message;
                wsCloseFlag = false;
                totalAnswer = "";

                String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build();
                String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
                Request request = new Request.Builder().url(url).build();
                WebSocket webSocket = client.newWebSocket(request, this);

                while (!wsCloseFlag) {
                    Thread.sleep(200);
                }
            } catch (Exception e) {
                Log.e("BigModelNew", "Error: ", e);
                if (onMessageCallback != null) {
                    onMessageCallback.onCompleteResponse("Error: " + e.getMessage());
                }
            }
        }).start();
    }

    // 添加回调接口
    public interface OnMessageCallback {
        void onPartialResponse(String partialResponse);

        void onCompleteResponse(String completeResponse);
    }

    private OnMessageCallback onMessageCallback;

    public void setOnMessageCallback(OnMessageCallback callback) {
        this.onMessageCallback = callback;
    }

    public boolean canAddHistory() {
        int history_length = 0;
        for (RoleContent temp : historyList) {
            history_length = history_length + temp.content.length();
        }
        if (history_length > 12000) {
            // 移除前5条历史记录
            for (int i = 0; i < 5 && !historyList.isEmpty(); i++) {
                historyList.remove(0);
            }
            return false;
        }
        return true;
    }
}
