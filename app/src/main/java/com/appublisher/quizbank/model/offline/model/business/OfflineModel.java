package com.appublisher.quizbank.model.offline.model.business;

import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.offline.activity.OfflineActivity;
import com.appublisher.quizbank.model.offline.activity.OfflineClassActivity;
import com.appublisher.quizbank.model.offline.adapter.PurchasedCoursesAdapter;
import com.appublisher.quizbank.model.offline.model.db.OfflineDAO;
import com.appublisher.quizbank.model.offline.netdata.PurchasedClassM;
import com.appublisher.quizbank.model.offline.netdata.PurchasedCourseM;
import com.appublisher.quizbank.model.offline.netdata.PurchasedCoursesResp;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * 离线模块逻辑层
 */
public class OfflineModel {

    /**
     * 获取本地下载的RoomId列表
     * @return ArrayList
     */
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
     * 判断RoomId在本地是否存在
     * @param roomId roomId
     * @return Boolean
     */
    public static boolean isRoomIdExist(String roomId) {
        if (roomId == null) return false;

        ArrayList<String> list = getLocalRoomIdList();
        if (list == null) return false;

        for (String s : list) {
            if (roomId.equals(s)) return true;
        }

        return false;
    }

    /**
     * 处理已购课程列表回调
     * @param activity OfflineActivity
     * @param resp 回调数据
     */
    public static void dealPurchasedCoursesResp(final OfflineActivity activity,
                                                PurchasedCoursesResp resp) {
        if (resp == null || resp.getResponse_code() != 1) return;

        // 保存至数据库
        OfflineDAO.savePurchasedData(
                GsonManager.getGson().toJson(resp, PurchasedCoursesResp.class));

        final ArrayList<PurchasedCourseM> courses = resp.getList();
        if (courses == null) return;

        PurchasedCoursesAdapter adapter = new PurchasedCoursesAdapter(activity, courses);
        activity.mLvAll.setAdapter(adapter);

        activity.mLvAll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= courses.size()) return;

                PurchasedCourseM course = courses.get(position);
                if (course == null) return;

                Intent intent = new Intent(activity, OfflineClassActivity.class);
                intent.putExtra("class_list", course.getClasses());
                activity.startActivity(intent);
            }
        });
    }

    /**
     * 按下全部按钮时的View变化
     * @param activity OfflineActivity
     */
    public static void pressAllBtn(OfflineActivity activity) {
        activity.mTvAll.setTextColor(Color.WHITE);
        activity.mAllLine.setVisibility(View.VISIBLE);
        activity.mLvAll.setVisibility(View.VISIBLE);

        activity.mLocalLine.setVisibility(View.INVISIBLE);
        activity.mTvLocal.setTextColor(
                activity.getResources().getColor(R.color.offline_tab_unpress));
        activity.mLvLocal.setVisibility(View.INVISIBLE);
    }

    /**
     * 按下已下载按钮时的View变化
     * @param activity OfflineActivity
     */
    public static void pressLocalBtn(OfflineActivity activity) {
        activity.mTvLocal.setTextColor(Color.WHITE);
        activity.mLocalLine.setVisibility(View.VISIBLE);
        activity.mLvLocal.setVisibility(View.VISIBLE);

        activity.mTvAll.setTextColor(activity.getResources().getColor(R.color.offline_tab_unpress));
        activity.mAllLine.setVisibility(View.INVISIBLE);
        activity.mLvAll.setVisibility(View.INVISIBLE);
    }

    /**
     * 通过位置返回CheckBox
     * @param activity OfflineClassActivity
     * @param position 位置
     * @return CheckBox
     */
    public static CheckBox getCheckBoxByPosition(OfflineClassActivity activity, int position) {
        if (activity.mClasses == null || position >= activity.mClasses.size()) return null;

        PurchasedClassM classM = activity.mClasses.get(position);
        if (classM == null || classM.getStatus() != 2) return null; // 不能下载时，不显示CheckBox

        View view = Utils.getViewByPosition(position, activity.mLv);
        return (CheckBox) view.findViewById(R.id.item_purchased_classes_cb);
    }
}
