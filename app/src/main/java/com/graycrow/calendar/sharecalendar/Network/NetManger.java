package com.graycrow.calendar.sharecalendar.Network;

import android.os.AsyncTask;
import android.util.Log;

import com.graycrow.calendar.sharecalendar.Model.ScheduleInfo;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

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

        try {
            // 1. Http 셋팅
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod(method);
            conn.setDoInput(true);

            // 2. POST일 경우 body 전송
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
            }

            // 3. HTTP 요청 후 응답을 받음
            conn.connect();
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                is = conn.getInputStream();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] byteBuffer = new byte[1024];
                byte[] byteData = null;
                int nLength = 0;

                while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                    baos.write(byteBuffer, 0, nLength);
                }
                byteData = baos.toByteArray();
                return new String(byteData);
            }
            return null;
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
        try {
            new HttpReqeusetTask().execute("http://52.78.25.63:8080/api/" + scheduleInfo.email, "POST", json).get();
        }catch (InterruptedException e){
            Log.e("InterruptedException : ", e.getMessage());
        }
        catch (ExecutionException e){
            Log.e("ExecutionException : ", e.getMessage());
        }
    }

    /* 서버 데이터 삭제 요청 */
    public void delFromServer (String email, long id) {
        try {
            new HttpReqeusetTask().execute("http://52.78.25.63:8080/api/del/" + email + "/" + Long.toString(id), "GET", null).get();
        }catch (InterruptedException e){
            Log.e("InterruptedException : ", e.getMessage());
        }
        catch (ExecutionException e){
            Log.e("ExecutionException : ", e.getMessage());
        }
    }

    /* 서버 회원 등록 */
    public void joinToServer(String email, String token)
    {
        try {
            new HttpReqeusetTask().execute("http://52.78.25.63:8080/api/" + email + "/" + token, "GET", null).get();
        }catch (InterruptedException e){
            Log.e("InterruptedException : ", e.getMessage());
        }
        catch (ExecutionException e){
            Log.e("ExecutionException : ", e.getMessage());
        }
    }
}
