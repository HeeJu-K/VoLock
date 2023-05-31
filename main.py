from fastapi import FastAPI, File, UploadFile
import os
import wespeakerruntime as wespeaker
import uvicorn

app = FastAPI()
speaker = wespeaker.Speaker(lang='en')

@app.get("/")
def read_root():
    return {"Hello": "World"}
@app.post("/cosine_score")
async def compute_cosine_score(wav_file: UploadFile = File(...),wav_file_1: UploadFile = File(...)):
    # 保存上传的文件到临时文件夹
    print("file reading...")
    with open("temp.wav", "wb") as buffer:
        buffer.write(wav_file.file.read())
    with open("temp1.wav", "wb") as buffer:
        buffer.write(wav_file_1.file.read())
    print("calculating...")
    # 计算相似度得分
    emb1 = speaker.extract_embedding("temp.wav")[0]
    emb2 = speaker.extract_embedding("temp1.wav")[0]
    score = speaker.compute_cosine_score(emb1, emb2)

    # 删除临时文件
    os.remove("temp.wav")

    return str(score)

if __name__ == "__main__":
    uvicorn.run(app,port=5556,host="0.0.0.0")