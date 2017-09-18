package com.trendyfy.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.trendyfy.R;
import com.trendyfy.activity.ProductDetailsActivity;
import com.trendyfy.model.ProductListModel;
import com.trendyfy.volley.WebserviceConstants;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private Context context;
    private List<ProductListModel> productList;
    private List<ProductListModel> filteredList;

    public ProductListAdapter(Context context, List<ProductListModel> productList) {
        this.productList = productList;
        this.context = context;

        filteredList = new ArrayList<>();
        filteredList.addAll(productList);


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView txt_product_name;
        public final TextView txt_mrp_price;
        public final TextView txt_selling_price;
        public final TextView txt_discount;
        public final SimpleDraweeView mImageView;
        public final LinearLayout mLayoutItem;

        public ViewHolder(View itemView) {

            super(itemView);

            mImageView = (SimpleDraweeView) itemView.findViewById(R.id.image1);
            txt_product_name = (TextView) itemView.findViewById(R.id.txt_product_name);
            txt_mrp_price = (TextView) itemView.findViewById(R.id.txt_mrp_price);
            txt_selling_price = (TextView) itemView.findViewById(R.id.txt_selling_price);
            txt_discount = (TextView) itemView.findViewById(R.id.txt_discount);

            mLayoutItem = (LinearLayout) itemView.findViewById(R.id.layout_item);
            //thumbnail.setOnClickListener(this);

        }


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.individual_product_item, parent, false);

        ViewHolder detailsViewHolder = new ViewHolder(v);
        return detailsViewHolder;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        if (holder.mImageView.getController() != null) {
            holder.mImageView.getController().onDetach();
        }
        if (holder.mImageView.getTopLevelDrawable() != null) {
            holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ProductListModel productListDTO = productList.get(position);
        final Uri uri = Uri.parse(WebserviceConstants.IMAGE_URL +
                productListDTO.getImage1().substring(1, productListDTO.getImage1().length()));
        holder.mImageView.setImageURI(uri);
        //ImageLoader.getInstance().displayImage(productListDTO.getProductImageName(), holder.thumbnail, options);
        holder.txt_product_name.setText(productListDTO.getProductName());

        if(productListDTO.getMRP()== productListDTO.getSellingPrice()){
            holder.txt_mrp_price.setVisibility(View.INVISIBLE);
            holder.txt_discount.setVisibility(View.INVISIBLE);

        }else{
            holder.txt_mrp_price.setVisibility(View.VISIBLE);
            holder.txt_discount.setVisibility(View.VISIBLE);

            // set mrp price
            holder.txt_mrp_price.setText("Rs. " + productListDTO.getMRP());
            holder.txt_mrp_price.setPaintFlags(holder.txt_mrp_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            // set discount
            double discountCalc = (((productListDTO.getMRP() - productListDTO.getSellingPrice()) / productListDTO.getMRP()) * 100) ;
            String discount= String.format("%.2f", discountCalc);
            holder.txt_discount.setText(discount+"%");

        }

        holder.txt_selling_price.setText("Rs. " + productListDTO.getSellingPrice());



        holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("productObj", productList.get(position));
                context.startActivity(intent);

            }
        });


    }


    @Override
    public int getItemCount() {
        return productList.size();
    }


    public void getFilteredList(String text) {
        productList.clear();
        if (text.length() == 0) {
            productList.addAll(filteredList);
        } else {
            for (ProductListModel productDTO : filteredList) {
                if (productDTO.getProductName().toUpperCase()
                        .contains(text.toUpperCase())) {
                    productList.add(productDTO);
                }
            }
        }

        notifyDataSetChanged();
    }
}
