package com.appublisher.quizbank.common.offline.model.db;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.appublisher.quizbank.thirdparty.duobeiyun.DuobeiYunClient;

import java.util.List;

/**
 * 离线模块DAO层
 */
public class OfflineDAO {

    /**
     * 根据room_id查询数据
     *
     * @return 视频数据
     */
    public static List<Offline> findByRoomId(String room_id) {
        try {
            return new Select().from(Offline.class)
                    .where("room_id = ?", room_id)
                    .execute();
        } catch (Exception e) {
            // Empty
        }

        return null;
    }

    /**
     * 查询数据库中保存的全部的RoomId
     *
     * @return Offline
     */
    public static List<Offline> findAllRoomId() {
        try {
            return new Select().from(Offline.class)
                    .where("room_id IS NOT NULL")
                    .execute();
        } catch (Exception e) {
            // Empty
        }

        return null;
    }

    /**
     * 查询数据库中保存的全部的成功下载的对象
     *
     * @return Offline
     */
    public static List<Offline> findAllSuccess() {
        try {
            return new Select().from(Offline.class)
                    .where("room_id IS NOT NULL and is_success = 1")
                    .execute();
        } catch (Exception e) {
            // Empty
        }

        return null;
    }

    /**
     * 查询已购课程数据
     *
     * @return Offline
     */
    public static String findFirstPurchasedData() {
        Offline offline = null;

        try {
            offline = new Select().from(Offline.class)
                    .where("purchased_data IS NOT NULL")
                    .executeSingle();
        } catch (Exception e) {
            // Empty
        }

        if (offline != null) return offline.purchased_data;

        return null;
    }

    /**
     * 保存下载成功状态
     *
     * @param room_id room_id
     */
    public static void saveRoomId(String room_id, int course_id) {
        if (room_id == null || room_id.length() == 0) return;

        Offline item = new Offline();
        item.is_success = 1;
        item.room_id = room_id;
        item.course_id = course_id;
        item.save();
    }

    /**
     * 删除RoomId
     *
     * @param room_id room_id
     */
    public static void deleteRoomId(String room_id, int course_id) {
        if (room_id == null || room_id.length() == 0) return;
        List<Offline> items = findByRoomId(room_id);
        if (items != null && items.size() == 1) {
            DuobeiYunClient.delete(room_id);
        }
        try {
            new Delete().from(Offline.class)
                    .where("room_id = ? AND course_id = ?", new Object[]{room_id, course_id + ""})
                    .execute();
        } catch (Exception e) {
            // Empty
        }
    }

    /**
     * 删除CourseId为空的数据
     */
    public static void deleteNullCourseId() {
        try {
            new Delete().from(Offline.class)
                    .where("course_id IS NULL")
                    .execute();
        } catch (Exception e) {
            // Empty
        }
    }

    /**
     * 清除所有记录
     */
    public static void deletaAll() {
        try {
            new Delete().from(Offline.class).execute();
        } catch (Exception e) {
            // Empty
        }
    }
}
