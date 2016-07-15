package com.example.graycrow.sharecalendar.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by graycrow on 2016-07-15.
 */
public class ScheduleInfo {
    public List<ScheduleInfo> scheduleInfoList; // 일정 리스트
    public Date date;                   // 날짜 정보

    /* 생성자 */
    public ScheduleInfo()
    {
        scheduleInfoList = new ArrayList<ScheduleInfo>();
    }
}
