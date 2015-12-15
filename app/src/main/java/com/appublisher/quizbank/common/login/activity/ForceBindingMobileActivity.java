package com.appublisher.quizbank.common.login.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 登录注册后强制绑定手机号
 */
public class ForceBindingMobileActivity extends AppCompatActivity implements RequestCallback{

    private String mPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_force_binding_mobile);

        // ActionBar
        CommonModel.setToolBar(this);

        // Variable Init
        final Request request = new Request(this, this);

        // View Init
        final EditText etMobile = (EditText) findViewById(R.id.forcebindingmobile_phone);
        Button btnNext = (Button) findViewById(R.id.forcebindingmobile_next);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneNum = etMobile.getText().toString();

                if (mPhoneNum.isEmpty()) {
                    ToastManager.showToast(ForceBindingMobileActivity.this, "手机号不能为空");
                    return;
                }

                ProgressDialogManager.showProgressDialog(ForceBindingMobileActivity.this);
                request.isUserExists(mPhoneNum);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
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
