package com.rsm.rsmauthenticator;
import com.loopj.android.http.*;

/**
 * Created by James on 22/01/2016.
 */
public class HttpClient {
    private static final String BASE_URL = "138.91.61.37/AppDashboard/api/";
    private static AsyncHttpClient httpClient = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        httpClient.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        httpClient.post(getAbsoluteUrl(url),params,responseHandler);
    }

    private static String getAbsoluteUrl(String url) {
        return BASE_URL + url;
    }
}
