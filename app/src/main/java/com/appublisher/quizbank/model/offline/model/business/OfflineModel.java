package com.appublisher.quizbank.model.offline.model.business;

import android.graphics.Color;
import android.os.Environment;
import android.view.View;

import com.appublisher.quizbank.R;
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

    /**
     * 处理已购课程列表回调
     * @param activity OfflineActivity
     * @param resp 回调数据
     */
    public static void dealPurchasedCoursesResp(OfflineActivity activity,
                                                PurchasedCoursesResp resp) {
        if (resp == null || resp.getResponse_code() != 1) return;

        // 保存至数据库
        OfflineDAO.savePurchasedData(
                GsonManager.getGson().toJson(resp, PurchasedCoursesResp.class));
    }

    /**
     * 按下全部按钮时的View变化
     * @param activity OfflineActivity
     */
    public static void pressAllBtn(OfflineActivity activity) {
        activity.mTvAll.setTextColor(Color.WHITE);
        activity.mAllLine.setVisibility(View.VISIBLE);
        activity.mTvLocal.setTextColor(activity.getResources().getColor(R.color.tab_unpress));
        activity.mLocalLine.setVisibility(View.INVISIBLE);
    }

    /**
     * 按下已下载按钮时的View变化
     * @param activity OfflineActivity
     */
    public static void pressLocalBtn(OfflineActivity activity) {
        activity.mTvLocal.setTextColor(Color.WHITE);
        activity.mLocalLine.setVisibility(View.VISIBLE);
        activity.mTvAll.setTextColor(activity.getResources().getColor(R.color.tab_unpress));
        activity.mAllLine.setVisibility(View.INVISIBLE);
    }

}
