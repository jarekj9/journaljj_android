package com.example.j_jan.myapplication;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;



public class MainActivity extends AppCompatActivity {


    //----------mTcpClient and ConnectTask required for TCP connection-------------------------
    TcpClient mTcpClient;

    public class ConnectTask extends AsyncTask<String, String, TcpClient> {

        @Override
        protected TcpClient doInBackground(String... message) {

            //we create a TCPClient object
            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //response received from server
            Log.d("test", "response " + values[0]);
            //process server response here....

        }
    }
    //----------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView editTextMessage = (TextView) findViewById(R.id.editTextMessage);
        final TextView textViewstatus = (TextView) findViewById(R.id.textViewstatus);


        Button buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //sends the message to the server
                if (mTcpClient != null) {

                    String encrypted = "";  //encryption
                    String sourceStr = editTextMessage.getText().toString();
                    try {
                        encrypted = AESUtils.encrypt(sourceStr);
                        Log.d("TEST", "encrypted:" + encrypted);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mTcpClient.sendMessage(encrypted);
                    textViewstatus.setText("Status: Message sent");
                }

                else {
                    textViewstatus.setText("Status: Not connected");
                }

            }

        });

        Button buttonConnect = (Button) findViewById(R.id.buttonConnect);
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mTcpClient != null) {
                    textViewstatus.setText("Status: Already connected");
                }

                else{
                    textViewstatus.setText("Status: Trying connection");
                    new ConnectTask().execute("");
                    try {
                        Thread.sleep(2000);  //delay before checking if it is connected
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (mTcpClient != null){
                        textViewstatus.setText("Status: Connection succesfull");
                    }
                    else{
                        textViewstatus.setText("Status: Connection failed");
                    }
                }

            }

        });

        Button buttonDisconnect = (Button) findViewById(R.id.buttonDisconnect);
        buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTcpClient != null) {
                    mTcpClient.stopClient();
                    mTcpClient=null;
                    textViewstatus.setText("Status: Disconnected");
                }
                else{
                    textViewstatus.setText("Status: Not connected");
                }
            }

        });

    }






}

