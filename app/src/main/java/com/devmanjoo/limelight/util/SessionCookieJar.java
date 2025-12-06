package com.devmanjoo.limelight.util;

import java.util.*;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class SessionCookieJar implements CookieJar {
    private final Map<String, List<Cookie>> store = new HashMap<>();

    @Override public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        store.put(url.host(), cookies);
    }
    @Override public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = store.get(url.host());
        return cookies != null ? cookies : new ArrayList<>();
    }
}
