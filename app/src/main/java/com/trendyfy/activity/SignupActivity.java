package com.trendyfy.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trendyfy.R;
import com.trendyfy.customspinner.adapter.SpinnerAdapter;
import com.trendyfy.customspinner.model.SpinnerItemModel;
import com.trendyfy.customviews.CustomProgressDialog;
import com.trendyfy.model.LoginResponseModel;
import com.trendyfy.model.StateModel;
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


public class SignupActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "SignupActivity";
    private Activity mActivity;
    private EditText edt_customer_name;
    private EditText edt_email;
    private EditText edt_password;
    private EditText edt_confirm_pwd;
    private EditText edt_mobile_no;
    private EditText edt_address;
    private EditText edt_city;
    private EditText edt_pincode;
    private List<StateModel> stateModelList;
    private boolean isEdit;
    private LoginResponseModel loginResponseModel;

    private Spinner spin_state;
    private SpinnerItemModel selectedStateItem;
    private SpinnerAdapter stateSpinnerAdapter;
    private List<SpinnerItemModel> spinnerStateItemModelList;
    private int selectedStatePosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mActivity = SignupActivity.this;

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        // set the toolbar title
        getSupportActionBar().setTitle(isEdit ? "Edit Profile" : "Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        init();

        getStateList();
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
        edt_customer_name = (EditText) findViewById(R.id.edt_customer_name);
        edt_password = (EditText) findViewById(R.id.edt_pwd);
        edt_confirm_pwd = (EditText) findViewById(R.id.edt_confirm_pwd);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_mobile_no = (EditText) findViewById(R.id.edt_mobile_no);
        edt_address = (EditText) findViewById(R.id.edt_address);
        edt_city = (EditText) findViewById(R.id.edt_city);
        edt_pincode = (EditText) findViewById(R.id.edt_pincode);
        spin_state = (Spinner) findViewById(R.id.spin_state);

        if (isEdit) {
            loginResponseModel = AppPreference.getObjectFromPref(mActivity, PreferenceHelp.USER_INFO);
            edt_customer_name.setText(loginResponseModel.getName());
            edt_password.setText(loginResponseModel.getPassword());
            edt_confirm_pwd.setText(loginResponseModel.getPassword());
            edt_email.setText(loginResponseModel.getEmail());
            edt_mobile_no.setText(loginResponseModel.getMobileNo());
            edt_address.setText(loginResponseModel.getAddress());
            edt_city.setText(loginResponseModel.getCity());
            edt_pincode.setText(loginResponseModel.getPincode());
            edt_customer_name.setText(loginResponseModel.getName());

        }

        Button btn_signup = (Button) findViewById(R.id.btn_signup);
        btn_signup.setText(isEdit ? "Update" : "SIGN UP");
        btn_signup.setEnabled(!isEdit ? true: false);
        btn_signup.setOnClickListener(this);

        spin_state.setOnItemSelectedListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                if (validateForm()) {
                    callSignUpWebservice();
                }
                break;
        }

    }

    private boolean validateForm() {
        if (edt_customer_name.getText().toString().equalsIgnoreCase("")) {
            Utils.customDialog("Please enter customer name.", mActivity);
            return false;
        } else if (edt_email.getText().toString().equalsIgnoreCase("")) {
            Utils.customDialog("Please enter username.", mActivity);
            return false;
        } else if (edt_password.getText().toString().equalsIgnoreCase("")) {
            Utils.customDialog("Please enter password.", mActivity);
            return false;
        } else if (edt_confirm_pwd.getText().toString().equalsIgnoreCase("")) {
            Utils.customDialog("Please enter confirm password.", mActivity);
            return false;
        } else if (!edt_password.getText().toString().equalsIgnoreCase(edt_confirm_pwd.getText().toString())) {
            Utils.customDialog("Password and Confirm password does not match.", mActivity);
            return false;
        } else if (edt_mobile_no.getText().toString().equalsIgnoreCase("")) {
            Utils.customDialog("Please enter mobile number.", mActivity);
            return false;
        } else if (edt_address.getText().toString().equalsIgnoreCase("")) {
            Utils.customDialog("Please enter address.", mActivity);
            return false;
        } else if (selectedStateItem.EXTRA_TEXT.equalsIgnoreCase("0")) {
            Utils.customDialog("Please enter valid state.", mActivity);
            return false;
        } else if (edt_city.getText().toString().equalsIgnoreCase("")) {
            Utils.customDialog("Please enter city.", mActivity);
            return false;
        } else if (edt_pincode.getText().toString().equalsIgnoreCase("")) {
            Utils.customDialog("Please enter pincode.", mActivity);
            return false;
        }
        return true;
    }

    private void setStateSpinner() {

        spinnerStateItemModelList = new ArrayList<>();

        for (int i = 0; i < stateModelList.size(); i++) {
            SpinnerItemModel item = new SpinnerItemModel();

            item.Id = (i + 1) + "";
            item.TEXT = stateModelList.get(i).getStateName();
            item.STATE = false;
            item.EXTRA_TEXT = stateModelList.get(i).getA_ID();

            if (loginResponseModel != null && loginResponseModel.getStateID() ==
                    Integer.parseInt(stateModelList.get(i).getA_ID())) {
                selectedStatePosition = i;
            }
            spinnerStateItemModelList.add(item);
        }
        setStateSpinnerAdapter();
    }


    private void setStateSpinnerAdapter() {
        stateSpinnerAdapter = new SpinnerAdapter(mActivity, R.layout.spinner_row_item_lay,
                spinnerStateItemModelList);
        spin_state.setAdapter(stateSpinnerAdapter);
        spin_state.setSelection(selectedStatePosition);
    }


    private void getStateList() {
        try {
            if (Utils.isOnline(mActivity)) {
                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.GET_STATE, null,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    Type type = new TypeToken<ArrayList<StateModel>>() {
                                    }.getType();
                                    stateModelList = new Gson().fromJson(response,
                                            type);

                                    // Set Spinner
                                    setStateSpinner();
                                    CustomProgressDialog.hideProgressDialog();
                                } catch (Exception e) {
                                    CustomProgressDialog.hideProgressDialog();
                                    e.printStackTrace();
                                }


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

    private void callSignUpWebservice() {
        try {
            if (Utils.isOnline(mActivity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("CustomerName", edt_customer_name.getText().toString());
                params.put("CustomerEmail", edt_email.getText().toString());
                params.put("CustomerPassword", edt_password.getText().toString());
                params.put("Customeraddress", edt_address.getText().toString());
                params.put("CustomerMobileNo", edt_mobile_no.getText().toString());
                params.put("CustomerCity", edt_city.getText().toString());
                params.put("Customerstate", selectedStateItem.TEXT);
                params.put("CustomerPincode", edt_pincode.getText().toString());

                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.CUSTOMER_SIGNUP, params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    finish();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spin_state:
                selectedStateItem = spinnerStateItemModelList.get(position);
                spinnerStateItemModelList.get(position).STATE = true;

                for (int i = 0; i < spinnerStateItemModelList.size(); i++) {
                    if (i == position) {
                        spinnerStateItemModelList.get(i).STATE = true;
                    } else {
                        spinnerStateItemModelList.get(i).STATE = false;
                    }
                }
                stateSpinnerAdapter.notifyDataSetChanged();


                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
