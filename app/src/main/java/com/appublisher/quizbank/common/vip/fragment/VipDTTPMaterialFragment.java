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
import com.appublisher.quizbank.common.vip.netdata.VipDTTPResp;

/**
 * 小班：单题突破 材料Fragment
 */

public class VipDTTPMaterialFragment extends Fragment{

    private static final String ARGS_DATA = "data";
    private VipDTTPResp mResp;

    public static VipDTTPMaterialFragment newInstance(VipDTTPResp resp) {
        Bundle args = new Bundle();
        args.putString("data", GsonManager.modelToString(resp));
        VipDTTPMaterialFragment fragment = new VipDTTPMaterialFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResp = GsonManager.getModel(getArguments().getString(ARGS_DATA), VipDTTPResp.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vip_dttp_material_fragment, container, false);

        // 材料
        WebView webView = (WebView) view.findViewById(R.id.vip_dttp_material_webview);
        webView.setBackgroundColor(0);
        webView.loadDataWithBaseURL(null, getMaterial(mResp), "text/html", "UTF-8", null);

        // 上滑Button
        final ScrollView scrollView =
                (ScrollView) view.findViewById(R.id.vip_dttp_material_scrollview);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.vip_dttp_totop);
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
    private static String getMaterial(VipDTTPResp resp) {
        if (resp == null || resp.getResponse_code() != 1) return "";
        VipDTTPResp.QuestionBean questionBean = resp.getQuestion();
        if (questionBean == null) return "";
        return questionBean.getMaterial();
    }
}
