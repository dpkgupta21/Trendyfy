package com.trendyfy.preference;


import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

public class AppPreference {


    public static final String PREF_NAME = "Trendyfy_PREFERENCES";

    public static void setTotalCartPrice(Context context, float cartPrice) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(PreferenceHelp.TOTAL_CART_PRICE, cartPrice);
        editor.apply();
    }

    public static float getTotalCartPrice(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getFloat(PreferenceHelp.TOTAL_CART_PRICE,
                0.0f);
    }

    public static void setCityName(Context context, String cityName) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferenceHelp.CITY_NAME, cityName);
        editor.apply();
    }

    public static String getCityName(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(PreferenceHelp.CITY_NAME,
                null);
    }

    public static void setCityId(Context context, String cityId) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferenceHelp.CITY_ID, cityId);
        editor.apply();
    }

    public static String getCityId(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(PreferenceHelp.CITY_ID,
                null);
    }

    public static void setLocationName(Context context, String locationName) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferenceHelp.LOCATION_NAME, locationName);
        editor.apply();
    }

    public static String getLocationName(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(PreferenceHelp.LOCATION_NAME,
                null);
    }

    public static void setLocationId(Context context, String locationId) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferenceHelp.LOCATION_ID, locationId);
        editor.apply();
    }

    public static String getLocationId(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(PreferenceHelp.LOCATION_ID,
                null);
    }

    public static void setStrCategoryId(Context context, String locationId) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferenceHelp.STR_CATEGORY_ID, locationId);
        editor.apply();
    }

    public static String getStrCategoryId(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(PreferenceHelp.STR_CATEGORY_ID,
                null);
    }

    public static void setPageType(Context context, String pageType) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferenceHelp.PAGE_TYPE, pageType);
        editor.apply();
    }

    public static String getPageType(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(PreferenceHelp.PAGE_TYPE,
                "");
    }

    public static void setItemName(Context context, String itemName) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferenceHelp.ITEM_NAME, itemName);
        editor.apply();
    }

    public static String getItemName(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(PreferenceHelp.ITEM_NAME,
                "");
    }

    /**
     * This genric method use to put object into preference<br>
     * How to use<br>
     * Bean bean = new Bean();<br>
     * putObjectIntoPref(context,bean,key)
     *
     * @param context Context of an application
     * @param e       your genric object
     * @param key     String key which is associate with object
     */
    public static <E> void putObjectIntoPref(Context context, E e, String key) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        try {
            editor.putString(key, ObjectSerializer.serialize(e));
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        editor.commit();

    }

    public static <E> void removeObjectIntoPref(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.commit();

    }

    /**
     * This method is use to get your object from preference.<br>
     * How to use<br>
     * Bean bean = getObjectFromPref(context,key);
     *
     * @param context
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <E> E getObjectFromPref(Context context, String key) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            return (E) ObjectSerializer.deserialize(context.getSharedPreferences(PREF_NAME,
                    Context.MODE_PRIVATE).getString(key, null));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
