package com.duobeiyun.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzip {

    private static final int BUF_SIZE = 1024;

    public static void unzip(File zip, File targetDir) throws IOException {
        InputStream in = new FileInputStream(zip);
        unzip(in, targetDir);
        in.close();
    }

    public static void unzip(InputStream in, File targetDir) throws IOException {
        final ZipInputStream zipIn = new ZipInputStream(in);
        final byte[] b = new byte[BUF_SIZE];
        ZipEntry zipEntry;
        while ((zipEntry = zipIn.getNextEntry()) != null) {
            String zipEntryName = zipEntry.getName().replace("\\", "");
            final File file = new File(targetDir, zipEntryName);
            if (!zipEntry.isDirectory()) {
                final File parent = file.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(file);
                int r;
                while ((r = zipIn.read(b)) != -1) {
                    fos.write(b, 0, r);
                }
                fos.close();
            } else {
                file.mkdirs();
            }
            zipIn.closeEntry();
        }
    }
}
