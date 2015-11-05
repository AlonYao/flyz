package com.appublisher.quizbank.dao;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.appublisher.quizbank.common.login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.utils.GsonManager;
import com.google.gson.Gson;

/**
 * 用户表DAO层
 */
public class UserDAO {

    /**
     * 查询用户数据
     * @return  用户数据
     */
    public static User findById() {
        try {
            return new Select().from(User.class)
                    .where("Id = ?", 1)
                    .executeSingle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 插入数据
     * @param user  用户信息
     * @param exam  考试项目
     */
    public static void insert(String user, String exam) {
        User item = new User();
        item.user = user;
        item.exam = exam;
        item.save();
    }

    /**
     * 更新数据
     * @param user 用户信息
     * @param exam 考试项目
     */
    public static void update(String user, String exam) {
        try {
            new Update(User.class)
                    .set("user = ?, exam = ?", user, exam)
                    .where("Id = ?", 1)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存数据
     * @param user  用户信息
     * @param exam  考试项目
     */
    public static void save(String user, String exam) {
        User item = findById();

        if (item != null) {
            update(user, exam);
        } else {
            insert(user, exam);
        }
    }

    /**
     * 更新考试项目信息
     * @param exam  考试项目信息
     */
    public static void updateExamInfo(String exam) {
        try {
            new Update(User.class)
                    .set("exam = ?", exam)
                    .where("Id = ?", 1)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新个人信息
     * @param user 用户个人信息
     */
    public static void updateUserInfo(String user) {
        try {
            new Update(User.class)
                    .set("user = ?", user)
                    .where("Id = ?", 1)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新学号
     * @param sno 学号
     */
    public static void updateSno(long sno) {
        try {
            User user = findById();
            if (user != null) {
                Gson gson = new Gson();
                String sUser = user.user;
                if (sUser != null && !sUser.equals("")) {
                    UserInfoModel userInfoModel = gson.fromJson(sUser, UserInfoModel.class);
                    userInfoModel.setSno(sno);
                    sUser = gson.toJson(userInfoModel);
                    updateUserInfo(sUser);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新手机号
     * @param mobileNum 手机号
     */
    public static void updateMobileNum(String mobileNum) {
        if (mobileNum == null || mobileNum.length() == 0) return;

        User user = findById();

        if (user == null) return;

        Gson gson = GsonManager.initGson();
        UserInfoModel userInfo = gson.fromJson(user.user, UserInfoModel.class);

        if (userInfo == null) return;

        userInfo.setMobile_num(mobileNum);

        String userString = gson.toJson(userInfo);

        updateUserInfo(userString);
    }
}
