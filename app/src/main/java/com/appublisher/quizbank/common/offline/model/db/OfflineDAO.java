package com.appublisher.quizbank.common.offline.model.db;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

/**
 * 离线模块DAO层
 */
public class OfflineDAO {

    /**
     * 根据room_id查询数据
     * @return 视频数据
     */
    public static Offline findByRoomId(String room_id) {
        try {
            return new Select().from(Offline.class)
                    .where("room_id = ?", room_id)
                    .executeSingle();
        } catch (Exception e) {
            // Empty
        }

        return null;
    }

    /**
     * 查询已购课程数据
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
     * @param room_id room_id
     */
    public static void saveRoomId(String room_id) {
        if (room_id == null || room_id.length() == 0) return;

        Offline item = findByRoomId(room_id);

        if (item == null) {
            item = new Offline();
            item.is_success = 1;
            item.room_id = room_id;
            item.save();
        } else {
            try {
                new Update(Offline.class)
                        .set("is_success = ?", 1)
                        .where("room_id = ?", room_id)
                        .execute();
            } catch (Exception e) {
                // Empty
            }
        }
    }

    /**
     * 删除RoomId
     * @param room_id room_id
     */
    public static void deleteRoomId(String room_id) {
        if (room_id == null || room_id.length() == 0) return;

        Offline item = findByRoomId(room_id);

        if (item == null) {
            item = new Offline();
            item.is_success = 0;
            item.room_id = room_id;
            item.save();
        } else {
            try {
                new Update(Offline.class)
                        .set("is_success = ?", 0)
                        .where("room_id = ?", room_id)
                        .execute();
            } catch (Exception e) {
                // Empty
            }
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