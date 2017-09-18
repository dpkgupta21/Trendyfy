package com.trendyfy.preference;


import android.content.Context;


public class PreferenceHelp {

    public static String ADDED_PRODUCT = "added_product";
    public static String USER_INFO = "user_info";
    public static String TOTAL_CART_PRICE = "total_cart_price";
    public static String PAGE_TYPE = "page_type";
    public static String ITEM_NAME = "item_name";
    public static String CITY_NAME = "city_name";
    public static String LOCATION_NAME = "location_name";
    public static String CITY_ID = "city_id";
    public static String LOCATION_ID = "location_id";
    public static String STR_CATEGORY_ID = "str_category_id";

    public static String getProductList(Context context) {
//        UserDTO userDTO = AppPreference.getObjectFromPref(context, PreferenceHelp.USER_INFO);
//        if (userDTO != null)
//            return userDTO.getId();
//        else
        return "0";
    }


}
