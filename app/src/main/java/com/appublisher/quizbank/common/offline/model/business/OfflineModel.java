package com.appublisher.quizbank.common.offline.model.business;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.login.model.LoginModel;
import com.appublisher.quizbank.common.offline.activity.OfflineActivity;
import com.appublisher.quizbank.common.offline.activity.OfflineClassActivity;
import com.appublisher.quizbank.common.offline.adapter.PurchasedClassesAdapter;
import com.appublisher.quizbank.common.offline.adapter.PurchasedCoursesAdapter;
import com.appublisher.quizbank.common.offline.model.db.Offline;
import com.appublisher.quizbank.common.offline.model.db.OfflineDAO;
import com.appublisher.quizbank.common.offline.netdata.PurchasedClassM;
import com.appublisher.quizbank.common.offline.netdata.PurchasedCourseM;
import com.appublisher.quizbank.common.offline.netdata.PurchasedCoursesResp;
import com.appublisher.quizbank.common.offline.network.OfflineRequest;
import com.appublisher.quizbank.thirdparty.duobeiyun.DuobeiYunClient;
import com.appublisher.quizbank.thirdparty.duobeiyun.listener.YunDownloadListener;
import com.appublisher.quizbank.utils.FileManager;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.appublisher.quizbank.utils.Utils;
import com.liulishuo.filedownloader.BaseDownloadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 离线模块逻辑层
 */
public class OfflineModel {

    private static downloadProgressListener mListener;

    public OfflineModel(downloadProgressListener listener) {
        mListener = listener;
    }

