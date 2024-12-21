import 'package:flutter/services.dart';
import 'package:flutter/foundation.dart';

class JavaChannel {
  static const MethodChannel _channel = MethodChannel('StoryTellerModel');

  static Future<String> sendMessage(String message) async {
    try {
      final String result =
          await _channel.invokeMethod('sendMessage', {'message': message});
      return result;
    } on PlatformException catch (e) {
      print('JavaChannel Error: ${e.code} - ${e.message}');
      throw '通信错误：${e.message}';
    }
  }
}
