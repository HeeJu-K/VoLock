package com.example.androidrecorder;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.json.JSONObject;

import android.Manifest.permission;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

public class MainActivity extends AppCompatActivity {

    // Initializing all variables..
    private TextView startTV, stopTV, playTV, stopplayTV, statusTV;
    private EditText recorderName;
    // creating a variable for media recorder object class.
    private MediaRecorder mRecorder;

    // creating a variable for mediaplayer class
    private MediaPlayer mPlayer;

    // string variable is created for storing a file name
    private static String mFileName = null;
//    private Button lockBtn, unlockBtn, uploadBtn;


    // constant for storing audio permission
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize all variables with their layout items.
        statusTV = findViewById(R.id.idTVstatus);
        startTV = findViewById(R.id.btnRecord);
        stopTV = findViewById(R.id.btnStop);
        playTV = findViewById(R.id.btnPlay);
        stopplayTV = findViewById(R.id.btnStopPlay);
        Button lockBtn = findViewById(R.id.lock);
        Button unlockBtn = findViewById(R.id.unlock);
        recorderName = findViewById(R.id.idRecorderName);
        Button uploadBtn = findViewById(R.id.ConfirmUpload);
        stopTV.setBackgroundColor(getResources().getColor(R.color.gray));
        playTV.setBackgroundColor(getResources().getColor(R.color.gray));
        stopplayTV.setBackgroundColor(getResources().getColor(R.color.gray));

        startTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("app debug message on click");
                // start recording method will
                // start the recording of audio.
                try {
                    startRecording();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        stopTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pause Recording method will
                // pause the recording of audio.
                pauseRecording();

            }
        });
        playTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // play audio method will play
                // the audio which we have recorded
                playAudio();
            }
        });
        stopplayTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pause play method will
                // pause the play of audio
                pausePlaying();
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // upload the recorded voice file
                confirmUpload();
            }
        });
        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // lock the Lock
                lockLock();
            }
        });
        unlockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // unlock the Lock
                unlockLock();
            }
        });
    }