    /**
     * 获取本地已经下载完成的课程列表
     *
     * @return ArrayList
     */
    public static ArrayList<PurchasedCourseM> getLocalCourseList(Activity activity) {
        // 获取本地数据
        HashMap<String, Offline> map = getLocalSuccessItems();
        String purchased_data = getLocalSave(activity);

        PurchasedCoursesResp resp =
                GsonManager.getModel(purchased_data, PurchasedCoursesResp.class);
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
                // 如果本地没有下载成功记录 且 不在下载队列中，则移除
                if (!isRoomIdDownload(classM.getRoom_id(), course.getId(), map)
                        && !isRoomIdInDownloadList(classM.getRoom_id(), course.getId())) {
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
     * 获取本地保存的已购数据
     *
     * @return String
     */
    private static String getLocalSave(Activity activity) {
        String data;

        // SharedPreferences中获取
        SharedPreferences offline = activity.getSharedPreferences("offline", 0);
        data = offline.getString(LoginModel.getUserId(), "");
        if (data.length() != 0) return data;

        // 数据库中获取
        return OfflineDAO.findFirstPurchasedData();
    }

    /**
     * 处理已购课程列表回调
     *
     * @param activity OfflineActivity
     * @param resp     回调数据
     */
    @SuppressLint("CommitPrefEdits")
    public static void dealPurchasedCoursesResp(final OfflineActivity activity,
                                                PurchasedCoursesResp resp) {
        if (resp == null || resp.getResponse_code() != 1) {
            activity.mTvNone.setVisibility(View.VISIBLE);
            return;
        }

        // 保存至SharedPreferences
        SharedPreferences offline = activity.getSharedPreferences("offline", 0);
        SharedPreferences.Editor editor = offline.edit();
        editor.putString(LoginModel.getUserId(),
                GsonManager.modelToString(resp, PurchasedCoursesResp.class));
        editor.commit();

        final ArrayList<PurchasedCourseM> courses = resp.getList();
        if (courses == null || courses.size() == 0) {
            activity.mTvNone.setVisibility(View.VISIBLE);
            return;
        }

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
     *
     * @param activity OfflineActivity
     */
    public static void pressAllBtn(OfflineActivity activity) {
        activity.mTvNone.setVisibility(View.GONE);
        activity.mTvAll.setTextColor(Color.WHITE);
        activity.mAllLine.setVisibility(View.VISIBLE);
        activity.mLvAll.setVisibility(View.VISIBLE);

        activity.mLocalLine.setVisibility(View.INVISIBLE);
        //noinspection deprecation
        activity.mTvLocal.setTextColor(
                activity.getResources().getColor(R.color.offline_tab_unpress));
        activity.mLvLocal.setVisibility(View.INVISIBLE);
    }

    /**
     * 按下已下载按钮时的View变化
     *
     * @param activity OfflineActivity
     */
    public static void pressLocalBtn(OfflineActivity activity) {
        activity.mTvNone.setVisibility(View.GONE);
        activity.mTvLocal.setTextColor(Color.WHITE);
        activity.mLocalLine.setVisibility(View.VISIBLE);
        activity.mLvLocal.setVisibility(View.VISIBLE);

        //noinspection deprecation
        activity.mTvAll.setTextColor(activity.getResources().getColor(R.color.offline_tab_unpress));
        activity.mAllLine.setVisibility(View.INVISIBLE);
        activity.mLvAll.setVisibility(View.INVISIBLE);
    }

    /**
     * 设置取消状态
     *
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
     *
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
     *
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
     * 判断RoomId是否被成功下载,需要课程id
     *
     * @param roomId roomId
     * @return Boolean
     */
    public static boolean isRoomIdDownload(String roomId, int course_id) {
        if (roomId == null) return false;
        List<Offline> items = OfflineDAO.findByRoomId(roomId);
        if (items != null && items.size() != 0) {
            for (int i = 0; i < items.size(); i++) {
                Offline item = items.get(i);
                if (item.is_success == 1 && item.course_id == course_id)
                    return true;
            }
        }
        return false;
    }

    /**
     * 判断RoomId是否被成功下载,需要课程id
     *
     * @param roomId roomId
     * @param course_id courseId
     * @param map 本地全部下载成功的对象
     * @return Boolean
     */
    public static boolean isRoomIdDownload(String roomId,
                                           int course_id,
                                           HashMap<String, Offline> map) {
        if (roomId == null || map == null || !map.containsKey(roomId)) return false;
        Offline item = map.get(roomId);
        return item != null && item.course_id == course_id;
    }

    /**
     * 获取本地成功下载的对象
     */
    public static HashMap<String, Offline> getLocalSuccessItems() {
        List<Offline> items = OfflineDAO.findAllSuccess();
        if (items == null) return new HashMap<>();

        HashMap<String, Offline> map = new HashMap<>();
        for (Offline item : items) {
            if (item == null) continue;
            map.put(item.room_id, item);
        }
        return map;
    }

    /**
     * 判断RoomId是否被成功下载,成功下载后记录第二次下载课程的ID
     *
     * @param roomId roomId
     * @return Boolean
     */
    public static boolean roomIdDownloaded(String roomId, int course_id) {
        if (roomId == null) return false;
        List<Offline> items = OfflineDAO.findByRoomId(roomId);
        if (items != null && items.size() != 0) {
            OfflineDAO.saveRoomId(roomId, course_id);
            return true;
        }
        return false;
    }

    /**
     * 显示本地视频列表
     *
     * @param activity OfflineActivity
     */
    public static void showLocalList(final OfflineActivity activity) {
        final ArrayList<PurchasedCourseM> courses = getLocalCourseList(activity);
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
     *
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
     *
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
     *
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
     *
     * @param activity OfflineClassActivity
     */
    public static void startDownload(final OfflineClassActivity activity) {
        if (OfflineConstants.mDownloadList == null || OfflineConstants.mDownloadList.size() == 0) {
            OfflineConstants.mStatus = OfflineConstants.DONE;
            return;
        }

        HashMap<String, Object> map = OfflineConstants.mDownloadList.get(0);
        if (map == null) return;

        // 更新当前正在下载的RoomId
        OfflineConstants.mCurDownloadRoomId = (String) map.get("room_id");

        // 已下载 或者 roomId非法 则进行下一项
        if (OfflineConstants.mCurDownloadRoomId == null
                || OfflineConstants.mCurDownloadRoomId.length() == 0
                || roomIdDownloaded(OfflineConstants.mCurDownloadRoomId,
                                    activity.mCourseId)) {
            OfflineClassActivity.mAdapter.notifyDataSetChanged();
            removeTopRoomId();
            startDownload(activity);
            return;
        }

        // 更新状态：等待中
        OfflineConstants.mStatus = OfflineConstants.WAITING;

        DuobeiYunClient.download(
                activity,
                OfflineConstants.mCurDownloadRoomId,
                new YunDownloadListener() {
            @Override
            public void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                // 空间不足提示
                if (totalBytes > Utils.getAvailableSDCardSize()) {
                    ToastManager.showToast(activity, "手机可用存储空间不足");
                    DuobeiYunClient.pauseDownload(task.getDownloadId());
                }
            }

            @Override
            public void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                int progress = (int) (((float) soFarBytes / (float) totalBytes) * 100);

                // 记录下载状态
                OfflineConstants.mPercent = progress;
                OfflineConstants.mLastTimestamp = System.currentTimeMillis();
                OfflineConstants.mStatus = OfflineConstants.PROGRESS;
                mListener.onProgress(progress);
            }

            @Override
            public void blockComplete(BaseDownloadTask task) {

            }

            @Override
            public void completed(BaseDownloadTask task) {
                // 更新数据库
                OfflineDAO.saveRoomId(OfflineConstants.mCurDownloadRoomId,
                                      activity.mCourseId);

                // 完成通知
                mListener.onFinish();

                // 更新下载列表，继续下载其他视频
                removeTopRoomId();
                if (OfflineConstants.mDownloadList.size() != 0) {
                    startDownload(activity);
                } else {
                    OfflineConstants.mStatus = OfflineConstants.DONE;
                }
            }

            @Override
            public void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            }

            @Override
            public void error(BaseDownloadTask task, Throwable e) {
                removeTopRoomId();
                if (OfflineConstants.mDownloadList.size() != 0) startDownload(activity);
                ToastManager.showToast(activity, "视频资源还没准备好，请耐心等待");
            }

            @Override
            public void warn(BaseDownloadTask task) {

            }
        });
    }

    /**
     * 判断RoomId是否在下载队列中
     *
     * @param roomId RoomId
     * @return 是否
     */
    public static boolean isRoomIdInDownloadList(String roomId, int course_id) {
        if (OfflineConstants.mDownloadList == null || roomId == null) return false;

        for (HashMap<String, Object> map : OfflineConstants.mDownloadList) {
            if (map == null) continue;
            String mapRoomId = (String) map.get("room_id");
            int courseId = (int) map.get("course_id");
            if (roomId.equals(mapRoomId) && course_id == courseId) return true;
        }

        return false;
    }

    /**
     * 移除顶部的RoomId
     */
    public static void removeTopRoomId() {
        if (OfflineConstants.mDownloadList == null
                || OfflineConstants.mDownloadList.size() == 0) return;
        OfflineConstants.mDownloadList.remove(0);
    }

    /**
     * 清除旧的资源
     */
    private static void cleanOldSource() {
        // 数据库
        OfflineDAO.deletaAll();

        // 多贝云文件夹
        FileManager.deleteFiles(Environment.getExternalStorageDirectory().toString() + "/duobeiyun");
    }

    /**
     * 检查版本
     */
    @SuppressLint("CommitPrefEdits")
    public static void checkVersion(Activity activity) {
        // SharedPreferences中获取版本号
        SharedPreferences offline = activity.getSharedPreferences("offline", 0);
        String preVersion = offline.getString("version", "");

        // 1.3.2之前的版本，清空数据
        if (Utils.compareVersion(preVersion, "1.3.2") < 0) {
            cleanOldSource();
        }

        // 1.3.3之前的版本，同步course_id字段
        if (Utils.compareVersion(preVersion, "1.3.3") <= 0) {
            addCourseId(activity);
        }

        // 更新版本号
        SharedPreferences.Editor editor = offline.edit();
        editor.putString("version", Globals.appVersion);
        editor.commit();
    }

    /**
     * 增加course_id字段
     */
    private static void addCourseId(Context context) {
        ArrayList<Offline> offlines = (ArrayList<Offline>) OfflineDAO.findAllRoomId();
        if (offlines == null) return;

        // SharedPreferences中获取已购数据
        SharedPreferences local = context.getSharedPreferences("offline", 0);
        String data = local.getString(LoginModel.getUserId(), "");

        PurchasedCoursesResp resp = GsonManager.getModel(data, PurchasedCoursesResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;

        ArrayList<PurchasedCourseM> courses = resp.getList();
        if (courses == null) return;

        for (Offline offline : offlines) {
            if (offline == null || offline.room_id == null || offline.room_id.length() == 0)
                continue;

            ArrayList<Integer> courseIds = findCouresIdByRoomId(offline.room_id, courses);

            for (Integer courseId : courseIds) {
                if (courseId == 0) continue;
                OfflineDAO.saveRoomId(offline.room_id, courseId);
            }
        }

        OfflineDAO.deleteNullCourseId();
    }

    /**
     * 通过RoomId查找CourseId列表
     * @param roomId roomId
     * @param courses 课程列表
     * @return 课程id列表
     */
    private static ArrayList<Integer> findCouresIdByRoomId(String roomId,
                                                   ArrayList<PurchasedCourseM> courses) {
        ArrayList<Integer> courseIds = new ArrayList<>();

        if (roomId == null || courses == null) return courseIds;

        // 遍历获取RoomId对应的CourseId
        for (PurchasedCourseM course : courses) {
            if (course == null) continue;

            ArrayList<PurchasedClassM> classes = course.getClasses();
            if (classes == null) continue;

            for (PurchasedClassM classM : classes) {
                if (classM == null || classM.getRoom_id() == null) continue;
                if (classM.getRoom_id().equals(roomId)) courseIds.add(course.getId());
            }
        }

        return courseIds;
    }

    public interface downloadProgressListener {
        void onProgress(int progress);
        void onFinish();
    }

    //判断是否有可删除的视频,返回true:有
    public static boolean isDeletedClass(OfflineClassActivity activity,
                                         PurchasedClassesAdapter adapter) {
        if (adapter.mClasses == null) return false;
        ArrayList<PurchasedClassM> mClasses = adapter.mClasses;
        for (int i = 0; i < mClasses.size(); i++) {
            PurchasedClassM classM = mClasses.get(i);
            boolean isRoomIdDownload =
                    OfflineModel.isRoomIdDownload(classM.getRoom_id(), activity.mCourseId);
            if (isRoomIdDownload) return true;
            if (OfflineModel.isRoomIdInDownloadList(
                    classM.getRoom_id(), activity.mCourseId)
                    && !OfflineConstants.mCurDownloadRoomId.equals(classM.getRoom_id())) {
                //在下载列表中
                return true;
            }
        }
        return false;
    }

    //判断是否有可下载的视频
    public static boolean isDownloadClass(OfflineClassActivity activity,
                                          PurchasedClassesAdapter adapter) {
        if (adapter.mClasses == null) return false;
        ArrayList<PurchasedClassM> mClasses = adapter.mClasses;
        for (int i = 0; i < mClasses.size(); i++) {
            PurchasedClassM classM = mClasses.get(i);
            boolean isRoomIdDownload =
                    OfflineModel.isRoomIdDownload(classM.getRoom_id(), activity.mCourseId);
            if (!isRoomIdDownload
                    && !OfflineModel.isRoomIdInDownloadList(
                    classM.getRoom_id(), activity.mCourseId))
                return true;
        }
        return false;
    }
}
