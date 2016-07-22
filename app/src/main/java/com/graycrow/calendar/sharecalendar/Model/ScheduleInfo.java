package com.graycrow.calendar.sharecalendar.Model;

import com.graycrow.calendar.sharecalendar.Model.CustomDataType.COLORS;
import com.graycrow.calendar.sharecalendar.Model.CustomDataType.WEATHER;

import java.util.Date;

/**
 * Created by graycrow on 2016-07-15.
 */

public class ScheduleInfo {
    public long    id;
    public String  email    = "";
    public String  title    = "";
    public String  explain  = " ";
    public String  loc      = "";
    public COLORS  color    = COLORS.NONE;
    public WEATHER weather  = WEATHER.NONE;
    public Date    st_time;
    public Date    ed_time;
}