package com.trendyfy.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.trendyfy.R;
import com.trendyfy.model.LoginResponseModel;
import com.trendyfy.model.ProductListModel;
import com.trendyfy.preference.AppPreference;
import com.trendyfy.preference.PreferenceHelp;
import com.trendyfy.utility.Constants;
import com.trendyfy.volley.WebserviceConstants;

import java.util.List;

public class CartListActivity extends AppCompatActivity implements View.OnClickListener {
    private Activity mActivity;
    private Button btn_cart_price;
    private Button btn_payment;
    private List<ProductListModel> productList;
    private static final int LOGIN_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        mActivity = CartListActivity.this;

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        // set the toolbar title
        getSupportActionBar().setTitle("Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_cart_price = (Button) findViewById(R.id.btn_cart_price);
        btn_payment = (Button) findViewById(R.id.btn_payment);

        LoginResponseModel userInfoModel = AppPreference.getObjectFromPref(mActivity, PreferenceHelp.USER_INFO);
        if (userInfoModel != null) {
            btn_payment.setText("Proceed");
        }
        btn_cart_price.setOnClickListener(this);
        btn_payment.setOnClickListener(this);

        //List<ProductListModel> addedProductList = new ArrayList<>();
        productList = AppPreference.getObjectFromPref(mActivity, PreferenceHelp.ADDED_PRODUCT);
        if (productList != null && productList.size() != 0) {
            //Show cart layout based on items

            setBuyNowPrice(productList);
        }
        setCartLayout(productList);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager recylerViewLayoutManager = new LinearLayoutManager(mActivity);

        recyclerView.setLayoutManager(recylerViewLayoutManager);
        recyclerView.setAdapter(new CartListActivity.SimpleStringRecyclerViewAdapter(mActivity, recyclerView, productList));


    }

