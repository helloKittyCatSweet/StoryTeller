class ChatMessage {
  final String text;
  final bool isUser;
  final String? ttsStatus;

  ChatMessage({
    required this.text,
    required this.isUser,
    this.ttsStatus,
  });
}
