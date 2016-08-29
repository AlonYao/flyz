package com.appublisher.quizbank.common.vip.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.EvaluationActivity;
import com.makeramen.roundedimageview.RoundedImageView;

public class VipIndexActivity extends BaseActivity {

    private TextView nickname;
    private RoundedImageView avatarImage;
    private TextView evaluationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_index);
        setToolBar(this);

        //initViews
        initViews();
        setValues();

    }

    public void initViews() {
        nickname = (TextView) findViewById(R.id.nickname);
        evaluationText = (TextView) findViewById(R.id.ev_txt);
        avatarImage = (RoundedImageView) findViewById(R.id.user_avatar);
    }

    public void setValues(){
        evaluationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(VipIndexActivity.this, EvaluationActivity.class);
                startActivity(intent);
            }
        });
    }

}
