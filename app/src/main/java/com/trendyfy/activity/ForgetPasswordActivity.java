package com.trendyfy.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trendyfy.R;
import com.trendyfy.customviews.CustomProgressDialog;
import com.trendyfy.model.LoginResponseModel;
import com.trendyfy.utility.Constants;
import com.trendyfy.utility.Utils;
import com.trendyfy.volley.Application;
import com.trendyfy.volley.CustomJsonRequest;
import com.trendyfy.volley.WebserviceConstants;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ForgetPasswordFragment";
    private EditText edt_email;
    private EditText edt_mobile_no;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        mActivity = this;

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        // set the toolbar title
        getSupportActionBar().setTitle("Forgot Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void init() {
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_mobile_no = (EditText) findViewById(R.id.edt_mobile_no);

        Button btn_forget_password = (Button) findViewById(R.id.btn_forget_password);
        btn_forget_password.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_forget_password:
                if (validateForm()) {
                    callForgetPasswordWebservice();
                }
                break;
        }

    }

    private boolean validateForm() {
        if (edt_email.getText().toString().equalsIgnoreCase("")) {
            Utils.customDialog("Please enter email id.", mActivity);
            return false;
        } else if (edt_mobile_no.getText().toString().equalsIgnoreCase("")) {
            Utils.customDialog("Please enter mobile number.", mActivity);
            return false;
        }
        return true;
    }


    private void callForgetPasswordWebservice() {
        try {
            if (Utils.isOnline(mActivity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("CustomerEmail", edt_email.getText().toString());
                params.put("CustomerMobileNo", edt_mobile_no.getText().toString());

                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.CUSTOMER_PASSWORD_JSON, params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {

                                    Type type = new TypeToken<ArrayList<LoginResponseModel>>() {
                                    }.getType();
                                    List<LoginResponseModel> lst = new Gson().fromJson(response,
                                            type);
                                    LoginResponseModel loginDTO = lst.get(0);


                                    if (loginDTO != null && loginDTO.getPassword() != null) {

                                        // Save user info in shared preference
                                        //AppPreference.putObjectIntoPref(mActivity, loginDTO, PreferenceHelp.USER_INFO);
                                        sendMessageWebservice(edt_mobile_no.getText().toString().trim(),
                                                "Your password is '" + loginDTO.getPassword() + "' .");
                                    }else{
                                        CustomProgressDialog.hideProgressDialog();
                                        Toast.makeText(mActivity, "Invalid User." , Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    CustomProgressDialog.hideProgressDialog();
                                    e.printStackTrace();
                                }
                                //;  CustomProgressDialog.hideProgressDialog();

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                CustomProgressDialog.hideProgressDialog();
                                Utils.showExceptionDialog(mActivity);
                            }
                        }
                );


                Application.getInstance().addToRequestQueue(jsonRequest);
                jsonRequest.setRetryPolicy(new DefaultRetryPolicy(150000, 0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }
        } catch (Exception e) {
            e.printStackTrace();
            CustomProgressDialog.hideProgressDialog();
        }
    }


    private void sendMessageWebservice(String recipientNo, String body) {
        try {
            if (Utils.isOnline(mActivity)) {
                String smsUrl = WebserviceConstants.SEND_SMS_WEBSERVICE_URL + recipientNo +
                        WebserviceConstants.MESSAGE_URL;
                String encodedUrl= URLEncoder.encode( body + " " + Constants.QUERY_MESSAGE, "UTF-8");
                smsUrl += encodedUrl;

                Log.i(TAG, smsUrl);


                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.GET,
                        smsUrl, null,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    CustomProgressDialog.hideProgressDialog();
                                    mActivity.finish();
                                    mActivity.startActivity(new Intent(mActivity, HomeActivity.class));
                                    Toast.makeText(mActivity, "Password has been sent on mobile.", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    CustomProgressDialog.hideProgressDialog();
                                    e.printStackTrace();
                                }
                                CustomProgressDialog.hideProgressDialog();

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                CustomProgressDialog.hideProgressDialog();
                                Toast.makeText(mActivity, "Password has been sent on mobile.", Toast.LENGTH_SHORT).show();
                                mActivity.finish();
                                mActivity.startActivity(new Intent(mActivity, HomeActivity.class));

                                //Utils.showExceptionDialog(mActivity);
                            }
                        }
                );


                Application.getInstance().addToRequestQueue(jsonRequest);
                jsonRequest.setRetryPolicy(new DefaultRetryPolicy(150000, 0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }
        } catch (Exception e) {
            e.printStackTrace();
            CustomProgressDialog.hideProgressDialog();
        }
    }
}
