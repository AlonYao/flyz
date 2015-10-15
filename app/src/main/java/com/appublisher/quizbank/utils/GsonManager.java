package com.appublisher.quizbank.utils;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.utils.gson.BooleanTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Gson库管理类
 */
public class GsonManager {
    /**
     * 初始化Gson对象
     * @return Gson对象
     */
    public static Gson initGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Boolean.class, new BooleanTypeAdapter())
                .registerTypeAdapter(boolean.class, new BooleanTypeAdapter())
                .create();
    }

    public static <T> T getObejctFromJSON(String jsonStr, Class<T> cls) {
        Gson gson = new Gson();
        T t = gson.fromJson(jsonStr, cls);
        return t;
    }

    /**
     * 获取Gson对象
     */
    public static Gson getGson() {
        return Globals.gson == null ? GsonManager.initGson() : Globals.gson;
    }

}
