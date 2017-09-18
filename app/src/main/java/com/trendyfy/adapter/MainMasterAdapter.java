package com.trendyfy.adapter;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.trendyfy.R;
import com.trendyfy.model.MainMasterModel;
import com.trendyfy.volley.WebserviceConstants;

import java.util.List;

public class MainMasterAdapter extends RecyclerView.Adapter<MainMasterAdapter.ViewHolder> {

    private Context context;
    private List<MainMasterModel> mainMasterList;
    private String pageType;
    private String itemName;
    private static MyClickListener myClickListener;

    public MainMasterAdapter(Context context, List<MainMasterModel> mainMasterList) {
        this.mainMasterList = mainMasterList;
        this.context = context;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView txt_page_type;
        public final TextView txt_item_name;
        public final SimpleDraweeView img_category;
        public final RelativeLayout mLayoutItem;

        public ViewHolder(View itemView) {

            super(itemView);

            img_category = (SimpleDraweeView) itemView.findViewById(R.id.img_category);
            txt_page_type = (TextView) itemView.findViewById(R.id.txt_page_type);
            txt_item_name = (TextView) itemView.findViewById(R.id.txt_item_name);

            mLayoutItem = (RelativeLayout) itemView.findViewById(R.id.layout_item);

            mLayoutItem.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        MainMasterAdapter.myClickListener = myClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.individual_main_master_item, parent, false);

        ViewHolder detailsViewHolder = new ViewHolder(v);
        return detailsViewHolder;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        if (holder.img_category.getController() != null) {
            holder.img_category.getController().onDetach();
        }
        if (holder.img_category.getTopLevelDrawable() != null) {
            holder.img_category.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        MainMasterModel mainMasterModelDTO = mainMasterList.get(position);
        final Uri uri = Uri.parse(WebserviceConstants.IMAGE_URL +
                mainMasterModelDTO.getImage1().substring(1, mainMasterModelDTO.getImage1().length()));
        holder.img_category.setImageURI(uri);



    }


    @Override
    public int getItemCount() {
        return mainMasterList.size();
    }


}
