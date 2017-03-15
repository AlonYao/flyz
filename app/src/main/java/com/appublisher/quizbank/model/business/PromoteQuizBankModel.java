package com.appublisher.quizbank.model.business;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_course.CourseWebViewActivity;
import com.appublisher.lib_course.promote.PromoteModel;
import com.appublisher.lib_course.promote.PromoteResp;
import com.appublisher.lib_login.volley.LoginParamBuilder;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.mock.activity.MockPreActivity;
import com.appublisher.quizbank.common.mock.dao.MockDAO;

import java.util.HashMap;

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

        if ("mokao".equals(alertBean.getTarget_type())) {
            int isBook = MockDAO.getIsDateById(alertBean.getTarget());
            if (isBook == 1) return;
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        Window window = alertDialog.getWindow();
        if (window == null) return;
        window.setContentView(R.layout.promote_alert);
        window.setBackgroundDrawableResource(R.color.transparency);

        ImageView ivImg = (ImageView) window.findViewById(R.id.promote_alert_img);
        TextView tvMsg = (TextView) window.findViewById(R.id.promote_alert_msg);
        ImageButton btnClose = (ImageButton) window.findViewById(R.id.promote_alert_close);
        Button btnConfirm = (Button) window.findViewById(R.id.promote_alert_confirm);

        //noinspection deprecation
        btnConfirm.setBackgroundColor(getContext().getResources().getColor(R.color.themecolor));

        // 图片
        getRequest().loadImage(alertBean.getAlert_image(), ivImg);

        // 关闭
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDateAdd1();
                alertDialog.dismiss();

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Close");
                UmengManager.onEvent(getContext(), "Ad", map);
            }
        });

        // 消息
        tvMsg.setText(alertBean.getAlert_text());

        // 跳转
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String target_type = alertBean.getTarget_type();
                String target = alertBean.getTarget();
                if (target == null) target = "";
                if ("url".equals(target_type)) {
                    // 外部链接
                    Intent intent = new Intent(getContext(), CourseWebViewActivity.class);
                    intent.putExtra("url", LoginParamBuilder.finalUrl(alertBean.getTarget()));
                    if (alertBean.getTarget() != null && alertBean.getTarget().contains("course_id"))
                        intent.putExtra("from", "course");
                    getContext().startActivity(intent);
                } else if ("mokao".equals(target_type)) {
                    // 模考
                    Intent intent = new Intent(getContext(), MockPreActivity.class);
                    intent.putExtra("type", "mock");
                    intent.putExtra("mock_id", Integer.parseInt(target));
                    getContext().startActivity(intent);
                }
                alertDialog.dismiss();

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Promote");
                UmengManager.onEvent(getContext(), "Ad", map);
            }
        });

        // 保存时间
        saveDate();
    }

}