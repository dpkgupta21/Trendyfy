package com.trendyfy.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.trendyfy.model.CityModel;
import com.trendyfy.model.LocationModel;
import com.trendyfy.preference.AppPreference;
import com.trendyfy.utility.Utils;
import com.trendyfy.volley.Application;
import com.trendyfy.volley.CustomJsonRequest;
import com.trendyfy.volley.WebserviceConstants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LocationChooseFragment extends DialogFragment implements
        AdapterView.OnItemSelectedListener,
        View.OnClickListener {

    private View mView;
    private Activity mActivity;

    private List<CityModel> cityModelList;
    private List<LocationModel> locationModelList;

    private Spinner spin_city;
    private SpinnerItemModel selectedCityItem;
    private SpinnerAdapter citySpinnerAdapter;
    private List<SpinnerItemModel> spinnerCityItemModelList;

    private Spinner spin_location;
    private SpinnerItemModel selectedLocationItem;
    private SpinnerAdapter locationSpinnerAdapter;
    private List<SpinnerItemModel> spinnerLocationItemModelList;


    public LocationChooseFragment() {

    }


    public static LocationChooseFragment newInstance() {
        LocationChooseFragment fragment = new LocationChooseFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = getActivity();
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_location_choose, container, false);



        spin_city = (Spinner) mView.findViewById(R.id.spin_city);
        spin_location = (Spinner) mView.findViewById(R.id.spin_location);

        Button btn_done = (Button) mView.findViewById(R.id.btn_done);
        Button btn_cancel = (Button) mView.findViewById(R.id.btn_cancel);

        spin_city.setOnItemSelectedListener(this);
        spin_location.setOnItemSelectedListener(this);

        btn_done.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);


        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            getDialog().setCanceledOnTouchOutside(false);
            CustomProgressDialog.showProgDialog(mActivity, null);
            getCityList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setLocationSpinner() {

        spinnerLocationItemModelList = new ArrayList<>();

        for (int i = 0; i < locationModelList.size(); i++) {
            SpinnerItemModel item = new SpinnerItemModel();

            item.Id = (i + 1) + "";
            item.TEXT = locationModelList.get(i).getLocationName();
            item.STATE = false;
            item.EXTRA_TEXT = locationModelList.get(i).getA_ID();

            spinnerLocationItemModelList.add(item);
        }
        setLocationSpinnerAdapter();
    }


    private void setLocationSpinnerAdapter() {
        locationSpinnerAdapter = new SpinnerAdapter(mActivity, R.layout.spinner_row_item_lay,
                spinnerLocationItemModelList);
        spin_location.setAdapter(locationSpinnerAdapter);
    }

    private void setCitySpinner() {

        spinnerCityItemModelList = new ArrayList<>();

        for (int i = 0; i < cityModelList.size(); i++) {
            SpinnerItemModel item = new SpinnerItemModel();

            item.Id = (i + 1) + "";
            item.TEXT = cityModelList.get(i).getCityName();
            item.STATE = false;
            item.EXTRA_TEXT = cityModelList.get(i).getA_ID();

            spinnerCityItemModelList.add(item);
        }
        setCitySpinnerAdapter();
    }


    private void setCitySpinnerAdapter() {
        citySpinnerAdapter = new SpinnerAdapter(mActivity, R.layout.spinner_row_item_lay,
                spinnerCityItemModelList);
        spin_city.setAdapter(citySpinnerAdapter);
    }


    private void getCityList() {
        try {
            if (Utils.isOnline(mActivity)) {
                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.GET_CITY, null,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    Type type = new TypeToken<ArrayList<CityModel>>() {
                                    }.getType();
                                    cityModelList = new Gson().fromJson(response,
                                            type);

                                    // Set Spinner
                                    setCitySpinner();
                                    CustomProgressDialog.hideProgressDialog();
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


    private void getLocationList(String cityName) {
        try {
            if (Utils.isOnline(mActivity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("CityName", cityName);
                params.put("strLocationName", "");
                params.put("strPageType", "Grocery-Grocery");
                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.GET_LOCATION_JSON, params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    Type type = new TypeToken<ArrayList<LocationModel>>() {
                                    }.getType();
                                    locationModelList = new Gson().fromJson(response,
                                            type);

                                    // Set Spinner
                                    setLocationSpinner();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spin_city:
                selectedCityItem = spinnerCityItemModelList.get(position);
                spinnerCityItemModelList.get(position).STATE = true;

                for (int i = 0; i < spinnerCityItemModelList.size(); i++) {
                    if (i == position) {
                        spinnerCityItemModelList.get(i).STATE = true;
                    } else {
                        spinnerCityItemModelList.get(i).STATE = false;
                    }
                }
                citySpinnerAdapter.notifyDataSetChanged();

                if (cityModelList != null && cityModelList.size() != 0) {
                    getLocationList(cityModelList.get(position).getCityName());
                }
                break;
            case R.id.spin_location:
                selectedLocationItem = spinnerLocationItemModelList.get(position);
                spinnerLocationItemModelList.get(position).STATE = true;

                for (int i = 0; i < spinnerLocationItemModelList.size(); i++) {
                    if (i == position) {
                        spinnerLocationItemModelList.get(i).STATE = true;
                    } else {
                        spinnerLocationItemModelList.get(i).STATE = false;
                    }
                }
                locationSpinnerAdapter.notifyDataSetChanged();

                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_done:
                if (selectedCityItem != null && selectedLocationItem != null) {
                    AppPreference.setCityId(mActivity, selectedCityItem.EXTRA_TEXT);
                    AppPreference.setCityName(mActivity, selectedCityItem.TEXT);
                    AppPreference.setLocationId(mActivity, selectedLocationItem.EXTRA_TEXT);
                    AppPreference.setLocationName(mActivity, selectedLocationItem.TEXT);

                    ProductListFragment fragment = ProductListFragment.newInstance();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frame_container, fragment, fragment.getClass().getName());
                    ft.commit();

                    getDialog().dismiss();
                } else {
                    Toast.makeText(mActivity, "Please select city or location.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_cancel:
                getDialog().dismiss();
                break;
        }
    }
}
