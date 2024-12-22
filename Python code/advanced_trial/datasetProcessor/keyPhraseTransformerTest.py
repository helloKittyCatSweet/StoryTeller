from keyphrasetransformer import KeyPhraseTransformer

# 确保 model_path 是正确的，并且包含了所有必要的模型文件
model_path = "D:\\examples\\KeyPhraseTransformer"
kp = KeyPhraseTransformer(model_path)

doc = """
I enjoy learning
Learning is a joyful thing!
I enjoy learning computer knowledge
😊 Computers are really fun! What kind of computer knowledge do you want to learn? 💻
I like  eating  lunch,but i  don not like eating  dinner.
"""

# 确保 get_key_phrases 方法存在
try:
    key_phrases = kp.get_key_phrases(doc)
    print(key_phrases)
except AttributeError as e:
    print(f"Error: {e}")