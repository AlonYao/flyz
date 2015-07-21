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
        if (item == null) return false;
        long dif = System.currentTimeMillis() - item.timestamp;
        return (dif / 1000 * 60 * 60) > 72;
    }
}
