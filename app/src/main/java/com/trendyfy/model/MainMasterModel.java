package com.trendyfy.model;

/**
 * Created by Deepak Gupta on 11-04-2017.
 */

public class MainMasterModel {

    private String A_ID;
    private String PageType;
    private String ItemType;
    private String Image1;
    private String categoryid;

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getA_ID() {
        return A_ID;
    }

    public void setA_ID(String a_ID) {
        A_ID = a_ID;
    }

    public String getPageType() {
        return PageType;
    }

    public void setPageType(String pageType) {
        PageType = pageType;
    }

    public String getItemType() {
        return ItemType;
    }

    public void setItemType(String itemType) {
        ItemType = itemType;
    }

    public String getImage1() {
        return Image1;
    }

    public void setImage1(String image1) {
        Image1 = image1;
    }
}
