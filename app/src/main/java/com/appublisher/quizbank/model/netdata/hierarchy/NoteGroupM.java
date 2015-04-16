package com.appublisher.quizbank.model.netdata.hierarchy;

import java.util.ArrayList;

/**
 * 知识点组数据模型
 */
public class NoteGroupM {
    int group_id;
    String name;
    ArrayList<NoteItemM> notes;

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<NoteItemM> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<NoteItemM> notes) {
        this.notes = notes;
    }
}
