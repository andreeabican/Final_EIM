package practicaltest01var07.eim.systems.cs.pub.ro.weatherapp;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by andreeabican on 20/05/2018.
 */

public class ClientThread extends Thread {

    private String className = "[CLIENT THREAD]";

    private Socket socket;
    private int port;
    private String address;
    private String URL;
    private TextView contentView;

    public ClientThread(int port, String URL, TextView contentView) {
        this.port = port;
        this.address = "localhost";
        this.URL = URL;
        this.contentView = contentView;
    }

    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e("INFO", className + " Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e("INFO", className + " Buffered Reader / Print Writer are null!");
                return;
            }

            printWriter.println(URL);
            printWriter.flush();

            String contentInformation;
            while ((contentInformation = bufferedReader.readLine()) != null) {
                final String finalizedContentInformation = contentInformation;
                contentView.post(new Runnable() {
                    @Override
                    public void run() {
                        contentView.setText(finalizedContentInformation);
                    }
                });
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, className + " An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }
}

