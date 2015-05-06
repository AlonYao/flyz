package com.appublisher.quizbank.model;

import com.appublisher.quizbank.activity.SystemNoticeActivity;
import com.appublisher.quizbank.adapter.NoticeListAdapter;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.netdata.notice.NoticeM;
import com.appublisher.quizbank.model.netdata.notice.NoticeResp;
import com.appublisher.quizbank.utils.GsonManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * SystemNoticeActivity Model
 */
public class SystemNoticeModel {

    /**
     * 处理通知回调
     * @param activity SystemNoticeActivity
     * @param response 回调数据
     */
    public static void dealNotificationsResp(SystemNoticeActivity activity, JSONObject response) {
        if (response == null) return;

        Gson gson = GsonManager.initGson();
        NoticeResp noticeResp = gson.fromJson(response.toString(), NoticeResp.class);

        if (noticeResp == null || noticeResp.getResponse_code() != 1) return;

        ArrayList<NoticeM> notices = noticeResp.getList();

        if (notices == null || notices.size() == 0) return;

        if (activity.mOffset == 0) {
            activity.mNotices = notices;

            NoticeListAdapter noticeListAdapter = new NoticeListAdapter(activity);
            activity.mXListView.setAdapter(noticeListAdapter);

            // 更新本地存储中的通知id
            NoticeM notice = notices.get(0);

            if (notice != null) {
                GlobalSettingDAO.save(notice.getId());
            }

        } else {
            activity.mNotices.addAll(notices);
        }
    }
}
