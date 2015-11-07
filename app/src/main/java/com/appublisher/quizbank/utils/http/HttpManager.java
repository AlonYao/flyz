package com.appublisher.quizbank.utils.http;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Http管理
 */
public class HttpManager extends AsyncTask<String, Void, String>{

    private static IHttpListener mHttpGetListener;

    public HttpManager(IHttpListener listener) {
        mHttpGetListener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        if (params == null) return null;

        try {
            URL url = new URL(params[0]);
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

            inputStream.close();

            return stringBuilder.toString();

        } catch (IOException e) {
            // Empty
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mHttpGetListener.onResponse(s);
    }
}
