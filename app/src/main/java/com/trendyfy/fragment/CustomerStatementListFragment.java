package com.trendyfy.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trendyfy.R;
import com.trendyfy.adapter.CustomerStatementListAdapter;
import com.trendyfy.customviews.CustomProgressDialog;
import com.trendyfy.model.CustomerStatementListModel;
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


public class CustomerStatementListFragment extends Fragment {

    private View view;
    private Activity mActivity;
    private RecyclerView recycle_statements;
    private CustomerStatementListAdapter statementListAdapter;
    private LoginResponseModel loginResponseModel;
    private TextView txt_no_statements;

    public CustomerStatementListFragment() {
        // Required empty public constructor
    }


    public static CustomerStatementListFragment newInstance() {
        CustomerStatementListFragment fragment = new CustomerStatementListFragment();
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
        view = inflater.inflate(R.layout.fragment_customer_statement_list, container, false);
        setHasOptionsMenu(true);
        mActivity = getActivity();
        loginResponseModel = AppPreference.getObjectFromPref(mActivity, PreferenceHelp.USER_INFO);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txt_no_statements = (TextView) view.findViewById(R.id.txt_no_statements);
        recycle_statements = (RecyclerView) view.findViewById(R.id.recycle_statements);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.VERTICAL);
        recycle_statements.setLayoutManager(layoutManager);


        // Get Product list
        getCustomerOrdersList();

    }


    private void getCustomerOrdersList() {
        try {
            if (Utils.isOnline(mActivity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("CustomerEmailId", loginResponseModel.getEmail());
                params.put("FromDate", "");
                params.put("Todate", "");

                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.CUSTOMER_ACCOUNT_STATEMENT_JSON, params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    Type type = new TypeToken<ArrayList<CustomerStatementListModel>>() {
                                    }.getType();
                                    List<CustomerStatementListModel> statementList = new Gson().fromJson(response,
                                            type);
                                    setUpListAdapter(statementList);
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


        private void setUpListAdapter(final List<CustomerStatementListModel> statementList) {

        if (statementList != null && statementList.size() != 0) {
            txt_no_statements.setVisibility(View.GONE);
            recycle_statements.setVisibility(View.VISIBLE);
            statementListAdapter = new CustomerStatementListAdapter(mActivity, statementList);
            recycle_statements.setAdapter(statementListAdapter);
        } else {
            txt_no_statements.setVisibility(View.VISIBLE);
            recycle_statements.setVisibility(View.GONE);
        }

    }
}
