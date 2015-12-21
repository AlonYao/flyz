package com.appublisher.quizbank.model.netdata.globalsettings;

/**
 * Created  on 15/12/15.
 */
public class NewVersion {
    String app_version;
    String size;
    String notice_text;
    boolean force_update;
    String target_url;

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getNotice_text() {
        return notice_text;
    }

    public void setNotice_text(String notice_text) {
        this.notice_text = notice_text;
    }

    public Boolean getForce_update() {
        return force_update;
    }

    public void setForce_update(boolean force_update) {
        this.force_update = force_update;
    }

    public String getTarget_url() {
        return target_url;
    }

    public void setTarget_url(String target_url) {
        this.target_url = target_url;
    }

    @Override
    public String toString() {
        return "{" +
                "app_version:'" + app_version + '\'' +
                ", size:'" + size + '\'' +
                ", notice_text:'" + notice_text + '\'' +
                ", force_update:" + force_update +
                ", target_url:'" + target_url + '\'' +
                '}';
    }
}
