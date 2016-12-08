package com.appublisher.quizbank.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_course.opencourse.activity.OpenCourseActivity;
import com.igexin.sdk.PushConsts;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by bihaitian on 16/4/7.
 */
public class PushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Logger.i("getui action=" + bundle.getInt("action"));
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用通常需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送。
                // 部分特殊情况下CID可能会发生变化，为确保应用服务端保存的最新的CID，应用程序在每次获取CID广播后，如果发现CID出现变化，需要重新进行一次关联绑定
                String cid = bundle.getString("clientid");
                Logger.i("getui Got CID:" + cid);
                break;
            case PushConsts.GET_MSG_DATA:
                // 获取透传（payload）数据
                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String data = new String(payload);
                    Logger.i("getui Got Payload:" + data);
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        String type = jsonObject.optString("type");
                        if ("openCourse".equals(type)) {
                            final Intent intent1 = new Intent(context, OpenCourseActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent1);

                            // Umeng
                            HashMap<String, String> map = new HashMap<>();
                            map.put("Type", "OpenCourse");
                            UmengManager.onEvent(context, "Push", map);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // TODO:接收处理透传（payload）数据
                }
                break;
            default:
                break;
        }
    }
}
