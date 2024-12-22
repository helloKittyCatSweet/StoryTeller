import 'package:flutter/material.dart';
import 'screens/chat_screen.dart';
import 'theme/app_theme.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '童话故事家',
      theme: AppTheme.theme,
      home: const ChatScreen(),
      debugShowCheckedModeBanner: false,
    );
  }
}
