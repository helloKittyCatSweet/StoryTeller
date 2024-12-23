import hashlib
import time
import uuid
import requests

# 有道翻译API配置信息
YOUDAO_URL = 'https://openapi.youdao.com/api'
# 获取方式见网址https://ai.youdao.com/doc.s#guide
APP_KEY = 'xxx'
APP_SECRET = 'xxx'

# 常用语言id
LANGUAGES = {
    "Chinese": "zh-CHS",
    "English": "en",
    "French": "fr",
    "Spanish": "es",
    "German": "de",
    "Japanese": "ja",
    "Korean": "ko",
    "Russian": "ru",
    "Portuguese": "pt",
    "Italian": "it"
}


def encrypt(signStr):
    hash_algorithm = hashlib.sha256()
    hash_algorithm.update(signStr.encode('utf-8'))
    return hash_algorithm.hexdigest()


def truncate(q):
    if q is None:
        return None
    size = len(q)
    return q if size <= 20 else q[0:10] + str(size) + q[size - 10:size]


def do_request(data):
    headers = {'Content-Type': 'application/x-www-form-urlencoded'}
    response = requests.post(YOUDAO_URL, data=data, headers=headers)
    response.raise_for_status()  # 检查请求是否成功
    return response


# 参数分别为 需要翻译的语句 源语言id 目标语言id
# 返回值为翻译结果
def translate(text, from_lang, to_lang):
    data = {
        'from': from_lang,
        'to': to_lang,
        'signType': 'v3',
        'curtime': str(int(time.time())),
        'appKey': APP_KEY,
        'q': text,
        'salt': str(uuid.uuid1()),
        'vocabId': ''  # 可选，用户词表ID
    }
    signStr = APP_KEY + truncate(text) + data['salt'] + data['curtime'] + APP_SECRET
    data['sign'] = encrypt(signStr)

    response = do_request(data)
    result = response.json()
    if result.get('errorCode') == '0':
        return result['translation'][0]
    else:
        raise Exception(f"Error: {result.get('errorCode')}, {result.get('errorMsg')}")


print(translate('Write a short story (3-5 paragraphs) which only uses very simple words that a 3 year old child would understand. The story should use the verb \"hang\", the noun \"foot\" and the adjective \"cute\". The story has the following features: the story should contain at least one dialogue. Remember to only use simple words!\n\nPossible story', 'en', 'ch'))
