package com.appublisher.quizbank.model.netdata.mock;

import java.util.List;

/**
 * Created by bihaitian on 15/9/18.
 */
public class MockPre {


    /**
     * response_code : 1
     * mock_time : 2015-09-24 11:00:00
     * mock_status : unstart
     * date_info : [{"text":"第一行","link":""},{"text":"第二行","link":""},{"text":"第三行","link":""},{"text":"第四行，a这是课程","link":"http://m.zhiboke.net/index.html#/live/unpurchased?course_id=264"}]
     * award_info : ["第一名奖励大保健","第二名奖励大包间","第三名奖励大宝剑"]
     * course_id : 264
     * is_purchased : false
     * exercise_id : 857
     */


    private int response_code;
    private String mock_time;
    private String mock_status;
    private int course_id;
    private boolean is_purchased;
    private List<DateInfoEntity> date_info;
    private List<String> award_info;

    private int exercise_id;

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

    public static class DateInfoEntity {
        /**
         * text : 第一行
         * link :
         */

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
}
