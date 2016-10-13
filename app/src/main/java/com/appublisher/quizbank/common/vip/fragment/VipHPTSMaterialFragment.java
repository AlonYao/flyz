package com.appublisher.quizbank.common.vip.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ScrollView;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.netdata.VipHPTSResp;

/**
 * 小班：互评提升 材料Tab
 */

public class VipHPTSMaterialFragment extends Fragment{

    private static final String ARGS_DATA = "data";
    private VipHPTSResp mResp;

    public static VipHPTSMaterialFragment newInstance(VipHPTSResp resp) {
        Bundle args = new Bundle();
        args.putString(ARGS_DATA, GsonManager.modelToString(resp));
        VipHPTSMaterialFragment fragment = new VipHPTSMaterialFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResp = GsonManager.getModel(getArguments().getString(ARGS_DATA), VipHPTSResp.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vip_hpts_material_fragment, container, false);

        // 材料
        WebView webView = (WebView) view.findViewById(R.id.vip_hpts_material_webview);
        webView.setBackgroundColor(0);
        webView.loadDataWithBaseURL(null, getMaterial(mResp), "text/html", "UTF-8", null);

        // 上滑Button
        final ScrollView scrollView =
                (ScrollView) view.findViewById(R.id.vip_hpts_material_scrollview);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.vip_hpts_totop);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        return view;
    }

    /**
     * 获取材料
     * @param resp VipMSJPResp
     * @return String
     */
    @NonNull
    private static String getMaterial(VipHPTSResp resp) {
        if (resp == null || resp.getResponse_code() != 1) return "";
        VipHPTSResp.QuestionBean questionBean = resp.getQuestion();
        if (questionBean == null) return "";
        return questionBean.getMaterial();
    }

}
