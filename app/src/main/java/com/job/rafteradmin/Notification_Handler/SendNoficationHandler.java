package com.job.rafteradmin.Notification_Handler;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class SendNoficationHandler {


    public static void sendFCMPush(Context context,String title,String msg) {
        final String Legacy_SERVER_KEY = "AAAARvRLf2U:APA91bGghNjPX-BZkyws_k_rWq9K5Hgoon0a7ZZgMzzrluf44vz-cAI0jVNNIbEg1OliMfFw47JY9dmcryTwskXhejKKosFEXKUg_Pin59pCMM3fBbBOjWf_rmRxzxgAQ5TF7R7iy5rA";
        JSONObject notificationObj = null;
        JSONObject dataObj = null;
        //JSONObject extraObj = null;
        try {
            notificationObj = new JSONObject();
            dataObj = new JSONObject();
            dataObj .put("title", title);
            dataObj .put("body", msg);
            // dataObj .put("action", "Notification.class");
            //dataObj .put("sound", "default");
            // dataObj .put("icon", "graphics"); //   icon_name image must be there in drawable
            //dataObj .put("tag", token);
            //dataObj .put("priority", "high");
            //extraObj = new JSONObject();
            //extraObj.put("text", msg);
            // extraObj.put("title", title);
            notificationObj.put("to","/topics/"+"JobNews");
            notificationObj.put("notification", dataObj);
            //notificationObj.put("data", extraObj);
            Log.e("!_@rj@_@@_PASS:>", notificationObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", notificationObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("!_@@_SUCESS", response + "");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("!_@@_Errors--", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }


}
