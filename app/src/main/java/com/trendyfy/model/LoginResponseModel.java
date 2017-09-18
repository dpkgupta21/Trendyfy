package com.trendyfy.model;

import java.io.Serializable;

/**
 * Created by Deepak Gupta on 11-04-2017.
 */

public class LoginResponseModel implements Serializable {

    private String A_Id;
    private String Name;
    private String Password;
    private String Email;
    private String Address;
    private String Pincode;
    private String MobileNo;
    private String City;
    private int StateID;
    private String SName;
    private String SPhone;
    private String SAddress;
    private String SPincode;
    private String SCity;
    private int SStateID;
    private String RegisterDate;
    private int CustomerCashBackAmount;
    private String Result;

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public int getStateID() {
        return StateID;
    }

    public void setStateID(int stateID) {
        StateID = stateID;
    }

    public int getSStateID() {
        return SStateID;
    }

    public void setSStateID(int SStateID) {
        this.SStateID = SStateID;
    }

    public String getA_Id() {
        return A_Id;
    }

    public void setA_Id(String a_Id) {
        A_Id = a_Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPincode() {
        return Pincode;
    }

    public void setPincode(String pincode) {
        Pincode = pincode;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getSName() {
        return SName;
    }

    public void setSName(String SName) {
        this.SName = SName;
    }

    public String getSPhone() {
        return SPhone;
    }

    public void setSPhone(String SPhone) {
        this.SPhone = SPhone;
    }

    public String getSAddress() {
        return SAddress;
    }

    public void setSAddress(String SAddress) {
        this.SAddress = SAddress;
    }

    public String getSPincode() {
        return SPincode;
    }

    public void setSPincode(String SPincode) {
        this.SPincode = SPincode;
    }

    public String getSCity() {
        return SCity;
    }

    public void setSCity(String SCity) {
        this.SCity = SCity;
    }

    public String getRegisterDate() {
        return RegisterDate;
    }

    public void setRegisterDate(String registerDate) {
        RegisterDate = registerDate;
    }

    public int getCustomerCashBackAmount() {
        return CustomerCashBackAmount;
    }

    public void setCustomerCashBackAmount(int customerCashBackAmount) {
        CustomerCashBackAmount = customerCashBackAmount;
    }
}
