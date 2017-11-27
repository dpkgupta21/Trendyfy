package com.trendyfy.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ChangeAddressActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Activity mActivity;
    private EditText edt_customer_name;
    private EditText edt_email;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_address);

        mActivity = ChangeAddressActivity.this;

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        // set the toolbar title
        getSupportActionBar().setTitle(isEdit ? "Edit Profile" : "Change Address");
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
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_mobile_no = (EditText) findViewById(R.id.edt_mobile_no);
        edt_address = (EditText) findViewById(R.id.edt_address);
        edt_city = (EditText) findViewById(R.id.edt_city);
        edt_pincode = (EditText) findViewById(R.id.edt_pincode);
        spin_state = (Spinner) findViewById(R.id.spin_state);

        if (isEdit) {
            loginResponseModel = AppPreference.getObjectFromPref(mActivity, PreferenceHelp.USER_INFO);
            edt_customer_name.setText(loginResponseModel.getName());
            edt_email.setText(loginResponseModel.getEmail());
            edt_mobile_no.setText(loginResponseModel.getMobileNo());
            edt_address.setText(loginResponseModel.getAddress());
            edt_city.setText(loginResponseModel.getCity());
            edt_pincode.setText(loginResponseModel.getPincode());
            edt_customer_name.setText(loginResponseModel.getName());
        }

        Button btn_signup = (Button) findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(this);

        spin_state.setOnItemSelectedListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                if (validateForm()) {
                    callChangeAddressWebservice();
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

    private void callChangeAddressWebservice() {
        try {
            if (Utils.isOnline(mActivity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("ShippingCustomerName", edt_customer_name.getText().toString());
                params.put("ShippingEmail", edt_email.getText().toString());
                params.put("ShippingAddress", edt_address.getText().toString());
                params.put("ShippingMobileNo", edt_mobile_no.getText().toString());
                params.put("ShippingCity", edt_city.getText().toString());
                params.put("Shippingstate", selectedStateItem.EXTRA_TEXT);
                params.put("shippingPinCode", edt_pincode.getText().toString());

                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.CUSTOMER_SHIPPING_DETAIL_JSON, params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    setUserInfo();
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

    private void setUserInfo() {
        loginResponseModel.setName(edt_customer_name.getText().toString());
        loginResponseModel.setEmail(edt_email.getText().toString());
        loginResponseModel.setMobileNo(edt_mobile_no.getText().toString());
        loginResponseModel.setCity(edt_city.getText().toString());
        loginResponseModel.setAddress(edt_address.getText().toString());
        loginResponseModel.setPincode(edt_pincode.getText().toString());
        loginResponseModel.setStateID(Integer.parseInt(selectedStateItem.EXTRA_TEXT));
        AppPreference.putObjectIntoPref(mActivity, loginResponseModel, PreferenceHelp.USER_INFO);
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
