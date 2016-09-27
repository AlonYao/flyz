package com.appublisher.quizbank.common.promote;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MockPreActivity;
import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.common.login.model.LoginModel;
import com.appublisher.quizbank.utils.GsonManager;

/**
 * 国考公告解读宣传
 */
public class PromoteQuizBankModel extends PromoteModel {

    public PromoteQuizBankModel(Context context) {
        super(context);
    }

    public void showPromoteAlert(String promoteData) {
        if (!isShow()) return;

        PromoteResp resp = GsonManager.getModel(promoteData, PromoteResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;

        final PromoteResp.AlertBean alertBean = resp.getAlert();
        if (alertBean == null || !alertBean.isEnable()) return;

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.promote_alert);
        window.setBackgroundDrawableResource(R.color.transparency);

        ImageView ivImg = (ImageView) window.findViewById(R.id.promote_alert_img);
        TextView tvMsg = (TextView) window.findViewById(R.id.promote_alert_msg);
        ImageButton btnClose = (ImageButton) window.findViewById(R.id.promote_alert_close);
        Button btnConfirm = (Button) window.findViewById(R.id.promote_alert_confirm);

        // 图片
        getRequest().loadImage(alertBean.getAlert_image(), ivImg);

        // 关闭
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        // 消息
        tvMsg.setText(alertBean.getAlert_text());

        // 跳转
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String target_type = alertBean.getTarget_type();
                if ("url".equals(target_type)) {
                    // 外部链接
                    String url = alertBean.getTarget()
                            + "?user_id=" + LoginModel.getUserId()
                            + "&user_token=" + LoginModel.getUserToken();
                    Intent intent = new Intent(getContext(), WebViewActivity.class);
                    intent.putExtra("url", url);
                    getContext().startActivity(intent);
                } else if ("mock".equals(target_type)) {
                    // 模考
                    Intent intent = new Intent(getContext(), MockPreActivity.class);
                    intent.putExtra("type", "mock");
                    getContext().startActivity(intent);
                }
            }
        });
    }

}
