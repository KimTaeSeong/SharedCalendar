package com.graycrow.calendar.sharecalendar.View.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.graycrow.calendar.sharecalendar.R;
import com.graycrow.calendar.sharecalendar.View.Fragment.CalendarFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private int mWidthPixels, mHeightPixels;

    private String mMailAddress;
    public Date mSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long now = System.currentTimeMillis();
        mSelectedDate = new Date(now);
/*
        DBManager dbm = new DBManager(this);
        try {
            dbm.openDataBase();

            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
            dbm.deleteAll();

            ScheduleInfo sc2 = new ScheduleInfo();
            sc2.weather = WEATHER.CLOUD;
            sc2.title = "sc2";
            sc2.explain = "11111";
            sc2.color = "파랑";
            sc2.st_time = date;
            date.setMinutes(date.getMinutes() + 30);
            sc2.ed_time = date;
            sc2.email = "gray";

            ScheduleInfo sc3 = new ScheduleInfo();
            sc3.weather = WEATHER.CLOUD;
            sc3.title = "sc3";
            sc3.explain = "22222";
            sc3.color = "빨강";
            sc3.st_time = date;
            date.setMinutes(date.getMinutes() + 30);
            sc3.ed_time = date;
            sc3.email = "kim";

            dbm.insertSchedule(sc2);
            dbm.insertSchedule(sc3);

            List<ScheduleInfo> list = dbm.selectAllSchedule("gray");

            dbm.deleteAll();

            List<ScheduleInfo> list2 = dbm.selectAllSchedule("gray");
            int x = 10;
        }
        catch (SQLException se)
        {

        }
*/

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        setContentView(R.layout.activity_main);

        // 1. mail 주소 가지고 옴
        SharedPreferences pref = getSharedPreferences("userinfo", MODE_PRIVATE);
        mMailAddress = pref.getString("email", "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // 2. Navigation veiw 생성
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 2-1. user info text를 메일주소로
        View v = navigationView.getHeaderView(0);
        TextView text = (TextView) v.findViewById(R.id.nav_header_name);
        text.setText(mMailAddress);

        //------------------------------------------------------------//
        android.support.v4.app.FragmentManager fm_calendar = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fmTransaction_calendar = fm_calendar.beginTransaction();
        fmTransaction_calendar.replace(R.id.container_main, new CalendarFragment());
        fmTransaction_calendar.commit();
        //-----------------------------------------------------------//

        WindowManager w = getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        mWidthPixels = metrics.widthPixels;
        mHeightPixels = metrics.heightPixels;

        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                mWidthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                mHeightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
        // 상태바와 메뉴바의 크기를 포함
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                mWidthPixels = realSize.x;
                mHeightPixels = realSize.y;
            } catch (Exception ignored) {
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.action_add){
            Intent intentSubActivity = new Intent(MainActivity.this, InputActivity.class);
            DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
            intentSubActivity.putExtra("selectedDate", sdFormat.format(mSelectedDate));
            startActivity(intentSubActivity);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        android.support.v4.app.FragmentManager fragmentManager2 = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();

        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            Intent intentSubActivity = new Intent(this, LoginActivity.class);
            intentSubActivity.putExtra("isLogout", "true");
            intentSubActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intentSubActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentSubActivity);
            Toast.makeText(getApplicationContext(), "로그아웃 하셨습니다!", Toast.LENGTH_LONG);
        }

        return true;
    }

    /* Titlebar에서 발생하는 클릭 이벤트를 플래그먼트로 넘겨줌 */
    public void onFragmentViewArrowClick(View v) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_main);
        if (fragment != null && fragment.isVisible()) {
            if (fragment instanceof CalendarFragment) {
                ((CalendarFragment) fragment).moveTextOnClick(v);
            }
        }
    }
}
