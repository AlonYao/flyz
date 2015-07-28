package com.appublisher.quizbank.dao;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.appublisher.quizbank.model.db.Grade;

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
     * 根据时间戳判断是否应该显示评价Alert
     * @param appVersion 版本号
     * @return 是否
     */
    public static boolean isShowGradeAlert(String appVersion) {
        if (appVersion == null) return false;
        Grade item = findByAppVersion(appVersion);
        if (item == null || item.is_grade == 1) return false;
        long dif = System.currentTimeMillis() - item.timestamp;
        return (dif / (1000 * 60 * 60)) > 72;
    }

    /**
     * 保存用户评价时的时间戳
     * @param appVersion 版本号
     * @param grade_timestamp 时间戳
     */
    public static void saveGradeTimestamp(String appVersion, long grade_timestamp) {
        if (grade_timestamp == 0) return;
        Grade item = findByAppVersion(appVersion);
        if (item == null) return;

        try {
            new Update(Grade.class)
                    .set("grade_timestamp = ?", grade_timestamp)
                    .where("app_version = ?", appVersion)
                    .execute();
        } catch (Exception e) {
            // Empty
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
        Grade item = findByAppVersion(appVersion);
        if (item == null) return true;
        if (item.is_grade == 1) return false;
        if (item.grade_timestamp == 0) return true;
        // 如果系统未检测到用户做出评价，但是评价动作已经超过了5秒，则默认视为完成评价
        long dev = (System.currentTimeMillis() - item.grade_timestamp) / 1000;
        return dev < 5;
    }
}
