package com.trendyfy.adapter;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.trendyfy.R;
import com.trendyfy.model.CustomerStatementListModel;
import com.trendyfy.volley.WebserviceConstants;

import java.util.List;

public class CustomerStatementListAdapter extends RecyclerView.Adapter<CustomerStatementListAdapter.ViewHolder> {

    private Context context;
    private List<CustomerStatementListModel> statementList;

    public CustomerStatementListAdapter(Context context, List<CustomerStatementListModel> statementList) {
        this.statementList = statementList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView txt_order_id;
        public final TextView txt_product_code;
        public final TextView txt_qty;
        public final TextView txt_price;
        public final TextView txt_payment_type;
        public final TextView txt_closing_balance_val;
        public final SimpleDraweeView img_product;

        public ViewHolder(View itemView) {

            super(itemView);

            img_product = (SimpleDraweeView) itemView.findViewById(R.id.img_product);
            txt_order_id = (TextView) itemView.findViewById(R.id.txt_order_id);
            txt_product_code = (TextView) itemView.findViewById(R.id.txt_product_code);
            txt_qty = (TextView) itemView.findViewById(R.id.txt_qty);
            txt_price = (TextView) itemView.findViewById(R.id.txt_price);
            txt_payment_type = (TextView) itemView.findViewById(R.id.txt_payment_type);
            txt_closing_balance_val = (TextView) itemView.findViewById(R.id.txt_closing_balance_val);
        }


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.individual_statement_item, parent, false);

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
        CustomerStatementListModel statementDTO = statementList.get(position);
        final Uri uri = Uri.parse(WebserviceConstants.IMAGE_URL +
                statementDTO.getImage1().substring(1, statementDTO.getImage1().length()));
        holder.img_product.setImageURI(uri);
        //ImageLoader.getInstance().displayImage(productListDTO.getProductImageName(), holder.thumbnail, options);
        holder.txt_order_id.setText(statementDTO.getOrderid());
        holder.txt_price.setText("Rs. " + statementDTO.getPrice());
        holder.txt_qty.setText("Qty. " + statementDTO.getQuantity());
        holder.txt_payment_type.setText(statementDTO.getPayentType1());
        holder.txt_closing_balance_val.setText(statementDTO.getTotalAmount());
        holder.txt_product_code.setText(statementDTO.getSKUID());
    }


    @Override
    public int getItemCount() {
        return statementList.size();
    }


}
