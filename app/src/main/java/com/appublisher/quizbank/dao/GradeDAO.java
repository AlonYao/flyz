package com.appublisher.quizbank.dao;

import android.app.Activity;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.appublisher.quizbank.activity.EvaluationActivity;
import com.appublisher.quizbank.activity.PracticeReportActivity;
import com.appublisher.quizbank.model.db.Grade;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户评价表DAO层
 */
public class GradeDAO {

    /**
     * 查询数据
     * @return Grade
     */
    public static Grade findByAppVersion(String appVersion) {
        if (appVersion == null) return null;

        try {
            return new Select().from(Grade.class)
                    .where("app_version = ?", appVersion)
                    .executeSingle();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 插入数据
     * @param appVersion 版本号
     */
    public static void insert(String appVersion) {
        if (appVersion == null) return;

        Grade item = findByAppVersion(appVersion);

        if (item != null) return;

        item = new Grade();
        item.app_version = appVersion;
        item.timestamp = System.currentTimeMillis();
        item.is_grade = 0;
        item.save();
    }

    /**
     * 更新时间戳
     * @param appVersion 版本号
     * @param timestamp 时间戳
     */
    public static void updateTimestamp(String appVersion, long timestamp) {
        try {
            new Update(Grade.class)
                    .set("timestamp = ?", timestamp)
                    .where("app_version = ?", appVersion)
                    .execute();
        } catch (Exception e) {
            // Empty
        }
    }

    /**
     * 记录用户评价
     * @param appVersion 版本号
     */
    public static void setGrade(String appVersion) {
        if (appVersion == null) return;

        try {
            new Update(Grade.class)
                    .set("is_grade = ?", 1)
                    .where("app_version = ?", appVersion)
                    .execute();
        } catch (Exception e) {
            // Empty
        }
    }

    /**
     * 查询全部数据
     * @return Grade
     */
    public static List<Grade> findAllByAppVersion() {
        try {
            return new Select().from(Grade.class).execute();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据时间戳判断是否应该显示评价Alert
     * @param appVersion 版本号
     * @return 是否
     */
    public static boolean isShowGradeAlert(String appVersion) {
        if (appVersion == null) return false;

        ArrayList<Grade> list = (ArrayList<Grade>) findAllByAppVersion();
        if (list == null || list.size() == 0) {
            insert(appVersion);
            return false;
        }

        Grade lastItem = list.get(list.size() - 1);
        if (lastItem == null) {
            insert(appVersion);
            return false;
        }

        // 第一层条件：判断版本号
        try {
            String lastVersion = lastItem.app_version;
            if (!lastVersion.equals(appVersion)) {
                // 本方法只适应两位以上的数字版本号
                String[] lastVersionArray = lastVersion.split("\\.");
                String[] appVersionArray = appVersion.split("\\.");

                if (Integer.parseInt(appVersionArray[0])
                        <= Integer.parseInt(lastVersionArray[0])) {
                    // 判断第一位
                    if (Integer.parseInt(appVersionArray[1])
                            <= Integer.parseInt(lastVersionArray[1])) {
                        // 判断第二位
                        // 如果前两位版本号相同，则不邀请评价
                        // 注：理论上不存在新版本号比旧版本号小的情况
                        return false;
                    } else {
                        insert(appVersion);
                        return true;
                    }
                } else {
                    insert(appVersion);
                    return true;
                }
            }
        } catch (Exception e) {
            insert(appVersion);
            return false;
        }

        // 第二层条件：用户首次打开App72个小时后开启
        if (lastItem.is_grade == 1) return false;
        long dif = System.currentTimeMillis() - lastItem.timestamp;
        return (dif / (1000 * 60 * 60)) > 72;
    }

    /**
     * 保存用户评价时的时间戳
     * @param appVersion 版本号
     * @param grade_timestamp 时间戳
     */
    public static void saveGradeTimestamp(String appVersion, long grade_timestamp) {
        Grade item = findByAppVersion(appVersion);
        if (item == null) {
            item = new Grade();
            item.app_version = appVersion;
            item.grade_timestamp = grade_timestamp;
            item.timestamp = System.currentTimeMillis();
            item.is_grade = 0;
            item.save();
        } else {
            try {
                new Update(Grade.class)
                        .set("grade_timestamp = ?", grade_timestamp)
                        .where("app_version = ?", appVersion)
                        .execute();
            } catch (Exception e) {
                // Empty
            }
        }
    }

    /**
     * 获取评价时间戳
     * @param appVersion 版本号
     * @return 时间戳
     */
    public static long getGradeTimestamp(String appVersion) {
        Grade item = findByAppVersion(appVersion);
        if (item == null) return 0;
        return item.grade_timestamp;
    }

    /**
     * 是否评价过
     * @param appVersion 版本号
     * @return 0：false 1：true
     */
    public static int isGrade(String appVersion) {
        Grade item = findByAppVersion(appVersion);
        if (item == null) return 0;
        return item.is_grade;
    }

    /**
     * 是否应该开启评价系统
     * @param appVersion 版本号
     * @return 是否
     */
    public static boolean isOpenGradeSys(String appVersion) {
        if (appVersion == null) return false;

        ArrayList<Grade> list = (ArrayList<Grade>) findAllByAppVersion();
        if (list == null || list.size() == 0) {
            return true;
        }

        Grade lastItem = list.get(list.size() - 1);
        if (lastItem == null) {
            return true;
        }

        if (appVersion.equals(lastItem.app_version)) {
            // 版本号没有变化
            if (lastItem.is_grade == 1) return false;
            if (lastItem.grade_timestamp == 0) return true;
            // 如果系统未检测到用户做出评价，但是评价动作已经超过了10秒，则默认视为完成评价
            long dev = (System.currentTimeMillis() - lastItem.grade_timestamp) / 1000;
            return dev < 10;
        } else {
            // 版本号发生了变化，判断版本号
            // 第一层条件
            try {
                String lastVersion = lastItem.app_version;
                // 本方法只适应两位以上的数字版本号
                String[] lastVersionArray = lastVersion.split("\\.");
                String[] appVersionArray = appVersion.split("\\.");

                if (Integer.parseInt(appVersionArray[0])
                        <= Integer.parseInt(lastVersionArray[0])) {
                    // 第一位相等
                    if (Integer.parseInt(appVersionArray[1])
                            <= Integer.parseInt(lastVersionArray[1])) {
                        // 第二位相等（小于的情况是为了防止版本号错误，理应不存在）
                        // 等价于版本号没有发生变化
                        if (lastItem.is_grade == 1) return false;
                        if (lastItem.grade_timestamp == 0) return true;
                        // 如果系统未检测到用户做出评价，但是评价动作已经超过了10秒，则默认视为完成评价
                        long dev = (System.currentTimeMillis() - lastItem.grade_timestamp) / 1000;
                        return dev < 10;
                    } else {
                        insert(appVersion);
                        return true;
                    }
                } else {
                    insert(appVersion);
                    return true;
                }
            } catch (Exception e) {
                insert(appVersion);
                return false;
            }
        }
    }

    /**
     * 获取用户当天第一次离开页面的日期
     * @param appVersion 版本号
     * @param activity EvaluationActivity：能力评估页 PracticeReportActivity：练习报告页
     * @return 日期
     */
    public static String getFirstLeaveDate(String appVersion, Activity activity) {
        Grade item = findByAppVersion(appVersion);
        if (item == null) return null;

        if (activity instanceof EvaluationActivity) {
            return item.first_leave_evaluation;
        } else if (activity instanceof PracticeReportActivity) {
            return item.first_leave_practicereport;
        }

        return null;
    }

    /**
     * 更新用户当天第一次离开练习报告页或者能力评估页的日期
     * @param appVersion 版本号
     * @param activity EvaluationActivity：能力评估页 PracticeReportActivity：练习报告页
     * @param date 日期
     */
    public static void updateFirstLeaveDate(String appVersion, String date, Activity activity) {
        Grade item = findByAppVersion(appVersion);
        if (item == null) return;

        try {
            if (activity instanceof EvaluationActivity) {
                new Update(Grade.class)
                        .set("first_leave_evaluation = ?", date)
                        .where("app_version = ?", appVersion)
                        .execute();
            } else if (activity instanceof PracticeReportActivity) {
                new Update(Grade.class)
                        .set("first_leave_practicereport = ?", date)
                        .where("app_version = ?", appVersion)
                        .execute();
            }
        } catch (Exception e) {
            // Empty
        }
    }
}
