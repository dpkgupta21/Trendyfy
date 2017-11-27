package com.trendyfy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.payUMoney.sdk.PayUmoneySdkInitilizer;
import com.payUMoney.sdk.SdkConstants;
import com.trendyfy.R;
import com.trendyfy.customviews.CustomAlert;
import com.trendyfy.customviews.CustomProgressDialog;
import com.trendyfy.model.CashbackAmountModel;
import com.trendyfy.model.LoginResponseModel;
import com.trendyfy.model.ProductListModel;
import com.trendyfy.notification.NotificationCountSetClass;
import com.trendyfy.preference.AppPreference;
import com.trendyfy.preference.PreferenceHelp;
import com.trendyfy.utility.Constants;
import com.trendyfy.utility.GroupCheckHelper;
import com.trendyfy.utility.Utils;
import com.trendyfy.volley.Application;
import com.trendyfy.volley.CustomJsonRequest;
import com.trendyfy.volley.WebserviceConstants;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DeliverySummaryActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {

    private Activity mActivity;
    private TextView txt_delivery_address_val;
    private EditText edt_use_cashback_val;
    private TextView txt_total_cashback_val;
    private TextView txt_total_remaining_amount_val;
    private TextView txt_use_cashback;
    private TextView txt_max_use_cashback_val;
    private LoginResponseModel userInfo;
    private float totalCartPrice;
    private float totalPayableCartPrice;
    private static String TAG = DeliverySummaryActivity.class.getSimpleName();
    private int previousAmount;
    private int cashBackAmount;

    public static final int EDIT_ADDRESS_REQUEST_CODE = 1002;

    // private TextView txt_amount_val;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_summary);
        mActivity = this;

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);

        // set the toolbar title
        getSupportActionBar().setTitle("Delivery Summary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        totalCartPrice = AppPreference.getTotalCartPrice(mActivity);
        totalPayableCartPrice = totalCartPrice;
        userInfo = (LoginResponseModel) AppPreference.getObjectFromPref(mActivity, PreferenceHelp.USER_INFO);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delivery_summary, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_cart);
        int addedProductInCart = GroupCheckHelper.getInstance().AddedProductInCart(mActivity);

        NotificationCountSetClass.setAddToCart(mActivity, item, addedProductInCart);

        //ActivityCompat.invalidateOptionsMenu(mActivity);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityCompat.invalidateOptionsMenu(mActivity);
    }

    private void init() {
        txt_delivery_address_val = (TextView) findViewById(R.id.txt_delivery_address_val);
        TextView txt_amount_val = (TextView) findViewById(R.id.txt_amount_val);
        txt_total_cashback_val = (TextView) findViewById(R.id.txt_total_cashback_val);
        txt_use_cashback = (TextView) findViewById(R.id.txt_use_cashback);
        txt_max_use_cashback_val = (TextView) findViewById(R.id.txt_use_cashback);

        edt_use_cashback_val = (EditText) findViewById(R.id.edt_use_cashback_val);
        txt_total_remaining_amount_val = (TextView) findViewById(R.id.txt_total_remaining_amount_val);
        Button btn_cod = (Button) findViewById(R.id.btn_cod);
        Button btn_online_payment = (Button) findViewById(R.id.btn_online_payment);

        btn_cod.setOnClickListener(this);
        btn_online_payment.setOnClickListener(this);
        txt_delivery_address_val.setOnClickListener(this);
        edt_use_cashback_val.addTextChangedListener(this);

        txt_amount_val.setText("Rs. " + totalCartPrice);
        txt_total_remaining_amount_val.setText("Rs. " + totalCartPrice);

        // Set Address Value
        setAddressValue();

        // Delete Item from cart
        deleteItemFromCart(userInfo.getA_Id());
    }

    private void setAddressValue() {
        txt_delivery_address_val.setText(userInfo.getAddress() + ", " + userInfo.getCity());
    }

    private void deleteItemFromCart(String userId) {
        try {

            if (Utils.isOnline(mActivity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("ipaddress", userId);

                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.DELETE_FROM_CART, params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    CustomProgressDialog.hideProgressDialog();

                                    List<ProductListModel> productList = AppPreference.getObjectFromPref(mActivity,
                                            PreferenceHelp.ADDED_PRODUCT);

                                    // Insert item into cart
                                    for (ProductListModel productObj : productList) {
                                        insertItemIntoCart(productObj, userInfo.getA_Id());
                                    }
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

    private void insertItemIntoCart(ProductListModel productObj, String userId) {
        try {

            if (Utils.isOnline(mActivity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("ProductID", productObj.getSKUID());
                params.put("itemSize", productObj.getUOMID());
                params.put("ipaddress", userId);
                params.put("quantity", "" + productObj.getAddInCartQty());

                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.ADD_IN_CART, params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    getCustomerCashbackAmount(userInfo.getEmail());

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

    private void getCustomerCashbackAmount(String emailId) {
        try {

            if (Utils.isOnline(mActivity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("CustomerEmailID", emailId);

                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.GET_CUSTOMER_CASHBACK, params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    CustomProgressDialog.hideProgressDialog();
                                    Type type = new TypeToken<ArrayList<CashbackAmountModel>>() {
                                    }.getType();
                                    List<CashbackAmountModel> cashbackAmountModelList = new Gson().fromJson(response,
                                            type);
                                    cashBackAmount = cashbackAmountModelList.get(0).getCashBackAmt();
                                    txt_total_cashback_val.setText(cashBackAmount + "");
                                    txt_max_use_cashback_val.setText("( Max: " + cashBackAmount / 4 + " )");
                                    if (cashBackAmount == 0) {
                                        txt_use_cashback.setVisibility(View.INVISIBLE);
                                        txt_max_use_cashback_val.setVisibility(View.INVISIBLE);
                                        edt_use_cashback_val.setVisibility(View.INVISIBLE);
                                    }
                                    Toast.makeText(mActivity, "Item added", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;


        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        if (s != null && s.length() > 0) {
            int enterAmount = 0;
            try {
                enterAmount = Integer.parseInt(s.toString());
            } catch (Exception e) {

            }
            if (enterAmount <= totalCartPrice && enterAmount <= cashBackAmount / 4) {
                previousAmount = enterAmount;
                totalPayableCartPrice = totalCartPrice - enterAmount;
                txt_total_remaining_amount_val.setText("Rs. " + totalPayableCartPrice);
            } else {
                edt_use_cashback_val.setText(previousAmount + "");
            }
        }
    }

    @Override
    public void onClick(View v) {
        String useCashBackString = null;
        int useCashBackVal = 0;
        switch (v.getId()) {
            case R.id.btn_cod:
                alertForConfirmCOD();
                break;
            case R.id.btn_online_payment:
                useCashBackString = edt_use_cashback_val.getText().toString();
                try {
                    useCashBackVal = Integer.parseInt(useCashBackString);
                } catch (Exception e) {

                }

                if (useCashBackVal <= cashBackAmount) {
                    insertIntoPaymentMasterOnlinePaymentJson();
                } else {
                    Utils.customDialog("Cannot use more than cashback amount", mActivity);
                }
                //makePayment();
                break;
            case R.id.txt_delivery_address_val:
                Intent intent = new Intent(mActivity, ChangeAddressActivity.class);
                intent.putExtra("isEdit", true);
                startActivityForResult(intent, EDIT_ADDRESS_REQUEST_CODE);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EDIT_ADDRESS_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    userInfo = AppPreference.getObjectFromPref(mActivity, PreferenceHelp.USER_INFO);
                    setAddressValue();
                }
                break;
            case PayUmoneySdkInitilizer.PAYU_SDK_PAYMENT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Success - Payment ID : " + data.getStringExtra(SdkConstants.PAYMENT_ID));
                    String paymentId = data.getStringExtra(SdkConstants.PAYMENT_ID);
                    paymentSuccessWS();
                } else if (resultCode == RESULT_CANCELED) {
                    Log.i(TAG, "failure");
                    Utils.showDialog(mActivity, "Message", "Payment cancelled.");
                } else if (resultCode == PayUmoneySdkInitilizer.RESULT_FAILED) {
                    Log.i("app_activity", "failure");
                    if (data != null) {
                        if (data.getStringExtra(SdkConstants.RESULT).equals("cancel")) {
                        } else {
                            Utils.showDialog(mActivity, "Message", "failure");
                        }
                    }
                } else if (resultCode == PayUmoneySdkInitilizer.RESULT_BACK) {
                    Log.i(TAG, "User returned without login");
                    Utils.showDialog(mActivity, "Message", "User returned without login");
                }
                break;

        }
    }

    private void paymentSuccessWS() {
        try {
            if (Utils.isOnline(mActivity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("ipaddress", userInfo.getA_Id());

                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL
                                + WebserviceConstants.INSERT_TNTO_PAYMENT_MASTER_SUCCESS_JSON, params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    CustomProgressDialog.hideProgressDialog();
//                                    if (new JSONArray(response).getJSONObject(0)
//                                            .getString("Result : ").equalsIgnoreCase("Record Inserted")) {

                                    float totalPrice = AppPreference.getTotalCartPrice(mActivity);
                                    String body = "Your order has confirmed. You have make payment of Rs."
                                            + totalPrice;
                                    sendMessageWebservice(userInfo.getMobileNo(), body);

                                    // remove added products from preferences and total price as well
                                    AppPreference.removeObjectIntoPref(mActivity, PreferenceHelp.ADDED_PRODUCT);
                                    AppPreference.setTotalCartPrice(mActivity, 0.0f);

                                    // Show home screen again
                                    startActivity(new Intent(mActivity, DeliveryCompleteActivity.class));
                                    finish();
                                    //  }
                                } catch (Exception e) {
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

    private void makePayment() {
        String phone = userInfo.getMobileNo();
        String productName = "product_name";
        String firstName = userInfo.getName().split(" ")[0];
        String txnId = generateTnxId();
        String email = userInfo.getEmail();
        String sUrl = "http://trendyfy.com/SuccessPage.aspx";
        String fUrl = "http://trendyfy.com/SuccessPage.aspx";
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        boolean isDebug = false;
        String key = "KtqxYg";
        String merchantId = "4799617";

        PayUmoneySdkInitilizer.PaymentParam.Builder builder = new PayUmoneySdkInitilizer.PaymentParam.Builder();
        builder.setAmount(totalPayableCartPrice)
                .setTnxId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(sUrl)
                .setfUrl(fUrl)
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setIsDebug(isDebug)
                .setKey(key)
                .setMerchantId(merchantId);

        String salt = "dPASVtX2";
        String serverCalculatedHash = hashCal(key + "|" + txnId + "|" + totalPayableCartPrice + "|" + productName + "|"
                + firstName.split(" ")[0] + "|" + email + "|" + udf1 + "|" + udf2 + "|" + udf3 + "|" + udf4 + "|" + udf5 + "|" + salt);
        PayUmoneySdkInitilizer.PaymentParam paymentParam = builder.build();
        paymentParam.setMerchantHash(serverCalculatedHash);
        PayUmoneySdkInitilizer.startPaymentActivityForResult(mActivity, paymentParam);
    }

    private String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

    private String generateTnxId() {
        Random random = new Random();
        String randomHash = hashCal(random.toString() + System.currentTimeMillis());
        if (randomHash.length() > 20)
            randomHash = randomHash.substring(0, 20);
        return randomHash;
    }

    private void insertIntoPaymentMasterOnlinePaymentJson() {
        try {
            if (Utils.isOnline(mActivity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("ipaddress", userInfo.getA_Id());
                params.put("ShippingCustomerName", userInfo.getName());
                params.put("ShippingEmail", userInfo.getEmail());
                params.put("ShippingAddress", userInfo.getAddress());
                params.put("ShippingMobileNo", userInfo.getMobileNo());
                params.put("ShippingCity", userInfo.getCity());
                params.put("Shippingstate", userInfo.getStateID() + "");
                params.put("shippingPinCode", userInfo.getPincode());
                params.put("Cashbackamount", edt_use_cashback_val.getText().toString());
                params.put("cardnumber", "");
                params.put("Nameofcard", "");

                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL
                                + WebserviceConstants.INSERT_TNTO_PAYMENT_MASTER_ONLINE_PAYMENT_JSON, params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    CustomProgressDialog.hideProgressDialog();
                                    if (new JSONArray(response).getJSONObject(0)
                                            .getString("Result : ").equalsIgnoreCase("Record Inserted")) {
                                        makePayment();
                                    }
                                } catch (Exception e) {
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


    private void insertIntoPaymentMaster() {
        try {

            if (Utils.isOnline(mActivity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("ipaddress", userInfo.getA_Id());
                params.put("ShippingCustomerName", userInfo.getName());
                params.put("ShippingEmail", userInfo.getEmail());
                params.put("ShippingAddress", userInfo.getAddress());
                params.put("ShippingMobileNo", userInfo.getMobileNo());
                params.put("ShippingCity", userInfo.getCity());
                params.put("Shippingstate", userInfo.getStateID() + "");
                params.put("shippingPinCode", userInfo.getPincode());
                params.put("Cashbackamount", edt_use_cashback_val.getText().toString());


                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.INSERT_INTO_PAYMENT_MASTER, params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {

                                    String body = "Your order has confirmed. You need to pay total amount Rs." + totalPayableCartPrice;
                                    sendMessageWebservice(userInfo.getMobileNo(), body);

                                    String adminMsgBody = "Order has been confirmed. Payment of Rs." + totalPayableCartPrice +
                                            "\nEmail Address :" + userInfo.getEmail() +
                                            "\nName :" + userInfo.getName() +
                                            "\nMobile no :" + userInfo.getMobileNo();

                                    sendMessageWebservice(Constants.ADMIN_MOB_NUMBER, adminMsgBody);
                                    // remove added products from preferences and total price as well
                                    AppPreference.removeObjectIntoPref(mActivity, PreferenceHelp.ADDED_PRODUCT);
                                    AppPreference.setTotalCartPrice(mActivity, 0.0f);

                                    // Show home screen again
                                    finish();
                                    startActivity(new Intent(mActivity, DeliveryCompleteActivity.class));
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

    private void sendMessageWebservice(String recipientNo, String body) {
        try {
            if (Utils.isOnline(mActivity)) {
                String smsUrl = WebserviceConstants.SEND_SMS_WEBSERVICE_URL + recipientNo +
                        WebserviceConstants.MESSAGE_URL;

                String encodedUrl = URLEncoder.encode(body + " " + Constants.QUERY_MESSAGE, "UTF-8");
                smsUrl += encodedUrl;

                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.GET,
                        smsUrl, null,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    CustomProgressDialog.hideProgressDialog();
                                    //Toast.makeText(mActivity, "Password has been sent on mobile.", Toast.LENGTH_SHORT).show();

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
                                // Toast.makeText(mActivity, "Password has been sent on mobile.", Toast.LENGTH_SHORT).show();
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

    private void callCODFunction() {
        String useCashBackString = null;
        int useCashBackVal = 0;
        useCashBackString = edt_use_cashback_val.getText().toString();

        try {
            useCashBackVal = Integer.parseInt(useCashBackString);
        } catch (Exception e) {

        }

        if (useCashBackVal <= cashBackAmount) {
            insertIntoPaymentMaster();
        } else {
            Utils.customDialog("Cannot use more than cashback amount.", mActivity);
        }
    }

    private void alertForConfirmCOD() {
        new CustomAlert(mActivity, this)
                .doubleButtonAlertDialog(
                        "Are you sure you want to place the order?",
                        "Yes",
                        "No", "dblBtnCallbackResponse", 1000);
    }


    /**
     * callback method of double button alert box.
     *
     * @param flag true if Ok button pressed otherwise false.
     * @param code is requestCode.
     */
    public void dblBtnCallbackResponse(Boolean flag, int code) {
        if (flag) {
            callCODFunction();
        }

    }

}
