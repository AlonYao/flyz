package com.appublisher.quizbank.model.netdata.mock;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

/**
 * Created by bihaitian on 15/9/18.
 */
public class MockPreResp {

    private int response_code;
    private String mock_time;
    private String mock_status;
    private int course_id;
    private boolean is_purchased;
    private List<DateInfoEntity> date_info;
    private List<String> award_info;

    private int exercise_id;
    private String list_intro;
    private boolean is_booked;
    private String download_link;
    private List<MockListBean> mock_list;

    protected MockPreResp(Parcel in) {
        response_code = in.readInt();
        mock_time = in.readString();
        mock_status = in.readString();
        course_id = in.readInt();
        is_purchased = in.readByte() != 0;
        award_info = in.createStringArrayList();
        exercise_id = in.readInt();
        list_intro = in.readString();
        is_booked = in.readByte() != 0;
        download_link = in.readString();
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public void setMock_time(String mock_time) {
        this.mock_time = mock_time;
    }

    public void setMock_status(String mock_status) {
        this.mock_status = mock_status;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    public void setIs_purchased(boolean is_purchased) {
        this.is_purchased = is_purchased;
    }

    public void setDate_info(List<DateInfoEntity> date_info) {
        this.date_info = date_info;
    }

    public void setAward_info(List<String> award_info) {
        this.award_info = award_info;
    }

    public int getResponse_code() {
        return response_code;
    }

    public String getMock_time() {
        return mock_time;
    }

    public String getMock_status() {
        return mock_status == null ? "" : mock_status;
    }

    public int getCourse_id() {
        return course_id;
    }

    public boolean getIs_purchased() {
        return is_purchased;
    }

    public List<DateInfoEntity> getDate_info() {
        return date_info;
    }

    public List<String> getAward_info() {
        return award_info;
    }

    public void setExercise_id(int exercise_id) {
        this.exercise_id = exercise_id;
    }

    public int getExercise_id() {
        return exercise_id;
    }

    public String getList_intro() {
        return list_intro;
    }

    public void setList_intro(String list_intro) {
        this.list_intro = list_intro;
    }

    public boolean isIs_booked() {
        return is_booked;
    }

    public void setIs_booked(boolean is_booked) {
        this.is_booked = is_booked;
    }

    public String getDownload_link() {
        return download_link;
    }

    public void setDownload_link(String download_link) {
        this.download_link = download_link;
    }

    public List<MockListBean> getMock_list() {
        return mock_list;
    }

    public void setMock_list(List<MockListBean> mock_list) {
        this.mock_list = mock_list;
    }

    public static class DateInfoEntity {

        private String text;
        private String link;

        public void setText(String text) {
            this.text = text;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getText() {
            return text;
        }

        public String getLink() {
            return link;
        }
    }

    public static class MockListBean {
        private int paper_id;
        private String paper_name;
        private int question_num;

        public int getPaper_id() {
            return paper_id;
        }

        public void setPaper_id(int paper_id) {
            this.paper_id = paper_id;
        }

        public String getPaper_name() {
            return paper_name;
        }

        public void setPaper_name(String paper_name) {
            this.paper_name = paper_name;
        }

        public int getQuestion_num() {
            return question_num;
        }

        public void setQuestion_num(int question_num) {
            this.question_num = question_num;
        }
    }
}
