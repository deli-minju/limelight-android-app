package com.devmanjoo.limelight.data;

import com.devmanjoo.limelight.util.SessionCookieJar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://devmanjoo.mycafe24.com/";
    private static Retrofit retrofit;

    public static Retrofit get() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new SessionCookieJar())
                    .build();
            Gson gson = new GsonBuilder().setLenient().create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
