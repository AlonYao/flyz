package com.appublisher.quizbank.common.interview.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_pay.PayListener;
import com.appublisher.lib_pay.PayModel;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.netdata.OrderStatusM;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProductOrderInfoActivity extends BaseActivity implements RequestCallback {
    private int order_num;
    private InterviewRequest mRequest;

    private TextView mProductNameTv;
    private TextView mOrderPriceTv;
    private TextView mOrderNumTv;
    private TextView mPayPriceTv;
    private View mAlipayView;
    private View mWxpayView;
    private CheckBox mAliCheckBox;
    private CheckBox mWxCheckBox;
    private Button mPayBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_order_info);
        setToolBar(this);

        order_num = getIntent().getIntExtra("order_num", -1);
        mRequest = new InterviewRequest(this, this);

        initViews();
        getData();
    }

    public void initViews() {
        mProductNameTv = (TextView) findViewById(R.id.product_name);
        mOrderPriceTv = (TextView) findViewById(R.id.product_money);
        mOrderNumTv = (TextView) findViewById(R.id.order_num);
        mPayPriceTv = (TextView) findViewById(R.id.pay_money);
        mAlipayView = findViewById(R.id.alipay_view);
        mWxpayView = findViewById(R.id.wxpay_view);
        mAliCheckBox = (CheckBox) findViewById(R.id.ali_checkbox);
        mWxCheckBox = (CheckBox) findViewById(R.id.wx_checkbox);
        mPayBtn = (Button) findViewById(R.id.pay_btn);

        mAlipayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAliCheckBox.isChecked()) {
                    mAliCheckBox.setChecked(true);
                    mWxCheckBox.setChecked(false);
                }
            }
        });

        mWxpayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mWxCheckBox.isChecked()) {
                    mWxCheckBox.setChecked(true);
                    mAliCheckBox.setChecked(false);
                }
            }
        });

        mAliCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mWxCheckBox.setChecked(false);
                }
            }
        });

        mWxCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mAliCheckBox.setChecked(false);
                }
            }
        });

        mPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PayModel payModel = new PayModel(ProductOrderInfoActivity.this);
                if (mWxCheckBox.isChecked()) {
                    payModel.wxPay(String.valueOf(order_num), new PayListener() {
                        @Override
                        public void isPaySuccess(boolean isPaySuccess, String orderId) {
                            if (isPaySuccess) {
                                setResult(200);
                                finish();
                            }
                        }
                    });
                } else if (mAliCheckBox.isChecked()) {
                    payModel.aliPay(String.valueOf(order_num), new PayListener() {
                        @Override
                        public void isPaySuccess(boolean isPaySuccess, String orderId) {
                            if (isPaySuccess) {
                                setResult(200);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    public void getData() {
        showLoading();
        mRequest.getOrderStatus(order_num);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        hideLoading();
        if (response == null) return;
        if ("order_status".equals(apiName)) {
            OrderStatusM orderStatusM = GsonManager.getModel(response, OrderStatusM.class);
            if (orderStatusM.getResponse_code() == 1) {
                OrderStatusM.OrderBean orderBean = orderStatusM.getOrder();
                setValue(orderBean);
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

    public void setValue(OrderStatusM.OrderBean orderBean) {
        mProductNameTv.setText(orderBean.getProduct_name());
        mOrderPriceTv.setText(orderBean.getPrice() + "元");
        mPayPriceTv.setText("￥" + orderBean.getPrice());
        mOrderNumTv.setText(orderBean.getOrder_num());
    }
}
