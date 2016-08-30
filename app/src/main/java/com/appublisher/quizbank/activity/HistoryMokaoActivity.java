package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.HistoryMokaoAdapter;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.netdata.historymokao.HistoryMokaoM;
import com.appublisher.quizbank.model.netdata.historymokao.HistoryMokaoResp;
import com.appublisher.quizbank.network.QRequest;
import com.google.gson.Gson;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryMokaoActivity extends ActionBarActivity implements RequestCallback {

    private ListView mLvHistoryMokao;
    private ImageView mIvNull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_mokao);

        // Toolbar
        CommonModel.setToolBar(this);

        // View 初始化
        mLvHistoryMokao = (ListView) findViewById(R.id.historymokao_lv);
        mIvNull = (ImageView) findViewById(R.id.quizbank_null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获取数据
        ProgressDialogManager.showProgressDialog(this, true);
        new QRequest(this, this).getHistoryMokao();

        // Umeng
        MobclickAgent.onPageStart("HistoryMokaoActivity");
        MobclickAgent.onResume(this);

        // TalkingData
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("HistoryMokaoActivity");
        MobclickAgent.onPause(this);

        // TalkingData
        TCAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("history_mokao".equals(apiName)) dealHistoryMokaoResp(response);

        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    /**
     * 处理历史模考回调
     * @param response 历史模考回调
     */
    private void dealHistoryMokaoResp(JSONObject response) {
        if (response == null) {
            mIvNull.setVisibility(View.VISIBLE);
            return;
        }

        Gson gson = new Gson();
        HistoryMokaoResp historyMokaoResp =
                gson.fromJson(response.toString(), HistoryMokaoResp.class);

        if (historyMokaoResp == null || historyMokaoResp.getResponse_code() != 1) {
            mIvNull.setVisibility(View.VISIBLE);
            return;
        }

        final ArrayList<HistoryMokaoM> historyMokaos = historyMokaoResp.getPaper_list();

        if (historyMokaos == null || historyMokaos.size() == 0) {
            mIvNull.setVisibility(View.VISIBLE);
            return;
        } else {
            mIvNull.setVisibility(View.GONE);
        }

        HistoryMokaoAdapter historyMokaoAdapter = new HistoryMokaoAdapter(this, historyMokaos);
        mLvHistoryMokao.setAdapter(historyMokaoAdapter);

        mLvHistoryMokao.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= historyMokaos.size()) return;

                HistoryMokaoM historyMokao = historyMokaos.get(position);

                if (historyMokao == null) return;

                String status = historyMokao.getStatus();

                //noinspection IfCanBeSwitch
                if ("undone".equals(status)) {
                    Intent intent = new Intent(HistoryMokaoActivity.this, MeasureActivity.class);
                    intent.putExtra("exercise_id", historyMokao.getExercise_id());
                    intent.putExtra("paper_type", "mokao");
                    intent.putExtra("paper_name", historyMokao.getName());
                    intent.putExtra("redo", true);
                    intent.putExtra("umeng_entry", "List");
                    startActivity(intent);
                } else if ("fresh".equals(status)) {
                    Intent intent = new Intent(HistoryMokaoActivity.this, MeasureActivity.class);
                    intent.putExtra("paper_id", historyMokao.getId());
                    intent.putExtra("paper_type", "mokao");
                    intent.putExtra("paper_name", historyMokao.getName());
                    intent.putExtra("redo", false);
                    intent.putExtra("umeng_entry", "List");
                    startActivity(intent);
                } else if ("done".equals(status)) {
                    // 跳转至练习报告页面
                    Intent intent = new Intent(HistoryMokaoActivity.this,
                            PracticeReportActivity.class);
                    intent.putExtra("exercise_id", historyMokao.getExercise_id());
                    intent.putExtra("paper_type", "mokao");
                    intent.putExtra("paper_name", historyMokao.getName());
                    intent.putExtra("from", "mokao_history_list");
                    intent.putExtra("paper_time", historyMokao.getDate());
                    startActivity(intent);
                }
            }
        });
    }
}
