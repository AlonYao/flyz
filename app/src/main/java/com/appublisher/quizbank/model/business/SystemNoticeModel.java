package com.appublisher.quizbank.model.business;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.NoticeDetailActivity;
import com.appublisher.quizbank.activity.SystemNoticeActivity;
import com.appublisher.quizbank.adapter.NoticeListAdapter;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.netdata.notice.NoticeM;
import com.appublisher.quizbank.model.netdata.notice.NoticeResp;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.QRequest;

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
    public static void dealNotificationsResp(final SystemNoticeActivity activity,
                                             JSONObject response) {
        if (response == null) return;

        NoticeResp noticeResp = GsonManager.getModel(response.toString(), NoticeResp.class);

        if (noticeResp == null || noticeResp.getResponse_code() != 1) return;

        final ArrayList<NoticeM> notices = noticeResp.getList();

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

        activity.mXListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (activity.mNotices == null || activity.mNotices.size() == 0) return;

                if (position - 1 < activity.mNotices.size()) {
                    NoticeM notice = activity.mNotices.get(position - 1);

                    if (notice == null) return;

                    // 记录当前View的红点view
                    ImageView ivRed =
                            (ImageView) view.findViewById(R.id.notice_item_redpoint);
                    ivRed.setVisibility(View.GONE);

                    // 修改字体颜色
                    TextView textView = (TextView) view.findViewById(R.id.notice_item_tv);
                    textView.setTextColor(ContextCompat.getColor(activity, R.color.grey));

                    // 通知服务器已读
                    new QRequest(activity).readNotification(
                            ParamBuilder.readNotification(String.valueOf(notice.getId())));

                    // 跳转
                    Intent intent = new Intent(activity, NoticeDetailActivity.class);
                    intent.putExtra("type", notice.getType());
                    intent.putExtra("content", notice.getContent());
                    activity.startActivityForResult(intent, ActivitySkipConstants.NOTICE_READ);
                }
            }
        });
    }

    /**
     * 显示空白图片
     * @param activity SystemNoticeActivity
     */
    public static void showNullImg(SystemNoticeActivity activity) {
        if (activity.mNotices == null || activity.mNotices.size() == 0) {
            activity.mIvNull.setVisibility(View.VISIBLE);
        } else {
            activity.mIvNull.setVisibility(View.GONE);
        }
    }
}
