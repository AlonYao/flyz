package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.HistoryMokaoAdapter;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.netdata.historymokao.HistoryMokaoM;
import com.appublisher.quizbank.model.netdata.historymokao.HistoryMokaoResp;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryMokaoActivity extends ActionBarActivity implements RequestCallback{

    private ListView lvHistoryMokao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_mokao);

        // Toolbar
        CommonModel.setToolBar(this);

        // View 初始化
        lvHistoryMokao = (ListView) findViewById(R.id.historymokao_lv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获取数据
        ProgressDialogManager.showProgressDialog(this, true);
        new Request(this, this).getHistoryMokao();

        // Umeng
        MobclickAgent.onPageStart("HistoryMokaoActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("HistoryMokaoActivity");
        MobclickAgent.onPause(this);
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
        if (response == null) return;

        Gson gson = new Gson();
        HistoryMokaoResp historyMokaoResp =
                gson.fromJson(response.toString(), HistoryMokaoResp.class);

        if (historyMokaoResp == null || historyMokaoResp.getResponse_code() != 1) return;

        final ArrayList<HistoryMokaoM> historyMokaos = historyMokaoResp.getPaper_list();

        if (historyMokaos == null || historyMokaos.size() == 0) return;

        HistoryMokaoAdapter historyMokaoAdapter = new HistoryMokaoAdapter(this, historyMokaos);
        lvHistoryMokao.setAdapter(historyMokaoAdapter);

        lvHistoryMokao.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= historyMokaos.size()) return;

                HistoryMokaoM historyMokao = historyMokaos.get(position);

                if (historyMokao == null) return;

                String status = historyMokao.getStatus();

                if ("undone".equals(status)) {
                    Intent intent = new Intent(HistoryMokaoActivity.this, MeasureActivity.class);
                    intent.putExtra("exercise_id", historyMokao.getExercise_id());
                    intent.putExtra("paper_type", "mokao");
                    intent.putExtra("paper_name", historyMokao.getName());
                    intent.putExtra("redo", true);
                    startActivity(intent);
                } else if ("fresh".equals(status)) {
                    Intent intent = new Intent(HistoryMokaoActivity.this, MeasureActivity.class);
                    intent.putExtra("paper_id", historyMokao.getId());
                    intent.putExtra("paper_type", "mokao");
                    intent.putExtra("paper_name", historyMokao.getName());
                    intent.putExtra("redo", false);
                    startActivity(intent);
                } else if ("done".equals(status)) {
                    // 解析
                    Intent intent =
                            new Intent(HistoryMokaoActivity.this, MeasureAnalysisActivity.class);
                    intent.putExtra("exercise_id", historyMokao.getExercise_id());
                    intent.putExtra("analysis_type", "mokao");
                    startActivity(intent);
                }
            }
        });
    }
}
