import ffmpeg

input_file = "./assets/abc_1.m4a"
output_file = "./assets/abc_1.wav"

ffmpeg.input(input_file).output(output_file).run()
