package com.trendyfy.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trendyfy.R;
import com.trendyfy.customviews.CustomProgressDialog;
import com.trendyfy.model.ProductListModel;
import com.trendyfy.model.SearchKeywordBindModel;
import com.trendyfy.utility.Utils;
import com.trendyfy.volley.CustomJsonRequest;
import com.trendyfy.volley.WebserviceConstants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DeepakGupta on 10/15/17.
 */

public class SearchAutoCompleteAdapter extends BaseAdapter implements Filterable{

    private Context mContext;
    private Filter filter = new CustomFilter();
    private List<SearchKeywordBindModel> originalList;
    private List<SearchKeywordBindModel> suggestions = new ArrayList<>();

    public SearchAutoCompleteAdapter(Context mContext, List<SearchKeywordBindModel> mList){
        this.mContext= mContext;
        originalList= mList;

    }

    public List<SearchKeywordBindModel> getSuggestions(){
        return  suggestions;
    }
    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public Object getItem(int i) {
        return suggestions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return suggestions.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.simple_dropdown_item_lay, viewGroup, false);
        }
        ((TextView) convertView.findViewById(R.id.txt_auto_search)).
                setText(suggestions.get(position).getSearchkeyword());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            suggestions.clear();
            // Check if the Original List and Constraint aren't null.
            if (originalList != null && constraint != null) {
                for (int i = 0; i < originalList.size(); i++) {
                    // Compare item in original list if it contains constraints.
                    if (originalList.get(i).getSearchkeyword().toLowerCase().contains(constraint)) {
                        // If TRUE add item in Suggestions.
                        suggestions.add(originalList.get(i));
                    }
                }
            }
            // Create new Filter Results and return this to publishResults;
            FilterResults results = new FilterResults();
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }




}
