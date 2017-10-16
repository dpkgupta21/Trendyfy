package com.trendyfy.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trendyfy.R;
import com.trendyfy.adapter.SideMenuListAdapter;
import com.trendyfy.customviews.CustomAlert;
import com.trendyfy.customviews.CustomProgressDialog;
import com.trendyfy.fragment.CustomerOrdersListFragment;
import com.trendyfy.fragment.CustomerStatementListFragment;
import com.trendyfy.fragment.LandingCategoryFragment;
import com.trendyfy.fragment.LocationChooseFragment;
import com.trendyfy.fragment.ProductListFragment;
import com.trendyfy.model.CashbackAmountModel;
import com.trendyfy.model.CategoryModel;
import com.trendyfy.model.LoginResponseModel;
import com.trendyfy.preference.AppPreference;
import com.trendyfy.preference.PreferenceHelp;
import com.trendyfy.utility.GroupCheckHelper;
import com.trendyfy.utility.SessionManager;
import com.trendyfy.utility.Utils;
import com.trendyfy.volley.Application;
import com.trendyfy.volley.CustomJsonRequest;
import com.trendyfy.volley.WebserviceConstants;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class HomeActivity extends AppCompatActivity implements ExpandableListView.OnChildClickListener {

    public Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    public Activity mActivity;
    private ImageView mTitle;
    private ExpandableListView mExpandableListView;
    private boolean backPressedToExitOnce = false;

    private SideMenuListAdapter menuListAdapter;
    private List<CategoryModel> categoryList;
    private List<CategoryModel> filteredList;

    private TextView txt_user_wallet_balance;
    private LoginResponseModel loginResponseModel;
    private  int cashBackAmount=0;
    private final MenuHandler menuHandler =
            new MenuHandler(HomeActivity.this);
    public static final int MENU_CASHBACK_HANDLER = 1001;

    public  String value="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mActivity = HomeActivity.this;
        initViews();

        // Call Main master screen
        displayMainMaster();
    }

    private void cashBackInThread(final String email){
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCustomerCashbackAmount(email);
            }
        }).start();
    }

    private void initViews() {
        loginResponseModel = AppPreference.getObjectFromPref(mActivity, PreferenceHelp.USER_INFO);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mTitle = (ImageView) findViewById(R.id.toolbar_title);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        setSupportActionBar(mToolbar);
        // set the toolbar title
        getSupportActionBar().setTitle(" ");
        mTitle.setVisibility(View.VISIBLE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);



        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {


            }

            @Override
            public void onDrawerOpened(View drawerView) {
                changeTxtOfNavigationView();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        mExpandableListView = (ExpandableListView) mNavigationView.findViewById(R.id.side_menu_list);

        categoryList = GetMenuItem();

        Gson gson = new Gson();
        value= gson.toJson(categoryList);

        menuListAdapter = new SideMenuListAdapter(this, categoryList);
        mExpandableListView.setAdapter(menuListAdapter);
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                return false;
            }
        });
        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int len = menuListAdapter.getGroupCount();

                for (int i = 0; i < len; i++) {
                    if (i != groupPosition) {
                        mExpandableListView.collapseGroup(i);
                    }
                }
            }
        });
        mExpandableListView.setOnChildClickListener(this);

        RelativeLayout relative_home = (RelativeLayout) mNavigationView.findViewById(R.id.relative_home);
        relative_home.setOnClickListener(homeButtonClick);

        mDrawerToggle.syncState();

    }

    private View.OnClickListener homeButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            displayMainMaster();
        }
    };

    public void SetTitle(String titleMsg) {
        getSupportActionBar().setTitle(titleMsg);
    }

    private void changeTxtOfNavigationView() {
        loginResponseModel = AppPreference.getObjectFromPref(mActivity, PreferenceHelp.USER_INFO);
        RelativeLayout relative_before_login = (RelativeLayout) mNavigationView.findViewById(R.id.relative_before_login);
        RelativeLayout relative_top = (RelativeLayout) mNavigationView.findViewById(R.id.relative_top);
        TextView loginTxt = (TextView) mNavigationView.findViewById(R.id.txt_login);
        TextView txt_username = (TextView) mNavigationView.findViewById(R.id.txt_username);
        TextView txt_user_img = (TextView) mNavigationView.findViewById(R.id.txt_user_img);
        TextView txt_user_email_id = (TextView) mNavigationView.findViewById(R.id.txt_user_email_id);


        if (null != loginResponseModel) {
            relative_before_login.setVisibility(View.GONE);
            relative_top.setVisibility(View.VISIBLE);

            txt_user_email_id.setText(loginResponseModel.getEmail());
            txt_username.setText(loginResponseModel.getName());

            txt_user_img.setText(loginResponseModel.getName().substring(0, 1));
            cashBackInThread(loginResponseModel.getEmail());

        } else {
            relative_before_login.setVisibility(View.VISIBLE);
            relative_top.setVisibility(View.GONE);
        }

        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    toggleDrawer();
                    if (null != loginResponseModel) {
                        Intent intent = new Intent(mActivity, ChangeAddressActivity.class);
                        intent.putExtra("isEdit", true);
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(mActivity, LoginActivity.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getCustomerCashbackAmount(String emailId) {
        try {

            if (Utils.isOnline(mActivity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("CustomerEmailID", emailId);


                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.GET_CUSTOMER_CASHBACK, params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Type type = new TypeToken<ArrayList<CashbackAmountModel>>() {
                                    }.getType();
                                    List<CashbackAmountModel> cashbackAmountModelList = new Gson().fromJson(response,
                                            type);
                                    cashBackAmount = cashbackAmountModelList.get(0).getCashBackAmt();

                                    handleMenuCountResponse(cashBackAmount);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

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

    private void handleMenuCountResponse(int cashBackAmount) {
        Message msg = menuHandler.obtainMessage(MENU_CASHBACK_HANDLER, cashBackAmount);
        menuHandler.sendMessage(msg);

    }
    public static class MenuHandler extends android.os.Handler {

        private static final String TAG = "MenuHandler";
        public final WeakReference<HomeActivity> mActivity;

        MenuHandler(HomeActivity activity) {
            mActivity = new WeakReference<HomeActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Utils.ShowLog(TAG, "handleMessage in MenuHandler");
            HomeActivity activity = mActivity.get();
            activity.updateCashbackAmount((int) msg.obj);


        }
    }

    private void updateCashbackAmount(int cashBackAmount){
        TextView txt_user_wallet_balance = (TextView) mNavigationView.findViewById(R.id.txt_user_wallet_balance);
        txt_user_wallet_balance.setText(cashBackAmount+"");
    }

    @Override
    protected void onResume() {
        super.onResume();
        final LoginResponseModel loginResponseModel = AppPreference.getObjectFromPref(mActivity, PreferenceHelp.USER_INFO);

        if (loginResponseModel == null) {
            setCategoryListBeforeLogin();
        } else {
            setCategoryListAfterLogin();
        }
    }

    private void setCategoryListBeforeLogin() {

        try {
            filteredList = new ArrayList<>();
            filteredList.addAll(categoryList);
            filteredList.remove(9);
            filteredList.remove(8);

            menuListAdapter.setCategoryModelList(filteredList);
            menuListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCategoryListAfterLogin() {
        menuListAdapter.setCategoryModelList(categoryList);
        menuListAdapter.notifyDataSetChanged();

    }

    private List<CategoryModel> GetMenuItem() {
        List<CategoryModel> categoryList = new ArrayList<>();
        String[] category = getResources().getStringArray(R.array.category_item);
        String[] categoryIconName = getResources().getStringArray(R.array.category_icon_name);
        String[] categoryNameForWebservice = getResources().getStringArray(R.array.category_item_for_webservice);
        for (int i = 0; i < category.length; i++) {
            CategoryModel model = new CategoryModel();
            model.setCategory(category[i]);
            model.setCategoryImageName(categoryIconName[i]);
            model.setCategoryNameForWebservice(categoryNameForWebservice[i]);
            List<String> subCategoryList = null;
            List<String> subCategoryListForWebservice = null;
            if (category[i].equalsIgnoreCase("Grocery")) {
                String[] groceryItem = getResources().getStringArray(R.array.grocery_subcategory_for_display);
                String[] groceryItemForWebservice = getResources().
                        getStringArray(R.array.grocery_subcategory_for_webservice);
                subCategoryList = new ArrayList<String>(Arrays.asList(groceryItem));
                subCategoryListForWebservice = new ArrayList<String>(Arrays.asList(groceryItemForWebservice));
                model.setSubCategoryList(subCategoryList);
                model.setSubCategoryListForWebservice(subCategoryListForWebservice);
            } else if (category[i].equalsIgnoreCase("Men")) {
                String[] menItem = getResources().getStringArray(R.array.men_subcategory_for_display);
                String[] menItemForWebservice = getResources().getStringArray(R.array.men_subcategory_for_webservice);
                subCategoryList = new ArrayList<String>(Arrays.asList(menItem));
                subCategoryListForWebservice = new ArrayList<String>(Arrays.asList(menItemForWebservice));
                model.setSubCategoryList(subCategoryList);
                model.setSubCategoryListForWebservice(subCategoryListForWebservice);
            } else if (category[i].equalsIgnoreCase("Women")) {
                String[] womenItem = getResources().getStringArray(R.array.women_subcategory_for_display);
                String[] womenItemForWebservice = getResources().getStringArray(R.array.women_subcategory_for_display);
                subCategoryList = new ArrayList<String>(Arrays.asList(womenItem));
                subCategoryListForWebservice = new ArrayList<String>(Arrays.asList(womenItemForWebservice));
                model.setSubCategoryList(subCategoryList);
                model.setSubCategoryListForWebservice(subCategoryListForWebservice);
            } else if (category[i].equalsIgnoreCase("Home & Lifestyle")) {
                String[] homeItem = getResources().getStringArray(R.array.home_subcategory_for_display);
                String[] homeItemForWebservice = getResources().getStringArray(R.array.home_subcategory_for_webservice);
                subCategoryList = new ArrayList<String>(Arrays.asList(homeItem));
                subCategoryListForWebservice = new ArrayList<String>(Arrays.asList(homeItemForWebservice));
                model.setSubCategoryList(subCategoryList);
                model.setSubCategoryListForWebservice(subCategoryListForWebservice);
            } else if (category[i].equalsIgnoreCase("Electronics")) {
                String[] electronicsItem = getResources().getStringArray(R.array.electronics_subcategory_for_display);
                String[] electronicsItemForWebservice = getResources().
                        getStringArray(R.array.electronics_subcategory_for_webservice);

                subCategoryList = new ArrayList<String>(Arrays.asList(electronicsItem));
                subCategoryListForWebservice = new ArrayList<String>(Arrays.asList(electronicsItemForWebservice));
                model.setSubCategoryList(subCategoryList);
                model.setSubCategoryListForWebservice(subCategoryListForWebservice);
            } else if (category[i].equalsIgnoreCase("Kids")) {
                String[] kidsItem = getResources().getStringArray(R.array.kids_subcategory_for_display);
                String[] kidsItemForWebservice = getResources().getStringArray(R.array.kids_subcategory_for_webservice);
                subCategoryList = new ArrayList<String>(Arrays.asList(kidsItem));
                subCategoryListForWebservice = new ArrayList<String>(Arrays.asList(kidsItemForWebservice));
                model.setSubCategoryList(subCategoryList);
                model.setSubCategoryListForWebservice(subCategoryListForWebservice);
            } else if (category[i].equalsIgnoreCase("Books & Stationary")) {
                String[] booksItem = getResources().getStringArray(R.array.books_subcategory_for_display);
                String[] booksItemForWebservice = getResources().getStringArray(R.array.books_subcategory_for_webservice);
                subCategoryList = new ArrayList<String>(Arrays.asList(booksItem));
                subCategoryListForWebservice = new ArrayList<String>(Arrays.asList(booksItemForWebservice));
                model.setSubCategoryList(subCategoryList);
                model.setSubCategoryListForWebservice(subCategoryListForWebservice);
            } else if (category[i].equalsIgnoreCase(getString(R.string.orders))) {
                String[] orderItem = getResources().getStringArray(R.array.orders_for_display);
                subCategoryList = new ArrayList<String>(Arrays.asList(orderItem));
                model.setSubCategoryList(subCategoryList);
                model.setSubCategoryListForWebservice(subCategoryList);
            } else if (category[i].equalsIgnoreCase(getString(R.string.user_account))) {
                String[] userAccountItem = getResources().getStringArray(R.array.user_account_for_display);
                subCategoryList = new ArrayList<String>(Arrays.asList(userAccountItem));
                model.setSubCategoryList(subCategoryList);
                model.setSubCategoryListForWebservice(subCategoryList);
            } else if (category[i].equalsIgnoreCase(getString(R.string.settings))) {
                String[] settingsItem = getResources().getStringArray(R.array.settings_for_display);
                subCategoryList = new ArrayList<String>(Arrays.asList(settingsItem));
                model.setSubCategoryList(subCategoryList);
                model.setSubCategoryListForWebservice(subCategoryList);
            }
            categoryList.add(model);
        }

        return categoryList;
    }

    /// <summary>
    /// Adds the frament.
    /// </summary>
    /// <param name="fragment">The fragment.</param>
    /// <param name="addBackstack">if set to <c>true</c> [add backstack].</param>
    public void addFrament(Fragment fragment, boolean addBackstack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (addBackstack) {
            ft.add(R.id.frame_container, fragment, fragment.getClass().getName());
            ft.addToBackStack(fragment.getClass().getName());
        } else {
            ft.replace(R.id.frame_container, fragment, fragment.getClass().getName());
        }
        ft.commit();
    }


    private void displayMainMaster() {
        toggleDrawer();
        Fragment fragment = null;
        mDrawerLayout.closeDrawer(GravityCompat.START);

        fragment = LandingCategoryFragment.newInstance();
        addFrament(fragment, false);
    }


    public void callProductListFragment() {

        Fragment fragment = null;
        fragment = ProductListFragment.newInstance();

        addFrament(fragment, false);

    }


    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition,
                                int childPosition, long l) {
        // Click only for item
        if (!GroupCheckHelper.isHeadingExist(categoryList.get(groupPosition).getCategory(),
                categoryList.get(groupPosition).getSubCategoryList().get(childPosition))) {
            // For grocery Only
            switch (groupPosition) {
                case 0:

                    toggleDrawer();

                    savePageAndItemTypeInPreference(groupPosition, childPosition);

                    String cityId = AppPreference.getCityId(mActivity);

                    AppPreference.setStrCategoryId(mActivity, categoryList.get(groupPosition).
                            getSubCategoryListForWebservice().get(childPosition));

                    if (cityId != null) {
                        ProductListFragment fragment = ProductListFragment.newInstance();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.frame_container, fragment, fragment.getClass().getName());
                        ft.commit();
                    } else {

                        FragmentManager fm = getSupportFragmentManager();
                        LocationChooseFragment dFragment = LocationChooseFragment.newInstance(false);
                        // Show DialogFragment
                        dFragment.show(fm, "Dialog Fragment");
                    }
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:

                    displayView(groupPosition, childPosition);
                    break;

                case 7:
                    displayUserAccountFragment(childPosition);
                    break;

                case 8:
                    displayOrderSummaryFragment(childPosition);
                    break;
                case 9:
                    displaySettingFragment(childPosition);
                    break;

            }
        }
        return false;
    }


    private void displayUserAccountFragment(int childPosition) {
        Fragment fragment = null;
        switch (childPosition) {
            case 0:
                toggleDrawer();
                Intent forgetPasswordIntent = new Intent(mActivity, ForgetPasswordActivity.class);
                startActivity(forgetPasswordIntent);
               /* fragment = ForgetPasswordFragment.newInstance();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_container, fragment, fragment.getClass().getName());
                ft.commit();*/
                break;
        }
    }

    private void displaySettingFragment(int childPosition) {
        Fragment fragment = null;
        switch (childPosition) {


            case 0:

                Intent intent = new Intent(mActivity, SignupActivity.class);
                intent.putExtra("isEdit", true);
                startActivity(intent);

                break;
            case 1:
                logoutFromApp();
                break;

        }


    }


    private void logoutFromApp() {
        new CustomAlert(mActivity, this)
                .doubleButtonAlertDialog(
                        "Are you sure you want to logout?",
                        "Ok",
                        "Cancel", "dblBtnCallbackResponse", 1000);
    }


    /**
     * callback method of double button alert box.
     *
     * @param flag true if Ok button pressed otherwise false.
     * @param code is requestCode.
     */
    public void dblBtnCallbackResponse(Boolean flag, int code) {
        if (flag) {
            finish();
            new SessionManager(mActivity).logoutUser();
        }

    }

    private void displayOrderSummaryFragment(int childPosition) {
        Fragment fragment = null;
        toggleDrawer();

        switch (childPosition) {
            case 0:
                fragment = CustomerOrdersListFragment.newInstance();
                break;
            case 1:
                fragment = CustomerStatementListFragment.newInstance();
                break;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, fragment, fragment.getClass().getName());
        ft.commit();
    }

    private void savePageAndItemTypeInPreference(int groupPosition, int childPosition) {
        String itemName = categoryList.get(groupPosition).getSubCategoryListForWebservice().
                get(childPosition);
        String pageType = GroupCheckHelper.groupGroceryHeading(groupPosition, itemName);


        AppPreference.setPageType(mActivity, pageType);

        if (pageType.equalsIgnoreCase("Grocery")) {
            AppPreference.setItemName(mActivity, "Grocery");
        } else if (pageType.equalsIgnoreCase("Dairy")) {
            AppPreference.setItemName(mActivity, "Dairy");
        }else if (pageType.equalsIgnoreCase("Cake")) {
            AppPreference.setItemName(mActivity, "Cake");
        }

    }

    private void displayView(int groupPosition, int childPosition) {
        toggleDrawer();
        Fragment fragment = null;
        mDrawerLayout.closeDrawer(GravityCompat.START);
        //String pageType = categoryList.get(groupPosition).getCategoryNameForWebservice();
        String itemName = categoryList.get(groupPosition).getSubCategoryListForWebservice().get(childPosition);
        String pageType = GroupCheckHelper.groupHeading(groupPosition, itemName);
        try {
            if (pageType == null) {
                pageType = categoryList.get(groupPosition).getCategoryNameForWebservice();
            }

            AppPreference.setPageType(mActivity, pageType);
            AppPreference.setItemName(mActivity, itemName);

            fragment = ProductListFragment.newInstance();
            addFrament(fragment, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /// <summary>
    /// Toogles side menu bar.
    /// </summary>
    /// <returns>The drawer.</returns>
    private void toggleDrawer() {
        if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawer(mNavigationView);
        } else {
            mDrawerLayout.openDrawer(mNavigationView);
        }
    }


    private Fragment getCurrentFragment() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        return fragment;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof LandingCategoryFragment) {
            if (backPressedToExitOnce) {
                super.onBackPressed();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                startActivity(intent);


            } else {
                this.backPressedToExitOnce = true;
                Toast.makeText(mActivity, "Press again to exit", Toast.LENGTH_SHORT).show();
                new android.os.Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        backPressedToExitOnce = false;
                    }
                }, 2000);
            }
        } else {
            displayMainMaster();
        }


    }

}