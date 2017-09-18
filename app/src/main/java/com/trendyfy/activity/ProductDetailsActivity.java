package com.trendyfy.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trendyfy.R;
import com.trendyfy.customviews.CustomProgressDialog;
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

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ProductListModel productObj;
    private Activity mActivity;
    private List<ProductListModel> sameItemProductList;
    private TextView txt_product_size;
    private TextView txt_available_qty;
    private TextView txt_color;
    private TextView txt_added_in_cart_val;
    private LinearLayout layout_size;
    private TextView txt_shipping_charge_lbl;

    // Prices
    private TextView txt_selling_price;
    private TextView txt_mrp_price;
    private TextView txt_discount;
    private TextView txt_product_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        mActivity = this;

        //Getting product object from previous screen
        if (getIntent() != null) {
            productObj = (ProductListModel) getIntent().getSerializableExtra("productObj");
        }

        init();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);

        // set the toolbar title
        getSupportActionBar().setTitle("Product detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SimpleDraweeView mImageView = (SimpleDraweeView) findViewById(R.id.image1);
        Button btn_add_cart = (Button) findViewById(R.id.btn_add_cart);
        TextView btn_buy_now = (TextView) findViewById(R.id.btn_buy_now);
        txt_added_in_cart_val = (TextView) findViewById(R.id.txt_added_in_cart_val);
        txt_shipping_charge_lbl = (TextView) findViewById(R.id.txt_shipping_charge_lbl);

        layout_size = (LinearLayout) findViewById(R.id.layout_size);

        Uri uri = Uri.parse(WebserviceConstants.IMAGE_URL +
                productObj.getImage1().substring(1, productObj.getImage1().length()));
        mImageView.setImageURI(uri);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ImagePagerActivity.class);
                intent.putExtra("image", productObj.getImage1());
                startActivity(intent);

            }
        });


        btn_add_cart.setOnClickListener(this);

        btn_buy_now.setOnClickListener(this);

        // Get Product detail webservice

        getProductDetail();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_detail, menu);
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
        updateAddedProductQty();
    }

    private boolean validateForm() {
        if (layout_size.getVisibility() == View.VISIBLE && (productObj.getSize() == null || productObj.getSize().equalsIgnoreCase(""))) {
            Toast.makeText(mActivity, "Please select size.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getProductDetail() {
        try {
            final String pageType = AppPreference.getPageType(mActivity);
            String itemName = AppPreference.getItemName(mActivity);

            if (Utils.isOnline(mActivity)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("PageType", pageType);
                params.put("ItemName", itemName);
                params.put("strItemID", productObj.getItemId());
                params.put("strLocation", "");

                CustomProgressDialog.showProgDialog(mActivity, null);
                CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST,
                        WebserviceConstants.CONNECTION_URL + WebserviceConstants.GET_PRODUCT, params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    Type type = new TypeToken<ArrayList<ProductListModel>>() {
                                    }.getType();
                                    sameItemProductList = new Gson().fromJson(response,
                                            type);
                                    productObj.setImage1(sameItemProductList.get(0).getImage1());
                                    if (sameItemProductList.get(0).getSize() != null &&
                                            !sameItemProductList.get(0).getSize().equalsIgnoreCase("")) {
                                        // Set product size
                                        setProductSize(pageType);
                                    } else {
                                        layout_size.setVisibility(View.GONE);
                                        setProductWhenNoSize(pageType);
                                    }
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

    private void setProductWhenNoSize(final String pageType) {
        productObj.setPageType(pageType);
        productObj.setAvaliableQty(sameItemProductList.get(0).getAvaliableQty());
        productObj.setUOMID(sameItemProductList.get(0).getUOMID());
        productObj.setColor(sameItemProductList.get(0).getColor());
        if (sameItemProductList.get(0).getColor() != null &&
                !sameItemProductList.get(0).getColor().equalsIgnoreCase("")) {
            txt_color.setVisibility(View.VISIBLE);
            txt_color.setText("Color : " + sameItemProductList.get(0).getColor());
        } else {
            txt_color.setVisibility(View.GONE);
        }
        if (sameItemProductList.get(0).getColor() != null &&
                !sameItemProductList.get(0).getColor().equalsIgnoreCase("")) {
            txt_available_qty.setVisibility(View.VISIBLE);
            txt_available_qty.setText("Available Qty : " + sameItemProductList.get(0).getAvaliableQty());
        } else {
            txt_available_qty.setVisibility(View.GONE);
        }

        getAddedSameProduct();
    }

    private void setProductSize(final String pageType) {
        if (pageType.equalsIgnoreCase("Grocery") || pageType.equalsIgnoreCase("Dairy")) {
            txt_product_size.setText("Select Quantity");
            txt_shipping_charge_lbl.setVisibility(View.GONE);
        } else {
            txt_product_size.setText("Select Size");
            txt_shipping_charge_lbl.setVisibility(View.VISIBLE);
        }
        layout_size.setVisibility(View.VISIBLE);
        LinearLayout linear_size = (LinearLayout) findViewById(R.id.linear_size);
        linear_size.setVisibility(View.VISIBLE);
        linear_size.removeAllViews();
        final ImageView txtView[] = new ImageView[sameItemProductList.size()];
        for (int i = 0; i < sameItemProductList.size(); i++) {
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                    160,
                    160);

            linearParams.gravity= Gravity.CENTER;
            linearParams.setMargins(20, 20, 20, 20);
            final ImageView txt = new ImageView(mActivity);
            txtView[i] = txt;
            txtView[i].setTag(i + "");
            txtView[i].setImageDrawable(getTextDrawable(i, false));
            txtView[i].setScaleType(ImageView.ScaleType.FIT_CENTER);
            //txtView[i].setText(sameItemProductList.get(i).getSize());
            txtView[i].setBackgroundResource(R.drawable.circle_shape);
            //txtView[i].setTextColor(getResources().getColor(R.color.black));
            //txtView[i].setTextSize(12);
           // txtView[i].setGravity(View.TEXT_ALIGNMENT_CENTER);
            //txtView[i].setIncludeFontPadding(false);
            txtView[i].setLayoutParams(linearParams);
            txtView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = Integer.parseInt(v.getTag().toString());
                    for (int j = 0; j < txtView.length; j++) {
                        if (pos == j) {

                            txtView[j].setImageDrawable(getTextDrawable(j, true));
                            txtView[j].setBackgroundResource(R.drawable.fill_circle_shape);
                           // txtView[j].setTextColor(getResources().getColor(R.color.fill_circle_color));
                        } else {

                            txtView[j].setImageDrawable(getTextDrawable(j, false));
                            txtView[j].setBackgroundResource(R.drawable.circle_shape);
                           // txtView[j].setTextColor(getResources().getColor(R.color.black));
                        }
                    }
                    if (pageType.equalsIgnoreCase("Grocery")) {
                        txt_product_size.setText("Quantity: " + sameItemProductList.get(pos).getSize());
                     } else {
                        txt_product_size.setText("Size : " + sameItemProductList.get(pos).getSize());
                    }

                    // Set price according to choose size
                    setPriceValues(sameItemProductList.get(pos).getMRP(),
                            sameItemProductList.get(pos).getSellingPrice());

                    // Set product description
                    setProductDescription(sameItemProductList.get(pos).getDescrption());


                    txt_available_qty.setVisibility(View.VISIBLE);
                    txt_available_qty.setText("Available Qty : " + sameItemProductList.get(pos).getAvaliableQty());
                    if (sameItemProductList.get(pos).getColor() != null &&
                            !sameItemProductList.get(pos).getColor().equalsIgnoreCase("")) {
                        txt_color.setVisibility(View.VISIBLE);
                        txt_color.setText("Color : " + sameItemProductList.get(pos).getColor());
                    } else {
                        txt_color.setVisibility(View.GONE);
                    }

                    productObj.setDescrption(sameItemProductList.get(pos).getDescrption());
                    productObj.setPageType(pageType);
                    productObj.setSize(sameItemProductList.get(pos).getSize());
                    productObj.setAvaliableQty(sameItemProductList.get(pos).getAvaliableQty());
                    productObj.setUOMID(sameItemProductList.get(pos).getUOMID());
                    productObj.setColor(sameItemProductList.get(pos).getColor());
                    productObj.setSellingPrice(sameItemProductList.get(pos).getSellingPrice());

                    getAddedSameProduct();
                }
            });
            linear_size.addView(txt);
        }
    }

    private  TextDrawable getTextDrawable(int position, boolean isSelected){
        int circleColor=0;
        int textColor=0;
        if(isSelected){
            circleColor= Color.TRANSPARENT;
            textColor= getResources().getColor(R.color.colorPrimary);
        }else{
            circleColor= Color.TRANSPARENT;
            textColor= Color.BLACK;
        }

        String sizeTxtVal= sameItemProductList.get(position).getSize();
        int txtSize= sizeTxtVal.length() > 6 ? Utils.convertSpToPixels(8, mActivity):
                Utils.convertSpToPixels(12, mActivity);
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(textColor)
                .fontSize(txtSize) /* size in px */
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(sizeTxtVal, circleColor);


        return  drawable;
    }

    private void addProductInCart() {
        try {
            CustomProgressDialog.showProgDialog(mActivity, null);
            List<ProductListModel> addedProductList = AppPreference.getObjectFromPref(mActivity,
                    PreferenceHelp.ADDED_PRODUCT);
            if (addedProductList == null) {
                addedProductList = new ArrayList<>();
            }
            boolean isAlreadyInCart = false;
            if (addedProductList != null && addedProductList.size() != 0) {
                for (int i = 0; i < addedProductList.size(); i++) {
                    ProductListModel addedProductListModel = addedProductList.get(i);
                    if (productObj.getUOMID() != null &&
                            addedProductListModel.getUOMID().equalsIgnoreCase(productObj.getUOMID())) {
                        if (addedProductListModel.getAddInCartQty() < productObj.getAvaliableQty()) {
                            addedProductListModel.setAddInCartQty(addedProductListModel.getAddInCartQty() + 1);

                            addedProductListModel.setPageType(AppPreference.getPageType(mActivity));
                            addedProductListModel.setItemName(AppPreference.getItemName(mActivity));
                            addedProductList.set(i, addedProductListModel);
                            isAlreadyInCart = true;
                            txt_added_in_cart_val.setText("" + addedProductListModel.getAddInCartQty());
                            Toast.makeText(mActivity, "Item added in cart.", Toast.LENGTH_SHORT).show();
                        } else {
                            isAlreadyInCart = true;
                            Toast.makeText(mActivity, "Item out of stock.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            if (!isAlreadyInCart) {
                txt_added_in_cart_val.setText("" + 1);

                productObj.setPageType(AppPreference.getPageType(mActivity));
                productObj.setItemName(AppPreference.getItemName(mActivity));
                productObj.setAddInCartQty(1);
                addedProductList.add(productObj);

            }

            AppPreference.putObjectIntoPref(mActivity, addedProductList, PreferenceHelp.ADDED_PRODUCT);

            NotificationCountSetClass.setNotifyCount(addedProductList.size());

            CustomProgressDialog.hideProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            CustomProgressDialog.hideProgressDialog();
        }
    }

    private void init() {
        TextView txt_product_name = (TextView) findViewById(R.id.txt_product_name);
        txt_product_size = (TextView) findViewById(R.id.txt_product_size);
        txt_available_qty = (TextView) findViewById(R.id.txt_available_qty);
        txt_color = (TextView) findViewById(R.id.txt_color);
        txt_selling_price = (TextView) findViewById(R.id.txt_selling_price);
        txt_mrp_price = (TextView) findViewById(R.id.txt_mrp_price);
        txt_discount = (TextView) findViewById(R.id.txt_discount);
        txt_product_description = (TextView) findViewById(R.id.txt_product_description);

        txt_product_name.setText(productObj.getProductName());

        setPriceValues(productObj.getMRP(), productObj.getSellingPrice());
    }

    private void setPriceValues(double mrpPrice, double sellingPrice) {
        txt_selling_price.setText(getString(R.string.rs) + sellingPrice);


        if (mrpPrice == sellingPrice) {
            txt_mrp_price.setVisibility(View.GONE);
            txt_discount.setVisibility(View.GONE);
        } else {
            txt_mrp_price.setVisibility(View.VISIBLE);
            txt_discount.setVisibility(View.VISIBLE);

            txt_mrp_price.setText(getString(R.string.rs) + mrpPrice);
            txt_mrp_price.setPaintFlags(txt_mrp_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            double discountCalc = (((mrpPrice - sellingPrice) / mrpPrice) * 100);
            String discount = String.format("%.2f", discountCalc);
            txt_discount.setText(discount + "%");
        }
    }


    private void setProductDescription(String description) {
        if(description!=null && !description.equalsIgnoreCase("")){
            txt_product_description.setVisibility(View.VISIBLE);
            txt_product_description.setText(description);
        }



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_cart:
                startActivity(new Intent(mActivity, CartListActivity.class));
                break;
        }
        return true;
    }

    private void getAddedSameProduct() {
        try {
            List<ProductListModel> addedProductList = AppPreference.getObjectFromPref(mActivity,
                    PreferenceHelp.ADDED_PRODUCT);
            if (addedProductList == null) {
                addedProductList = new ArrayList<>();
            }
            if (addedProductList != null && addedProductList.size() != 0) {
                for (int i = 0; i < addedProductList.size(); i++) {
                    ProductListModel addedProductListModel = addedProductList.get(i);
                    if (addedProductListModel.getUOMID().equalsIgnoreCase(productObj.getUOMID())) {
                        txt_added_in_cart_val.setText("" + addedProductListModel.getAddInCartQty());
                        break;
                    } else {
                        txt_added_in_cart_val.setText("" + 0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            CustomProgressDialog.hideProgressDialog();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_cart:
                if (validateForm()) {
                    addProductInCart();
                    updateAddedProductQty();
                }
                break;
            case R.id.btn_buy_now:
                if (validateForm()) {
                    // Add product in cart and add total cart price
                    addProductInCart();
                    updateAddedProductQty();
                    // Redirect to cart screen
                    startActivity(new Intent(mActivity, CartListActivity.class));
                }
                break;


        }
    }

    private void updateAddedProductQty() {

        ActivityCompat.invalidateOptionsMenu(mActivity);
        getAddedSameProduct();
    }

}
