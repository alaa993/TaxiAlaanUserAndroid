package com.taxialaan.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import androidx.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by jayakumar on 29/01/17.
 */
///

public class G extends Application {

    public static final String TAG = G.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    public static Handler handler = new Handler();

    private static G mInstance;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        initCalligraphyConfig();
        MobileAds.initialize(mInstance, "ca-app-pub-6606021354718512~3331887888");
    }


    public static void runOnUiThread(Runnable runnable) {
        if (handler != null) {
            handler.post(runnable);
        }

    }

    public static void runOnUiThread(Runnable runnable, long delay) {
        if (handler != null) {
            handler.postDelayed(runnable, delay);
        }
    }

    private void initCalligraphyConfig() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                                              .setDefaultFontPath(getResources().getString(R.string.bariol))
                                              .setFontAttrId(R.attr.fontPath)
                                              .build()
                                     );
    }

    public static void toast(final String body) {
        Toast toast = Toast.makeText(getInstance().getApplicationContext(), body, Toast.LENGTH_LONG);
        //  LinearLayout toastLayout = (LinearLayout) toast.getView();
        // TextView toastTV = (TextView) toastLayout.getChildAt(0);
        // toastTV.setTypeface(G.getInstance());
        toast.show();
    }

    public static synchronized G getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void cancelRequestInQueue(String tag) {
        getRequestQueue().cancelAll(tag);
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the no_user tag if tag is empty
        req.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 0));
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 0));
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    public static String trimMessage(String json) {
        String trimmedString = "";

        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    JSONArray value = jsonObject.getJSONArray(key);
                    for (int i = 0, size = value.length(); i < size; i++) {
                        Log.e("Errors in Form", "" + value.getString(i));
                        trimmedString += value.getString(i);
                        if (i < size - 1) {
                            trimmedString += '\n';
                        }
                    }
                } catch (JSONException e) {

                    trimmedString += jsonObject.optString(key);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        Log.e("Trimmed", "" + trimmedString);

        return trimmedString;
    }


}
