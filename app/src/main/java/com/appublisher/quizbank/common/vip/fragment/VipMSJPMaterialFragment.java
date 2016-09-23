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
import com.appublisher.quizbank.common.vip.netdata.VipMSJPResp;

/**
 * 小班：名师精批 材料Tab
 */
public class VipMSJPMaterialFragment extends Fragment{

    private static final String ARGS_DATA = "data";
    private VipMSJPResp mResp;

    public static VipMSJPMaterialFragment newInstance(VipMSJPResp resp) {
        Bundle args = new Bundle();
        args.putString("data", GsonManager.modelToString(resp));
        VipMSJPMaterialFragment fragment = new VipMSJPMaterialFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResp = GsonManager.getModel(getArguments().getString(ARGS_DATA), VipMSJPResp.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vip_msjp_material_fragment, container, false);
        WebView webView = (WebView) view.findViewById(R.id.vip_msjp_material_webview);
        final ScrollView scrollView =
                (ScrollView) view.findViewById(R.id.vip_msjp_material_scrollview);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.vip_msjp_totop);

        webView.setBackgroundColor(0);
        webView.loadDataWithBaseURL(null, getMaterial(mResp), "text/html", "UTF-8", null);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        return view;
    }

    @NonNull
    private static String getMaterial(VipMSJPResp resp) {
        if (resp == null || resp.getResponse_code() != 1) return "";
        VipMSJPResp.QuestionBean questionBean = resp.getQuestion();
        if (questionBean == null) return "";
        return questionBean.getMaterial();
    }
}
