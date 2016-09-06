package com.appublisher.quizbank.common.vip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.customui.XListView;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.common.vip.adapter.NotificationAdapter;
import com.appublisher.quizbank.common.vip.netdata.VipNotificationResp;
import com.appublisher.quizbank.common.vip.network.VipRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class VipNotificationActivity extends BaseActivity implements RequestCallback {

    private VipRequest mRequest;
    private List<VipNotificationResp.NotificationsBean> list;
    private NotificationAdapter adapter;
    private XListView listView;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_notification);
        setToolBar(this);
        mRequest = new VipRequest(this, this);
        list = new ArrayList<VipNotificationResp.NotificationsBean>();
        adapter = new NotificationAdapter(this, list);
        mRequest.getVipNotifications(page);
        ProgressDialogManager.showProgressDialog(this);
        initViews();
    }

    private void initViews() {
        listView = (XListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setPullLoadEnable(true);
        listView.setPullRefreshEnable(true);
        listView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                page = 1;
                mRequest.getVipNotifications(page);
            }

            @Override
            public void onLoadMore() {
                page++;
                mRequest.getVipNotifications(page);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VipNotificationResp.NotificationsBean notificationsBean = list.get(position - 1);
                int type = notificationsBean.getType();
                if (type == 3) {
                    String url = notificationsBean.getRedirect_url();
                    if (url == null || "".equals(url)) return;
                    final Intent intent = new Intent(VipNotificationActivity.this, WebViewActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                } else {
                    int exercise_id = notificationsBean.getExercise_id();
                    mRequest.getExerciseDetail(exercise_id);
                }
            }
        });
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        ProgressDialogManager.closeProgressDialog();
        if (response == null) return;
        if ("notification_list".equals(apiName)) {
            listView.stopLoadMore();
            listView.stopRefresh();
            VipNotificationResp notificationResp = GsonManager.getModel(response, VipNotificationResp.class);
            if (page == 1)
                list.clear();
            list.addAll(notificationResp.getNotifications());
            adapter.notifyDataSetChanged();
        } else if ("exercise_detail".equals(apiName)) {
            //未完待续
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }
}
