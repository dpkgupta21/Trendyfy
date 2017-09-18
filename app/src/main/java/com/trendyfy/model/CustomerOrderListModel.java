package com.trendyfy.model;

import java.io.Serializable;

/**
 * Created by Deepak Gupta on 11-04-2017.
 */

public class CustomerOrderListModel implements Serializable {

    private String ORDERID;
    private String ORDERdATE;
    private String CUSTOMERNAME;
    private String CUSTOMERADDRESS;
    private String cUSTOMERMOBILENO;
    private String CUSTOMERPINCODE;
    private String INVOICEDATE;
    private String INVOICENO;
    private String PAYMENTTYPE;
    private String VENDORNAME;
    private String VENDORaDDRESS;
    private String VENDOREMAIL;
    private String VATCST;
    private String semail;
    private String WayBillNo;
    private String ModelName;
    private String Image1;
    private String ProductValue;

    public String getORDERID() {
        return ORDERID;
    }

    public void setORDERID(String ORDERID) {
        this.ORDERID = ORDERID;
    }

    public String getORDERdATE() {
        return ORDERdATE;
    }

    public void setORDERdATE(String ORDERdATE) {
        this.ORDERdATE = ORDERdATE;
    }

    public String getCUSTOMERNAME() {
        return CUSTOMERNAME;
    }

    public void setCUSTOMERNAME(String CUSTOMERNAME) {
        this.CUSTOMERNAME = CUSTOMERNAME;
    }

    public String getCUSTOMERADDRESS() {
        return CUSTOMERADDRESS;
    }

    public void setCUSTOMERADDRESS(String CUSTOMERADDRESS) {
        this.CUSTOMERADDRESS = CUSTOMERADDRESS;
    }

    public String getcUSTOMERMOBILENO() {
        return cUSTOMERMOBILENO;
    }

    public void setcUSTOMERMOBILENO(String cUSTOMERMOBILENO) {
        this.cUSTOMERMOBILENO = cUSTOMERMOBILENO;
    }

    public String getCUSTOMERPINCODE() {
        return CUSTOMERPINCODE;
    }

    public void setCUSTOMERPINCODE(String CUSTOMERPINCODE) {
        this.CUSTOMERPINCODE = CUSTOMERPINCODE;
    }

    public String getINVOICEDATE() {
        return INVOICEDATE;
    }

    public void setINVOICEDATE(String INVOICEDATE) {
        this.INVOICEDATE = INVOICEDATE;
    }

    public String getINVOICENO() {
        return INVOICENO;
    }

    public void setINVOICENO(String INVOICENO) {
        this.INVOICENO = INVOICENO;
    }

    public String getPAYMENTTYPE() {
        return PAYMENTTYPE;
    }

    public void setPAYMENTTYPE(String PAYMENTTYPE) {
        this.PAYMENTTYPE = PAYMENTTYPE;
    }

    public String getVENDORNAME() {
        return VENDORNAME;
    }

    public void setVENDORNAME(String VENDORNAME) {
        this.VENDORNAME = VENDORNAME;
    }

    public String getVENDORaDDRESS() {
        return VENDORaDDRESS;
    }

    public void setVENDORaDDRESS(String VENDORaDDRESS) {
        this.VENDORaDDRESS = VENDORaDDRESS;
    }

    public String getVENDOREMAIL() {
        return VENDOREMAIL;
    }

    public void setVENDOREMAIL(String VENDOREMAIL) {
        this.VENDOREMAIL = VENDOREMAIL;
    }

    public String getVATCST() {
        return VATCST;
    }

    public void setVATCST(String VATCST) {
        this.VATCST = VATCST;
    }

    public String getSemail() {
        return semail;
    }

    public void setSemail(String semail) {
        this.semail = semail;
    }

    public String getWayBillNo() {
        return WayBillNo;
    }

    public void setWayBillNo(String wayBillNo) {
        WayBillNo = wayBillNo;
    }

    public String getModelName() {
        return ModelName;
    }

    public void setModelName(String modelName) {
        ModelName = modelName;
    }

    public String getImage1() {
        return Image1;
    }

    public void setImage1(String image1) {
        Image1 = image1;
    }

    public String getProductValue() {
        return ProductValue;
    }

    public void setProductValue(String productValue) {
        ProductValue = productValue;
    }
}
