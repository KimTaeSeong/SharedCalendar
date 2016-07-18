package com.example.graycrow.sharecalendar.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by graycrow on 2016-07-16.
 */
public class DBManager extends SQLiteOpenHelper {
    private static DBManager sInstance;
    private static String TAG = "DBManager";      // Tag just for the LogCat window
    private static String DB_PATH = "";           //destination path (location) of our database on device
    private static String DB_NAME ="mycalendar";  // Database name
    private static SQLiteDatabase mDataBase;
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
    /*
    public static synchronized DBManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBManager(context.getApplicationContext());
        }
        return sInstance;
    }*/

    public DBManager(Context context) {
        super(context, DB_NAME, null, 1);
        // 1. 데이터베이스 버전에 따른 경로 설정
        if(android.os.Build.VERSION.SDK_INT >= 17){
            DB_PATH = context.getApplicationInfo().dataDir + "/db/";
        }
        else
            DB_PATH = "/data/data/" + context.getPackageName() + "/db/";

        mDataBase = this.getWritableDatabase();
    }

    /*
    Default Table 생성
     */
    public boolean openDataBase() throws SQLException
    {
        // 1. 데이터베이스를 불러옴. 없으면 새로 생성
        mDataBase.execSQL("CREATE TABLE IF NOT EXISTS schedules (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "email          TEXT, "     +
                "start_time     DATETIME, " +
                "end_time       DATETIME, " +
                "color          TEXT, "     +
                "weather        TEXT, "     +
                "title          TEXT, "     +
                "explain        TEXT );");

        mDataBase.execSQL("CREATE TABLE IF NOT EXISTS user_info (" +
                "id             TEXT, "     +
                "join_time      DATETIME);");
        return true;
    }

    /* id와 관련된 모든 db정보를 가지고 옴 */
    public List<ScheduleInfo> selectAllSchedule(String strMail)
    {
        List<ScheduleInfo> list = new ArrayList<ScheduleInfo>();
        Cursor c = this.mDataBase.rawQuery("SELECT * FROM schedules WHERE email='" + strMail + "';" , null);

        while(true)
        {
            if(c.moveToNext() == false)
                break;

            ScheduleInfo tmpData = new ScheduleInfo();
            tmpData.id = c.getLong(c.getColumnIndex("_id"));
            tmpData.email = c.getString(c.getColumnIndex("email"));
            tmpData.title =  c.getString(c.getColumnIndex("title"));
            tmpData.color = c.getString(c.getColumnIndex("color"));
            tmpData.explain =  c.getString(c.getColumnIndex("explain"));
            tmpData.weather =  WEATHER.valueOf(c.getString(c.getColumnIndex("weather")));
            try {
                tmpData.st_time = format.parse(c.getString(c.getColumnIndex("start_time")));
                tmpData.ed_time = format.parse(c.getString(c.getColumnIndex("end_time")));
            }catch (ParseException pe)
            {
                tmpData.st_time = null;
                tmpData.ed_time = null;
            }
            list.add(tmpData);
        }
        return list;
    }
    /* 스케줄 정보 삽입 */
    public long insertSchedule(ScheduleInfo info) throws SQLException
    {
        // 1. 예외 처리
        if(info == null)
            return -1;
        if(info.st_time == null || info.ed_time == null)
            return -1;

        // 2. id를 기준으로 정보 업데이트
        ContentValues insertValues = new ContentValues();
        insertValues.put("email", info.email);
        insertValues.put("title", info.title);
        insertValues.put("explain", info.explain);
        insertValues.put("color", info.color);
        insertValues.put("weather", info.weather.toString());
        insertValues.put("start_time", format.format(info.st_time));
        insertValues.put("end_time", format.format(info.ed_time));

        return mDataBase.insert("schedules", null, insertValues);
    }

    /* 스케줄 정보 업데이트 */
    public void updateSchedule(ScheduleInfo info) throws SQLException
    {
        // 1. 예외 처리
        if(info == null)
            return;
        if(info.st_time == null || info.ed_time == null)
            return;

        // 2. id를 기준으로 정보 업데이트
        ContentValues updateValues = new ContentValues();
        updateValues.put("title", info.title);
        updateValues.put("explain", info.explain);
        updateValues.put("color", info.color);
        updateValues.put("weather", info.weather.toString());
        updateValues.put("start_time", info.st_time.getTime());
        updateValues.put("end_time", info.st_time.getTime());

        mDataBase.update("schedules", updateValues, "_id=" + info.id, null);
    }
    /* 특정 데이터 삭제 */
    public void deleteSchedule(String email, long id)
    {
        mDataBase.delete("schedules", "email=? and _id=?", new String[]{email, Long.toString(id)});
    }

    /* 모든 데이터 삭제*/
    public void deleteAll()
    {
        mDataBase.delete("schedules", null, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public synchronized void close()
    {
        if(mDataBase != null)
            mDataBase.close();
        super.close();
    }
}
