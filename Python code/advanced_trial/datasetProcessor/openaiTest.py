# from openai import OpenAI
# import os
#
# # 设置您的OpenAI API密钥
# client = OpenAI(api_key=os.environ['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'])
#
# # 您想要概括的文本
# text_to_summarize = """
# I enjoy learning
# Learning is a joyful thing!
# I enjoy learning computer knowledge
# 😊 Computers are really fun! What kind of computer knowledge do you want to learn? 💻
# I like eating lunch, but I don't like eating dinner.
# """
#
# # 调用OpenAI API来生成概括
# response = client.completions.create(
#     engine="davinci",  # 选择一个模型，例如davinci
#     prompt=text_to_summarize,  # 提供给API的提示
#     max_tokens=50  # 返回的概括文本的最大令牌数
# )
#
# # 打印概括结果
# print(response.choices[0].text.strip())

import os
from openai import OpenAI
from dotenv import load_dotenv

load_dotenv()  # 加载 .env 文件中的环境变量

# 使用环境变量替代硬编码的 API 密钥
api_key = os.getenv('OPENAI_API_KEY')

client = OpenAI(
    # 若没有配置环境变量，请用百炼API Key将下行替换为：api_key="sk-xxx",
    api_key=api_key,
    base_url="https://dashscope.aliyuncs.com/compatible-mode/v1",
)
completion = client.chat.completions.create(
    model="qwen-plus",  # 模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
    messages=[
        {'role': 'system', 'content': 'You are a helpful assistant.'},
        {'role': 'user', 'content': '你是谁？'}],
)

print(completion.model_dump_json())