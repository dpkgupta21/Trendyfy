package com.trendyfy.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trendyfy.R;
import com.trendyfy.adapter.ProductListAdapter;
import com.trendyfy.adapter.SearchListAdapter;
import com.trendyfy.customviews.CustomProgressDialog;
import com.trendyfy.model.ProductListModel;
import com.trendyfy.preference.AppPreference;
import com.trendyfy.utility.Utils;
import com.trendyfy.volley.Application;
import com.trendyfy.volley.CustomJsonRequest;
import com.trendyfy.volley.WebserviceConstants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SearchListActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity mActivity;
    private RecyclerView productRecyclerView;
    private SearchListAdapter mAdapter;
    private EditText edt_search;
    //private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView txt_no_products;
    private ImageView img_no_internet;
    private TextView txt_try_again;
    private RelativeLayout relative_no_data;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);

        mActivity = SearchListActivity.this;

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        // set the toolbar title
        getSupportActionBar().setTitle("Search Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
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

    public void initViews() {
        // Handle logo click of toolbar to First screen


        relative_no_data = (RelativeLayout) findViewById(R.id.relative_no_data);
        txt_try_again = (TextView) findViewById(R.id.txt_try_again);
        img_no_internet = (ImageView) findViewById(R.id.img_no_internet);
        txt_no_products = (TextView) findViewById(R.id.txt_no_products);

        edt_search = (EditText) findViewById(R.id.edt_search);
        ImageView img_search = (ImageView) findViewById(R.id.img_search);

        edt_search.setText(getIntent().getStringExtra("searchProduct"));

        productRecyclerView = (RecyclerView) findViewById(R.id.recycle_product);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        productRecyclerView.setLayoutManager(layoutManager);

        txt_try_again.setOnClickListener(this);
        img_search.setOnClickListener(this);
        // Show location fragment according to Grocery type
        //showCityAndLocation();
        // Get Product list
        getProductList();


    }

//    private void showCityAndLocation() {
//        String pageType = AppPreference.getPageType(mActivity);
//        LinearLayout linear_grocery_address = (LinearLayout) view.findViewById(R.id.linear_grocery_address);
//        if (pageType.equalsIgnoreCase("Grocery") || pageType.equalsIgnoreCase("Dairy")) {
//            // set city and location at top of screen
//            linear_grocery_address.setVisibility(View.VISIBLE);
//            ((TextView) view.findViewById(R.id.txt_city_val)).
//                    setText(AppPreference.getCityName(mActivity));
//            ((TextView) view.findViewById(R.id.txt_location_val)).
//                    setText(AppPreference.getLocationName(mActivity));
//
//            linear_grocery_address.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    LocationChooseFragment dFragment = LocationChooseFragment.newInstance();
//                    // Show DialogFragment
//                    dFragment.show(getFragmentManager(), "Dialog Fragment");
//                }
//            });
//
//        } else {
//            linear_grocery_address.setVisibility(View.GONE);
//        }
//    }

    private void getProductList() {
        try {
            if (Utils.isOnline(mActivity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("strSearch", edt_search.getText().toString().trim());

                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.PRODUCT_DETAIL_SEARCH, params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    List<ProductListModel> productList = null;
                                    Log.i("info", "Response :" + response);
                                    if (!response.equalsIgnoreCase("[{\"Result : \":\"Record Not Found\"}]")) {
                                        Type type = new TypeToken<ArrayList<ProductListModel>>() {
                                        }.getType();
                                        productList = new Gson().fromJson(response,
                                                type);


                                    }

                                    setUpListAdapter(productList);

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
                                showNoInternetConnectivity();
                                Utils.showExceptionDialog(mActivity);
                            }
                        }
                );


                Application.getInstance().addToRequestQueue(jsonRequest);
                jsonRequest.setRetryPolicy(new DefaultRetryPolicy(150000, 0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            } else {
                showNoInternetConnectivity();
            }
        } catch (Exception e) {
            e.printStackTrace();
            CustomProgressDialog.hideProgressDialog();
        }
    }


    private void setUpListAdapter(final List<ProductListModel> productList) {

        if (productList != null && productList.size() != 0) {
            showMessageAccordingData(true);
            mAdapter = new SearchListAdapter(mActivity, productList);
            productRecyclerView.setAdapter(mAdapter);
        } else {
            showMessageAccordingData(false);
        }
    }

    private void showNoInternetConnectivity() {
        productRecyclerView.setVisibility(View.GONE);
        relative_no_data.setVisibility(View.VISIBLE);

    }

    private void showMessageAccordingData(boolean isDataAvailable) {
        if (isDataAvailable) {
            relative_no_data.setVisibility(View.GONE);
            productRecyclerView.setVisibility(View.VISIBLE);
        } else {
            productRecyclerView.setVisibility(View.GONE);
            relative_no_data.setVisibility(View.VISIBLE);
            img_no_internet.setVisibility(View.GONE);
            txt_no_products.setText("No Products Available.");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_search:
            case R.id.txt_try_again:
                //showCityAndLocation();
                if (!edt_search.getText().toString().equalsIgnoreCase("")) {
                    // Get Product list
                    getProductList();
                } else {
                    Utils.customDialog("Field cannot be blank or empty.", mActivity);
                }

                break;

        }
    }
}
