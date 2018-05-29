package com.playcorder.playcoderTester;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.gravitize.playcorder.EEOFBehaviour;
import com.gravitize.playcorder.PlaycorderCallback;
import com.gravitize.playcorder.PlaycorderPlayer;
import com.gravitize.playcorder.PlaycorderRecorder;
import com.gravitize.playcorder.PlaycorderUtils;
import com.gravitize.playcorder.SPlaycorderPacket;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private String TAG = "Playcorder";
    private final int PERMISSION_REQUEST_ID = 456;

    private String lastFilename = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_ID);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        Button button = (Button) findViewById(R.id.buttonPlay);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (lastFilename == "") {
                    return;
                }

                try {
                    PlaycorderPlayer player = new PlaycorderPlayer(lastFilename, EEOFBehaviour.Stop, new PlaycorderCallback() {
                        @Override
                        public void PacketReceived(SPlaycorderPacket packet) {
                            Log.d(TAG, "Packet Received (Time: " + packet.Time + " Size: " + packet.Size + ")");
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        button = (Button) findViewById(R.id.buttonRecord);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File rootsd = Environment.getExternalStorageDirectory();
                lastFilename = rootsd.getAbsolutePath() + "/" + PlaycorderUtils.GetFilename("data", "raw");
//                String dataFilename = rootsd.getAbsolutePath() + "/Playcorder/" + PlaycorderUtils.GetFilename("data", "raw");
//                File directory = new File(Environment.getExternalStorageDirectory(), "Playcorder");
//                if (!directory.exists()) {
//                    directory.mkdirs();
//                }

                final PlaycorderRecorder recorder = new PlaycorderRecorder(lastFilename);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d(TAG, "Recording packets");

                            byte[] bytes = new byte[]{1, 2, 3, 4};
                            recorder.SavePacket(bytes);
                            Thread.sleep(500);

                            recorder.SavePacket(bytes);
                            Thread.sleep(500);

                            recorder.SavePacket(bytes);
                            Thread.sleep(500);

                            Log.d(TAG, "Recording done");

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                        recorder.Close();
                    }
                }).start();


            }
        });
    }

    @Override
    protected void onDestroy() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "Permission Granted");

                } else {
                    Log.d(TAG, "Permission Denied");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
