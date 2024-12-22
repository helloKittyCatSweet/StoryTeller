import json
import storyFromAI

# 以下密钥信息从控制台获取
appid = "b67ce07b"  # 填写控制台中获取的 APPID 信息
api_secret = "NDNlNjYwZTM2MGJjMTM1M2ViNmIwYWE1"  # 填写控制台中获取的 APISecret 信息
api_key = "1ce91b76b2f888b3cdfa19365602349a"  # 填写控制台中获取的 APIKey 信息

# 调用微调大模型时，设置为“patch”
domain = "patch"

# 云端环境的服务地址
Spark_url = "wss://spark-api-n.xf-yun.com/v1.1/chat"  # 微调v1.5环境的地址

def get_text(role, content):
    json_con = {"role": role, "content": "请根据以下故事内容给出故事题目："+ content}
    return [json_con]  # 返回一个列表，因为API期望数组格式

def get_story_theme(output_text):
    question = get_text("user", output_text)
    storyFromAI.answer = ""
    try:
        storyFromAI.main(appid, api_key, api_secret, Spark_url, domain, question)
        # 提取“题目：”之后和“#”之前的内容
        # start_index = storyFromAI.answer.find("题目：") + len("题目：")
        # end_index = storyFromAI.answer.find("#", start_index)
        # if end_index == -1:  # 如果没有找到"#"，则取剩余的所有内容
        #     end_index = len(storyFromAI.answer)
        # theme = storyFromAI.answer[start_index:end_index].strip()
        return storyFromAI.answer
    except Exception as e:
        print(f"Error: {e}")
        return ""

def process_json_blocks(json_blocks):
    for block in json_blocks:
        output = block.get("output", "")
        if output:  # 确保output字段存在
            theme = get_story_theme(output)
            block["instruction"] = theme
    return json_blocks

def load_json_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        return json.load(file)

def save_json_file(json_data, file_path):
    with open(file_path, 'w', encoding='utf-8') as file:
        json.dump(json_data, file, ensure_ascii=False, indent=4)

if __name__ == '__main__':
    json_file_path = 'fairyTaleJSONDataset/fairy_tale.json'  # 替换为你的JSON文件路径
    json_data = load_json_file(json_file_path)

    if isinstance(json_data, list):  # 确保json_data是一个列表
        processed_json_data = process_json_blocks(json_data)
        save_json_file(processed_json_data, json_file_path)
        print("JSON文件已更新。")
    else:
        print("JSON数据格式不正确，预期为包含多个JSON对象的列表。")