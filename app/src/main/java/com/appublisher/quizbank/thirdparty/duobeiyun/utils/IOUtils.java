package com.appublisher.quizbank.thirdparty.duobeiyun.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by liuguolin on 25/3/2016.
 */
public class IOUtils {
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
}
