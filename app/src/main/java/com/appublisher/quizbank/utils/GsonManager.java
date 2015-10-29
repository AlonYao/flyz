package com.appublisher.quizbank.utils;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.utils.gson.BooleanTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

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
    public static String setObjectToJSON(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static <T> T getObejctFromJSON(String jsonStr, Class<T> cls) {
        Gson gson = new Gson();
        T t = gson.fromJson(jsonStr, cls);
        return t;
    }

    public static List getListObjectFromJSON(String jsonStr, TypeToken typeToken) {
        Gson gson = new Gson();
        List list = gson.fromJson(jsonStr, typeToken.getType());
        return list;
    }

    /**
     * 获取Gson对象
     * @return Gson
     */
    public static Gson getGson() {
        return Globals.gson == null ? initGson() : Globals.gson;
    }
}
