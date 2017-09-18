package com.trendyfy.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trendyfy.R;
import com.trendyfy.model.CategoryModel;
import com.trendyfy.utility.GroupCheckHelper;

import java.util.List;


public class SideMenuListAdapter extends BaseExpandableListAdapter {

    private Activity mActivity;
    private List<CategoryModel> categoryModelList;

    public SideMenuListAdapter(Activity mActivity, List<CategoryModel> categoryModelList) {
        this.mActivity = mActivity;
        this.categoryModelList = categoryModelList;
    }

    public void setCategoryModelList(List<CategoryModel> categoryModelList) {
        this.categoryModelList = categoryModelList;
    }

    @Override
    public int getGroupCount() {
        return categoryModelList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return categoryModelList.get(groupPosition).getSubCategoryList().size();
    }

    @Override
    public CategoryModel getGroup(int groupPosition) {
        return categoryModelList.get(groupPosition);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return categoryModelList.get(groupPosition).getSubCategoryList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View mView = convertView;
        ViewHolderGroup holder = null;
        if (mView == null) {
            holder = new ViewHolderGroup();
            mView = mActivity.getLayoutInflater().inflate(R.layout.category_group_view, null);

            holder.txt_category = (TextView) mView.findViewById(R.id.txt_category);
            holder.img_category = (ImageView) mView.findViewById(R.id.img_category_icon);

            mView.setTag(holder);
        } else {
            holder = (ViewHolderGroup) mView.getTag();
        }

        try {
            holder.txt_category.setText(categoryModelList.get(groupPosition).getCategory());

            String imageName = categoryModelList.get(groupPosition).getCategoryImageName();
            if (imageName != null && !imageName.equalsIgnoreCase("")) {
                holder.img_category.setVisibility(View.VISIBLE);
                // Set Image
                int resID = mActivity.getResources().getIdentifier(
                        imageName, "drawable", mActivity.getPackageName());
                holder.img_category.setImageDrawable(mActivity.getResources().getDrawable(resID));
            } else {
                holder.img_category.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {

        }

        return mView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View mView = convertView;
        ViewHolderChild holder = null;
        if (mView == null) {
            holder = new ViewHolderChild();
            mView = mActivity.getLayoutInflater().inflate(R.layout.subcategory_child_view, null);

            holder.txt_subcategory = (TextView) mView.findViewById(R.id.txt_subcategory);
            mView.setTag(holder);
        } else {
            holder = (ViewHolderChild) mView.getTag();
        }

        try {
            CategoryModel categoryItem = categoryModelList.get(groupPosition);
            String childCateoryItem = categoryItem.getSubCategoryList().get(childPosition);

            holder.txt_subcategory.setText(childCateoryItem);
            if (GroupCheckHelper.isTextHeading(categoryItem.getCategory(),
                    categoryModelList.get(groupPosition).getSubCategoryList().get(childPosition))) {
                holder.txt_subcategory.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                holder.txt_subcategory.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }

        } catch (Exception e) {

        }

        return mView;

    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public class ViewHolderGroup {
        public TextView txt_category;
        public ImageView img_category;
    }

    public class ViewHolderChild {
        public TextView txt_subcategory;

    }
}
