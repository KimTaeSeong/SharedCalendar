package com.example.graycrow.sharecalendar.Network;

import android.os.AsyncTask;
import android.util.Log;

import com.example.graycrow.sharecalendar.Model.ScheduleInfo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by graycrow on 2016-07-20.
 */
public class NetManger {

    private class HttpReqeusetTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return requestHttp(urls[0], urls[1], urls[2]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        }
    }

    private String requestHttp(String myurl, String method, String body) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod(method);
            conn.setDoInput(true);

            if (method == "POST") {
                conn.setDoOutput(true);
                try {
                    OutputStream os = conn.getOutputStream();
                    os.write(body.getBytes("euc-kr"));
                    os.flush();
                    os.close();
                }catch (Exception e)
                {
                    Log.e("ss","ss");
                }
/*
                BufferedReader br = new BufferedReader( new OutputStreamReader( conn.getInputStream(), "EUC-KR" ), conn.getContentLength() );
                String buf;

                // 표준출력으로 한 라인씩 출력
                while( ( buf = br.readLine() ) != null ) {
                    System.out.println( buf );
                }

                // 스트림을 닫는다.
                br.close();*/
            }
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private static NetManger instance = new NetManger();


    /*  생성자 */
    private NetManger () {}

    /* 조회 method */
    public static NetManger getInstance () {
        return instance;
    }

    /* 서버로 전송 */
    public void sendToServer (ScheduleInfo scheduleInfo) {
        Gson gson = new Gson();
        String json = gson.toJson(scheduleInfo);
        new HttpReqeusetTask().execute("http://52.78.25.63:8080/api/kimts", "POST", json);
    }
}
