package com.hrfsoftlab.jakir.socket_programming;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerActivity extends AppCompatActivity {

    public final static String ROOT_DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/Files/MuDi/mxl files";

    private static final String TAG = "ServerActivity";
    private TextView tvServerStatus;

    public static String SERVERIP = "10.0.2.15";

    public static final int SERVERPORT = 8080;

    private Handler handler = new Handler();

    private ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        tvServerStatus = (TextView) findViewById(R.id.tvServerStatus);

        SERVERIP = getLocalIpAddress();

        Thread fst = new Thread(new ServerThread());
        fst.start();

    }

    private String getLocalIpAddress() {
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    public class ServerThread implements Runnable {

        @Override
        public void run() {
            try {
                Log.e(TAG, "Server IP: " + SERVERIP);
                if (SERVERIP != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tvServerStatus.setText("Listening On Ip: " + SERVERIP);
                        }
                    });

                    serverSocket = new ServerSocket(SERVERPORT);
                    while (true) {
                        // LISTEN FOR INCOMING CLIENTS
                        Socket client = serverSocket.accept();

                        Log.e(TAG, "Client Socket: " + client);
                        new Clients_Handler(client, ROOT_DIRECTORY).start();

                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tvServerStatus.setText("Couldn't detect internet connection.");
                        }
                    });
                }
            } catch (IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvServerStatus.setText("Error");
                    }
                });
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            // MAKE SURE YOU CLOSE THE SOCKET UPON EXITING
            serverSocket.close();
            Log.e(TAG,"Socket Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
