package com.trendyfy.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;

import com.trendyfy.customviews.CustomAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static int convertSpToPixels(float sp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        return px;
    }

    public static final void hideKeyboard(Activity ctx) {

        if (ctx.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(ctx.getCurrentFocus().getWindowToken(), 0);
        }
    }


    public static final boolean isOnline(Context context) {

        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        return conMgr.getActiveNetworkInfo() != null

                && conMgr.getActiveNetworkInfo().isAvailable()

                && conMgr.getActiveNetworkInfo().isConnected();
    }


    public static ProgressDialog createProgressDialog(Context context, String message, boolean isCancelable) {
        ProgressDialog pdialog = new ProgressDialog(context);
        if (message == null)
            pdialog.setMessage("Loading....");
        else
            pdialog.setMessage(message);
        pdialog.setIndeterminate(true);
        pdialog.setCancelable(isCancelable);
        return pdialog;
    }


    public static void showNoNetworkDialog(Context ctx) {

        showDialog(ctx, "No Network Connection",
                "Internet is not available. Please check your network connection.")
                .show();

        //customDialog("Internet is not available. Please check your network connection.", ctx);
    }

    public static void showExceptionDialog(Context ctx) {

        showDialog(ctx, "Error",
                "Some Error occured. Please try later.")
                .show();

        ///customDialog("Some Error occured. Please try later.", ctx);

    }


    public static AlertDialog showDialog(Context ctx, String title, String msg) {

        return showDialog(ctx, title, msg,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });

    }


    public static AlertDialog showDialog(Context ctx, String title, String msg,
                                         DialogInterface.OnClickListener listener) {

        return showDialog(ctx, title, msg, "Ok", null, listener, null);
    }


    public static AlertDialog showDialog(Context ctx, String title, String msg,
                                         String btn1, String btn2, DialogInterface.OnClickListener listener) {

        return showDialog(ctx, title, msg, btn1, btn2, listener,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

    }


    public static AlertDialog showDialog(Context ctx, String title, String msg,
                                         String btn1, String btn2,
                                         DialogInterface.OnClickListener listener1,
                                         DialogInterface.OnClickListener listener2) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(msg).setCancelable(false)
                .setPositiveButton(btn1, listener1);
        if (btn2 != null)
            builder.setNegativeButton(btn2, listener2);

        AlertDialog alert = builder.create();
        alert.show();
        return alert;

    }


    public static void ShowLog(String tag, String response) {
        Log.i(tag, response);
    }


    public static boolean getWebServiceStatus(JSONObject json) {

        try {

            return json.getBoolean("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static int getWebServiceStatusByInt(JSONObject json) {

        try {

            return json.getInt("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static String getWebServiceMessage(JSONObject json) {

        try {
            return json.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Error";
    }


    public static void customDialog(String msg, Context context) {
        new CustomAlert(context).singleButtonAlertDialog(msg, "Ok", null, 0);

    }

    public static boolean isDOBValid(String fromDate, String toDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date date1 = sdf.parse(fromDate);
            Date date2 = sdf.parse(toDate);
            if (date2.after(date1)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isFromDateGreater(String fromDate, String toDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sdf.parse(fromDate);
            Date date2 = sdf.parse(toDate);
            if (date2.after(date1)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }


    public static Date getDateFromString(String date) {
        Date date1 = null;
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
            date1 = dateFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date1;
    }

    public static boolean isValidEmail(String email) {

        String emailExp = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,10}$";
        Pattern pattern = Pattern.compile(emailExp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("countrycode.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static List<Map<String, String>> getCountryCode(Context context) {
        List<Map<String, String>> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(Utils.loadJSONFromAsset(context));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Map<String, String> map = new HashMap<>();
                map.put("name", jsonObject.getString("name"));
                map.put("dial_code", jsonObject.getString("dial_code"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static String secondsToDate(long seconds) {

        Date date = new Date(seconds * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM | h:mm a");
        return sdf.format(date);
    }


    public static String locationUrl(String latitude, String longitude, String type, String radius) {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=" + radius + "&types=" + type + "&sensor=true&key=AIzaSyAhm1YfISuWPrwKHOBx7OGGCuc6WdSryWk";

        return url;
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
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE).edit();
        try {
            editor.putString(key, ObjectSerializer.serialize(e));
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        editor.commit();

    }

    public static <E> void removeObjectIntoPref(Context context, String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE).edit();
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
            SharedPreferences pref = context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
            return (E) ObjectSerializer.deserialize(pref.getString(key, null));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getStringFromPref(Context context, String key) {
        return context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).getString(key, null);
    }

    public static void putStringIntoPref(Context context, String key, String value) {
        context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit().putString(key, value).commit();
    }

    public static void putIntIntoPref(Context context, String key, int value) {
        context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit().putInt(key, value).commit();
    }

    public static void putBooleanIntoPref(Context context, String key, boolean value) {
        context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit().putBoolean(key, value).commit();
    }

    public static void putLongIntoPref(Context context, String key, long value) {
        context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit().putLong(key, value).commit();
    }

    public static int getIntFromPref(Context context, String key) {
        return context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).getInt(key, 0);
    }

    public static boolean getBooleanFromPref(Context context, String key, boolean defValue) {
        return context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).getBoolean(key, defValue);
    }

    public static long getLongFromPref(Context context, String key) {
        return context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).getLong(key, 0);
    }


}
