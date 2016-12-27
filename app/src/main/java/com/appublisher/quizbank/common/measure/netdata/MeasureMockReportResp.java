package com.appublisher.quizbank.common.measure.netdata;

import com.appublisher.quizbank.common.measure.bean.MeasureAnswerBean;
import com.appublisher.quizbank.common.measure.bean.MeasureCategoryBean;
import com.appublisher.quizbank.common.measure.bean.MeasureNotesBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.bean.MeasureScoresBean;

import java.util.List;

/**
 * 做题模块：历史练习
 */

public class MeasureMockReportResp {

    private int response_code;
    private String exercise_name;
    private String status;
    private int duration;
    private int start_from;
    private List<MeasureCategoryBean> category;
    private List<MeasureNotesBean> notes;
    private double score;
    private double defeat;
    private double avg_duration;
    private List<MeasureScoresBean> scores;
    private MockRankBean mock_rank;
    private List<HistoryMockBean> history_mock;
    private List<MeasureQuestionBean> questions;
    private List<MeasureAnswerBean> answers;

    public double getAvg_duration() {
        return avg_duration;
    }

    public void setAvg_duration(double avg_duration) {
        this.avg_duration = avg_duration;
    }

    public MockRankBean getMock_rank() {
        return mock_rank;
    }

    public void setMock_rank(MockRankBean mock_rank) {
        this.mock_rank = mock_rank;
    }

    public List<HistoryMockBean> getHistory_mock() {
        return history_mock;
    }

    public void setHistory_mock(List<HistoryMockBean> history_mock) {
        this.history_mock = history_mock;
    }

    public static class HistoryMockBean {
        private double avg;
        private double user_score;
        private String date;
        private double defeat;
        private double top;

        public double getDefeat() {
            return defeat;
        }

        public void setDefeat(double defeat) {
            this.defeat = defeat;
        }

        public double getTop() {
            return top;
        }

        public void setTop(double top) {
            this.top = top;
        }

        public double getAvg() {
            return avg;
        }

        public void setAvg(double avg) {
            this.avg = avg;
        }

        public double getUser_score() {
            return user_score;
        }

        public void setUser_score(double user_score) {
            this.user_score = user_score;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    public static class MockRankBean {
        private boolean available;
        private String available_time;
        private List<Integer> distribute;
        private boolean defeat_up;
        private boolean score_up;

        public String getAvailable_time() {
            return available_time;
        }

        public void setAvailable_time(String available_time) {
            this.available_time = available_time;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        public List<Integer> getDistribute() {
            return distribute;
        }

        public void setDistribute(List<Integer> distribute) {
            this.distribute = distribute;
        }

        public boolean isDefeat_up() {
            return defeat_up;
        }

        public void setDefeat_up(boolean defeat_up) {
            this.defeat_up = defeat_up;
        }

        public boolean isScore_up() {
            return score_up;
        }

        public void setScore_up(boolean score_up) {
            this.score_up = score_up;
        }
    }

    public List<MeasureQuestionBean> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MeasureQuestionBean> questions) {
        this.questions = questions;
    }

    public List<MeasureAnswerBean> getAnswers() {
        return answers;
    }

    public void setAnswers(List<MeasureAnswerBean> answers) {
        this.answers = answers;
    }

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public String getExercise_name() {
        return exercise_name;
    }

    public void setExercise_name(String exercise_name) {
        this.exercise_name = exercise_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getStart_from() {
        return start_from;
    }

    public void setStart_from(int start_from) {
        this.start_from = start_from;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getDefeat() {
        return defeat;
    }

    public void setDefeat(double defeat) {
        this.defeat = defeat;
    }

    public List<MeasureCategoryBean> getCategory() {
        return category;
    }

    public void setCategory(List<MeasureCategoryBean> category) {
        this.category = category;
    }

    public List<MeasureNotesBean> getNotes() {
        return notes;
    }

    public void setNotes(List<MeasureNotesBean> notes) {
        this.notes = notes;
    }

    public List<MeasureScoresBean> getScores() {
        return scores;
    }

    public void setScores(List<MeasureScoresBean> scores) {
        this.scores = scores;
    }

}
