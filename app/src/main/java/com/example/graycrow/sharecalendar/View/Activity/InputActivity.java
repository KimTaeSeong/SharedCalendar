package com.example.graycrow.sharecalendar.View.Activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.graycrow.sharecalendar.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InputActivity extends AppCompatActivity {
    private Date mSelectedDate;
    private Date mStartdDate;
    private Date mEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // 1. 기본 툴바 title 제거
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("");

        // 2. Spinner 설정
        List<String> weatherList = new ArrayList<String>();
        weatherList.add("없음");
        weatherList.add("맑음");
        weatherList.add("구름");
        weatherList.add("비");
        weatherList.add("눈");

        List<String> colorList = new ArrayList<String>();
        colorList.add("파랑");
        colorList.add("빨강");
        colorList.add("초록");
        colorList.add("노랑");

        Spinner weatherSpinner = (Spinner)this.findViewById(R.id.weather_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.weather_row, weatherList);
        weatherSpinner.setAdapter(adapter);

        Spinner colorSpinner = (Spinner)this.findViewById(R.id.color_spinner);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.color_row, colorList);
        colorSpinner.setAdapter(adapter2);

        // 3. 이전 액티비티로 부터 날짜를 가지고 옴
        DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
        Intent intent = getIntent();
        try {
            mSelectedDate = sdFormat.parse(intent.getExtras().getString("selectedDate"));
        }catch (ParseException pe){
            // 예외 발생시 오늘 날짜로 지정
            long now = System.currentTimeMillis();
            mSelectedDate = new Date(now);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.infrag_create_btn:
                // 1. 옵션 정보들을 가지고 옴

                // 2. 데이터베이스에 저장

                // 3. 액티비티 재 호출
                break;
            case R.id.infrag_cancel_btn:
                this.onBackPressed();
                //getActivity().moveTaskToBack(false);
                break;
        }
    }
}
