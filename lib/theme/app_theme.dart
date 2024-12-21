import 'package:flutter/material.dart';

class AppTheme {
  static const Color primaryColor = Color(0xFF6B4EFF);
  static const Color backgroundColor = Color(0xFFF5F5F5);
  static const Color chatBubbleUser = Color(0xFF6B4EFF);
  static const Color chatBubbleBot = Color(0xFFF0F2F5);
  static const Color textColorUser = Colors.white;
  static const Color textColorBot = Color(0xFF1A1A1A);

  static final ThemeData theme = ThemeData(
    primaryColor: primaryColor,
    scaffoldBackgroundColor: Colors.white,
    fontFamily: 'SimSun',
    appBarTheme: const AppBarTheme(
      elevation: 0,
      centerTitle: true,
      backgroundColor: Colors.white,
      foregroundColor: primaryColor,
      titleTextStyle: TextStyle(
        fontFamily: 'SimSun',
        fontSize: 20,
        fontWeight: FontWeight.bold,
        color: primaryColor,
      ),
    ),
    textTheme: const TextTheme(
      bodyLarge: TextStyle(fontFamily: 'SimSun'),
      bodyMedium: TextStyle(fontFamily: 'SimSun'),
      titleLarge: TextStyle(fontFamily: 'SimSun'),
      titleMedium: TextStyle(fontFamily: 'SimSun'),
    ),
  );
}
