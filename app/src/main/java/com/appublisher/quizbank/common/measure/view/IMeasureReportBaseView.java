package com.appublisher.quizbank.common.measure.view;

import com.appublisher.quizbank.common.measure.bean.MeasureNotesBean;
import com.appublisher.quizbank.common.measure.bean.MeasureReportCategoryBean;

import java.util.List;

/**
 * 做题模块：练习报告view层（Base）
 */

public interface IMeasureReportBaseView{


    void showCategory(List<MeasureReportCategoryBean> list, String from);

    void showNotes(List<MeasureNotesBean> list);
}
