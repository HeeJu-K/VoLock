from fastapi import FastAPI, File, UploadFile
import os
import wespeakerruntime as wespeaker
import uvicorn
import ffmpeg
import requests
import time
import asyncio



app = FastAPI()
speaker = wespeaker.Speaker(lang='en')

async def repeat():
    print("in repeat")
    while True:
        await compute_score()
        print("repeated once")
        await asyncio.sleep(1)

async def compute_score():
    print("here in compute score")
    url = "http://10.155.234.136/recording.wav"
    tmp_file_path = f"./esp/espRecording.wav"
    if not os.path.exists(tmp_file_path):
        with open(tmp_file_path, "xb"):
            pass
    print("before request")
    response = await  requests.get(url)
    print("here")
    if response.status_code == 200:
        # Request was successful
        with open(tmp_file_path, 'wb') as file:
            file.write(response.content)
        print("File saved successfully.")
    else:
        # Request failed
        print("Error:", response.status_code)
        return

    print("file is made")    
    directory = "./assets"
    wav_files = os.listdir(directory)
    print("wav files", wav_files)
    score = 0
    # Go through each WAV file
    for wav_file in wav_files:
        print("in for loop")
        file_path = os.path.join(directory, wav_file)
        emb1 = speaker.extract_embedding("./esp/espRecording.wav")[0]
        emb2 = speaker.extract_embedding(file_path)[0]
        score = speaker.compute_cosine_score(emb1, emb2)
        print("first score", score)
        if score>0.6:
            break
    
    return str(score)


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

@app.post("/get_score")
async def get_cosine_score(file: UploadFile):
    print("in get score")
    tmp_file_path = f"./esp/espRecording.wav"
    if not os.path.exists(tmp_file_path):
        with open(tmp_file_path, "xb"):
            pass

    with open(tmp_file_path, "wb") as buffer:
        buffer.write(await file.read())
    print("file is made")    
    directory = "./assets"
    # wav_files = [file for file in directory if file.endswith(".wav")]
    wav_files = os.listdir(directory)
    print("wav files", wav_files)
    score = 0
    # Go through each WAV file
    for wav_file in wav_files:
        print("in for loop")
        file_path = os.path.join(directory, wav_file)
        emb1 = speaker.extract_embedding("./esp/espRecording.wav")[0]
        emb2 = speaker.extract_embedding(file_path)[0]
        score = speaker.compute_cosine_score(emb1, emb2)
        print("first score", score)
        if score>0.6:
            break
    
    return str(score)
    
    # Open the WAV file
    # with wave.open(file_path, 'rb') as wav:

@app.get("/request")
async def handleFile():
    return await compute_score()

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

#async def run_uvicorn():
#    uvicorn.run(app, port=5556, host="0.0.0.0")

#async def two_tasks():
#    task1 = asyncio.create_task(run_uvicorn())
#    task2 = asyncio.create_task(repeat())
#    await asyncio.gather(task1, task2)

if __name__ == "__main__":
    uvicorn.run(app,port=5556,host="0.0.0.0")
    # asyncio.run(two_tasks())

