package com.trendyfy.model;

import java.io.Serializable;

/**
 * Created by Deepak Gupta on 11-04-2017.
 */

public class CustomerStatementListModel implements Serializable {

    private String TOTAL;
    private String TotalAmount;
    private String orderid;
    private String SKUID;
    private int quantity;
    private int price;
    private String payentType1;
    private String Image1;

    public String getImage1() {
        return Image1;
    }

    public void setImage1(String image1) {
        Image1 = image1;
    }

    public String getTOTAL() {
        return TOTAL;
    }

    public void setTOTAL(String TOTAL) {
        this.TOTAL = TOTAL;
    }

    public String getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        TotalAmount = totalAmount;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getSKUID() {
        return SKUID;
    }

    public void setSKUID(String SKUID) {
        this.SKUID = SKUID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPayentType1() {
        return payentType1;
    }

    public void setPayentType1(String payentType1) {
        this.payentType1 = payentType1;
    }
}
