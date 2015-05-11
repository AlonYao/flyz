package com.appublisher.quizbank.utils;


import android.annotation.SuppressLint;
import android.support.v7.app.ActionBarActivity;

import com.appublisher.quizbank.Globals;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 通用工具类
 */
public class Utils {

    /**
     * 判断是否是游客身份
     * @return 是与否
     */
    public static boolean isGuest() {
        String guest_id = Globals.sharedPreferences.getString("guest_id", "");
        return guest_id != null && !guest_id.equals("");
    }

    /**
     * 转换成百分比(保留3位小数点)
     * @param rate 比率
     * @return 百分比
     */
    public static String rateToString(float rate) {
        int rateInt = (int) (Math.round(rate*10000)/100.0);
        return String.valueOf(rateInt);
    }

    /**
     * JSONArray拼接方法
     * @param ja1  旧的JSONArray
     * @param ja2  新的JSONArray
     * @return  ja1 + ja2
     */
    public static JSONArray jointJSONArray(JSONArray ja1, JSONArray ja2) {
        if (ja1 != null && ja2 != null) {
            StringBuilder sb = new StringBuilder();

            // 第一个JSONArray字符串
            String sJa1 = ja1.toString();
            if (!sJa1.equals("")) {
                sJa1 = sJa1.substring(0, sJa1.length() - 1);
                sb.append(sJa1);
                sb.append(",");
            }

            // 第二个JSONArray字符串
            String sJa2 = ja2.toString();
            if (!sJa2.equals("")) {
                sJa2 = sJa2.substring(1);
                sb.append(sJa2);
            }

            try {
                return new JSONArray(sb.toString());
            } catch (JSONException e) {
                return new JSONArray();
            }
        } else {
            return new JSONArray();
        }
    }

    /**
     * 日期转换 Date To String
     * @param date  日期
     * @param format  日期格式
     * @return  String类型的日期
     */
    public static String DateToString(Date date, String format) {
        if (date != null && format != null) {
            try {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf =
                        new SimpleDateFormat(format);
                return sdf.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    /**
     * 获取当前时间的前一天
     * @return  昨天的时间
     */
    public static Date getYesterdayDate() {
        Date dNow = new Date();   //当前时间
        Date dBefore;

        Calendar calendar = Calendar.getInstance();  //得到日历
        calendar.setTime(dNow);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
        dBefore = calendar.getTime();   //得到前一天的时间

        return dBefore;
    }

    /**
     * boolean类型数据转换成int类型数据（用于数据库保存）
     * @param booleanValue  boolean数据
     * @return  int数据 0：false 1：true
     */
    public static int booleanToInt(boolean booleanValue) {
        int intValue = 0;

        if (booleanValue) {
            intValue = 1;
        }

        return intValue;
    }

    /**
     * 计算指定日期与当前日期的日期差
     * @param date 指定日期
     * @return 日期差
     */
    public static long dateMinusNow(String date) {
        long day = 0;

        if (date != null && !date.equals("")) {
            try {
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY,0);
                cal.set(Calendar.MINUTE,0);
                cal.set(Calendar.SECOND,0);
                Date finalD;
                finalD = formatter.parse(date);
                Calendar finalCal = Calendar.getInstance();
                finalCal.setTime(finalD);
                finalCal.set(Calendar.HOUR_OF_DAY,0);
                finalCal.set(Calendar.MINUTE,0);
                finalCal.set(Calendar.SECOND,0);

                day = (finalCal.getTimeInMillis()/1000 - cal.getTimeInMillis()/1000)/(24*60*60);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return day;
    }

    /**
     * 日期转换
     * @param date 原日期
     * @param targetType 转换后的日期类型
     * @return 转换后的日期
     */
    public static String switchDate(String date, String targetType) {
        if (date == null || date.equals("")) return "";

        if ("hh-dd".equals(targetType)) {
            try {
                return date.substring(5, 10);
            } catch (Exception e) {
                return "";
            }
        }

        return "";
    }

    /**
     * 更新Menu
     * @param activity Activity
     */
    public static void updateMenu(ActionBarActivity activity) {
        if(android.os.Build.VERSION.SDK_INT >= 11) {
            activity.invalidateOptionsMenu();	// 改变menu的状态
        } else {
            activity.supportInvalidateOptionsMenu();
        }
    }
}
