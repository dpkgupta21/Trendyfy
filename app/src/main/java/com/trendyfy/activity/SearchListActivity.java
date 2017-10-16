package com.trendyfy.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.trendyfy.R;
import com.trendyfy.adapter.ProductListAdapter;
import com.trendyfy.adapter.SearchAutoCompleteAdapter;
import com.trendyfy.adapter.SearchListAdapter;
import com.trendyfy.customviews.CustomProgressDialog;
import com.trendyfy.fragment.LocationChooseFragment;
import com.trendyfy.fragment.ProductListFragment;
import com.trendyfy.model.ProductListModel;
import com.trendyfy.model.SearchKeywordBindModel;
import com.trendyfy.model.SearchKeywordDataModel;
import com.trendyfy.preference.AppPreference;
import com.trendyfy.utility.Utils;
import com.trendyfy.volley.Application;
import com.trendyfy.volley.CustomJsonRequest;
import com.trendyfy.volley.WebserviceConstants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SearchListActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    private Activity mActivity;
    private RecyclerView productRecyclerView;
    private SearchListAdapter mAdapter;

    //private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView txt_no_products;
    private ImageView img_no_internet;
    private RelativeLayout relative_no_data;

    private List<SearchKeywordBindModel> mSuggestionSearchList = null;
    private SearchAutoCompleteAdapter mSuggestionAdapter;
    private AutoCompleteTextView auto_complete_search;


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
        img_no_internet = (ImageView) findViewById(R.id.img_no_internet);
        txt_no_products = (TextView) findViewById(R.id.txt_no_products);
        auto_complete_search = (AutoCompleteTextView) findViewById(R.id.edt_search);

        productRecyclerView = (RecyclerView) findViewById(R.id.recycle_product);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        productRecyclerView.setLayoutManager(layoutManager);

        ImageView img_close = (ImageView) findViewById(R.id.img_close);
        img_close.setOnClickListener(this);
        auto_complete_search.setOnItemClickListener(this);

        CustomProgressDialog.showProgDialog(mActivity, null);
        findSearchKeyword();
    }

    /**
     * Returns a search result for the given book title.
     */
    private void findSearchKeyword() {
        try {
            if (Utils.isOnline(mActivity)) {
                CustomProgressDialog.showProgDialog(mActivity, null);

                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.SEARCH_KEYWORD_BIND, null,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    CustomProgressDialog.hideProgressDialog();
                                    Log.i("info", "Response :" + response);
                                    if (!response.equalsIgnoreCase("[{\"Result : \":\"Record Not Found\"}]")) {
                                        Type type = new TypeToken<ArrayList<SearchKeywordBindModel>>() {
                                        }.getType();
                                        mSuggestionSearchList = new Gson().fromJson(response,
                                                type);
                                        mSuggestionAdapter = new SearchAutoCompleteAdapter(mActivity,
                                                mSuggestionSearchList);
                                        auto_complete_search.setThreshold(1);
                                        auto_complete_search.setAdapter(mSuggestionAdapter);
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

                            }
                        }
                );

                Application.getInstance().addToRequestQueue(jsonRequest);
                jsonRequest.setRetryPolicy(new DefaultRetryPolicy(150000, 0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getSearchKeywordDataList(String a_id) {
        try {
            if (Utils.isOnline(mActivity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("A_Id", a_id);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.SEARCH_KEYWORD_DATA, params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    List<SearchKeywordDataModel> searchKeywordList = null;
                                    Log.i("info", "Response :" + response);
                                    if (!response.equalsIgnoreCase("[{\"Result : \":\"Record Not Found\"}]")) {
                                        Type type = new TypeToken<ArrayList<SearchKeywordDataModel>>() {
                                        }.getType();
                                        searchKeywordList = new Gson().fromJson(response,
                                                type);


                                    }
                                    CustomProgressDialog.showProgDialog(mActivity, null);
                                    getProductList(searchKeywordList);

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


    private void getProductList(List<SearchKeywordDataModel> searchKeywordDataModelList) {
        try {

            String pageType = searchKeywordDataModelList.get(0).getPageType();
            String itemName = searchKeywordDataModelList.get(0).getItemType();
            AppPreference.setPageType(mActivity, pageType);
            AppPreference.setItemName(mActivity, itemName);


            LinearLayout linear_grocery_address = (LinearLayout) findViewById(R.id.linear_grocery_address);
            if (pageType.equalsIgnoreCase("Grocery") || pageType.equalsIgnoreCase("Dairy")) {

                linear_grocery_address.setVisibility(View.VISIBLE);
                linear_grocery_address.setOnClickListener(this);

                String cityId = AppPreference.getCityId(mActivity);

                if (cityId != null) {

                    ((TextView) findViewById(R.id.txt_city_val)).
                            setText(AppPreference.getCityName(mActivity));
                    ((TextView) findViewById(R.id.txt_location_val)).
                            setText(AppPreference.getLocationName(mActivity));

                } else {

                    FragmentManager fm = getSupportFragmentManager();
                    LocationChooseFragment dFragment = LocationChooseFragment.newInstance(true);
                    // Show DialogFragment
                    dFragment.show(fm, "Dialog Fragment");

                    return;
                }
            } else {
                linear_grocery_address.setVisibility(View.GONE);
            }


            if (Utils.isOnline(mActivity)) {
                Utils.ShowLog("TAG", "getProductList");
                HashMap<String, String> params = new HashMap<>();
                params.put("PageType", pageType);
                params.put("ItemName", itemName);
                if (pageType.equalsIgnoreCase("Grocery")) {
                    params.put("strLocation", AppPreference.getLocationId(mActivity));
                    params.put("strGrocerycategoryID", searchKeywordDataModelList.get(0).getCategoryId());
                } else if (pageType.equalsIgnoreCase("Dairy")) {
                    params.put("strLocation", AppPreference.getLocationId(mActivity));
                    params.put("strGrocerycategoryID", "");
                } else {
                    params.put("strLocation", "");
                    params.put("strGrocerycategoryID", "");
                }


                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.GET_PRODUCT_NEW, params,
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
            case R.id.linear_grocery_address:
                LocationChooseFragment dFragment = LocationChooseFragment.newInstance(true);
                // Show DialogFragment
                dFragment.show(getSupportFragmentManager(), "Dialog Fragment");
                break;
            case R.id.img_close:
                auto_complete_search.setText("");
                break;


        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        try {
            List<SearchKeywordBindModel> suggestionList = mSuggestionAdapter.getSuggestions();

            SearchKeywordBindModel keyword = suggestionList.get(position);
            String a_id = keyword.getA_Id();
            auto_complete_search.setText(keyword.getSearchkeyword());
            CustomProgressDialog.showProgDialog(mActivity, null);
            getSearchKeywordDataList(a_id);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
