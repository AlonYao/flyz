package com.appublisher.quizbank.model.netdata.hierarchy;

import java.util.ArrayList;

/**
 * 知识点层级接口数据模型（第一层）
 */
public class HierarchyM {
    int id;
    String name;
    int parent_id;
    int note_type;
    int total;
    int done;
    ArrayList<HierarchyM> childs;

    int category_id;
    int level;
    ArrayList<NoteGroupM> note_group;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getParent_id() {
        return parent_id;
    }

    public int getNote_type() {
        return note_type;
    }

    public int getTotal() {
        return total;
    }

    public int getDone() {
        return done;
    }

    public ArrayList<HierarchyM> getChilds() {
        return childs;
    }

    public int getCategory_id() {
        return category_id;
    }

    public int getLevel() {
        return level;
    }

    public ArrayList<NoteGroupM> getNote_group() {
        return note_group;
    }
}
