package com.appublisher.quizbank.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.widget.ScrollView;

import com.appublisher.quizbank.Globals;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用工具类
 */
public class Utils {

    /**
     * 判断是否是游客身份
     *
     * @return 是与否
     */
    public static boolean isGuest() {
        String guest_id = Globals.sharedPreferences.getString("guest_id", "");
        return !guest_id.equals("");
    }

    /**
     * 转换成百分比(保留2位小数点)
     *
     * @param rate 比率
     * @return 百分比
     */
    public static String rateToPercent(float rate) {
        int rateInt = (int) (Math.round(rate * 10000) / 100.0);
        return String.valueOf(rateInt);
    }

    /**
     * JSONArray拼接方法
     *
     * @param ja1 旧的JSONArray
     * @param ja2 新的JSONArray
     * @return ja1 + ja2
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
     *
     * @param date   日期
     * @param format 日期格式
     * @return String类型的日期
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
     *
     * @return 昨天的时间
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
     * 获取当前日期(yyyy-MM-dd)
     *
     * @return 当前日期
     */
    public static String getCurDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        return format.format(curDate);
    }

    /**
     * boolean类型数据转换成int类型数据（用于数据库保存）
     *
     * @param booleanValue boolean数据
     * @return int数据 0：false 1：true
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
     *
     * @param date 指定日期
     * @return 日期差
     */
    public static long dateMinusNow(String date) {
        long day = 0;

        if (date != null && !date.equals("")) {
            try {
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                Date finalD;
                finalD = formatter.parse(date);
                Calendar finalCal = Calendar.getInstance();
                finalCal.setTime(finalD);
                finalCal.set(Calendar.HOUR_OF_DAY, 0);
                finalCal.set(Calendar.MINUTE, 0);
                finalCal.set(Calendar.SECOND, 0);
                //指定日期－当前日期
                day = (finalCal.getTimeInMillis() / 1000 - cal.getTimeInMillis() / 1000) / (24 * 60 * 60);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return day;
    }

    /**
     * 计算指定日期与当前日期的秒数差
     *
     * @param date 指定日期
     * @return 秒数差
     */
    public static long getSecondsByDateMinusNow(String date) {
        long seconds = 0;

        if (date == null || date.length() == 0) return 0;

        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ParsePosition parsePosition = new ParsePosition(0);
            Date time = formatter.parse(date, parsePosition);
            seconds = time.getTime() - new Date().getTime();
        } catch (Exception e) {
            // Empty
        }

        return seconds / 1000;
    }

    /**
     * 日期转换
     *
     * @param date       原日期
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
     *
     * @param activity Activity
     */
    public static void updateMenu(ActionBarActivity activity) {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            activity.invalidateOptionsMenu();    // 改变menu的状态
        } else {
            activity.supportInvalidateOptionsMenu();
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue px
     * @return sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue sp
     * @return px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 判断邮箱是否合法
     *
     * @param email 邮箱
     * @return 是否
     */
    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) return false;

        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m = p.matcher(email);

        return m.matches();
    }

    /**
     * ScrollView截屏
     *
     * @return Bitmap
     */
    public static Bitmap getBitmapByView(ScrollView scrollView) {
        try {
            int h = 0;
            Bitmap bitmap;
            // 获取listView实际高度
            for (int i = 0; i < scrollView.getChildCount(); i++) {
                h += scrollView.getChildAt(i).getHeight();
            }
            // 创建对应大小的bitmap
            bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                    Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bitmap);
            scrollView.draw(canvas);

//            // 测试输出，这部分代码预留，方便测试
//            FileOutputStream out = null;
//            try {
//                // 音频本地缓存文件夹
//                String folder = activity.getApplicationContext().getFilesDir().getAbsolutePath()
//                        + "/pic/";
//                FileMange.mkDir(folder);
//
//                out = new FileOutputStream(folder + "test.png");
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            try {
//                if (null != out) {
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//                    out.flush();
//                    out.close();
//                }
//            } catch (IOException e) {
//                // Empty
//            }

            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    /**
     * ViewPager截屏
     *
     * @return Bitmap
     */
    public static Bitmap getBitmapByView(ViewPager viewPager) {
        try {
            Bitmap bitmap;
            // 创建对应大小的bitmap
            bitmap = Bitmap.createBitmap(viewPager.getWidth(), viewPager.getHeight(),
                    Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bitmap);
            viewPager.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    /**
     * 获取百分比
     *
     * @param x 分子
     * @param y 分母
     * @return 比率（保留1位小数）
     */
    public static String getPercent1(int x, int y) {
        if (y == 0 || x == 0) return "0%";

        double baix = x * 1.0;
        double baiy = y * 1.0;
        double fen = baix / baiy;
        DecimalFormat df1 = new DecimalFormat("##.0%"); // ##.00%,百分比格式，后面不足2位的用0补齐
        return df1.format(fen);
    }
}
