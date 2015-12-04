package com.appublisher.quizbank.wxapi;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.pay.PayConstants;
import com.appublisher.quizbank.utils.ToastManager;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        api = WXAPIFactory.createWXAPI(this, getString(R.string.weixin_appid));
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        // 微信支付回调
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            int errCode = resp.errCode;
            if (errCode == 0) {
                ToastManager.showToast(WXPayEntryActivity.this, "微信支付成功");
                PayConstants.mIsPaySuccess = true;
            } else if (errCode == -1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.wx_pay);
                builder.setMessage(getString(R.string.wx_pay_error));
                builder.show();
            } else if (errCode == -2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.wx_pay);
                builder.setMessage(getString(R.string.wx_pay_cancel));
                builder.show();
            }
            // 0：成功 -1：错误 -2：用户取消
            finish();
        }
    }
}