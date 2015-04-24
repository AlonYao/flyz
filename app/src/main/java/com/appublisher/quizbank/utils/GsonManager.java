package com.appublisher.quizbank.utils;

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
}
