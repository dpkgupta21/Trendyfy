package com.trendyfy.model;

import java.util.List;

/**
 * Created by Deepak Gupta on 11-04-2017.
 */

public class CategoryModel {

    private String category;
    private String categoryImageName;
    private String categoryNameForWebservice;
    private List<String> subCategoryList;
    private List<String> subCategoryListForWebservice;

    public List<String> getSubCategoryListForWebservice() {
        return subCategoryListForWebservice;
    }

    public void setSubCategoryListForWebservice(List<String> subCategoryListForWebservice) {
        this.subCategoryListForWebservice = subCategoryListForWebservice;
    }

    public String getCategoryNameForWebservice() {
        return categoryNameForWebservice;
    }

    public void setCategoryNameForWebservice(String categoryNameForWebservice) {
        this.categoryNameForWebservice = categoryNameForWebservice;
    }



    public String getCategoryImageName() {
        return categoryImageName;
    }

    public void setCategoryImageName(String categoryImageName) {
        this.categoryImageName = categoryImageName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getSubCategoryList() {
        return subCategoryList;
    }

    public void setSubCategoryList(List<String> subCategoryList) {
        this.subCategoryList = subCategoryList;
    }
}