    private void setBuyNowPrice(List<ProductListModel> productList) {
        this.productList = productList;
        double cartPrice = 0.0;
        double shippingCharges = 0.0;
        for (ProductListModel productDto : productList) {
            cartPrice += (productDto.getSellingPrice() * productDto.getAddInCartQty());
            // if product is grocery then not include shipping charge
            if (!productDto.getPageType().equalsIgnoreCase("Grocery") && !productDto.getPageType().equalsIgnoreCase("Dairy")) {
                cartPrice += Constants.SHIPPING_CHARGES * productDto.getAddInCartQty();
                shippingCharges += Constants.SHIPPING_CHARGES * productDto.getAddInCartQty();
            }
        }
        // Added shipping charges at last
        ((TextView) findViewById(R.id.txt_shipping_charges)).setText("Rs. " + shippingCharges);

        AppPreference.setTotalCartPrice(mActivity, (float) cartPrice);
        btn_cart_price.setText("Rs. " + cartPrice);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_cart_price:
                break;
            case R.id.btn_payment:
                // Updated cart product in preference
                AppPreference.putObjectIntoPref(mActivity, productList, PreferenceHelp.ADDED_PRODUCT);

                // Check is user logged in or not
                LoginResponseModel loginResponse = AppPreference.getObjectFromPref(mActivity, PreferenceHelp.USER_INFO);
                if (loginResponse == null) {
                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    startActivityForResult(intent, LOGIN_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(mActivity, DeliverySummaryActivity.class);
                    startActivity(intent);
                   // Toast.makeText(mActivity, "Already logged in. Need to redirect to payment screen", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder> implements View.OnClickListener {

        private List<ProductListModel> productList;
        private RecyclerView mRecyclerView;
        private Activity mActivity;


        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutRemove;
            public final TextView txt_product_name, txt_selling_price, txt_qty_val, txt_size_val, txt_add_qty, txt_minus_qty,
                    txt_available_qty_val, txt_mrp_price, txt_discount;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (SimpleDraweeView) view.findViewById(R.id.image_cartlist);
                mLayoutRemove = (LinearLayout) view.findViewById(R.id.layout_action1);
                txt_product_name = (TextView) view.findViewById(R.id.txt_product_name);
                txt_selling_price = (TextView) view.findViewById(R.id.txt_selling_price);
                txt_mrp_price = (TextView) view.findViewById(R.id.txt_mrp_price);
                txt_qty_val = (TextView) view.findViewById(R.id.txt_qty_val);
                txt_size_val = (TextView) view.findViewById(R.id.txt_size_val);
                txt_add_qty = (TextView) view.findViewById(R.id.txt_add_qty);
                txt_minus_qty = (TextView) view.findViewById(R.id.txt_minus_qty);
                txt_available_qty_val = (TextView) view.findViewById(R.id.txt_available_qty_val);
                txt_discount = (TextView) view.findViewById(R.id.txt_discount);
            }
        }

        public SimpleStringRecyclerViewAdapter(Activity mActivity, RecyclerView recyclerView,
                                               List<ProductListModel> productList) {
            this.productList = productList;
            mRecyclerView = recyclerView;
            this.mActivity = mActivity;
        }

        @Override
        public CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cartlist_item, parent, false);
            return new CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        @Override
        public void onBindViewHolder(final CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder holder,
                                     final int position) {
            ProductListModel productListDTO = productList.get(position);
            String image1[] = productListDTO.getImage1().split(",");
            final Uri uri = Uri.parse(WebserviceConstants.IMAGE_URL +
                    image1[0].substring(1, image1[0].length()));
            holder.mImageView.setImageURI(uri);
            holder.txt_product_name.setText(productListDTO.getProductName());
            holder.txt_mrp_price.setText("Rs. " + (productListDTO.getMRP() * productListDTO.getAddInCartQty()));
            holder.txt_selling_price.setText("Rs. " + (productListDTO.getSellingPrice() * productListDTO.getAddInCartQty()));
            holder.txt_qty_val.setText("" + productListDTO.getAddInCartQty());
            holder.txt_size_val.setText(productListDTO.getSize());
            holder.txt_available_qty_val.setText("Only " + productListDTO.getAvaliableQty() + " units in stock");

            holder.txt_mrp_price.setPaintFlags(holder.txt_mrp_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            double discountCalc = (((productListDTO.getMRP() - productListDTO.getSellingPrice()) / productListDTO.getMRP()) * 100);
            String discount = String.format("%.2f", discountCalc);
            holder.txt_discount.setText(discount + "%");

            holder.txt_minus_qty.setTag(position);
            holder.txt_add_qty.setTag(position);

            holder.txt_add_qty.setOnClickListener(this);
            holder.txt_minus_qty.setOnClickListener(this);

            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                    intent.putExtra("productObj", productList.get(position));
                    intent.putExtra("isFromCart", true);
                    mActivity.startActivity(intent);
                }
            });

            //Set click action
            holder.mLayoutRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    productList.remove(position);

                    AppPreference.putObjectIntoPref(mActivity, productList, PreferenceHelp.ADDED_PRODUCT);
                    notifyDataSetChanged();

                    // Remove product list view if cart is empty
                    if (productList.size() == 0) {
                        ((CartListActivity) mActivity).setCartLayout(productList);
                    }
                    ((CartListActivity) mActivity).setBuyNowPrice(productList);

                }
            });



        }

        @Override
        public int getItemCount() {
            return productList != null ? productList.size() : 0;
        }


        @Override
        public void onClick(View v) {
            int pos = Integer.parseInt(v.getTag().toString());
            ProductListModel productListModel = productList.get(pos);
            switch (v.getId()) {
                case R.id.txt_add_qty:
                    if (productListModel.getAddInCartQty() < productListModel.getAvaliableQty()) {

                        productListModel.setAddInCartQty(productListModel.getAddInCartQty() + 1);
                        productList.set(pos, productListModel);
                        notifyItemChanged(pos);


                        // Update buy now price after added qty
                        ((CartListActivity) mActivity).setBuyNowPrice(productList);
                    } else {
                        Toast.makeText(mActivity, "Cannot add more than available qty.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.txt_minus_qty:
                    if (productListModel.getAddInCartQty() > 1) {
                        productListModel.setAddInCartQty(productListModel.getAddInCartQty() - 1);
                        productList.set(pos, productListModel);
                        notifyItemChanged(pos);

                        // Update buy now price after added qty
                        ((CartListActivity) mActivity).setBuyNowPrice(productList);
                    } else {
                        Toast.makeText(mActivity, "Cannot take less qty.", Toast.LENGTH_SHORT).show();
                    }

                    break;

            }
        }
    }

    protected void setCartLayout(List<ProductListModel> productList) {
        LinearLayout layoutCartItems = (LinearLayout) findViewById(R.id.layout_items);
        LinearLayout layoutCartPayments = (LinearLayout) findViewById(R.id.layout_payment);
        LinearLayout layoutCartNoItems = (LinearLayout) findViewById(R.id.layout_cart_empty);

        if (productList != null && productList.size() > 0) {
            layoutCartNoItems.setVisibility(View.GONE);
            layoutCartItems.setVisibility(View.VISIBLE);
            layoutCartPayments.setVisibility(View.VISIBLE);
        } else {
            layoutCartNoItems.setVisibility(View.VISIBLE);
            layoutCartItems.setVisibility(View.GONE);
            layoutCartPayments.setVisibility(View.GONE);

            Button bStartShopping = (Button) findViewById(R.id.bAddNew);
            bStartShopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;


        }
        return true;
    }

    @Override
    public void onBackPressed() {
        AppPreference.putObjectIntoPref(mActivity, productList, PreferenceHelp.ADDED_PRODUCT);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                btn_payment.setText("Proceed");
            }
        }
    }
}
