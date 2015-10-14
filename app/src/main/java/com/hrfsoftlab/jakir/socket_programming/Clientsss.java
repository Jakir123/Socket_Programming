package com.hrfsoftlab.jakir.socket_programming;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Clientsss extends AppCompatActivity {

    public final static String
            FILE_TO_RECEIVED = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp/";

    private static final String TAG = "Client_Activity";
    private EditText etServerIp;
    private EditText etMsg;
    private Button btnConnectClients;
    private Button btnSendMsg;

    private String serverIpAddress = "";
    private boolean connected = false;

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientsss);


        Log.e(TAG, "ONCREATE METHOD");

        initializations();
        eventClickListener();
    }

    private void eventClickListener() {
        btnConnectClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!connected) {
                    serverIpAddress = etServerIp.getText().toString().trim();
                    connectsClient();
                }
            }
        });

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = etMsg.getText().toString().trim();

                ClientResponseTask clientResponseTask=new ClientResponseTask(msg);
                clientResponseTask.execute();

            }
        });
    }

    private void connectsClient() {
        if (!serverIpAddress.equals("")) {
            Thread cThread = new Thread(new ClientThread());
            cThread.start();
        }
    }


    private void initializations() {
        etServerIp = (EditText) findViewById(R.id.etServerIp);
        etMsg = (EditText) findViewById(R.id.etMsg);
        btnSendMsg = (Button) findViewById(R.id.btnMsgSend);
        btnConnectClients = (Button) findViewById(R.id.btnConnect);

    }


    private class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Log.e(TAG, "C: Connecting...");
                socket = new Socket(serverAddr, ServerActivity.SERVERPORT);
                connected = true;
                Log.e(TAG, "C: Connected..." + socket);

            } catch (Exception e) {
                Log.e(TAG, "C: Error", e);
                connected = false;
            }
        }
    }


    @Override
    protected void onDestroy() {
        if (socket != null) try {
            socket.close();
            Log.e(TAG, "C: Socket Closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }


    protected class ClientResponseTask extends AsyncTask<Void,Void,Void> {
        String msg;

        ClientResponseTask(String msg){
            this.msg=msg;

        }

        @Override
        protected Void doInBackground(Void... params) {
            if (connected) {
                try {
                    Log.e("ClientActivity", "C: Sending command.");
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                            .getOutputStream())), true);
                    // WHERE YOU ISSUE THE COMMANDS
                    out.println(msg);
                    out.flush();
                    Log.e("ClientActivity", "C: Sent.");

                    BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                    DataInputStream dis = new DataInputStream(bis);

                    Log.e(TAG, "Start File Reading From Server!");
                    int filesCount = dis.readInt();
                    Log.e(TAG, "Files Count: " + filesCount);
                    File[] files = new File[filesCount];

                    for (int i = 0; i < filesCount; i++) {
                        long fileLength = dis.readLong();
                        Log.e(TAG, "File Length: " + fileLength);

                        String fileName = dis.readUTF();
                        Log.e(TAG, "File Name: " + fileName);

                        files[i] = new File(FILE_TO_RECEIVED + "/" + fileName);

                        FileOutputStream fos = new FileOutputStream(files[i]);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);

                        for (int j = 0; j < fileLength; j++) bos.write(bis.read());
                        Log.e(TAG, "Reading Content Of " + fileName + " From Socket Is Done!");
                        bos.close();
                    }

                    dis.close();
                    Log.e(TAG, "Reading Content Of All File From Socket Is Done!");
                    connected=false;

                } catch (Exception e) {
                    Log.e(TAG, "Error", e);
                }
            }
            else {
                connectsClient();
            }
            return null;
        }
    }
}