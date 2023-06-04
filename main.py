from fastapi import FastAPI, File, UploadFile
import os
import wespeakerruntime as wespeaker
import uvicorn
import ffmpeg

app = FastAPI()
speaker = wespeaker.Speaker(lang='en')

@app.get("/")
def read_root():
    return {"Hello": "World"}

@app.post("/upload")
async def upload_file(file: UploadFile ):
    print("here in backend upload")
    print("printing uploaded filename", file.filename)
    tmp_file_path = f"./assets/{file.filename}"
    output_file_path = os.path.splitext(tmp_file_path)[0] + ".wav"

    # Save the uploaded file to a temporary folder
    print("File uploading...")
    # temp_file = "/assets/uploaded_file.m4a"
    if not os.path.exists(tmp_file_path):
        with open(tmp_file_path, "xb"):
            pass

    with open(tmp_file_path, "wb") as buffer:
        buffer.write(await file.read())

    ffmpeg.input(tmp_file_path).output(output_file_path).run()
    os.remove(tmp_file_path)
    print("File uploaded:", file.filename)

    return {"filename": file.filename, "message": "File uploaded successfully."}

@app.get("/cosine_score")
async def compute_cosine_score():
    # 保存上传的文件到临时文件夹
    print("file reading...")
    #with open("./assets/abc.wav", "wb") as buffer:
    # with open("temp.wav", "wb") as buffer:
    #    buffer.write(wav_file.file.read())
    #with open("./assets/heeju.wav", "wb") as buffer:
    # with open("temp1.wav", "wb") as buffer:
        #buffer.write(wav_file_1.file.read())
    #print("calculating...")
    # 计算相似度得分
    emb1 = speaker.extract_embedding("./assets/heeju.wav")[0]
    emb2 = speaker.extract_embedding("./assets/abc.wav")[0]
    score = speaker.compute_cosine_score(emb1, emb2)

    # 删除临时文件
    #os.remove("temp.wav")

    return str(score)

if __name__ == "__main__":
    uvicorn.run(app,port=5556,host="0.0.0.0")
