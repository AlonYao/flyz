package com.appublisher.quizbank.common.vip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.customui.XListView;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.common.vip.adapter.VipNotificationAdapter;
import com.appublisher.quizbank.common.vip.netdata.VipNotificationResp;
import com.appublisher.quizbank.common.vip.network.VipParamBuilder;
import com.appublisher.quizbank.common.vip.network.VipRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VipNotificationActivity extends BaseActivity implements RequestCallback {

    private VipRequest mRequest;
    private List<VipNotificationResp.NotificationsBean> list;
    private VipNotificationAdapter adapter;
    private XListView listView;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_notification);
        setToolBar(this);
        mRequest = new VipRequest(this, this);
        list = new ArrayList<VipNotificationResp.NotificationsBean>();
        adapter = new VipNotificationAdapter(this, list);
        mRequest.getVipNotifications(page);
        showLoading();
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
                    mRequest.postReadNotification(VipParamBuilder.readNotification(notificationsBean.getId()));
                    list.get(position - 1).setIs_read(true);
                    adapter.notifyDataSetChanged();
                    skipExerciseDetail(notificationsBean.getExercise_id(), notificationsBean.getExercise_type());
                }
            }
        });
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        hideLoading();
        if (response == null) return;
        if ("notification_list".equals(apiName)) {
            listView.stopLoadMore();
            listView.stopRefresh();
            VipNotificationResp notificationResp = GsonManager.getModel(response, VipNotificationResp.class);
            if (page == 1)
                list.clear();
            list.addAll(notificationResp.getNotifications());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        hideLoading();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        hideLoading();
    }

    public void skipExerciseDetail(int exerciseId, int exerciseType) {
        Class<?> cls = null;
        switch (exerciseType) {
            case 1:
                cls = VipMSJPActivity.class;
                break;
            case 2:
                cls = VipDTTPActivity.class;
                break;
            case 3:
                cls = VipZJZDActivity.class;
                break;
            case 4:
                ToastManager.showToast(this, "此消息请在电脑端查看");
                break;
            case 5:
                cls = VipBDGXActivity.class;
                break;
            case 6:
                cls = VipBDGXActivity.class;
                break;
            case 7:
                cls = VipYDDKActivity.class;
                break;
            case 8:

                break;
            case 9:
                cls = VipHPTSActivity.class;
                break;
            case 10:
                ToastManager.showToast(this, "此消息请在电脑端查看");
            default:
                break;
        }
        if (cls != null) {
            final Intent intent = new Intent(this, cls);
            intent.putExtra("exerciseId", exerciseId);
            intent.putExtra("exerciseType", exerciseType);
            startActivity(intent);
        }
    }
}

