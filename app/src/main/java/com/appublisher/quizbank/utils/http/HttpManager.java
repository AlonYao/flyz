package com.appublisher.quizbank.utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Http管理
 */
public class HttpManager implements IHttpListener{

    private static IHttpListener mHttpGetListener;

    public HttpManager(IHttpListener listener) {
        mHttpGetListener = listener;
    }

    @Override
    public void onResponse(String response) {

    }

    public void httpGetString(final String uri) {
        if (uri == null || uri.length() == 0) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(uri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    InputStream inputStream = connection.getInputStream();

                    BufferedReader bufferedReader =
                            new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder stringBuilder = new StringBuilder();
                    String text;

                    while ((text = bufferedReader.readLine()) != null) {
                        stringBuilder.append(text);
                    }

                    mHttpGetListener.onResponse(stringBuilder.toString());

                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
