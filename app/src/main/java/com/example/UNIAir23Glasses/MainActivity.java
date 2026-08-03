package com.example.UNIAir23Glasses;


import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.AudioFormat;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GlassesApp";
    private static final String SERVER_IP = "172.27.150.78"; // phone hotspot IP
    private static final int SERVER_PORT = 8888;

    private Button connectButton, startAudioButton, stopAudioButton;
    private TextView statusView, messageView, debugLog;

    private Socket socket;
    private OutputStream out;
    private InputStream in;

    private AudioRecord recorder;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectButton = findViewById(R.id.connectButton);
        startAudioButton = findViewById(R.id.startAudioButton);
        statusView = findViewById(R.id.statusView);
        messageView = findViewById(R.id.messageView);
        debugLog = findViewById(R.id.debugLog);

        getDeviceInfo();
        connectButton.setOnClickListener(v -> connectToServer());
        startAudioButton.setOnClickListener(v -> startAudioCapture());
    }

    public void getDeviceInfo(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        int ip = dhcpInfo.ipAddress;
        String ipString = String.format("%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));
        addDebugLog("Glasses WiFi IP: " + ipString);
    }
    private void connectToServer() {
        new Thread(() -> {
            try {
                addDebugLog("Attempting connection to " + SERVER_IP + ":" + SERVER_PORT);
                socket = new Socket();
                socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT), 5000);
                addDebugLog("Connection established successfully");
                out = socket.getOutputStream();
                setStatusView("Connected to Phone Server");
                addDebugLog("Connection established successfully");
                listenForMessages();
            } catch (Exception e) {
                addDebugLog("Connection failed: " + e.getMessage());
                setStatusView("Connection failed");
            }
        }).start();
    }


    private void listenForMessages() {
        new Thread(() -> {
            try {
                // Use the existing InputStream, don't reopen/auto-close it
                InputStream in = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int read;

                addDebugLog("Listening for messages from Phone...");

                while ((read = in.read(buffer)) > 0) {
                    // Decode exactly what was received, including spaces
                    String msg = new String(buffer, 0, read, StandardCharsets.UTF_8);

                    // Log with full fidelity
                    addDebugLog("Received message (" + read + " bytes): \"" + msg + "\"");

                    // Update UI safely
                    runOnUiThread(() -> messageView.setText(msg));
                }

                addDebugLog("Message stream ended");
            } catch (IOException e) {
                addDebugLog("Error reading message: " + e.getMessage());
            }
        }).start();
    }


    private void startAudioCapture() {
        int bufferSize = AudioRecord.getMinBufferSize(
                16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        addDebugLog("Audio buffer size: " + bufferSize);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            addDebugLog("RECORD_AUDIO permission not granted, requesting...");
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1011
            );
            return;
        }

        try {
            recorder = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    16000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize);

            addDebugLog("AudioRecord initialized successfully");
        } catch (Exception e) {
            addDebugLog("Error initializing AudioRecord: " + e.getMessage());
            return;
        }

        recorder.startRecording();
        addDebugLog("Audio recording started");

        new Thread(() -> {
            try {
                ByteArrayOutputStream audioBuffer = new ByteArrayOutputStream();
                byte[] buffer = new byte[bufferSize];

                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 3000) { // 3 seconds
                    int read = recorder.read(buffer, 0, buffer.length);
                    if (read > 0) {
                        audioBuffer.write(buffer, 0, read);
                    }
                }

                recorder.stop();
                recorder.release();
                recorder = null;
                addDebugLog("Audio recording stopped after 3 seconds");

                if (out != null) {
                    byte[] audioData = audioBuffer.toByteArray();
                    DataOutputStream dout = new DataOutputStream(out);
                    dout.writeInt(audioData.length);   // send length prefix
                    dout.write(audioData);             // send audio bytes
                    dout.flush();
                    addDebugLog("Sent audio clip of " + audioData.length + " bytes to server");
                } else {
                    addDebugLog("Output stream is null, cannot send audio");
                }

            } catch (Exception e) {
                addDebugLog("Error during audio capture/send: " + e.getMessage());
            }
        }).start();

        runOnUiThread(() -> statusView.setText("Recording 3s audio..."));
    }


    private void addDebugLog(String msg) {
        runOnUiThread(() -> debugLog.append("\n" + msg));
    }
    private void setStatusView(String msg) {
        runOnUiThread(() -> statusView.setText(msg));
    }
}
