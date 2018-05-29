package com.gravitize.playcorder;

import android.os.Environment;
import android.util.Log;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class PlaycorderPlayer {
    private final String TAG = "PlaycorderPlayer";

    private DataInputStream dataFileStream;

    private int lastPacketTime = 0;
    private SPlaycorderPacket lastPacket;

    private EEOFBehaviour eofBehaviour;

    private DataInputStream InitializeStream(String streamFilename) throws IOException {
        try {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Log.e(TAG, "Media not mounted. Aborting initialization. Media state = " + Environment.getExternalStorageState());
                throw new IOException("Media not mounted");
            }

            FileInputStream inputFileStream = new FileInputStream(new File(streamFilename));
            DataInputStream inputDataStream = new DataInputStream(inputFileStream);

            return inputDataStream;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Cannot load " + streamFilename + " input stream (" + e.toString() + ")");
        }

        return null;
    }

    public PlaycorderPlayer(String streamFilename, final EEOFBehaviour eofBehaviour, final PlaycorderCallback callback) throws IOException {
        this.eofBehaviour = eofBehaviour;
        dataFileStream = InitializeStream(streamFilename);
        if (dataFileStream != null) {
            lastPacket = new SPlaycorderPacket();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        // Read a Packet
                        if (!ReadPacket(dataFileStream, lastPacket)) {
                            // File has ended, or an error occured
                            switch (eofBehaviour) {
                                case Restart:
                                    try {
                                        dataFileStream.reset();
                                    } catch (IOException e) {
                                        return;
                                    }
                                    break;
                                case Stop:
                                    return;
                            }
                        }

                        // Wait if needed
                        try {
                            int time = lastPacket.Time - lastPacketTime;
                            time = Math.max(0, time);
                            Thread.sleep(time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        lastPacketTime = lastPacket.Time;

                        // Callback
                        callback.PacketReceived(lastPacket);
                    }
                }
            });
            t.start();
        }
    }

    private static boolean ReadPacket(InputStream stream, SPlaycorderPacket packet) {
        byte[] intBuffer = new byte[4];

        // Read timestamp
        try {
            if (stream.available() == 0) {
                return false;
            }

            stream.read(intBuffer, 0, 4);
            ByteBuffer wrapped = ByteBuffer.wrap(intBuffer); // big-endian by default
            packet.Time = wrapped.getInt();
//            // Convert endianness (ARM is big endian, PC little)
//            packet.Time = (packet.Time & 0xff) << 24 | (packet.Time & 0xff00) << 8 |
//                            (packet.Time & 0xff0000) >> 8 | (packet.Time >> 24) & 0xff;

            // Read size
            stream.read(intBuffer, 0, 4);
            wrapped = ByteBuffer.wrap(intBuffer); // big-endian by default
            packet.Size = wrapped.getInt();
//            // Convert endianness (ARM is big endian, PC little)
//            packet.Size = (packet.Size & 0xff) << 24 | (packet.Size & 0xff00) << 8 |
//                    (packet.Size & 0xff0000) >> 8 | (packet.Size >> 24) & 0xff;

            packet.Packet = new byte[packet.Size];
            // Read Packet
            stream.read(packet.Packet, 0, packet.Size);
        } catch (IOException e) {
            return false;
        }

        return true;
    }
};



