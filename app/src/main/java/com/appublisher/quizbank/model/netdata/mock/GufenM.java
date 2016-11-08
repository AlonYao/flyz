package com.appublisher.quizbank.model.netdata.mock;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by jinbao on 2016/11/8.
 */

public class GufenM{
    private String name;
    private List<PaperListBean> paper_list;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PaperListBean> getPaper_list() {
        return paper_list;
    }

    public void setPaper_list(List<PaperListBean> paper_list) {
        this.paper_list = paper_list;
    }

    public static class PaperListBean {
        private int id;
        private int exercise_id;
        private String name;
        private int persons_num;
        private String status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getExercise_id() {
            return exercise_id;
        }

        public void setExercise_id(int exercise_id) {
            this.exercise_id = exercise_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPersons_num() {
            return persons_num;
        }

        public void setPersons_num(int persons_num) {
            this.persons_num = persons_num;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
