package com.appublisher.quizbank.utils;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.utils.gson.BooleanTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

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

    /**
     * 获取数据模型
     * @param jsonStr JSON字符串
     * @param cls 数据模型class
     * @param <T> <T>
     * @return 数据模型
     */
    public static <T> T getObejctFromJSON(String jsonStr, Class<T> cls) {
        try {
            return getGson().fromJson(jsonStr, cls);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取数据模型
     * @param jsonObject JSONObject
     * @param cls 数据模型class
     * @param <T> <T>
     * @return 数据模型
     */
    public static <T> T getModel(JSONObject jsonObject, Class<T> cls) {
        if (jsonObject == null) return null;
        try {
            return getGson().fromJson(jsonObject.toString(), cls);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取Gson对象
     * @return Gson
     */
    public static Gson getGson() {
        return Globals.gson == null ? initGson() : Globals.gson;
    }

    /**
     * 将模型转换成字符串
     * @param object 数据模型
     * @param cls 数据模型class
     * @return 字符串
     */
    public static String modelToString(Object object, Class<?> cls) {
        return getGson().toJson(object, cls);
    }
}
