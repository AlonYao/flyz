package com.appublisher.quizbank.common.measure.view;

import com.appublisher.lib_basic.activity.IBaseView;
import com.appublisher.quizbank.common.measure.netdata.MeasureSearchResp;

import java.util.List;

/**
 * 做题模块：搜题 view层
 */

public interface IMeasureSearchView extends IBaseView{

    void stopXListView();

    void showContent(List<MeasureSearchResp.SearchItemBean> list);

    void showLoadMore(List<MeasureSearchResp.SearchItemBean> list);

    void hideSoftKeyboard();

    void showNoMoreToast();

    void showNotice(String keywords, int count);

    void showNone();
}
