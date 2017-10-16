package com.trendyfy.model;

/**
 * Created by Deepak Gupta on 11-04-2017.
 */

public class SearchKeywordDataModel {

    private String PageType;
    private String itemType;
    private String CategoryId;

    public String getPageType() {
        return PageType;
    }

    public void setPageType(String pageType) {
        PageType = pageType;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }
}
