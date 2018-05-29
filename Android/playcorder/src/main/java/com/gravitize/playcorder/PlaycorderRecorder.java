package com.gravitize.playcorder;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class PlaycorderRecorder {
    private final String TAG = "PlaycorderRecorder";

    private DataOutputStream dataFileStream;
    private String streamFilename = "";
    private long startTime = 0;

    public PlaycorderRecorder(String _streamFilename) {
        streamFilename = _streamFilename;
    }

    private DataOutputStream InitializeOutputStream(String streamFilename) {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }

        try {
            File file = new File(streamFilename);
            FileOutputStream outputFileStream = new FileOutputStream(file);
            DataOutputStream outputDataStream = new DataOutputStream(outputFileStream);

            return outputDataStream;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Cannot create output stream " + streamFilename + " (" + e.toString() + ")");
        }

        return null;
    }

    public void Close() {
        try {
            dataFileStream.close();
            dataFileStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SavePacket(byte[] buffer) {
        if (dataFileStream == null) {
            dataFileStream = InitializeOutputStream(streamFilename);
        }

        try {
            // Timestamp
            dataFileStream.writeInt((int)(System.currentTimeMillis() - startTime));
            // Size
            dataFileStream.writeInt(buffer.length);
            // Packet
            dataFileStream.write(buffer, 0, buffer.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
};



