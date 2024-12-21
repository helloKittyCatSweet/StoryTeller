package com.example.story_teller;

import android.content.Context;
import android.util.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import androidx.annotation.NonNull;

public class BigModelPlugin implements FlutterPlugin, MethodCallHandler {
    private static final String CHANNEL = "StoryTellerModel";
    private MethodChannel channel;
    private BigModelNew bigModelNew;
    private Context context;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding binding) {
        context = binding.getApplicationContext();
        channel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL);
        channel.setMethodCallHandler(this);
        bigModelNew = new BigModelNew("user", false, context, channel);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("sendMessage")) {
            String message = call.argument("message");
            if (message == null) {
                result.error("INVALID_ARGUMENT", "Message cannot be null", null);
                return;
            }
            try {
                bigModelNew.setOnMessageCallback(new BigModelNew.OnMessageCallback() {
                    @Override
                    public void onPartialResponse(String partialResponse) {
                        channel.invokeMethod("onPartialResponse", partialResponse);
                    }

                    @Override
                    public void onCompleteResponse(String completeResponse) {
                        result.success(completeResponse);
                    }
                });
                
                bigModelNew.sendMessage(message);
            } catch (Exception e) {
                Log.e("BigModelPlugin", "Error: ", e);
                result.error("ERROR", e.getMessage(), null);
            }
        } else if (call.method.equals("onTtsStart")) {
            // TTS 开始播放
            result.success(null);
        } else if (call.method.equals("onTtsComplete")) {
            // TTS 播放完成
            result.success(null);
        } else if (call.method.equals("onTtsError")) {
            // TTS 错误
            result.error("TTS_ERROR", call.argument("error"), null);
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPlugin.FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        channel = null;
    }
} 