//    public static void uploadFile(String uploadUrl, String filePath) {
//        System.out.println("in uploadFile, uploadFile: "+ uploadUrl);
//        System.out.println("in uploadFile, filePath: "+ filePath);
//        // Open the file for reading
//        FileInputStream fileInputStream = new FileInputStream(filePath);
//
//        // Create the connection
//        URL url = new URL(uploadUrl);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setDoOutput(true);
//        connection.setRequestMethod("POST");
//        System.out.println("in uploadFile, where00 ");
//
//        // Set the content type to "multipart/form-data"
//        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------1234567890");
//        System.out.println("in uploadFile, where11 ");
//        System.out.println(connection);
//
//        // Create the data output stream
//        DataOutputStream dataOutputStream = null;
//        try {
//
//            dataOutputStream = new DataOutputStream(connection.getOutputStream());
//            // Write data to the server
//            // ...
//            dataOutputStream.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Handle the exception or display an error message
//        } finally {
//            if (dataOutputStream != null) {
//                dataOutputStream.close();
//            }
//        }
//        System.out.println("in uploadFile, where22 ");
//
//        // Write the file contents to the output stream
//        byte[] buffer = new byte[4096];
//        int bytesRead;
//        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
//            dataOutputStream.write(buffer, 0, bytesRead);
//        }
//        System.out.println("in uploadFile, where33 ");
//
//        // Close the streams
//        fileInputStream.close();
//        dataOutputStream.flush();
//        dataOutputStream.close();
//
//        // Get the response code
//        int responseCode = connection.getResponseCode();
//
//        // Handle the response code as needed
//        if (responseCode == HttpURLConnection.HTTP_OK) {
//            // File uploaded successfully
//        } else {
//            // Handle the error case
//        }
//    }
    private void startRecording() throws IOException {
        // check permission method is used to check
        // that the user has granted permission
        // to record and store the audio.
        System.out.println(" app debug message in start recording");
        System.out.println(CheckPermissions());
        if (CheckPermissions()) {
            System.out.println("app debug message has permission");
            // setbackgroundcolor method will change
            // the background color of text view.
            stopTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
            startTV.setBackgroundColor(getResources().getColor(R.color.gray));
            playTV.setBackgroundColor(getResources().getColor(R.color.gray));
            stopplayTV.setBackgroundColor(getResources().getColor(R.color.gray));
            System.out.println("app debug message has permission");

            // we are here initializing our filename variable
            // with the path of the recorded audio file.
//            mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//            File file = new File(path, "/AudioRecording.3gp");
//            File file = new File(path, "/AudioRecording.wav");
            File file = new File(path, "/" + recorderName.getText().toString() + ".m4a");

//            mFileName = path + "/AudioRecording.3gp";
//            mFileName = path + "/AudioRecording.m4a";
            mFileName = path + "/" +recorderName.getText().toString() +".m4a";

//            // START for wav recording test
//            mFileName = path + "/AudioRecording.wav";
//            AudioFileProcess audioFileProcess = new AudioFileProcess();
//            float[] audioData = audioFileProcess.ReadingAudioFile(mFileName);
//
//            float [] manipulatedAudioData = new float[audioData.length];
//            /*
//              Assume did some manipulation on the wav file aka over audioData array[] and got new array named manipulatedAudioData[]
//            */
//            short int16[] =  float32ToInt16(manipulatedAudioData); // suppose, the new wav file's each sample will be in int16 Format
//            audioFileProcess.WriteCleanAudioWav(this,"AudioRecording.wav", int16);
//            System.out.println(mFileName);
//
//            // END of wav recording test

            // below method is used to initialize
            // the media recorder class
            mRecorder = new MediaRecorder();

            // below method is used to set the audio
            // source which we are using a mic.
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            // below method is used to set the output format of the audio.
//            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setAudioEncodingBitRate(16 * 44100);
            mRecorder.setAudioSamplingRate(44100);

            // below method is used to set the
            // audio encoder for our recorded audio.
//            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            // below method is used to set the
            // output file location for our recorded audio
            mRecorder.setOutputFile(mFileName);
            System.out.println(mRecorder.getMaxAmplitude());

            try {
                // below method will prepare
                // our audio recorder class
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("TAG", "prepare() failed");
            }
            // start method will start
            // the audio recording.
            mRecorder.start();
            statusTV.setText("Recording Started");
        } else {
            System.out.println("here in request permissions");
            // if audio recording permissions are
            // not granted by user below method will
            // ask for runtime permission for mic and storage.
            RequestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // this method is called when user will
        // grant the permission for audio recording.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    System.out.println("permission To Record");
                    System.out.println(grantResults[0]);
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean CheckPermissions() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        System.out.println("check permissions()");
        System.out.println(result);
        System.out.println(result1);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
//        }
    }
    public void playAudio() {
        stopTV.setBackgroundColor(getResources().getColor(R.color.gray));
        startTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        playTV.setBackgroundColor(getResources().getColor(R.color.gray));
        stopplayTV.setBackgroundColor(getResources().getColor(R.color.purple_200));

        // for playing our recorded audio
        // we are using media player class.
        mPlayer = new MediaPlayer();
        try {
            // below method is used to set the
            // data source which will be our file name
            mPlayer.setDataSource(mFileName);

            // below method will prepare our media player
            mPlayer.prepare();

            // below method will start our media player.
            mPlayer.start();
            statusTV.setText("Recording Started Playing");
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
    }

    public void pauseRecording() {
        stopTV.setBackgroundColor(getResources().getColor(R.color.gray));
        startTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        playTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        stopplayTV.setBackgroundColor(getResources().getColor(R.color.purple_200));

        // below method will stop
        // the audio recording.
        mRecorder.stop();

        // below method will release
        // the media recorder class.
        mRecorder.release();
        mRecorder = null;
        statusTV.setText("Recording Stopped");
    }

    public void pausePlaying() {
        // this method will release the media player
        // class and pause the playing of our recorded audio.
        mPlayer.release();
        mPlayer = null;
        stopTV.setBackgroundColor(getResources().getColor(R.color.gray));
        startTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        playTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        stopplayTV.setBackgroundColor(getResources().getColor(R.color.gray));
        statusTV.setText("Recording Play Stopped");
    }

    public void confirmUpload() {
        String uploadUrl = "http://192.168.0.167:5556/upload";
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, "/" + recorderName.getText().toString() + ".m4a");
        String filePath = path + "/" +recorderName.getText().toString() +".m4a";

        HttpPostTask httpPostTask = new HttpPostTask();
//        httpPostTask.execute(uploadUrl, filePath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            httpPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uploadUrl, filePath);
        } else {
            httpPostTask.execute(uploadUrl, filePath);
        }
//        try {
//            uploadFile(uploadUrl, filePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // this method will send a http request to Azure
//        // send a recorded file as well as name of the recorder
//        try {
//            JSONObject jsonObject = new JSONObject();
////            jsonObject.put("job", jobEdt.getText().toString());
////                jsonObject.put("wavFile", base64Wav);
//            String recordedFileStr = jsonObject.toString();
//            new PostData().execute( "upload");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
    public void lockLock() {
        // this method will send a http request to the ESP32
        // and send a signal to the lock the lock.

        try {
            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("job", jobEdt.getText().toString());
            String jsonString = jsonObject.toString();
            new PostLockStatus().execute("lock");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unlockLock() {
        // this method will send a http request to the ESP32
        // and send a signal to the unlock the lock.
        try {
            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("job", jobEdt.getText().toString());
            String jsonString = jsonObject.toString();
            new PostLockStatus().execute( "unlock");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class HttpPostTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String uploadUrl = params[0];
            String filePath = params[1];

            try {
                // Create the connection
                URL url = new URL(uploadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                // Set the content type to "multipart/form-data"
                String boundary = "---------------------------1234567890";
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                // Create the data output stream
                try (DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream())) {
                    dataOutputStream.writeBytes("--" + boundary + "\r\n");

                    // Add file field
                    String fileName = new File(filePath).getName();
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n");
                    dataOutputStream.writeBytes("Content-Type: audio/mp4a-latm\r\n\r\n");

                    // Write file data
                    try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            dataOutputStream.write(buffer, 0, bytesRead);
                        }
                        dataOutputStream.flush();
                    }

                    // End boundary
                    dataOutputStream.writeBytes("\r\n--" + boundary + "--\r\n");
                }

                // Get the response code
                int responseCode = connection.getResponseCode();

                // Handle the response code as needed
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // File uploaded successfully
                } else {
                    // Handle the error case
                }

                return responseCode;

            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception or display an error message
                return -1; // Return an error code
            }
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // File uploaded successfully
            } else {
                // Handle the error case
            }
        }
    }
    private static class PostLockStatus extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {

                // get the endpoint name from arguments
                String endpoint = strings[0];
                System.out.println(endpoint);
                // on below line creating a url to post the data.
//                URL url = new URL("https://reqres.in/api/users");
//                URL url = new URL("http://10.155.234.136/" + endpoint);
                URL url = new URL("http://192.168.0.128/" + endpoint);
//                URL url = new URL("http://10.155.234.136/lock");
                System.out.println(url);
                // on below line opening the connection.
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("GET");
                int responseCode = client.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("MainActivity", "POST request sent successfully.");
                } else {
                    Log.d("MainActivity", "POST request failed.");
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
//        @Override
//        protected void onPostExecute(String response) {
//            super.onPostExecute(response);
//            // display a Toast message to indicate that the data has been posted to the API
//            Toast.makeText(MainActivity.this, "Data has been posted to the API.", Toast.LENGTH_SHORT).show();
//        }
    }
}
