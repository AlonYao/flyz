package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.HistoryMokaoAdapter;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.activity.MeasureActivity;
import com.appublisher.quizbank.common.measure.activity.MeasureReportActivity;
import com.appublisher.quizbank.model.netdata.historymokao.HistoryMokaoM;
import com.appublisher.quizbank.model.netdata.historymokao.HistoryMokaoResp;
import com.appublisher.quizbank.network.QRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryMokaoActivity extends BaseActivity implements RequestCallback {

    private ListView mLvHistoryMokao;
    private ImageView mIvNull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_mokao);

        // Toolbar
        setToolBar(this);

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
                    intent.putExtra(MeasureConstants.INTENT_PAPER_ID, historyMokao.getExercise_id());
                    intent.putExtra(MeasureConstants.INTENT_PAPER_TYPE, MeasureConstants.MOKAO);
                    intent.putExtra(MeasureConstants.INTENT_REDO, true);
                    startActivity(intent);
                } else if ("fresh".equals(status)) {
                    Intent intent = new Intent(HistoryMokaoActivity.this, MeasureActivity.class);
                    intent.putExtra(MeasureConstants.INTENT_PAPER_ID, historyMokao.getId());
                    intent.putExtra(MeasureConstants.INTENT_PAPER_TYPE, MeasureConstants.MOKAO);
                    startActivity(intent);
                } else if ("done".equals(status)) {
                    // 跳转至练习报告页面
                    Intent intent = new Intent(HistoryMokaoActivity.this,
                            MeasureReportActivity.class);
                    intent.putExtra(MeasureConstants.INTENT_PAPER_ID, historyMokao.getExercise_id());
                    intent.putExtra(MeasureConstants.INTENT_PAPER_TYPE, MeasureConstants.MOKAO);
                    startActivity(intent);
                }

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", String.valueOf(position + 1));
                UmengManager.onEvent(HistoryMokaoActivity.this, "Minilist", map);
            }
        });
    }
}
