package com.appublisher.quizbank.model.offline.model;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.appublisher.quizbank.model.db.Paper;

/**
 * 离线模块DAO层
 */
public class OfflineCourseDAO {

    /**
     * 根据room_id查询数据
     * @return 视频数据
     */
    public static OfflineCourse findByRoomId(String room_id) {
        try {
            return new Select().from(Paper.class)
                    .where("room_id = ?", room_id)
                    .executeSingle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 保存已购课程数据
     * @param purchased_data 已购课程数据
     */
    public static void savePurchasedData(String purchased_data) {
        OfflineCourse offlineCourse = null;

        try {
            offlineCourse = new Select().from(OfflineCourse.class)
                    .where("Id = ?", 1)
                    .executeSingle();
        } catch (Exception e) {
            // Empty
        }

        if (offlineCourse == null) {
            offlineCourse = new OfflineCourse();
            offlineCourse.purchased_data = purchased_data;
            offlineCourse.save();
        } else {
            try {
                new Update(OfflineCourse.class)
                        .set("purchased_data = ?", purchased_data)
                        .where("Id = ?", 1)
                        .execute();
            } catch (Exception e) {
                // Empty
            }
        }
    }

    /**
     * 保存下载成功状态
     * @param room_id room_id
     */
//    public static void savePurchasedData(String room_id) {
//        OfflineCourse offlineCourse = null;
//
//        try {
//            offlineCourse = new Select().from(OfflineCourse.class)
//                    .where("Id = ?", 1)
//                    .executeSingle();
//        } catch (Exception e) {
//            // Empty
//        }
//
//
//    }

}
