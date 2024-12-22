import json

# 假设你的原始JSON文件名为 original.json
import re

input_filename = 'story.json'
# 新的JSON文件名
output_filename = 'fairy_tale.json'

# 读取原始JSON文件
with open(input_filename, 'r', encoding='utf-8') as file:
    data = json.load(file)

# 创建一个新的列表，用于存储新的JSON结构
new_data = []

# 遍历原始数据中的每个元素
for item in data:
    # 提取name和content字段
    instruction = item.get('name', '')
    content = item.get('content', '')
    if content:
        content = re.sub(r'\s+','',content.strip())
    else:
        continue

    # 创建新的JSON结构
    new_item = {
        "instruction": "请根据以下主题讲一个小故事："+instruction,
        "input": "",
        "output": content
    }

    # 将新的JSON结构添加到列表中
    new_data.append(new_item)

# 将新的JSON结构写入新的文件
with open(output_filename, 'w', encoding='utf-8') as file:
    json.dump(new_data, file, ensure_ascii=False, indent=4)

print(f'New JSON file has been created: {output_filename}')