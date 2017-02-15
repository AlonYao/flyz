package com.appublisher.quizbank.common.interview.netdata;

import java.util.List;

/**
 * Created by jinbao on 2016/11/17.
 */

public class InterviewPaperDetailResp {

    private int response_code;
    private List<QuestionsBean> questions;
    private SingleAudioBean single_audio;
    private AllAudioBean all_audio;

    public SingleAudioBean getSingle_audio() {
        return single_audio;
    }

    public void setSingle_audio(SingleAudioBean single_audio) {
        this.single_audio = single_audio;
    }

    public AllAudioBean getAll_audio() {
        return all_audio;
    }

    public void setAll_audio(AllAudioBean all_audio) {
        this.all_audio = all_audio;
    }

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public List<QuestionsBean> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionsBean> questions) {
        this.questions = questions;
    }

    public static class SingleAudioBean {
        private int product_id;
        private String product_type;
        private double price;
        boolean is_purchased;

        public int getProduct_id() {
            return product_id;
        }

        public void setProduct_id(int product_id) {
            this.product_id = product_id;
        }

        public String getProduct_type() {
            return product_type;
        }

        public void setProduct_type(String product_type) {
            this.product_type = product_type;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public boolean is_purchased() {
            return is_purchased;
        }

        public void setIs_purchased(boolean is_purchased) {
            this.is_purchased = is_purchased;
        }
    }

    public static class AllAudioBean {
        private int product_id;
        private String product_type;
        private double price;
        boolean is_purchased;

        public int getProduct_id() {
            return product_id;
        }

        public void setProduct_id(int product_id) {
            this.product_id = product_id;
        }

        public String getProduct_type() {
            return product_type;
        }

        public void setProduct_type(String product_type) {
            this.product_type = product_type;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public boolean is_purchased() {
            return is_purchased;
        }

        public void setIs_purchased(boolean is_purchased) {
            this.is_purchased = is_purchased;
        }
    }

    public static class QuestionsBean {
        private int id;
        private String material;
        private String question;
        private String notes;
        private String analysis;
        private String keywords;
        private String from;

        private String question_audio;   //  问题录音
        private int question_audio_duration;   // 录音时长
        private String analysis_audio;   // 解析录音
        private int analysis_audio_duration;   // 解析时长

        private String user_audio;   // 用户的录音地址
        private int user_audio_duration;   // 用户的录音的时长

        private boolean purchased_review;   // 是否购买名师点评
        private boolean purchased_audio;    //
        private boolean is_collected;    //  是否收藏

        private int comment_status;       // 点评的状态

        private String teacher_name;        // 名师点评老师
        private String teacher_audio;       // 点评语音
        private int teacher_audio_duration;     // 点评时长

        private String question_type;       // 数据来源类型

        private String reviewed_at;         // 时间戳
        public String getReviewed_at() {
            return reviewed_at;
        }

        public void setReviewed_at(String reviewed_at) {
            this.reviewed_at = reviewed_at;
        }

        public String getQuestion_type() {
            return question_type;
        }

        public void setQuestion_type(String question_type) {
            this.question_type = question_type;
        }

        public String getTeacher_name() {   return teacher_name; }
        public void setTeacher_name(String teacher_name) {
            this.teacher_name = teacher_name;
        }
        public String getTeacher_audio() { return teacher_audio;}
        public void setTeacher_audio(String teacher_audio) {
            this.teacher_audio = teacher_audio;
        }
        public int getTeacher_audio_duration() {
            return teacher_audio_duration;
        }

        public void setTeacher_audio_duration(int teacher_audio_duration) {
            this.teacher_audio_duration = teacher_audio_duration;
        }

        public int getComment_status() {
            return comment_status;
        }

        public void setComment_status(int comment_status) {
            this.comment_status = comment_status;
        }

        public int getUser_audio_duration() {
            return user_audio_duration;
        }

        public void setUser_audio_duration(int user_audio_duration) {
            this.user_audio_duration = user_audio_duration;
        }

        public boolean getIs_collected() {
            return is_collected;
        }

        public void setIs_collected(boolean is_collected) {
            this.is_collected = is_collected;
        }

        public boolean isPurchased_audio() {

            return purchased_audio;
        }

        public void setPurchased_audio(boolean purchased_audio) {
            this.purchased_audio = purchased_audio;
        }

        public boolean isPurchased_review() {

            return purchased_review;
        }

        public void setPurchased_review(boolean purchased_review) {
            this.purchased_review = purchased_review;
        }

        public String getUser_audio() {
            return user_audio == null ? "" : user_audio;
        }

        public void setUser_audio(String user_audio) {
            this.user_audio = user_audio;
        }

        public int getAnalysis_audio_duration() {

            return analysis_audio_duration;
        }

        public void setAnalysis_audio_duration(int analysis_audio_duration) {
            this.analysis_audio_duration = analysis_audio_duration;
        }

        public String getAnalysis_audio() {

            return analysis_audio;
        }

        public void setAnalysis_audio(String analysis_audio) {
            this.analysis_audio = analysis_audio;
        }

        public int getQuestion_audio_duration() {

            return question_audio_duration;
        }

        public void setQuestion_audio_duration(int question_audio_duration) {
            this.question_audio_duration = question_audio_duration;
        }

        public String getQuestion_audio() {

            return question_audio;
        }

        public void setQuestion_audio(String question_audio) {
            this.question_audio = question_audio;
        }



        private String status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMaterial() {
            return material;
        }

        public void setMaterial(String material) {
            this.material = material;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getAnalysis() {
            return analysis;
        }

        public void setAnalysis(String analysis) {
            this.analysis = analysis;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
