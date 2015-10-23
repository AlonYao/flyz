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
import com.appublisher.quizbank.model.offline.model.db.Offline;
import com.appublisher.quizbank.model.offline.model.db.OfflineDAO;
import com.appublisher.quizbank.model.offline.netdata.PurchasedClassM;
import com.appublisher.quizbank.model.offline.netdata.PurchasedCourseM;
import com.appublisher.quizbank.model.offline.netdata.PurchasedCoursesResp;
import com.appublisher.quizbank.model.offline.network.OfflineRequest;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.Utils;
import com.duobeiyun.DuobeiYunClient;
import com.duobeiyun.listener.DownloadTaskListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 离线模块逻辑层
 */
public class OfflineModel {

    private static downloadProgressListener mListener;

    public OfflineModel(downloadProgressListener listener) {
        mListener = listener;
    }

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
     * 获取本地已经下载完成的课程列表
     * @return ArrayList
     */
    public static ArrayList<PurchasedCourseM> getLocalCourseList() {
        Offline item = OfflineDAO.findById();
        if (item == null) return null;

        PurchasedCoursesResp resp =
                GsonManager.getGson().fromJson(item.purchased_data, PurchasedCoursesResp.class);
        if (resp == null || resp.getResponse_code() != 1) return null;

        ArrayList<PurchasedCourseM> courses = resp.getList();
        if (courses == null) return null;

        Iterator<PurchasedCourseM> iCourses = courses.iterator();
        while (iCourses.hasNext()) {
            PurchasedCourseM course = iCourses.next();
            if (course == null) continue;

            ArrayList<PurchasedClassM> classes = course.getClasses();
            if (classes == null) continue;

            Iterator<PurchasedClassM> iClasses = classes.iterator();
            while (iClasses.hasNext()) {
                PurchasedClassM classM = iClasses.next();
                if (classM == null) continue;
                // 如果本地没有下载成功记录，则移除
                if (!isRoomIdDownload(classM.getRoom_id())) {
                    iClasses.remove();
                }
            }

            if (classes.size() == 0) {
                iCourses.remove();
            }
        }

        return courses;
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
                intent.putExtra("from", "all");
                intent.putExtra("bar_title", course.getName());
                intent.putExtra("course_id", course.getId());
                activity.startActivity(intent);
            }
        });
    }

    /**
     * 按下全部按钮时的View变化
     * @param activity OfflineActivity
     */
    public static void pressAllBtn(OfflineActivity activity) {
        activity.mTvNone.setVisibility(View.GONE);
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
        activity.mTvNone.setVisibility(View.GONE);
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
        if (activity.mClasses == null
                || position >= activity.mClasses.size()) return null;

        PurchasedClassM classM = activity.mClasses.get(position);
        // 不能下载或已下载（已下载时，删除页面需要显示CheckBox）时，不显示CheckBox
        if (classM == null || classM.getStatus() != 2
                || (isRoomIdDownload(classM.getRoom_id()) && activity.mMenuStatus != 2))
            return null;

        View view = Utils.getViewByPosition(position, OfflineClassActivity.mLv);
        return (CheckBox) view.findViewById(R.id.item_purchased_classes_cb);
    }

    /**
     * 设置取消状态
     * @param activity OfflineClassActivity
     */
    public static void setCancel(OfflineClassActivity activity) {
        activity.mMenuStatus = 0;
        activity.invalidateOptionsMenu();
        OfflineClassActivity.mAdapter.notifyDataSetChanged();
        activity.mBtnBottom.setVisibility(View.GONE);

        int size = activity.mClasses.size();
        for (int i = 0; i < size; i++) {
            activity.mSelectedMap.put(i, false);
        }
    }

    /**
     * 通过位置获取RoomId
     * @param position 位置
     * @return RoomId
     */
    public static String getRoomIdByPosition(OfflineClassActivity activity, int position) {
        if (activity.mClasses == null
                || position >= activity.mClasses.size()) return null;

        PurchasedClassM classM = activity.mClasses.get(position);
        if (classM == null) return null;

        return classM.getRoom_id();
    }

    /**
     * 通过位置获取课堂名称（Class）
     * @param position 位置
     * @return 课堂名称
     */
    public static String getClassNameByPosition(OfflineClassActivity activity, int position) {
        if (activity.mClasses == null
                || position >= activity.mClasses.size()) return null;

        PurchasedClassM classM = activity.mClasses.get(position);
        if (classM == null) return null;

        return classM.getName();
    }

    /**
     * 判断RoomId是否被成功下载
     * @param roomId roomId
     * @return Boolean
     */
    public static boolean isRoomIdDownload(String roomId) {
        if (roomId == null) return false;
        Offline item = OfflineDAO.findByRoomId(roomId);
        return item != null && item.is_success == 1;
    }

    /**
     * 显示本地视频列表
     * @param activity OfflineActivity
     */
    public static void showLocalList(final OfflineActivity activity) {
        final ArrayList<PurchasedCourseM> courses = getLocalCourseList();
        if (courses == null || courses.size() == 0) {
            activity.mTvNone.setVisibility(View.VISIBLE);
        }

        PurchasedCoursesAdapter adapter = new PurchasedCoursesAdapter(activity, courses);
        activity.mLvLocal.setAdapter(adapter);

        activity.mLvLocal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (courses == null || position >= courses.size()) return;

                PurchasedCourseM course = courses.get(position);
                if (course == null) return;

                Intent intent = new Intent(activity, OfflineClassActivity.class);
                intent.putExtra("class_list", course.getClasses());
                intent.putExtra("from", "local");
                intent.putExtra("bar_title", course.getName());
                intent.putExtra("course_id", course.getId());
                activity.startActivity(intent);
            }
        });
    }

    /**
     * 显示已购视频列表
     * @param activity OfflineActivity
     */
    public static void showAllList(final OfflineActivity activity) {
        // 如果数据正常，则不从网络获取数据
        if (activity.mPurchasedCoursesResp != null
                && activity.mPurchasedCoursesResp.getResponse_code() == 1) return;

        ProgressDialogManager.showProgressDialog(activity);
        OfflineRequest request = new OfflineRequest(activity, activity);
        request.getPurchasedCourses();
    }

    /**
     * 重置选中Map
     * @param activity OfflineClassActivity
     */
    public static void initSelectedMap(OfflineClassActivity activity) {
        if (activity.mClasses == null) return;

        int size = activity.mClasses.size();
        for (int i = 0; i < size; i++) {
            activity.mSelectedMap.put(i, false);
        }
    }

    /**
     * 判断指定位置是否被选定
     * @param activity OfflineClassActivity
     * @param position 位置
     * @return 是否
     */
    public static boolean isPositionSelected(OfflineClassActivity activity, int position) {
        return activity.mSelectedMap != null
                && activity.mSelectedMap.containsKey(position)
                && activity.mSelectedMap.get(position);
    }

    /**
     * 开始下载
     * @param activity OfflineClassActivity
     */
    public static void startDownload(final OfflineClassActivity activity) {
        if (OfflineConstants.mDownloadList == null || OfflineConstants.mDownloadList.size() == 0)
            return;

        HashMap<String, Object> map = OfflineConstants.mDownloadList.get(0);
        if (map == null) return;

        final String roomId = (String) map.get("room_id");
        final int position = (int) map.get("position");

        OfflineConstants.mStatus = OfflineConstants.WAITING;

        DuobeiYunClient.download(
                activity,
                roomId,
                new DownloadTaskListener() {
                    @Override
                    public void onProgress(int progress, int fileLength) {
                        super.onProgress(progress, fileLength);
                        OfflineConstants.mPercent = progress;
                        OfflineConstants.mCurDownloadPosition = position;
                        // 记录下载状态
                        OfflineConstants.mLastTimestamp = System.currentTimeMillis();
                        OfflineConstants.mStatus = OfflineConstants.PROGRESS;
                        mListener.onProgress(progress);
                    }

                    @Override
                    public void onError(String error) {
                        super.onError(error);
                        OfflineConstants.mDownloadList.remove(0);

                        if (OfflineConstants.mDownloadList.size() != 0) {
                            startDownload(activity);
                        }
                    }

                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file);
                        // 更新数据库
                        OfflineDAO.saveRoomId(roomId);

                        mListener.onFinish();

                        // 更新下载列表，继续下载其他视频
                        OfflineConstants.mDownloadList.remove(0);

                        if (OfflineConstants.mDownloadList.size() != 0) {
                            startDownload(activity);
                        } else {
                            OfflineConstants.mStatus = OfflineConstants.DONE;
                        }
                    }
                });
    }

    /**
     * 判断RoomId是否在下载队列中
     * @param roomId RoomId
     * @return 是否
     */
    public static boolean isRoomIdInDownloadList(String roomId) {
        if (OfflineConstants.mDownloadList == null || roomId == null) return false;

        for (HashMap<String, Object> map : OfflineConstants.mDownloadList) {
            if (map == null) continue;
            String mapRoomId = (String) map.get("room_id");
            if (roomId.equals(mapRoomId)) return true;
        }

        return false;
    }

    public interface downloadProgressListener{
        void onProgress(int progress);

        void onFinish();
    }

}
