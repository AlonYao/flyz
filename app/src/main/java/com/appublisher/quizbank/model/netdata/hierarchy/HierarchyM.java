package com.appublisher.quizbank.model.netdata.hierarchy;

import java.util.ArrayList;

/**
 * 知识点层级接口数据模型（第一层）
 */
public class HierarchyM {
    int id;
    String name;
    int parent_id;
    int total;
    int done;
    ArrayList<HierarchyM> childs;
    int level;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getParent_id() {
        return parent_id;
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

    public int getLevel() {
        return level;
    }
}
