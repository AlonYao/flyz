package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.business.KnowledgeTreeModel;
import com.appublisher.quizbank.network.Request;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 专项练习
 */
public class SpecialProjectActivity extends ActionBarActivity implements RequestCallback {

    public LinearLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_project);

        // ToolBar
        CommonModel.setToolBar(this);

        // View 初始化
        mContainer = (LinearLayout) findViewById(R.id.specialproject_container);

        // 获取数据
        ProgressDialogManager.showProgressDialog(this, true);
        new Request(this, this).getNoteHierarchy("all");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onPageStart("SpecialProjectActivity");
        MobclickAgent.onResume(this);

        // TalkingData
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("SpecialProjectActivity");
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
        if ("note_hierarchy".equals(apiName))
            new KnowledgeTreeModel(this, mContainer, KnowledgeTreeModel.TYPE_NOTE)
                    .dealHierarchyResp(response);

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
}
