package com.example.graycrow.sharecalendar.View.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.graycrow.sharecalendar.Model.COLORS;
import com.example.graycrow.sharecalendar.Model.DBManager;
import com.example.graycrow.sharecalendar.Model.ScheduleInfo;
import com.example.graycrow.sharecalendar.Model.WEATHER;
import com.example.graycrow.sharecalendar.Network.NetManger;
import com.example.graycrow.sharecalendar.R;
import com.google.gson.Gson;

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
        colorList.add("없음");
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

    /* 스케줄을 데이터베이스에 저장 */
    private ScheduleInfo saveSchedule() throws SQLException
    {
        dbManager.openDataBase();

        if(mStartdDate.getTime() > mEndDate.getTime())
        {
            Toast.makeText(getApplicationContext(), "시작시간은 종료시간보다 작아야 합니다", Toast.LENGTH_SHORT);
            return null;
        }

        //dbManager.deleteAll();
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.title = titleEditText.getText().toString();
        scheduleInfo.st_time = mStartdDate;
        scheduleInfo.ed_time = mEndDate;
        String colorStr = mColorSpinner.getSelectedItem().toString();

        if(colorStr == "없음")
            scheduleInfo.color = COLORS.NONE;
        else if(colorStr == "파랑")
            scheduleInfo.color = COLORS.BLUE;
        else if(colorStr == "빨강")
            scheduleInfo.color = COLORS.RED;
        else if(colorStr == "초록")
            scheduleInfo.color = COLORS.GREEN;
        else if(colorStr == "노랑")
            scheduleInfo.color = COLORS.YELLOW;

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

        return scheduleInfo;
    }

    /* 날짜 클릭 시 DatePicker 호출 */
    public void onClickDatePicker(View v) {
        DatePickerDialog dialog;
        switch (v.getId()) {
            case R.id.st_date_textview :
                // 1-1. 시작 날짜 조정
                dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mStartdDate.setYear(year - 1900);
                        mStartdDate.setMonth(monthOfYear);
                        mStartdDate.setDate(dayOfMonth);
                        stDateTextView.setText(dateFormat.format(mStartdDate));

                        // 1-2. 종료 날짜 자동 일치
                        mEndDate.setYear(mStartdDate.getYear());
                        mEndDate.setMonth(mStartdDate.getMonth());
                        mEndDate.setDate(mStartdDate.getDate());
                        edDateTextView.setText(dateFormat.format(mEndDate));
                    }
                }, mStartdDate.getYear() + 1900, mStartdDate.getMonth(), mStartdDate.getDate());
                dialog.show();
                break;
            case R.id.ed_date_textview :
                // 2. 종료 날짜 조정
                dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mEndDate.setYear(year - 1900);
                        mEndDate.setMonth(monthOfYear);
                        mEndDate.setDate(dayOfMonth);
                        edDateTextView.setText(dateFormat.format(mEndDate));
                    }
                }, mEndDate.getYear() + 1900, mEndDate.getMonth(), mEndDate.getDate());
                dialog.show();
                break;
        }
    }

    /* 시간 클릭 시 TimePicker 호출 */
    public void onClickTimePicker(View v) {
        TimePickerDialog dialog;
        switch (v.getId()) {
            case R.id.st_time_textview :
                // 1-1. 시작 시간 조정
                dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mStartdDate.setHours(hourOfDay);
                        mStartdDate.setMinutes(minute);
                        stTimeTextView.setText(timeFormat.format(mStartdDate));

                        // 1-2. 종료 시간을 시작 시간 +1 만큼 더함함
                        mEndDate = (Date)mStartdDate.clone();
                        mEndDate.setHours(mEndDate.getHours() + 1);
                        edTimeTextView.setText(timeFormat.format(mEndDate));
                    }
                }, mStartdDate.getHours(), mStartdDate.getMinutes(), false);
                dialog.show();
                break;
            case R.id.ed_time_textview :
                // 2. 종료 시간 조정
                dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mEndDate.setHours(hourOfDay);
                        mEndDate.setMinutes(minute);
                        edTimeTextView.setText(timeFormat.format(mEndDate));
                    }
                }, mEndDate.getHours(), mEndDate.getMinutes(), false);
                dialog.show();
                break;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.infrag_create_btn:
                try {
                    // 1. 옵션 정보들을 가지고 와 데이터베이스에 저장
                    ScheduleInfo scheduleInfo = saveSchedule();
                    if(scheduleInfo != null) {
                        // 2. 이를 서버에 전송
                        //NetManger.getInstance().sendToServer(scheduleInfo);

                        Toast.makeText(getApplicationContext(), "일정 추가 완료", Toast.LENGTH_SHORT);
                        this.onBackPressed();
                    }
                }catch (SQLException sqle)
                {
                    Log.e("DB Error : ", sqle.getMessage());
                    Toast.makeText(getApplicationContext(), "DB Error!", Toast.LENGTH_LONG);
                }

                break;
            case R.id.infrag_cancel_btn:

                this.onBackPressed();

                break;
        }
    }
}
