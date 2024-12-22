class TtsRequest {
  final String text;

  TtsRequest({required this.text});

  Map<String, dynamic> toJson() {
    return {
      'text': text,
    };
  }
}
