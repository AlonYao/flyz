package com.appublisher.quizbank.common.interview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.adapter.CommentProductsAdapter;
import com.appublisher.quizbank.common.interview.netdata.CommentProductM;
import com.appublisher.quizbank.common.interview.netdata.InterviewCommentProductsResp;
import com.appublisher.quizbank.common.interview.netdata.InterviewTeacherRemarkNumResp;
import com.appublisher.quizbank.common.interview.network.InterviewParamBuilder;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;
import com.appublisher.quizbank.network.ParamBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class InterviewCommentProductActivity extends BaseActivity implements RequestCallback {

    private TextView mCommentCountTv;
    private ListView mListView;
    private InterviewRequest mRequest;
    private CommentProductsAdapter mAdapter;
    private InterviewCommentProductsResp mCommentProductsResp;
    private static final int PAY_SUCCESS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_buy_teacher_comment);

        setToolBar(this);

        mRequest = new InterviewRequest(this, this);
        mAdapter = new CommentProductsAdapter(this);

        initViews();
        getData();
    }

    public void initViews() {
        mCommentCountTv = (TextView) findViewById(R.id.comment_count);
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCommentProductsResp == null) return;
                final List<CommentProductM> commentProductMs = mCommentProductsResp.getList();
                if (commentProductMs.size() >= (position + 1)) {
                    mRequest.genOrder(InterviewParamBuilder.genOrder(commentProductMs.get(position).getProduct_id(), commentProductMs.get(position).getProduct_type(), "1", "", ""));
                }
            }
        });
    }

    public void getData() {
        showLoading();
        mRequest.getTeacherRemarkRemainder(2);
        mRequest.getTeacherCommentProducts();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        hideLoading();
        if (response == null) return;
        if ("comment_products".equals(apiName)) {
            InterviewCommentProductsResp commentProductsResp = GsonManager.getModel(response, InterviewCommentProductsResp.class);
            if (commentProductsResp != null && commentProductsResp.getResponse_code() == 1) {
                mCommentProductsResp = commentProductsResp;
                mAdapter.setCommentProductsResp(commentProductsResp);
                mAdapter.notifyDataSetChanged();
            } else {
                ToastManager.showToast(this, "数据有误");
            }
        } else if ("gen_order".equals(apiName)) {
            if (response.optInt("response_code") == 1) {
                final Intent intent = new Intent(this, ProductOrderInfoActivity.class);
                intent.putExtra("order_num", response.optInt("order_num"));
                startActivityForResult(intent, PAY_SUCCESS);
            } else {
                ToastManager.showToast(this, "订单创建失败，请联系客服");
            }
        } else if ("get_user_service_status".equals(apiName)) {
            InterviewTeacherRemarkNumResp interviewTeacherRemarkNumResp = GsonManager.getModel(response, InterviewTeacherRemarkNumResp.class);
            if (interviewTeacherRemarkNumResp.getResponse_code() == 1) {
                if (interviewTeacherRemarkNumResp.getData().size() < 1) return;
                for (int i = 0; i < interviewTeacherRemarkNumResp.getData().size(); i++) {
                    if ("num".equals(interviewTeacherRemarkNumResp.getData().get(i).getId())) {
                        mCommentCountTv.setText(interviewTeacherRemarkNumResp.getData().get(0).getVal());
                        return;
                    }
                }

            }
        }

    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        hideLoading();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        hideLoading();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAY_SUCCESS && resultCode == PAY_SUCCESS) {
            setResult(PAY_SUCCESS);
            finish();
        }
    }
}
