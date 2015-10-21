package com.appublisher.quizbank.model.offline.model.db;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.appublisher.quizbank.model.db.Paper;

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
            return new Select().from(Paper.class)
                    .where("room_id = ?", room_id)
                    .executeSingle();
        } catch (Exception e) {
            // Empty
        }

        return null;
    }

    /**
     * 保存已购课程数据
     * @param purchased_data 已购课程数据
     */
    public static void savePurchasedData(String purchased_data) {
        Offline offline = null;

        try {
            offline = new Select().from(Offline.class)
                    .where("Id = ?", 1)
                    .executeSingle();
        } catch (Exception e) {
            // Empty
        }

        if (offline == null) {
            offline = new Offline();
            offline.purchased_data = purchased_data;
            offline.save();
        } else {
            try {
                new Update(Offline.class)
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

}
