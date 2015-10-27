package com.appublisher.quizbank.model.entity.umeng;

/**
 * 友盟分享中的分享文字实体类
 */
public class UMShareContentEntity {

    String type; // 种类：能力评估(evaluation)、练习报告(practice_report)、单题解析(measure_analysis)
    float rank; // 排名
    int learningDays; // 学习天数
    float score; // 估分
    float defeat; // 击败了多少同学
    String accuracy; // 正确率
    String examName; // 估分考试名称
    String paperType; // 练习类型：mini模考、快速智能练习等等

    public String getPaperType() {
        return paperType;
    }

    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getRank() {
        return rank;
    }

    public void setRank(float rank) {
        this.rank = rank;
    }

    public int getLearningDays() {
        return learningDays;
    }

    public void setLearningDays(int learningDays) {
        this.learningDays = learningDays;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getDefeat() {
        return defeat;
    }

    public void setDefeat(float defeat) {
        this.defeat = defeat;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }
}
