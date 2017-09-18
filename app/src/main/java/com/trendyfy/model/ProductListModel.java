package com.trendyfy.model;

import java.io.Serializable;

/**
 * Created by Deepak Gupta on 11-04-2017.
 */

public class ProductListModel implements Serializable {

    private String SKUID;
    private String ProductName;
    private String Image1;
    private String ItemId;
    private String ModelName;
    private double MRP;
    private String Size;
    private double SellingPrice;
    private int AvaliableQty;
    private int AddInCartQty=0;
    private String UOMID;
    private String Color;
    private String pageType;
    private String itemName;
    private String descrption;
    private String Result;

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public String getDescrption() {
        return descrption;
    }

    public void setDescrption(String descrption) {
        this.descrption = descrption;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getUOMID() {
        return UOMID;
    }

    public void setUOMID(String UOMID) {
        this.UOMID = UOMID;
    }

    public int getAddInCartQty() {
        return AddInCartQty;
    }

    public void setAddInCartQty(int addInCartQty) {
        AddInCartQty = addInCartQty;
    }

    public int getAvaliableQty() {
        return AvaliableQty;
    }

    public void setAvaliableQty(int avaliableQty) {
        AvaliableQty = avaliableQty;
    }

    public double getMRP() {
        return MRP;
    }

    public void setMRP(double MRP) {
        this.MRP = MRP;
    }

    public double getSellingPrice() {
        return SellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        SellingPrice = sellingPrice;
    }

    public String getSKUID() {
        return SKUID;
    }

    public void setSKUID(String SKUID) {
        this.SKUID = SKUID;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getImage1() {
        return Image1;
    }

    public void setImage1(String image1) {
        Image1 = image1;
    }

    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String itemId) {
        ItemId = itemId;
    }

    public String getModelName() {
        return ModelName;
    }

    public void setModelName(String modelName) {
        ModelName = modelName;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }
}
