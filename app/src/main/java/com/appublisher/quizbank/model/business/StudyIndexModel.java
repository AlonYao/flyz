package com.appublisher.quizbank.model.business;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.lib_login.model.db.User;
import com.appublisher.lib_login.model.db.UserDAO;
import com.appublisher.lib_login.model.netdata.UserExamInfoModel;
import com.appublisher.quizbank.common.interview.fragment.InterviewIndexFragment;
import com.appublisher.quizbank.fragment.StudyIndexFragment;
import com.appublisher.quizbank.model.netdata.CarouselResp;
import com.appublisher.quizbank.model.netdata.exam.ExamDetailModel;
import com.appublisher.quizbank.model.netdata.exam.ExamItemModel;
import com.appublisher.quizbank.model.netdata.mock.GufenM;
import com.appublisher.quizbank.model.netdata.mock.MockGufenResp;
import com.appublisher.quizbank.network.QRequest;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * HomePageFragment Model
 */
public class StudyIndexModel {

    /**
     * 设置考试项目倒计时
     *
     * @param textView textView
     */
    public static void setExamCountDown(TextView textView, QRequest QRequest) {
        User user = UserDAO.findById();

        if (user == null) return;


        ExamItemModel examItemModel = GsonManager.getModel(user.exam, ExamItemModel.class);

        if (examItemModel == null) return;

        String name = examItemModel.getName();
        String date = examItemModel.getDate();

        long day = Utils.dateMinusNow(date);
        if (day < 0) {
            day = 0;
            QRequest.getExamList();
        }

        String text = "距离" + name + "\n还有" + String.valueOf(day) + "天";
        textView.setText(text);
    }

    /**
     * 更新考试时间
     *
     * @param examDetailModel 考试列表数据模型
     * @param textView        考试项目控件
     */
    public static void updateExam(ExamDetailModel examDetailModel, TextView textView) {
        if (examDetailModel == null || examDetailModel.getResponse_code() != 1) return;

        ArrayList<ExamItemModel> list = examDetailModel.getExams();
        if (list == null) return;

        int userExamId = LoginModel.getExamInfo().getExam_id();
        if (userExamId == 0) return;

        for (ExamItemModel examItemModel : list) {
            if (examItemModel == null) continue;
            if (examItemModel.getExam_id() == userExamId) {
                // 更新用户考试项目
                UserExamInfoModel curUserExam = LoginModel.getExamInfo();
                if (curUserExam == null) return;

                String name = examItemModel.getName();
                String date = examItemModel.getDate();

                curUserExam.setName(name);
                curUserExam.setDate(date);

                // 更新数据库
                LoginModel.updateExamInfo(
                        GsonManager.modelToString(curUserExam, UserExamInfoModel.class));

                // 更新页面显示
                long day = Utils.dateMinusNow(date);
                if (day < 0) day = 0;
                String text = "距离" + name + "\n" + "还有" + String.valueOf(day) + "天";
                textView.setText(text);
            }
        }
    }

    /**
     * 更新考试项目
     *
     * @param model    考试项目数据
     * @param fragment HomePageFragment
     */
    public static void updateExam(ExamItemModel model, StudyIndexFragment fragment) {
        if (model == null) return;
        LoginModel.updateExamInfo(GsonManager.modelToString(model, ExamItemModel.class));
        setExamCountDown(fragment.examNameTv, fragment.mQRequest);
    }

    /**
     * 模考估分回调
     *
     * @param response
     * @param fragment
     */
    public static void dealMockGufenResp(JSONObject response, StudyIndexFragment fragment) {
        final MockGufenResp mockGufenResp = GsonManager.getModel(response, MockGufenResp.class);

        if (mockGufenResp.getResponse_code() == 1) {

            fragment.mockGufenResp = mockGufenResp;

            if (mockGufenResp.getMock() != null) {
                MockGufenResp.MockBean mockBean = mockGufenResp.getMock();
                fragment.mockNameTv.setText(mockBean.getName());
                fragment.mockView.setVisibility(View.VISIBLE);
                fragment.mock_id = mockBean.getId();
            }

            if (mockGufenResp.getGufen() != null) {
                GufenM gufenBean = mockGufenResp.getGufen();
                fragment.assessNameTv.setText(gufenBean.getName());
                fragment.assessView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 轮播图处理
     *
     * @param jsonObject
     * @param fragment
     */
    public static void dealCarouselResp(JSONObject jsonObject, Fragment fragment) {
        CarouselResp carouselResp = GsonManager.getModel(jsonObject, CarouselResp.class);
        if (carouselResp != null && carouselResp.getResponse_code() == 1) {
            int width = Utils.getWindowWidth(fragment.getActivity());
            int height = width / 69 * 20;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);

            if (fragment instanceof StudyIndexFragment) {
                ((StudyIndexFragment) fragment).carouselView.setLayoutParams(params);
                if (carouselResp.getWritten() != null && carouselResp.getWritten().size() != 0) {
                    ((StudyIndexFragment) fragment).carouselWrittenList.clear();
                    ((StudyIndexFragment) fragment).carouselWrittenList.addAll(carouselResp.getWritten());
                    ((StudyIndexFragment) fragment).carouselAdapter.notifyDataSetChanged();
                    ((StudyIndexFragment) fragment).carouselView.setVisibility(View.VISIBLE);
                    ((StudyIndexFragment) fragment).initDots(carouselResp.getWritten().size());
                } else {
                    ((StudyIndexFragment) fragment).carouselView.setVisibility(View.GONE);
                }
            } else if (fragment instanceof InterviewIndexFragment) {
                ((InterviewIndexFragment) fragment).carouselView.setLayoutParams(params);
                if (carouselResp.getInterview() != null && carouselResp.getInterview().size() != 0) {
                    ((InterviewIndexFragment) fragment).carouselInterviewList.clear();
                    ((InterviewIndexFragment) fragment).carouselInterviewList.addAll(carouselResp.getInterview());
                    ((InterviewIndexFragment) fragment).carouselAdapter.notifyDataSetChanged();
                    ((InterviewIndexFragment) fragment).carouselView.setVisibility(View.VISIBLE);
                    ((InterviewIndexFragment) fragment).initDots(carouselResp.getInterview().size());
                } else {
                    ((InterviewIndexFragment) fragment).carouselView.setVisibility(View.GONE);
                }
            }
        }
    }

}
