package com.appublisher.quizbank.model.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.model.login.model.netdata.CommonResponseModel;
import com.appublisher.quizbank.model.login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.google.gson.Gson;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 修改昵称Activity
 */
public class NicknameChangeActivity extends ActionBarActivity implements RequestCallback {

    private String mNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_nickname_change);

        // ActionBar
        CommonModel.setToolBar(this);

        // view初始化
        final EditText etNickName = (EditText) findViewById(R.id.nickname_change_et);
        Button btnNickName = (Button) findViewById(R.id.nickname_change_btn);

        btnNickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNickname = etNickName.getText().toString();
                if (!mNickname.isEmpty()) {
                    String preNickName = getIntent().getStringExtra("nickname");
                    if (!mNickname.equals(preNickName)) {
                        ProgressDialogManager.showProgressDialog(NicknameChangeActivity.this, false);
                        new Request(NicknameChangeActivity.this, NicknameChangeActivity.this).
                                changeUserInfo(ParamBuilder.changeUserInfo("nickname", mNickname));
                    }
                }
            }
        });
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

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response != null) {
            Gson gson = new Gson();
            CommonResponseModel commonResponseModel = gson.fromJson(
                    response.toString(), CommonResponseModel.class);

            if (commonResponseModel != null && commonResponseModel.getResponse_code() == 1) {
                // 修改成功
                User user = UserDAO.findById();
                if (user != null) {
                    UserInfoModel userInfo = gson.fromJson(user.user, UserInfoModel.class);
                    userInfo.setNickname(mNickname);
                    UserDAO.updateUserInfo(gson.toJson(userInfo));

                    ToastManager.showToast(this, "修改成功");

                    Intent intent = new Intent(this, UserInfoActivity.class);
                    intent.putExtra("user_info", gson.toJson(userInfo));
                    setResult(10, intent);

                    finish();
                }
            }
        }

        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }
}
