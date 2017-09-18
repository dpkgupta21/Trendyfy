package com.trendyfy.volley;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.trendyfy.utility.Utils;

import java.io.UnsupportedEncodingException;
import java.util.Map;


public class CustomJsonRequest extends Request<String> {

    private Listener<String> listener;
    private Map<String, String> params;
    //private String cacheKey;

    public CustomJsonRequest(String url, Map<String, String> params,
                             Listener<String> reponseListener,
                             ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        // cacheKey = params.get("action");
        this.listener = reponseListener;
        this.params = params;
    }

    public CustomJsonRequest(int method, String url, Map<String, String> params,
                             Listener<String> reponseListener,
                             ErrorListener errorListener) {
        super(method, url, errorListener);
        //cacheKey = params.get("action");
        this.listener = reponseListener;
        this.params = params;
    }


    @Override
    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
        return params;
    }


    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            //JSONObject jsonObject = new JSONObject(jsonString);
            String parseResponse = null;
            if (jsonString.contains("[")) {
                parseResponse = jsonString.substring(jsonString.indexOf("["),
                        jsonString.indexOf("</string>"));

            } else {
                parseResponse = jsonString.substring(jsonString.lastIndexOf("<string xmlns=\"http://tempuri.org/\">"),
                        jsonString.indexOf("</string>"));
                parseResponse = parseResponse.replace("<string xmlns=\"http://tempuri.org/\">", "");
            }
            Utils.ShowLog("TAG", "" + jsonString);
            // force response to be cached
            //Map<String, String> headers = response.headers;
            //long cacheExpiration = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
            //long now = System.currentTimeMillis();
            //Cache.Entry entry = new Cache.Entry();
            //entry.data = response.data;
            //entry.etag = headers.get("ETag");
            //entry.ttl = now + cacheExpiration;
            //entry.serverDate = HttpHeaderParser.parseDateAsEpoch(headers.get("Date"));
            //entry.responseHeaders = headers;
            //entry = HttpHeaderParser.parseCacheHeaders(response);
            //Application.getInstance().getRequestQueue().getCache().put(cacheKey, entry);
            Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
            //Application.getInstance().getRequestQueue().getCache().put(cacheKey, entry);
            return Response.success(parseResponse,
                    entry);
            //return Response.success(jsonObject, entry);
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {

        listener.onResponse(response);
    }


}