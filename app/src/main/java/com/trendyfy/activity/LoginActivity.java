package com.trendyfy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trendyfy.R;
import com.trendyfy.customviews.CustomProgressDialog;
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


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private Activity mActivity;
    private EditText edt_email;
    private EditText edt_password;
    private CheckBox chkShowPassword;
    private RelativeLayout relative_create_an_account;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mActivity = LoginActivity.this;

        init();
    }

    private void init() {
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_password = (EditText) findViewById(R.id.edt_pwd);
        relative_create_an_account= (RelativeLayout) findViewById(R.id.relative_create_an_account);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        chkShowPassword = (CheckBox) findViewById(R.id.chk_show_password);
        TextView txt_forgot_password = (TextView) findViewById(R.id.txt_forgot_password);


        relative_create_an_account.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        txt_forgot_password.setOnClickListener(this);


        chkShowPassword.setOnCheckedChangeListener(chkShowPasswordChanged);



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (validateForm()) {
                    callLoginWebservice();
                }
                break;

            case R.id.relative_create_an_account:
                Intent intent = new Intent(mActivity, SignupActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_forgot_password:
                Intent forgetPasswordIntent = new Intent(mActivity, ForgetPasswordActivity.class);
                startActivity(forgetPasswordIntent);
                break;
        }

    }

    private boolean validateForm() {
        if (edt_email.getText().toString().equalsIgnoreCase("")) {
            Utils.customDialog("Please enter username.", mActivity);
            return false;
        } else if (edt_password.getText().toString().equalsIgnoreCase("")) {
            Utils.customDialog("Please enter password.", mActivity);
            return false;
        }
        return true;
    }

    private void callLoginWebservice() {
        try {
            if (Utils.isOnline(mActivity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("CustomerEmail", edt_email.getText().toString());
                params.put("CustomerPassword", edt_password.getText().toString());

                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.CUSTOMER_LOGIN, params,
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

                                        setResult();
                                        //onBackPressed();
                                    } else {
                                        Utils.customDialog("Username and password are not correct.", mActivity);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                CustomProgressDialog.hideProgressDialog();

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

    private CompoundButton.OnCheckedChangeListener chkShowPasswordChanged = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                edt_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                edt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        }
    };


    private void setResult() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
