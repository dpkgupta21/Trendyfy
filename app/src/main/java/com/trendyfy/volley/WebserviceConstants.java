package com.trendyfy.volley;

/**
 * Created by deepak.gupta on 21-04-2017.
 */

public class WebserviceConstants {
    public static String WEBSERVICE_URL = "http://webservice.trendyfy.com/";
    public static String CONNECTION_URL = WEBSERVICE_URL+"myservice.asmx/";
    public static String SEND_SMS_WEBSERVICE_URL = "http://103.16.142.193/api.php?username=trendyfy&password=kishor"+
            "&sender=TRNDFY&sendto=";
    public  static String MESSAGE_URL="&message=";

    public static String IMAGE_URL = "http://trendyfy.com/";

    public static String GET_PRODUCT = "GetProductJSON";
    public static String GET_PRODUCT_NEW = "GetProductJSONnew";
    public static String CUSTOMER_LOGIN = "CustomerloginJSON";
    public static String CUSTOMER_SIGNUP = "CustomerRegistrationJSON";
    public static String GET_CITY = "GeCityJSON";
    public static String GET_LOCATION_JSON = "GetLocationJSON";
    public static String ADD_IN_CART = "InsertItemIntoChartJSON";
    public static String DELETE_FROM_CART ="DeleteItemFromChartJSON";
    public static String LANDING_CATEGORY = "MainMasterMobileJSON";
    public static String GET_STATE ="GeStateJSON";
    public static String INSERT_INTO_PAYMENT_MASTER ="InsertIntopaymentMasterJSON";
    public static String CUSTOMER_SHIPPING_DETAIL_JSON ="CustomerShippingDetailJSON";
    public static String CUSTOMER_LOGIN_BY_ID_JSON ="CustomerloginbyIdJSON";
    public static String INSERT_TNTO_PAYMENT_MASTER_ONLINE_PAYMENT_JSON ="InsertIntopaymentMasterOnlinePaymentJSON";
    public static String INSERT_TNTO_PAYMENT_MASTER_SUCCESS_JSON ="InsertIntopaymentMasterforsuccessJSON";
    public static String CUSTOMER_PASSWORD_JSON= "GetCustomerPasswordJSON";
    public static String CUSTOMER_TRACK_ORDER_JSON="CustomerTrackOrderJSON";
    public static String CUSTOMER_ACCOUNT_STATEMENT_JSON="CustomerAccountStatementJSON";
    public static String GET_CUSTOMER_CASHBACK="GetCustomerCashBack";

}
