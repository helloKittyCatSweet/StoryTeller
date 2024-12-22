import json

# 输入和输出文件路径
input_file_path = 'story.json'
output_file_path = '../fairyTaleJSONDataset/fairy_tale.json'

# 初始化一个空列表来存储所有新的故事
new_stories = []

# 读取原始JSON文件
with open(input_file_path, 'r', encoding='utf-8') as file:
    # 逐行读取文件
    for line_number, line in enumerate(file, start=1):
        # 尝试解析JSON
        try:
            story = json.loads(line)
        except json.JSONDecodeError as e:
            print(f"Error decoding JSON on line {line_number}: {e}")
            continue

        # 提取text内容
        text_content = story.get('output', '')

        # 创建一个新的故事字典
        new_story = {
            "instruction": "",
            "input": "",
            "output": text_content
        }

        # 将新的故事添加到列表中
        new_stories.append(new_story)

# 将所有新故事写入新的JSON文件（Alpaca格式）
with open(output_file_path, 'w', encoding='utf-8') as output_file:
    # 使用中括号包裹最外层
    json.dump(new_stories, output_file, ensure_ascii=False, indent=4)

print(f"Processed {len(new_stories)} stories and saved to {output_file_path}")