package practicaltest01var07.eim.systems.cs.pub.ro.weatherapp;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by andreeabican on 20/05/2018.
 */

public class ServerThread extends Thread {

    private String className = "[SERVER THREAD]";

    private ServerSocket server;
    private HashMap<String, String> data = null;

    public ServerThread(int port) throws IOException {
        data = new HashMap<>();

        try {
            server = new ServerSocket(port);
            Log.d(Constants.TAG, className + " Created server socket");
        }
        catch (Exception e) {
            Log.d("INFO", "Ceva nu a mers bine " + e.getMessage());
        }
    }

    public void run() {
        Log.d(Constants.TAG, className + " Started server thread");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Log.d(Constants.TAG, className + " While not interrupted, the server will accept connections");
                Socket client = server.accept();
                CommunicationThread communicationThread = new CommunicationThread(client, this);
                communicationThread.start();
            } catch (IOException e) {
                Log.d(Constants.TAG, className + " IOException " + e.getMessage());
            }
        }

    }

    public String getData(String URL) {
        return data.get(URL);
    }

    public void setData(String URL, String content) {
        data.put(URL, content);
    }

    public void stopThread() {
        interrupt();
        if (server != null) {
            try {
                server.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, className + " An exception has occurred: " + ioException.getMessage());
            }
        }
    }
}
