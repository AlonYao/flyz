package com.appublisher.quizbank.model.offline.model.business;

import android.os.Environment;

import com.appublisher.quizbank.model.offline.activity.OfflineActivity;
import com.appublisher.quizbank.model.offline.model.db.OfflineDAO;
import com.appublisher.quizbank.model.offline.netdata.PurchasedCoursesResp;
import com.appublisher.quizbank.utils.GsonManager;

import java.io.File;
import java.util.ArrayList;

/**
 * 离线模块逻辑层
 */
public class OfflineModel {

    public static ArrayList<String> getLocalRoomIdList() {
        ArrayList<String> list = new ArrayList<>();

        try {
            String dirPath =
                    Environment.getExternalStorageDirectory().toString() + "/" + "duobeiyun";
            File file = new File(dirPath);

            File[] files = file.listFiles();

            for (File f : files) {
                String name = f.getName();
                if (!name.contains(".zip")) continue;
                name = name.replace(".zip", "");
                list.add(name);
            }

        } catch (Exception e) {
            // Empty
        }

        return list;
    }

    public static void dealPurchasedCoursesResp(OfflineActivity activity,
                                                PurchasedCoursesResp resp) {
        if (resp == null || resp.getResponse_code() != 1) return;

        // 保存至数据库
        OfflineDAO.savePurchasedData(
                GsonManager.getGson().toJson(resp, PurchasedCoursesResp.class));
    }
}
