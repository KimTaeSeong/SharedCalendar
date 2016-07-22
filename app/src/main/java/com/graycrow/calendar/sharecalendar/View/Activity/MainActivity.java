package com.graycrow.calendar.sharecalendar.View.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.graycrow.calendar.sharecalendar.R;
import com.graycrow.calendar.sharecalendar.Service.QuickstartPreferences;
import com.graycrow.calendar.sharecalendar.Service.RegistrationIntentService;
import com.graycrow.calendar.sharecalendar.View.Fragment.CalendarFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private int mWidthPixels, mHeightPixels;

    private String mMailAddress;
    public Date mSelectedDate;

    /**
     * LocalBroadcast 리시버를 정의한다. 토큰을 획득하기 위한 READY, GENERATING, COMPLETE 액션에 따라 UI에 변화를 준다.
     */
    public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if(action.equals(QuickstartPreferences.REGISTRATION_READY)){
                    // 액션이 READY일 경우
                    //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                    //mInformationTextView.setVisibility(View.GONE);
                } else if(action.equals(QuickstartPreferences.REGISTRATION_GENERATING)){
                    // 액션이 GENERATING일 경우
                    //mRegistrationProgressBar.setVisibility(ProgressBar.VISIBLE);
                   // mInformationTextView.setVisibility(View.VISIBLE);
                    //mInformationTextView.setText(getString(R.string.registering_message_generating));
                } else if(action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)){
                    // 액션이 COMPLETE일 경우
                    //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                    //mRegistrationButton.setText(getString(R.string.registering_message_complete));
                    //mRegistrationButton.setEnabled(false);
                    String token = intent.getStringExtra("token");
                    //mInformationTextView.setText(token);
                }

            }
        };
    }

    /**
     * Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
     */
    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * 앱이 화면에서 사라지면 등록된 LocalBoardcast를 모두 삭제한다.
     */
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Google Play Service를 사용할 수 있는 환경이지를 체크한다.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("Google Play Service : ", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * 앱이 실행되어 화면에 나타날때 LocalBoardcastManager에 액션을 정의하여 등록한다.
     */
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long now = System.currentTimeMillis();
        mSelectedDate = new Date(now);

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        setContentView(R.layout.activity_main);

        // 1. Push 토큰 인스턴트 생성
        registBroadcastReceiver();
        getInstanceIdToken();



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
