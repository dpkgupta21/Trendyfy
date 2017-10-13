package com.trendyfy.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trendyfy.R;
import com.trendyfy.activity.CartListActivity;
import com.trendyfy.activity.ChangeAddressActivity;
import com.trendyfy.activity.HomeActivity;
import com.trendyfy.activity.LoginActivity;
import com.trendyfy.activity.SignupActivity;
import com.trendyfy.adapter.ProductListAdapter;
import com.trendyfy.customviews.CustomProgressDialog;
import com.trendyfy.model.LoginResponseModel;
import com.trendyfy.model.ProductListModel;
import com.trendyfy.notification.NotificationCountSetClass;
import com.trendyfy.preference.AppPreference;
import com.trendyfy.preference.PreferenceHelp;
import com.trendyfy.utility.GroupCheckHelper;
import com.trendyfy.utility.Utils;
import com.trendyfy.volley.Application;
import com.trendyfy.volley.CustomJsonRequest;
import com.trendyfy.volley.WebserviceConstants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ProductListFragment extends Fragment implements View.OnClickListener {

    private View view;
    private Activity mActivity;
    private RecyclerView productRecyclerView;
    private ProductListAdapter productListAdapter;
    //private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView txt_no_products;
    private ImageView img_no_internet;
    private TextView txt_try_again;
    private RelativeLayout relative_no_data;

    public ProductListFragment() {
        // Required empty public constructor
    }


    public static ProductListFragment newInstance() {
        ProductListFragment fragment = new ProductListFragment();
        Bundle b = new Bundle();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_list, container, false);
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityCompat.invalidateOptionsMenu(mActivity);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

        try {
            final MenuItem searchItem = menu.findItem(R.id.action_search);

            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MenuItemCompat.expandActionView(searchItem);
                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    productListAdapter.getFilteredList(newText);
                    return true;
                }
            });

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Get the notifications MenuItem
        MenuItem item = menu.findItem(R.id.action_cart);
        int addedProductInCart = GroupCheckHelper.getInstance().AddedProductInCart(mActivity);

        NotificationCountSetClass.setAddToCart(mActivity, item, addedProductInCart);
        //ActivityCompat.invalidateOptionsMenu(mActivity);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cart) {

           /* NotificationCountSetClass.setAddToCart(MainActivity.this, item, notificationCount);
            invalidateOptionsMenu();*/
            startActivity(new Intent(mActivity, CartListActivity.class));

           /* notificationCount=0;//clear notification count
            invalidateOptionsMenu();*/
        } else if (id == R.id.action_login) {
            LoginResponseModel loginResponseModel = AppPreference.getObjectFromPref(mActivity,
                    PreferenceHelp.USER_INFO);
            if (loginResponseModel != null) {
                Intent intent = new Intent(mActivity, ChangeAddressActivity.class);
                intent.putExtra("isEdit", true);
                startActivity(intent);
            } else {
                startActivity(new Intent(mActivity, LoginActivity.class));
            }
        }
        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = getActivity();

        // Handle logo click of toolbar to First screen
        Toolbar toolbar = ((HomeActivity) mActivity).mToolbar;
        ImageView img_logo = (ImageView) toolbar.findViewById(R.id.toolbar_title);

        relative_no_data = (RelativeLayout) view.findViewById(R.id.relative_no_data);
        txt_try_again = (TextView) view.findViewById(R.id.txt_try_again);
        img_no_internet = (ImageView) view.findViewById(R.id.img_no_internet);
        txt_no_products = (TextView) view.findViewById(R.id.txt_no_products);

        productRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_product);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        productRecyclerView.setLayoutManager(layoutManager);

        txt_try_again.setOnClickListener(this);
        img_logo.setOnClickListener(this);

        // Show location fragment according to Grocery type
        showCityAndLocation();
        // Get Product list
        getProductList();


    }

    private void showCityAndLocation() {
        String pageType = AppPreference.getPageType(mActivity);
        LinearLayout linear_grocery_address = (LinearLayout) view.findViewById(R.id.linear_grocery_address);
        if (pageType.equalsIgnoreCase("Grocery") || pageType.equalsIgnoreCase("Dairy")) {
            // set city and location at top of screen
            linear_grocery_address.setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.txt_city_val)).
                    setText(AppPreference.getCityName(mActivity));
            ((TextView) view.findViewById(R.id.txt_location_val)).
                    setText(AppPreference.getLocationName(mActivity));

            linear_grocery_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LocationChooseFragment dFragment = LocationChooseFragment.newInstance();
                    // Show DialogFragment
                    dFragment.show(getFragmentManager(), "Dialog Fragment");
                }
            });

        } else {
            linear_grocery_address.setVisibility(View.GONE);
        }
    }

    private void getProductList() {
        try {
            String pageType = AppPreference.getPageType(mActivity);
            String itemName = AppPreference.getItemName(mActivity);
            if (Utils.isOnline(mActivity)) {
                Utils.ShowLog("TAG", "getProductList");
                HashMap<String, String> params = new HashMap<>();
                params.put("PageType", pageType);
                params.put("ItemName", itemName);
                if (pageType.equalsIgnoreCase("Grocery")) {
                    params.put("strLocation", AppPreference.getLocationId(mActivity));
                    params.put("strGrocerycategoryID", AppPreference.getStrCategoryId(mActivity) == null ?
                            "" : AppPreference.getStrCategoryId(mActivity));
                } else if (pageType.equalsIgnoreCase("Dairy")) {
                    params.put("strLocation", AppPreference.getLocationId(mActivity));
                    params.put("strGrocerycategoryID", "");
                } else {
                    params.put("strLocation", "");
                    params.put("strGrocerycategoryID", "");
                }

                CustomProgressDialog.showProgDialog(mActivity, null);
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
            productListAdapter = new ProductListAdapter(mActivity, productList);
            productRecyclerView.setAdapter(productListAdapter);
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
            case R.id.txt_try_again:
                showCityAndLocation();

                // Get Product list
                getProductList();

                break;
            case R.id.toolbar_title:
                mActivity.finish();
                Intent intent = new Intent(mActivity, HomeActivity.class);
                mActivity.startActivity(intent);
                break;
        }
    }
}
