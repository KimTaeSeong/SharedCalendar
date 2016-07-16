package com.example.graycrow.sharecalendar.View.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

import com.example.graycrow.sharecalendar.R;
import com.example.graycrow.sharecalendar.View.Fragment.CalendarFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private int mWidthPixels, mHeightPixels;

    private String mMailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        setContentView(R.layout.activity_main);

        // 1. mail 주소 가지고 옴
        SharedPreferences pref = getSharedPreferences("userinfo", MODE_PRIVATE);
        mMailAddress = pref.getString("email", "");

        //NetManager.getInstance().getTodayParkingData(2013, 10, 4, 7244);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

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
        fmTransaction_calendar.replace(R.id.container, new CalendarFragment());
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
        //-----------------------------------------------------------//
/*
        fragmentTransaction.replace(R.id.container, new Fragment_ad());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();*/
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
        } /*else if (id == R.id.menu2) {

        }else if (id == R.id.menu4) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // 제목셋팅
            alertDialogBuilder.setTitle("공지 사항");
            alertDialogBuilder.setMessage(" 1.거주자 우선주차 공유시 거주자 우선주차제비용 할인\n\n" +
                    " 2.거주자우선주차장에 불법 주차시 견인 조치\n\n" +
                    " 3.관련문의 : 교통과 박정자 053-661-3046\n\n" +
                    " 4.광고문의 : 계명대학교 교통공학과 ");
            // AlertDialog 셋팅
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            // 다이얼로그 생성
            AlertDialog alertDialog = alertDialogBuilder.create();

            // 다이얼로그 보여주기
            alertDialog.show();
        } else if (id == R.id.menu5) {

        } else if (id == R.id.menu6) {

        }else if (id == R.id.menu7) {
            ConfigMode.switchMode();

            if(ConfigMode.isConfigMode()) {
                Toast.makeText(this, "관리자 모드로 전환되었습니다", Toast.LENGTH_SHORT).show();
                item.setTitle("사용자모드로 전환");
            }
            else {
                Toast.makeText(this, "사용자 모드로 전환되었습니다", Toast.LENGTH_SHORT).show();
                item.setTitle("관리자모드로 전환");
            }
        }*/
/*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);*/
        return true;
    }
}
