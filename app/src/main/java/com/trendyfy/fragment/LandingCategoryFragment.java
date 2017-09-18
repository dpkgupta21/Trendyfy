package com.trendyfy.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.trendyfy.activity.CartListActivity;
import com.trendyfy.activity.HomeActivity;
import com.trendyfy.activity.LoginActivity;
import com.trendyfy.activity.SignupActivity;
import com.trendyfy.adapter.MainMasterAdapter;
import com.trendyfy.customviews.CustomProgressDialog;
import com.trendyfy.model.LoginResponseModel;
import com.trendyfy.model.MainMasterModel;
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


public class LandingCategoryFragment extends Fragment implements View.OnClickListener{

    private View view;
    private Activity mActivity;
    private RecyclerView recycle_category;
    private MainMasterAdapter mainMasterAdapter;

    private List<MainMasterModel> mainMasterModelList;

    private TextView txt_no_category;
    private ImageView img_no_internet;
    private TextView txt_try_again;
    private RelativeLayout relative_no_data;

    public LandingCategoryFragment() {
        // Required empty public constructor
    }


    public static LandingCategoryFragment newInstance() {
        LandingCategoryFragment fragment = new LandingCategoryFragment();
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
        view = inflater.inflate(R.layout.fragment_landing_category, container, false);
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.landing_category_menu, menu);


    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        LoginResponseModel userModel = AppPreference.getObjectFromPref(mActivity, PreferenceHelp.USER_INFO);
        MenuItem menuLogin = menu.findItem(R.id.action_login);
        if (userModel == null) {
            menuLogin.setVisible(false);
        } else {
            menuLogin.setVisible(true);
        }

        MenuItem item = menu.findItem(R.id.action_cart);

        int addedProductInCart = GroupCheckHelper.getInstance().AddedProductInCart(mActivity);

        NotificationCountSetClass.setAddToCart(mActivity, item, addedProductInCart);

        //ActivityCompat.invalidateOptionsMenu(mActivity);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityCompat.invalidateOptionsMenu(mActivity);
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
                Intent intent = new Intent(mActivity, SignupActivity.class);
                intent.putExtra("isEdit", true);
                intent.putExtra("isForEditProfile", true);
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


        txt_no_category= (TextView)  view.findViewById(R.id.txt_no_category);
        relative_no_data = (RelativeLayout) view.findViewById(R.id.relative_no_data);
        txt_try_again= (TextView)  view.findViewById(R.id.txt_try_again);
        img_no_internet= (ImageView)  view.findViewById(R.id.img_no_internet);

        recycle_category = (RecyclerView) view.findViewById(R.id.recycle_category);
//        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1,
//                StaggeredGridLayoutManager.VERTICAL);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2);

        // Create a custom SpanSizeLookup where the first item spans both columns
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position > 1 ? 1 : 2;
            }
        });

        recycle_category.setLayoutManager(layoutManager);
        txt_try_again.setOnClickListener(this);

        // Get Main master list
        getMainMasterList();

    }


    private void getMainMasterList() {
        try {
            if (Utils.isOnline(mActivity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("A_Id", "");

                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.LANDING_CATEGORY, params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    Type type = new TypeToken<ArrayList<MainMasterModel>>() {
                                    }.getType();
                                    mainMasterModelList = new Gson().fromJson(response,
                                            type);
                                    setUpListAdapter(mainMasterModelList);
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
                                //Show no internet connectivity message
                                showNoInternetConnectivity();
                            }
                        }
                );


                Application.getInstance().addToRequestQueue(jsonRequest);
                jsonRequest.setRetryPolicy(new DefaultRetryPolicy(150000, 0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }else{
                //Show no internet connectivity message
                showNoInternetConnectivity();
            }
        } catch (Exception e) {
            e.printStackTrace();
            CustomProgressDialog.hideProgressDialog();
        }
    }

    private void showNoInternetConnectivity(){
        recycle_category.setVisibility(View.GONE);
        relative_no_data.setVisibility(View.VISIBLE);

    }

    private void setUpListAdapter(final List<MainMasterModel> mainMasterModelList) {

        if (mainMasterModelList != null && mainMasterModelList.size() != 0) {

            showMessageAccordingData(true);

            mainMasterAdapter = new MainMasterAdapter(mActivity, mainMasterModelList);
            recycle_category.setAdapter(mainMasterAdapter);

            mainMasterAdapter.setOnItemClickListener(new MainMasterAdapter.MyClickListener() {


                @Override
                public void onItemClick(int position, View v) {
                    switch (v.getId()) {
                        case R.id.layout_item:
                            displayView(position);
                            break;

                    }
                }
            });
        } else {
            showMessageAccordingData(false);
        }

    }

    private void showMessageAccordingData(boolean isDataAvailable){
        if(isDataAvailable){
            relative_no_data.setVisibility(View.GONE);
            recycle_category.setVisibility(View.VISIBLE);
        }else{
            recycle_category.setVisibility(View.GONE);
            relative_no_data.setVisibility(View.VISIBLE);
            img_no_internet.setVisibility(View.GONE);
            txt_no_category.setText("No Data Available.");
        }
    }
    private void displayView(int position) {

        String pageType = mainMasterModelList.get(position).getPageType();
        String itemName = mainMasterModelList.get(position).getItemType();

        AppPreference.setPageType(mActivity, pageType);
        AppPreference.setItemName(mActivity, itemName);

        if (pageType.equalsIgnoreCase("Grocery")) {
            displayForGrocery(position);
        } else {

            ((HomeActivity) mActivity).callProductListFragment();
        }
    }

    private void displayForGrocery(int position) {
        String cityId = AppPreference.getCityId(mActivity);

        AppPreference.setStrCategoryId(mActivity, mainMasterModelList.get(position).getCategoryid());

        if (cityId != null) {
            ProductListFragment fragment = ProductListFragment.newInstance();
            android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frame_container, fragment, fragment.getClass().getName());
            ft.commit();
        } else {

            FragmentManager fm = getFragmentManager();
            LocationChooseFragment dFragment = LocationChooseFragment.newInstance();
            // Show DialogFragment
            dFragment.show(fm, "Dialog Fragment");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_try_again:
                // Get Main master list
                getMainMasterList();
                break;
        }
    }
}
