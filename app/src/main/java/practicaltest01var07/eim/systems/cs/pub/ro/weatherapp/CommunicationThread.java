package practicaltest01var07.eim.systems.cs.pub.ro.weatherapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by andreeabican on 20/05/2018.
 */

public class CommunicationThread extends Thread {

    private String className = "[COMMUNICATION THREAD]";

    private Socket clientSocket;
    private ServerThread serverThread;

    public CommunicationThread(Socket socket, ServerThread thread) {
        clientSocket = socket;
        serverThread = thread;
    }

    public void run() {

        if (clientSocket == null) {
            Log.d(Constants.TAG, className + " The client socket is null");
        }
        Log.d(Constants.TAG, className + " The server started a new thread for the a client communication");

        BufferedReader bufferedReader = null;
        PrintWriter printWriter = null;
        try {
            bufferedReader = Utilities.getReader(clientSocket);
            printWriter = Utilities.getWriter(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bufferedReader == null || printWriter == null) {
            Log.e(Constants.TAG, className + " Buffered Reader / Print Writer are null!");
            return;
        }

        String URL = null;
        try {
            URL = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (URL == null || URL.isEmpty()) {
            Log.d(Constants.TAG, className + " No information about the URL could be read from the socket");
        }

        String data = serverThread.getData(URL);

        if ( data != null) {
            Log.d(Constants.TAG, className + " Found information in the cache");
            printWriter.write(data);
            printWriter.flush();
        } else {
            Log.d(Constants.TAG, className + " Did not find info in cache. Getting info from webservice");
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(URL);

            try {
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpGet, responseHandler);

                printWriter.write(pageSourceCode);
                printWriter.flush();
                serverThread.setData(URL, pageSourceCode);
                Log.d(Constants.TAG, "Received content " + pageSourceCode);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (clientSocket != null) {
                    try {
                        clientSocket.close();
                    } catch (IOException ioException) {
                        Log.e(Constants.TAG, className + " An exception has occurred: " + ioException.getMessage());

                    }
                }
            }
        }
    }
}
