import 'package:flutter/foundation.dart';
import 'java_channel.dart';
import 'package:flutter/services.dart';

class SparkService {
  static const platform = MethodChannel('StoryTellerModel');

  Future<String> sendMessage(String message) async {
    try {
      platform.setMethodCallHandler((call) async {
        switch (call.method) {
          case 'onPartialResponse':
            // 处理部分响应
            break;
          case 'onTtsStart':
            // 显示正在播放语音
            break;
          case 'onTtsComplete':
            // 显示语音播放完成
            break;
          case 'onTtsError':
            // 显示语音播放错误
            break;
        }
      });

      final String response = await platform.invokeMethod('sendMessage', {
        'message': message,
      });
      return response;
    } catch (e) {
      return 'Error: $e';
    }
  }
}
