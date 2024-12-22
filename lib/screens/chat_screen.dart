import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../models/chat_message.dart';
import '../services/spark_service.dart';
import '../theme/app_theme.dart';

class ChatScreen extends StatefulWidget {
  const ChatScreen({Key? key}) : super(key: key);

  @override
  State<ChatScreen> createState() => _ChatScreenState();
}

class _ChatScreenState extends State<ChatScreen> with TickerProviderStateMixin {
  final TextEditingController _textController = TextEditingController();
  final FocusNode _focusNode = FocusNode();
  final ScrollController _scrollController = ScrollController();
  final SparkService _sparkService = SparkService();
  final List<ChatMessage> _messages = [];
  bool _isLoading = false;

  late final AnimationController _fadeController = AnimationController(
    duration: const Duration(milliseconds: 1000),
    vsync: this,
  );

  late final AnimationController _scaleController = AnimationController(
    duration: const Duration(milliseconds: 400),
    vsync: this,
  );

  late final Animation<double> _fadeAnimation = CurvedAnimation(
    parent: _fadeController,
    curve: Curves.easeIn,
  );

  late final Animation<double> _scaleAnimation = CurvedAnimation(
    parent: _scaleController,
    curve: Curves.elasticOut,
  );

  @override
  void initState() {
    super.initState();
    _fadeController.forward();
    _scaleController.forward();
  }

