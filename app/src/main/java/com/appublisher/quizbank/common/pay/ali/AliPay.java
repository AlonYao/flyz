package com.appublisher.quizbank.common.pay.ali;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.appublisher.quizbank.activity.WebViewActivity;

import java.lang.ref.WeakReference;

/**
 * 支付宝
 */
public class AliPay {
    private static final int SDK_PAY_FLAG = 1;
//    private static Handler mHandler;

    private static class MsgHandler extends Handler {
        private WeakReference<Activity> mActivity;

        public MsgHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @SuppressLint("CommitPrefEdits")
        @Override
        public void handleMessage(Message msg) {
            final WebViewActivity activity = (WebViewActivity) mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case SDK_PAY_FLAG: {
                        AliPayResult aliPayResult = new AliPayResult((String) msg.obj);
                        // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
//                        String resultInfo = aliPayResult.getResult();
                        String resultStatus = aliPayResult.getResultStatus();
                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                        if (TextUtils.equals(resultStatus, "9000")) {
                            WebViewActivity.isPaySuccess = true;
                            Toast.makeText(activity, "支付成功",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            // 判断resultStatus 为非“9000”则代表可能支付失败
                            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，
                            // 最终交易是否成功以服务端异步通知为准（小概率状态）
                            if (TextUtils.equals(resultStatus, "8000")) {
                                Toast.makeText(activity, "支付结果确认中",
                                        Toast.LENGTH_LONG).show();

                            } else {
                                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                Toast.makeText(activity, "支付失败",
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                        break;
                    }

                    default:
                        break;
                }
            }
        }
    }

    /**
     * 支付
     *
     * @param payInfo  支付信息
     * @param activity Activity
     */
    public static void pay(final String payInfo, final WebViewActivity activity) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //在子线程中new handler 必写
                Looper.prepare();
                PayTask alipay = new PayTask(activity);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);
                MsgHandler mHandler = new MsgHandler(activity);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
                Looper.loop();
            }
        }).start();
    }
}
