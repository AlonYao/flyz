package com.appublisher.quizbank.dao;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.appublisher.quizbank.model.db.GlobalSetting;

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
}