  @override
  void dispose() {
    _fadeController.dispose();
    _scaleController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: AppTheme.primaryColor,
        title: const Text(
          '童话故事家',
          style: TextStyle(
            fontSize: 20,
            fontWeight: FontWeight.bold,
            color: Colors.white,
          ),
        ),
        iconTheme: const IconThemeData(color: Colors.white),
        elevation: 2,
      ),
      body: GestureDetector(
        onTap: () => FocusScope.of(context).unfocus(),
        child: Column(
          children: [
            Expanded(
              child: _messages.isEmpty
                  ? _buildWelcomeMessage()
                  : _buildMessageList(),
            ),
            _buildInputArea(),
          ],
        ),
      ),
    );
  }

  Widget _buildWelcomeMessage() {
    return FadeTransition(
      opacity: _fadeAnimation,
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ScaleTransition(
              scale: _scaleAnimation,
              child: Container(
                padding: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  color: AppTheme.primaryColor.withOpacity(0.1),
                  shape: BoxShape.circle,
                  boxShadow: [
                    BoxShadow(
                      color: AppTheme.primaryColor.withOpacity(0.2),
                      blurRadius: 20,
                      spreadRadius: 5,
                    ),
                  ],
                ),
                child: Icon(
                  Icons.auto_stories,
                  size: 80,
                  color: AppTheme.primaryColor,
                ),
              ),
            ),
            const SizedBox(height: 24),
            SlideTransition(
              position: Tween<Offset>(
                begin: const Offset(0, 0.2),
                end: Offset.zero,
              ).animate(CurvedAnimation(
                parent: _fadeController,
                curve: const Interval(0.4, 1.0, curve: Curves.easeOut),
              )),
              child: FadeTransition(
                opacity: CurvedAnimation(
                  parent: _fadeController,
                  curve: const Interval(0.4, 1.0),
                ),
                child: Text(
                  '让我们开始创作童话故事吧！',
                  style: TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.w600,
                    color: Colors.grey[800],
                  ),
                ),
              ),
            ),
            const SizedBox(height: 12),
            SlideTransition(
              position: Tween<Offset>(
                begin: const Offset(0, 0.2),
                end: Offset.zero,
              ).animate(CurvedAnimation(
                parent: _fadeController,
                curve: const Interval(0.6, 1.0, curve: Curves.easeOut),
              )),
              child: FadeTransition(
                opacity: CurvedAnimation(
                  parent: _fadeController,
                  curve: const Interval(0.6, 1.0),
                ),
                child: Text(
                  '在下方输入您的想法，开启奇妙的故事之旅',
                  style: TextStyle(
                    fontSize: 16,
                    color: Colors.grey[600],
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildMessageList() {
    return ListView.builder(
      controller: _scrollController,
      padding: const EdgeInsets.all(16),
      itemCount: _messages.length,
      itemBuilder: (context, index) {
        final message = _messages[index];
        return _buildMessageBubble(message, index);
      },
    );
  }

  Widget _buildMessageBubble(ChatMessage message, int index) {
    return SlideTransition(
      position: Tween<Offset>(
        begin: Offset(message.isUser ? 1 : -1, 0),
        end: Offset.zero,
      ).animate(CurvedAnimation(
        parent: _scaleController,
        curve: Curves.easeOutQuart,
      )),
      child: FadeTransition(
        opacity: CurvedAnimation(
          parent: _scaleController,
          curve: Curves.easeIn,
        ),
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 12.0),
          child: Column(
            crossAxisAlignment: message.isUser
                ? CrossAxisAlignment.end
                : CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisAlignment: message.isUser
                    ? MainAxisAlignment.end
                    : MainAxisAlignment.start,
                children: [
                  if (!message.isUser) _buildAvatar(message.isUser),
                  const SizedBox(width: 12),
                  Flexible(
                    child: Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 16,
                        vertical: 12,
                      ),
                      decoration: BoxDecoration(
                        color: message.isUser
                            ? AppTheme.chatBubbleUser
                            : AppTheme.chatBubbleBot,
                        borderRadius: BorderRadius.only(
                          topLeft: const Radius.circular(20),
                          topRight: const Radius.circular(20),
                          bottomLeft: Radius.circular(message.isUser ? 20 : 5),
                          bottomRight: Radius.circular(message.isUser ? 5 : 20),
                        ),
                        boxShadow: [
                          BoxShadow(
                            color: Colors.black.withOpacity(0.05),
                            blurRadius: 5,
                            offset: const Offset(0, 2),
                          ),
                        ],
                      ),
                      child: Text(
                        message.text,
                        style: TextStyle(
                          color: message.isUser
                              ? AppTheme.textColorUser
                              : AppTheme.textColorBot,
                          fontSize: 16,
                          height: 1.4,
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 12),
                  if (message.isUser) _buildAvatar(message.isUser),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildAvatar(bool isUser) {
    return Container(
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.1),
            blurRadius: 5,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: CircleAvatar(
        radius: 20,
        backgroundColor: isUser ? AppTheme.primaryColor : Colors.white,
        child: Icon(
          isUser ? Icons.person : Icons.auto_stories,
          color: isUser ? Colors.white : AppTheme.primaryColor,
          size: 22,
        ),
      ),
    );
  }

  Widget _buildInputArea() {
    return Container(
      padding: const EdgeInsets.all(16.0),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            offset: const Offset(0, -2),
            blurRadius: 8,
            color: Colors.black.withOpacity(0.08),
          ),
        ],
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          Expanded(
            child: Container(
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(24),
                border: Border.all(color: Colors.grey.withOpacity(0.2)),
              ),
              child: TextField(
                controller: _textController,
                focusNode: _focusNode,
                maxLines: 1,
                textInputAction: TextInputAction.send,
                keyboardType: TextInputType.text,
                onSubmitted: (_) => _handleSubmit(),
                decoration: InputDecoration(
                  hintText: '请输入您的想法...',
                  hintStyle: TextStyle(color: Colors.grey[400]),
                  border: InputBorder.none,
                  contentPadding: const EdgeInsets.symmetric(
                    horizontal: 20,
                    vertical: 12,
                  ),
                  filled: true,
                  fillColor: Colors.grey[50],
                ),
              ),
            ),
          ),
          const SizedBox(width: 12),
          _isLoading
              ? const Padding(
                  padding: EdgeInsets.all(8.0),
                  child: SizedBox(
                    width: 24,
                    height: 24,
                    child: CircularProgressIndicator(
                      strokeWidth: 2,
                    ),
                  ),
                )
              : Container(
                  decoration: const BoxDecoration(
                    shape: BoxShape.circle,
                    color: AppTheme.primaryColor,
                  ),
                  child: Material(
                    color: Colors.transparent,
                    child: InkWell(
                      customBorder: const CircleBorder(),
                      onTap: _handleSubmit,
                      child: const Padding(
                        padding: EdgeInsets.all(12.0),
                        child: Icon(Icons.send, color: Colors.white),
                      ),
                    ),
                  ),
                ),
        ],
      ),
    );
  }

  void _scrollToBottom() {
    if (_scrollController.hasClients) {
      _scrollController.animateTo(
        _scrollController.position.maxScrollExtent,
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeOut,
      );
    }
  }

  void _handleSubmit() async {
    if (_textController.text.isEmpty || _isLoading) return;

    _scaleController.reset();
    _scaleController.forward();

    final userMessage = _textController.text;
    _textController.clear();
    _focusNode.requestFocus();

    setState(() {
      _messages.add(ChatMessage(text: userMessage, isUser: true));
      _isLoading = true;
    });

    WidgetsBinding.instance.addPostFrameCallback((_) => _scrollToBottom());

    try {
      final response = await _sparkService.sendMessage(userMessage);

      setState(() {
        _messages.add(ChatMessage(text: response, isUser: false));
        _isLoading = false;
      });

      WidgetsBinding.instance.addPostFrameCallback((_) => _scrollToBottom());
    } catch (e) {
      setState(() {
        _isLoading = false;
      });

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('发生错误: $e'),
          backgroundColor: Colors.red,
        ),
      );
    }
  }
}
