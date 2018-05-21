package practicaltest01var07.eim.systems.cs.pub.ro.weatherapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ServerThread serverThread;

    private EditText serverPortEditText;
    private Button serverConnectButton;

    private Button getContentButton;
    private EditText urlEditText;
    private TextView contentTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(Constants.TAG, "[MAIN ACTIVITY] Started main activity");

        serverPortEditText = (EditText) findViewById(R.id.server_port_edit);
        serverConnectButton = (Button) findViewById(R.id.connect_button);
        serverConnectButton.setOnClickListener(new ConnectButtonClickListener());

        getContentButton = (Button) findViewById(R.id.get_content_button);
        urlEditText = (EditText) findViewById(R.id.url_edit);
        contentTextView = (TextView) findViewById(R.id.content_text);
        getContentButton.setOnClickListener(new GetContentClickListener());
    }

    @Override
    protected void onDestroy() {
        Log.i("", "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }

    private class ConnectButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Log.d(Constants.TAG, "[MAIN ACTIVITY] Pressed Connect button");
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "No server address port", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                serverThread = new ServerThread(Integer.parseInt(serverPort));
                serverThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetContentClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Log.d(Constants.TAG, "[MAIN ACTIVITY] Pressed \"GetContent\" button");

            String port = serverPortEditText.getText().toString();
            if (port == null || port.isEmpty()) {
                Toast.makeText(getApplicationContext(), "No port entry", Toast.LENGTH_SHORT).show();
                return;
            }
            String url = urlEditText.getText().toString();
            if (url == null || url.isEmpty()) {
                Toast.makeText(getApplicationContext(), "No url entry", Toast.LENGTH_SHORT).show();
                return;
            }
            ClientThread clientThread = new ClientThread(Integer.valueOf(port), url, contentTextView);
            clientThread.start();
        }
    }

}
