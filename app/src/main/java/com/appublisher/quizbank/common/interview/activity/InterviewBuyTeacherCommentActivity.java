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
import com.appublisher.quizbank.common.interview.network.InterviewParamBuilder;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;
import com.appublisher.quizbank.network.ParamBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class InterviewBuyTeacherCommentActivity extends BaseActivity implements RequestCallback {

    private TextView mCommentCountTv;
    private ListView mListView;
    private int mCommentCount;
    private InterviewRequest mRequest;
    private CommentProductsAdapter mAdapter;
    private InterviewCommentProductsResp mCommentProductsResp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_buy_teacher_comment);

        setToolBar(this);

        mCommentCount = getIntent().getIntExtra("comment_count", -1);
        mRequest = new InterviewRequest(this, this);
        mAdapter = new CommentProductsAdapter(this);

        initViews();
        getData();
    }

    public void initViews() {
        mCommentCountTv = (TextView) findViewById(R.id.comment_count);
        mListView = (ListView) findViewById(R.id.listView);
        if (mCommentCount != -1)
            mCommentCountTv.setText(String.valueOf(mCommentCount));
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
                startActivity(intent);
            } else {
                ToastManager.showToast(this, "订单创建失败，请联系客服");
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
}
