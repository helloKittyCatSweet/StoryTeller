import json
import os
# from transformers import MarianMTModel, MarianTokenizer
#
# # 选择翻译模型
# model_name = "Helsinki-NLP/opus-mt-en-zh"
# tokenizer = MarianTokenizer.from_pretrained(model_name)
# model = MarianMTModel.from_pretrained(model_name)
#
# def translate(text):
#     # 编码输入文本
#     inputs = tokenizer(text, return_tensors="pt", padding=True, truncation=True)
#     # 生成翻译
#     translated = model.generate(**inputs)
#     # 解码输出
#     return tokenizer.decode(translated[0], skip_special_tokens=True)
#
# # 示例文本
# text = "Hello, how are you?"
# translation = translate(text)
#
# print("原文：", text)
# print("翻译结果：", translation)

from translate import Translator

# text = "Hello, how are you?"
translator = Translator(from_lang='en', to_lang='zh-cn')
# translation = translator.translate(text)
#
# print(translation)

def extract_instruction_and_story(story_data):
    # 提取instruction中的prompt和story
    instruction = story_data['instruction']['prompt:']
    instruction = translator.translate(instruction)
    story = story_data['story']
    story = translator.translate(story)
    return {"instruction": instruction, "input":"","output": story}


def process_json_file(file_path):
    # 读取JSON文件并处理
    with open(file_path, 'r', encoding='utf-8') as file:
        data = json.load(file)
    processed_data = [extract_instruction_and_story(item) for item in data]
    return processed_data


def process_folder(folder_path, output_file_path):
    # 收集所有处理后的数据
    all_processed_data = []
    # 遍历文件夹中的所有文件
    i = 1
    for filename in os.listdir(folder_path):
        if i > 2:
            break
        if filename.endswith('.json'):
            i += 1
            file_path = os.path.join(folder_path, filename)
            processed_data = process_json_file(file_path)
            all_processed_data.extend(processed_data)

    # 保存所有处理后的数据到一个新的JSON文件
    with open(output_file_path, 'w', encoding='utf-8') as file:
        json.dump(all_processed_data, file, ensure_ascii=False, indent=4)


# 指定文件夹路径和输出文件路径
folder_path = 'D://examples//python_ex//Laboratory//pythonProject//advanced_trial//tinyStoryEnglishDataset'  # 替换为您的文件夹路径
output_file_path = '../processed_tiny_story.json'  # 您希望保存的新文件路径

# 调用函数处理文件夹中的所有文件
process_folder(folder_path, output_file_path)
print(f"All processed JSON file saved to {output_file_path}")