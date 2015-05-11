package com.appublisher.quizbank.model.login.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.db.GlobalSetting;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.appublisher.quizbank.utils.GsonManager;
import com.google.gson.Gson;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

public class CannotGetSmsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_cannot_get_sms);

        // ActionBar
        CommonModel.setToolBar(this);

        // view初始化
        TextView tv = (TextView) findViewById(R.id.cannotget_sms_serviceqq);

        // 获取客服QQ
        GlobalSetting globalSetting = GlobalSettingDAO.findById();

        if (globalSetting != null) {
            Gson gson = GsonManager.initGson();
            GlobalSettingsResp globalSettingsResp =
                    gson.fromJson(globalSetting.content, GlobalSettingsResp.class);

            if (globalSettingsResp != null && globalSettingsResp.getResponse_code() == 1) {
                String qq = globalSettingsResp.getService_qq();
                tv.setText("客服QQ号：" + qq);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        TCAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
