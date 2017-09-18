package com.trendyfy.customspinner.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.trendyfy.R;
import com.trendyfy.customspinner.model.SpinnerItemModel;

import java.util.List;

/**
 * Created by deepak gupta on 08-05-2017.
 */

public class SpinnerAdapter extends ArrayAdapter<SpinnerItemModel> {
    /// <summary>
    /// Tag
    /// </summary>
    private static final String TAG = "SpinnerAdaptor";
    /// <summary>
    /// Activity object
    /// </summary>
    private Activity mActivity;
    /// <summary>
    /// List of Spinner Item model list
    /// </summary>
    public List<SpinnerItemModel> spinnerItemModelList;


    public SpinnerAdapter(Activity mActivity, int txtViewResourceId, List<SpinnerItemModel> spinnerItemModelList) {
        super(mActivity, txtViewResourceId, spinnerItemModelList);
        this.mActivity = mActivity;
        this.spinnerItemModelList = spinnerItemModelList;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = mActivity.getLayoutInflater().inflate(R.layout.custom_spinner_item,
                parent, false);
        TextView itemNameTV = (TextView) view.findViewById(R.id.txt_name);
        itemNameTV.setText(spinnerItemModelList.get(position).TEXT);

        if (spinnerItemModelList.get(position).STATE) {
            view.setBackgroundResource(R.color.dropdown);
            itemNameTV.setTextColor(mActivity.getResources().getColor(R.color.white));
        } else {
            view.setBackgroundResource(R.color.white);
            itemNameTV.setTextColor(mActivity.getResources().getColor(R.color.dropdown));
        }
        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = mActivity.getLayoutInflater().inflate(R.layout.custom_spinner_item,
                parent, false);
        TextView itemNameTV = (TextView) view.findViewById(R.id.txt_name);
        itemNameTV.setText(spinnerItemModelList.get(position).TEXT);
        return view;
    }
}
