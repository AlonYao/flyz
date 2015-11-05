package com.appublisher.quizbank.common.login.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.db.GlobalSetting;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.appublisher.quizbank.utils.GsonManager;
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
        TextView tv = (TextView) findViewById(R.id.cannotget_smscode_qq);

        // 获取客服QQ
        GlobalSetting globalSetting = GlobalSettingDAO.findById();

        if (globalSetting != null) {
            if (Globals.gson == null) Globals.gson = GsonManager.initGson();
            GlobalSettingsResp globalSettingsResp =
                    Globals.gson.fromJson(globalSetting.content, GlobalSettingsResp.class);

            String qq = getString(R.string.service_qq);

            if (globalSettingsResp != null && globalSettingsResp.getResponse_code() == 1) {
                qq = globalSettingsResp.getService_qq();
            }

            tv.setText("如果仍然无法收到短信验证码，请联系客服QQ:\n" + qq);
            CommonModel.setTextLongClickCopy(tv);
        }

        // 保存Activity
        QuizBankApp.getInstance().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("CannotGetSmsActivity");
        MobclickAgent.onResume(this);
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("CannotGetSmsActivity");
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
