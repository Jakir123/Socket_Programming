package com.hrfsoftlab.jakir.socket_programming;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button btnClients;
    private Button btnServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializations();
        eventClickListeners();
    }

    private void eventClickListeners() {
        btnClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Clients Button Pressed");
                Intent gotoClientActivity=new Intent(getApplicationContext(),Clientsss.class);
                gotoClientActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(gotoClientActivity);

            }
        });
        btnServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Server Button Pressed");
                Intent gotoServerActivity=new Intent(getApplicationContext(),ServerActivity.class);
                gotoServerActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(gotoServerActivity);

            }
        });

    }

    private void showToast(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
        Log.e(TAG,s);
    }

    private void initializations() {
        btnClients= (Button) findViewById(R.id.btnClients);
        btnServer= (Button) findViewById(R.id.btnServer);
    }

}
