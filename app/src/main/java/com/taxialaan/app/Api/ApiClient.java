package com.taxialaan.app.Api;


import com.taxialaan.app.G;
import com.taxialaan.app.Helper.SharedHelper;
import com.taxialaan.app.Helper.URLHelper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;

    ApiInterface apiInterface;

    public ApiClient() {
        getClient();
    }

    public static Retrofit getClient() {
        if (retrofit == null) {

            OkHttpClient.Builder client = new OkHttpClient.Builder();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.addInterceptor(getInterceptor());
            client.addInterceptor(logging);

            retrofit = new Retrofit.Builder()
                    .baseUrl(URLHelper.base)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();
        }
        return retrofit;
    }

    public ApiInterface getService() {
        if (apiInterface == null) {
            apiInterface = getClient().create(ApiInterface.class);
        }
        return apiInterface;
    }

    private static Interceptor getInterceptor() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                                                         .addHeader("X-Requested-With", "XMLHttpRequest")
                                                         .addHeader("Authorization", SharedHelper.getKey(G.getInstance(), "token_type") + " " + SharedHelper.getKey(G.getInstance(), "access_token"));

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };

        return interceptor;
    }

}
