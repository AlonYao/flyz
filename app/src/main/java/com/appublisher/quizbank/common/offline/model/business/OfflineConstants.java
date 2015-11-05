package com.appublisher.quizbank.common.offline.model.business;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 离线模块参数
 */
public class OfflineConstants {

    public static final int DONE = 0;
    public static final int WAITING = 1;
    public static final int PROGRESS = 2;

    public static ArrayList<HashMap<String, Object>> mDownloadList;

    public static int mPercent;

    public static String mCurDownloadRoomId;

    public static long mLastTimestamp;

    public static int mStatus;

}
