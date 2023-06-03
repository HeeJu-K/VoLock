import requests

# 定义HTTP接口的URL
url = "http://20.25.51.3:5556/cosine_score"

# 定义上传的文件
files = {
    "wav_file": open("Heeju.wav", "rb"),
    "wav_file_1":  open("Jiahui.wav", "rb")
}

# 发送HTTP请求
response = requests.post(url, files=files)

# 打印响应结果
print(response)
print(response.content)