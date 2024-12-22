import json
import translators as ts
import time

# print(ts.translate_text("Write a short story (3-5 paragraphs) which only uses very simple words that a 3 year old child would understand. In the story, try to at some point use the verb \"mail\", the noun \"number\" and the adjective \"adorable\". Remember to only use simple words!", to_language='cn'))

i = 1
def extract_instruction_and_story(story_data):
    # 提取instruction中的prompt和story
    instruction = story_data.get('instruction', "")
    story = story_data.get('output', "")

    # 使用Translator库进行翻译
    translated_instruction = ts.translate_text(instruction, to_language='cn')
    translated_story = ts.translate_text(story,to_language='cn')

    # print(instruction)
    # print(story)
    # print(translated_instruction)
    # print(translated_story)
    global i
    print(f"{i}---------------------------")
    i += 1
    # time.sleep(1)

    return {"instruction": translated_instruction, "input": "", "output": translated_story}


def process_json_file(file_path):
    # 读取JSON文件并处理
    with open(file_path, 'r', encoding='utf-8') as file:
        data = json.load(file)

    # 处理每个故事数据
    processed_data = [extract_instruction_and_story(item) for item in data]

    # 保存处理后的数据到原文件
    with open(file_path, 'w', encoding='utf-8') as file:
        json.dump(processed_data, file, ensure_ascii=False, indent=4)


# 指定文件路径
file_path = "processed_tiny_story_10w.json"

# 调用函数处理文件
process_json_file(file_path)
print(f"Processed JSON file saved to {file_path}")
