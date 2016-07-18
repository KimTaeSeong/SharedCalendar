package com.example.graycrow.sharecalendar.View.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graycrow.sharecalendar.Model.DBManager;
import com.example.graycrow.sharecalendar.Model.ScheduleInfo;
import com.example.graycrow.sharecalendar.Model.WEATHER;
import com.example.graycrow.sharecalendar.R;

import java.sql.SQLException;
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

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
    private final DateFormat timeFormat = new SimpleDateFormat("hh : mm a");

    private TextView stDateTextView;
    private TextView stTimeTextView;
    private TextView edDateTextView;
    private TextView edTimeTextView;
    private EditText titleEditText;
    private Spinner  mColorSpinner;
    private Spinner  mWeatherSpinner;
    private DBManager dbManager;

    public void init()
    {
        // 1. Spinner 설정
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

        // 2. 시작날짜와 종료날짜 default 설정 후 textview에 출력
        mStartdDate = (Date)mSelectedDate.clone();
        mStartdDate.setMinutes((mStartdDate.getMinutes() / 5) * 5 + 5); // 현재 시간 + 5분 더
        mEndDate = (Date)mStartdDate.clone();
        mEndDate.setHours(mEndDate.getHours() + 1);

        stDateTextView = (TextView)findViewById(R.id.st_date_textview);
        stTimeTextView = (TextView)findViewById(R.id.st_time_textview);
        edDateTextView = (TextView)findViewById(R.id.ed_date_textview);
        edTimeTextView = (TextView)findViewById(R.id.ed_time_textview);

        stDateTextView.setText(dateFormat.format(mStartdDate));
        stTimeTextView.setText(timeFormat.format(mStartdDate));
        edDateTextView.setText(dateFormat.format(mEndDate));
        edTimeTextView.setText(timeFormat.format(mEndDate));

        // 3. 기타 컨트롤
        titleEditText = (EditText)findViewById(R.id.edit_title);
        mColorSpinner = (Spinner)this.findViewById(R.id.color_spinner);
        mWeatherSpinner = (Spinner)this.findViewById(R.id.weather_spinner);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // 1. 기본 툴바 title 제거
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("");

        // 2. 이전 액티비티로 부터 날짜를 가지고 옴
        DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
        Intent intent = getIntent();
        try {
            mSelectedDate = sdFormat.parse(intent.getExtras().getString("selectedDate"));
        }catch (ParseException pe){
            // 예외 발생시 오늘 날짜로 지정
            long now = System.currentTimeMillis();
            mSelectedDate = new Date(now);
        }

        // 3. Control set
        init();
        dbManager = new DBManager(this);
    }

    private ScheduleInfo saveSchedule() throws SQLException
    {
        dbManager.openDataBase();
        //dbManager.deleteAll();
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.title = titleEditText.getText().toString();
        scheduleInfo.st_time = mStartdDate;
        scheduleInfo.ed_time = mEndDate;
        scheduleInfo.color = mColorSpinner.getSelectedItem().toString();
        String weatherStr = mWeatherSpinner.getSelectedItem().toString();

        if(weatherStr == "없음")
            scheduleInfo.weather = WEATHER.NONE;
        else if(weatherStr == "맑음")
            scheduleInfo.weather = WEATHER.CLEAR;
        else if(weatherStr == "구름")
            scheduleInfo.weather = WEATHER.CLOUD;
        else if(weatherStr == "비")
            scheduleInfo.weather = WEATHER.RAIN;
        else if(weatherStr == "눈")
            scheduleInfo.weather = WEATHER.SNOW;

        SharedPreferences pref = getSharedPreferences("userinfo", MODE_PRIVATE);
        scheduleInfo.email = pref.getString("email", "");

        long id = dbManager.insertSchedule(scheduleInfo);
        scheduleInfo.id = id;

        List<ScheduleInfo> sc2 = dbManager.selectAllSchedule(scheduleInfo.email);

        return scheduleInfo;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.infrag_create_btn:
                try {
                    // 1. 옵션 정보들을 가지고 와 데이터베이스에 저장
                    ScheduleInfo scheduleInfo = saveSchedule();

                    // 2. 이를 서버에 전송

                    Toast.makeText(getApplicationContext(), "일정 추가 완료", Toast.LENGTH_LONG);
                }catch (SQLException sqle)
                {
                    Log.e("DB Error : ", sqle.getMessage());
                    Toast.makeText(getApplicationContext(), "DB Error!", Toast.LENGTH_LONG);
                }

                this.onBackPressed();

                break;
            case R.id.infrag_cancel_btn:

                this.onBackPressed();

                break;
        }
    }
}
