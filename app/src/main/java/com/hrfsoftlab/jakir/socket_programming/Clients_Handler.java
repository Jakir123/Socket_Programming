package com.hrfsoftlab.jakir.socket_programming;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author Jakir Hossain
 * @version 1.3.0
 * @desc description of the class
 * @link n/a
 * @created on 10/7/2015
 * @updated on
 * @modified by
 * @updated on
 * @since 1.0
 */
public class Clients_Handler extends Thread {

    private static final String TAG = "Clients_Handler";
    private Socket socket;
    private String directory;

    private BufferedReader in = null;

    File folder;
    File[] files;

    public Clients_Handler(Socket clientSocket, String dir) {
        this.socket = clientSocket;
        this.directory = dir;
        folder = new File(directory);
        files = folder.listFiles();
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            String clientSelection;
            while ((clientSelection = in.readLine()) != null) {
                switch (clientSelection) {
                    case "1":
                        break;
                    case "sync":
                        sendFilesToClients();
                        break;
                    default:
                        System.out.println("Incorrect command received.");
                        break;
                }
                in.close();
                break;
            }

        } catch (IOException ex) {

        }

    }

    private void sendFilesToClients() {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(socket.getOutputStream());
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeInt(files.length);

            for (File file : files) {
                long length = file.length();
                Log.e(TAG, "File Length: " + length);
                dos.writeLong(length);

                String name = file.getName();
                Log.e(TAG, "File Name: " + name);
                dos.writeUTF(name);

                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);

                int theByte = 0;
                while ((theByte = bis.read()) != -1) bos.write(theByte);
                Log.e(TAG, "Writing Content Of " + name + " To Socket Is Done!");

                bis.close();
            }
            dos.close();
            Log.e(TAG, "Writing Content Of All File To Socket Is Done!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
