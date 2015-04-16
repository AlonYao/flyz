package com.appublisher.quizbank.model.netdata.hierarchy;

import java.util.ArrayList;

/**
 * 知识点层级接口数据模型（第一层）
 */
public class HierarchyM {
    int category_id;
    String name;
    ArrayList<NoteGroupM> note_group;

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<NoteGroupM> getNote_group() {
        return note_group;
    }

    public void setNote_group(ArrayList<NoteGroupM> note_group) {
        this.note_group = note_group;
    }
}
