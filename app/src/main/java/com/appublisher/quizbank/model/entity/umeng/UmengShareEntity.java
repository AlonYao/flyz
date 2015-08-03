package com.appublisher.quizbank.model.entity.umeng;

import android.app.Activity;
import android.graphics.Bitmap;

/**
 * 友盟分享实体类
 */
public class UmengShareEntity {
    Activity activity;
    Bitmap bitmap;
    String content;
    String url;
    String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
