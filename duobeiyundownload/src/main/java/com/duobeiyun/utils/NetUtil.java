package com.duobeiyun.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class NetUtil {

    public static HttpURLConnection buildConnection(String url) throws IOException {
        return buildConnection(url, false);
    }

    public static HttpURLConnection buildConnection(String url, boolean isAlive) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(HttpConnParameter.POST.content);
        connection.setConnectTimeout(Integer.parseInt(HttpConnParameter.CONNECT_TIMEOUT.content));
        connection.setRequestProperty(HttpConnParameter.ACCEPT.header, HttpConnParameter.ACCEPT.content);
        connection.setRequestProperty(HttpConnParameter.ACCEPT_RANGE.header, HttpConnParameter.ACCEPT_RANGE.content);
        connection.setRequestProperty(HttpConnParameter.ACCEPT_LANGUAGE.header, HttpConnParameter.ACCEPT_LANGUAGE.content);
        connection.setRequestProperty(HttpConnParameter.CHARSET.header, HttpConnParameter.CHARSET.content);
        if (isAlive) {
            connection.setRequestProperty(HttpConnParameter.KEEP_CONNECT.header, HttpConnParameter.KEEP_CONNECT.content);
        }
        return connection;
    }

    /**
     * 获取网络类型
     *
     * @param context ...
     * @return 网络类型ID {@link Constants.NetType}
     */
    public static int getNetWorkType(Context context) {
        int type = Constants.NetType.INVALID;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String typeName = networkInfo.getTypeName();
            if (typeName.equalsIgnoreCase("WIFI")) {
                type = Constants.NetType.WIFI;
            } else if (typeName.equalsIgnoreCase("MOBILE")) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                type = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ?
                        Constants.NetType.G3 : Constants.NetType.G2) :
                        Constants.NetType.WAP;
            }
        }
        return type;
    }

    public static String get(String getUrl){
        HttpURLConnection conn = null;

        new Runnable() {
            @Override
            public void run() {

            }
        };
        try {
            URL url = new URL(getUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setUseCaches(false);
            if (conn.getResponseCode() == 200) {
                InputStream is = new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                is.close();
                os.close();
                String result = new String(os.toByteArray());
                return result;
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return "";
    }


    public static String getLocalIPAddress() {
        String ipAddress = "";
        try {
            Enumeration<NetworkInterface> netfaces = NetworkInterface.getNetworkInterfaces();
            // 遍历所用的网络接口
            while (netfaces.hasMoreElements()) {
                NetworkInterface nif = netfaces.nextElement();// 得到每一个网络接口绑定的地址
                Enumeration<InetAddress> inetAddresses = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inetAddresses.hasMoreElements()) {
                    InetAddress ip = inetAddresses.nextElement();
                    if (!ip.isLoopbackAddress() && isIPv4Address(ip.getHostAddress())) {
                        ipAddress = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipAddress;
    }

    /**
     * Ipv4地址检查
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
    /**
     * 检查是否是有效的IPV4地址
     * @param input the address string to check for validity
     * @return true if the input parameter is a valid IPv4 address
     */
    public static boolean isIPv4Address(final String input) {
        return IPV4_PATTERN.matcher(input).matches();
    }

    /**
     * 判断是否是3G+的移动网络
     *
     * @param context ...
     * @return ...
     */
    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }
}
