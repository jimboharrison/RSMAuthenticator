package com.rsm.rsmauthenticator;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by James on 06/04/2016.
 */
public class MobileResponseService extends IntentService {

    public static final String ApproveAction = "Approve";
    public static final String DeclineAction = "Decline";
    public static final String serverApiHost = "jh-devserver.cloudapp.net";
    String otp, requestTime, username, appName, requestId;
    OkHttpClient client;
    UserLocalStore localStore;
    TelephonyManager telephonyManager;

    public MobileResponseService() {
        super("MobileResponseService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);

        final String action = intent.getAction();

        otp = intent.getStringExtra("otp");
        username = intent.getStringExtra("username");
        appName = intent.getStringExtra("appName");
        requestTime = intent.getStringExtra("requestTime");
        requestId = intent.getStringExtra("requestId");
        requestTime = intent.getStringExtra("requestTime");
        String notificationId = intent.getStringExtra("notificationId");
        client = new OkHttpClient();
        localStore = new UserLocalStore(this);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        nMgr.cancel(Integer.parseInt(notificationId));

        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(serverApiHost)
                .addPathSegment("AppDashboard")
                .addPathSegment("api")
                .addPathSegment("AccessRequest")
                .build();


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();

        if(ApproveAction.equals(action)){
            int responseCode = 2;

            try{
                jsonObject.put("AccessReuqestId", Integer.parseInt(requestId));
                jsonObject.put("AccessRequestResponse", responseCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else if (DeclineAction.equals(action)){
            int responseCode = 1;

            try{
                jsonObject.put("AccessReuqestId", Integer.parseInt(requestId));
                jsonObject.put("AccessRequestResponse", responseCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            throw new IllegalArgumentException("Unsopported Action: " + action);
        }

        String json = jsonObject.toString();

        RequestBody requestBody = RequestBody.create(JSON, json);

        //adding basic authentication from logged in user on mobile app
        User user = localStore.getLoggedInUser();
        String credential = Credentials.basic(user.email, user.password);

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", credential)// adding authorization header to request
                .header("DeviceIMEI", telephonyManager.getDeviceId())//security step
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String hello = "failed call";
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

    }
}
