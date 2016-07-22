package com.calendar.graycrow.sharecalendar.View.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.calendar.graycrow.sharecalendar.R;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {
    private CallbackManager mCallbackManager;
    private LoginButton loginButton;

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);

        // 1. 로그인 확인
        if(isLoggedIn())
        {
            try {
                // 1-1. 만약 로그아웃 요청이 들어온 경우
                Intent intent = getIntent();
                String isLogout = intent.getExtras().getString("isLogout");
                LoginManager.getInstance().logOut();
            } catch (NullPointerException e) {
                // 1-2. 로그아웃 요청이 없을 경우 바로 달력화면으로 이동
                Intent intentSubActivity = new Intent(this, MainActivity.class);
                intentSubActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentSubActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentSubActivity);
            }
        }

        // 2. 로그인이 되어 있지 않다면 LoginActivity 보여줌
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        // 3. facebook login Callback 함수 등록
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), "로그인 하셨습니다!", Toast.LENGTH_LONG);
                Log.v("CheckLogin", "successfully connected to facebook");

                // 4-1. 로그인된 메일주소를 가지고 옴
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                                try {
                                    // 4-2. 이메일 주소를 저장
                                    SharedPreferences pref = getSharedPreferences("userinfo", MODE_PRIVATE);
                                    String email = object.getString("email");
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("email", email);
                                    editor.commit();

                                    Log.v("CheckLogin-email", email);
                                } catch (JSONException j) {
                                    Toast.makeText(getApplicationContext(), "에러 발생!", Toast.LENGTH_LONG);
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email");
                request.setParameters(parameters);
                request.executeAsync();

                // 5. 메인 페이지로 이동
                Intent intentSubActivity = new Intent(LoginActivity.this, MainActivity.class);
                intentSubActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentSubActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentSubActivity);
            }
            @Override
            public void onCancel() {
                // App code
                Log.v("CheckLogin", " connection to facebook cancelled");
            }
            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(getApplicationContext(), "에러 발생!", Toast.LENGTH_LONG);
                Log.v("CheckLogin", "Error on  connection to facebook");
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
