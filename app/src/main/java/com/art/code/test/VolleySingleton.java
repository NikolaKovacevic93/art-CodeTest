package com.art.code.test;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Nikola on 11/16/2017.
 */

public class VolleySingleton {

    private static VolleySingleton volleySingletonInstance;
    private RequestQueue vollayRequestQueue;
    private static Context context;

    private VolleySingleton(Context context) {
        this.context = context;
        vollayRequestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context){
        if(volleySingletonInstance == null){
            volleySingletonInstance = new VolleySingleton(context);
        }
        return volleySingletonInstance;
    }

    public RequestQueue getRequestQueue(){
        if(vollayRequestQueue == null){
            vollayRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return vollayRequestQueue;
    }

    public <T> void addTorequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }

}
