package com.appublisher.quizbank.dao;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.model.db.GlobalSetting;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;

/**
 * 全局配置数据库表DAO层
 */
public class GlobalSettingDAO {

    /**
     * 查询数据
     * @return  全局配置数据
     */
    public static GlobalSetting findById() {
        try {
            return new Select().from(GlobalSetting.class)
                    .where("Id = ?", 1)
                    .executeSingle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 插入数据
     * @param content 全局配置内容
     */
    public static void insert(String content) {
        GlobalSetting item = new GlobalSetting();
        item.content = content;
        item.save();
    }

    /**
     * 更新数据
     * @param content 全局配置内容
     */
    public static void update(String content) {
        try {
            new Update(GlobalSetting.class)
                    .set("content = ?", content)
                    .where("Id = ?", 1)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存数据
     * @param content 全局配置内容
     */
    public static void save(String content) {
        GlobalSetting item = findById();

        if (item != null) {
            update(content);
        } else {
            insert(content);
        }
    }

    /**
     * 保存最近一条通知的id
     * @param latest_notify 最近一条通知的id
     */
    public static void save(int latest_notify) {
        GlobalSetting item = findById();

        if (item != null) {
            update(latest_notify);
        } else {
            insert(latest_notify);
        }
    }

    /**
     * 插入最近一条通知的id
     * @param latest_notify 最近一条通知的id
     */
    public static void insert(int latest_notify) {
        GlobalSetting item = new GlobalSetting();
        item.latest_notify = latest_notify;
        item.save();
    }

    /**
     * 更新最近一条通知的id
     * @param latest_notify 最近一条通知的id
     */
    public static void update(int latest_notify) {
        try {
            new Update(GlobalSetting.class)
                    .set("latest_notify = ?", latest_notify)
                    .where("Id = ?", 1)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存使用次数
     * @param count 次数
     */
    public static void saveUseCount(int count) {
        GlobalSetting globalSetting = findById();

        if (globalSetting == null) {
            globalSetting = new GlobalSetting();
            globalSetting.use_count = count;
            globalSetting.save();
        } else {
            try {
                new Update(GlobalSetting.class)
                        .set("use_count = ?", count)
                        .where("Id = ?", 1)
                        .execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取使用次数
     * @return 使用次数
     */
    public static int getUseCount() {
        GlobalSetting globalSetting = findById();

        if (globalSetting == null) return 0;

        return globalSetting.use_count;
    }

    /**
     * 更新是否评价过的状态
     * @param is_grade  是否评价过
     * @deprecated
     */
    public static void updateIsGrade(boolean is_grade) {
        try {
            int valueInt = Utils.booleanToInt(is_grade);
            new Update(GlobalSetting.class)
                    .set("is_grade = ?", valueInt)
                    .where("Id = ?", 1)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取是否评价过
     * @return 是否
     * @deprecated
     */
    public static boolean isGrade() {
        GlobalSetting globalSetting = findById();
        return globalSetting != null && globalSetting.is_grade;
    }

    /**
     * 获取全局变量接口回调数据模型
     * @return GlobalSettingsResp
     */
    public static GlobalSettingsResp getGlobalSettingsResp() {
        GlobalSetting globalSetting = findById();
        if (globalSetting == null || globalSetting.content == null) return null;
        return GsonManager.getModel(globalSetting.content, GlobalSettingsResp.class);
    }
}
