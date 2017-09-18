package com.trendyfy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trendyfy.R;
import com.trendyfy.model.LoginResponseModel;
import com.trendyfy.preference.AppPreference;
import com.trendyfy.preference.PreferenceHelp;
import com.trendyfy.utility.Utils;
import com.trendyfy.volley.Application;
import com.trendyfy.volley.CustomJsonRequest;
import com.trendyfy.volley.WebserviceConstants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private Activity mActivity;
    private Timer timer;
    private long splashDelay = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mActivity = SplashActivity.this;

        init();
    }

    private void init() {
        try {
            final TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(mActivity, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            };
            timer = new Timer();
            LoginResponseModel loginResponseModel = AppPreference.getObjectFromPref(mActivity,
                    PreferenceHelp.USER_INFO);
            if (null != loginResponseModel) {
                if (Utils.isOnline(mActivity)) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("CustomerId", loginResponseModel.getA_Id());
                    CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                            WebserviceConstants.CONNECTION_URL + WebserviceConstants.CUSTOMER_LOGIN_BY_ID_JSON,
                            params,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        if (!response.equalsIgnoreCase("[]")) {
                                            Type type = new TypeToken<ArrayList<LoginResponseModel>>() {
                                            }.getType();
                                            List<LoginResponseModel> lst = new Gson().fromJson(response,
                                                    type);
                                            LoginResponseModel loginDTO = lst.get(0);

                                            // Save user info in shared preference
                                            AppPreference.putObjectIntoPref(mActivity, loginDTO, PreferenceHelp.USER_INFO);
                                            //onBackPressed();
                                            timer.schedule(task, splashDelay);
                                        } else {
                                            Utils.customDialog("Username and password are not correct.", mActivity);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    timer.schedule(task, splashDelay);
                                }
                            }
                    );
                    Application.getInstance().addToRequestQueue(jsonRequest);
                    jsonRequest.setRetryPolicy(new DefaultRetryPolicy(150000, 0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                }
            } else {
                timer.schedule(task, splashDelay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
