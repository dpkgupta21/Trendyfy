package com.trendyfy.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.trendyfy.R;
import com.trendyfy.model.CustomerOrderListModel;
import com.trendyfy.utility.Constants;
import com.trendyfy.volley.WebserviceConstants;

import java.util.List;

public class CustomerOrderListAdapter extends RecyclerView.Adapter<CustomerOrderListAdapter.ViewHolder> {

    private Context context;
    private List<CustomerOrderListModel> orderList;

    public CustomerOrderListAdapter(Context context, List<CustomerOrderListModel> orderList) {
        this.orderList = orderList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView txt_order_tracking_number;
        public final TextView txt_order_id;
        public final TextView txt_order_date;
        public final TextView txt_product_name;
        public final TextView txt_selling_price;
        public final TextView txt_payment_type;
        public final TextView txt_track;
        public final SimpleDraweeView img_product;

        public ViewHolder(View itemView) {

            super(itemView);

            img_product = (SimpleDraweeView) itemView.findViewById(R.id.img_product);
            txt_order_tracking_number = (TextView) itemView.findViewById(R.id.txt_order_tracking_number);
            txt_order_id = (TextView) itemView.findViewById(R.id.txt_order_id);
            txt_order_date = (TextView) itemView.findViewById(R.id.txt_order_date);
            txt_product_name = (TextView) itemView.findViewById(R.id.txt_product_name);
            txt_selling_price = (TextView) itemView.findViewById(R.id.txt_selling_price);
            txt_payment_type = (TextView) itemView.findViewById(R.id.txt_payment_type);
            txt_track = (TextView) itemView.findViewById(R.id.txt_track);


        }


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.individual_order_item, parent, false);

        ViewHolder detailsViewHolder = new ViewHolder(v);
        return detailsViewHolder;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        if (holder.img_product.getController() != null) {
            holder.img_product.getController().onDetach();
        }
        if (holder.img_product.getTopLevelDrawable() != null) {
            holder.img_product.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CustomerOrderListModel orderDTO = orderList.get(position);
        final Uri uri = Uri.parse(WebserviceConstants.IMAGE_URL +
                orderDTO.getImage1().substring(1, orderDTO.getImage1().length()));
        holder.img_product.setImageURI(uri);
        //ImageLoader.getInstance().displayImage(productListDTO.getProductImageName(), holder.thumbnail, options);
        holder.txt_product_name.setText(orderDTO.getModelName());
        holder.txt_selling_price.setText("Rs. " + orderDTO.getProductValue());
        holder.txt_order_id.setText(orderDTO.getORDERID());
        holder.txt_order_date.setText(orderDTO.getORDERdATE());
        holder.txt_order_tracking_number.setText(orderDTO.getWayBillNo());
        holder.txt_payment_type.setText(orderDTO.getPAYMENTTYPE());

        holder.txt_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse(Constants.TRACKING_URL));
                context.startActivity(viewIntent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }


}
