package com.example.app1;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class fingerprint {
    public  String getDeviceID(ContentResolver contentResolver) {
        String s = "设备指纹：\n";
        try {
            // 获取小米健康ID
            String miHealthId = Settings.Global.getString(contentResolver, "mi_health_id");
            Log.d("DeviceFingerprint", "Mi Health ID: " + miHealthId);
            if (miHealthId != null)
                s += "Mi Health ID: " + miHealthId + "\n";

            // 获取GC Booster UUID
            String gcboosterUUID = Settings.Global.getString(contentResolver, "gcbooster_uuid");
            Log.d("DeviceFingerprint", "GC Booster UUID: " + gcboosterUUID);
            if (gcboosterUUID != null)
                s += "GC Booster UUID: " + gcboosterUUID + "\n";

            // 获取MQS UUID
            String keyMqsUUID = Settings.Global.getString(contentResolver, "key_mqs_uuid");
            Log.d("DeviceFingerprint", "Key MQS UUID: " + keyMqsUUID);
            if (keyMqsUUID != null)
                s += "Key MQS UUID: " + keyMqsUUID + "\n";

            // 获取广告ID
            String adAaid = Settings.Global.getString(contentResolver, "ad_aaid");
            Log.d("DeviceFingerprint", "Ad AAID: " + adAaid);
            if (adAaid != null)
                s += "Ad AAID: " + adAaid + "\n";

            // 获取ANDROID_ID
            @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
            Log.d("DeviceFingerprint", "Android ID: " + androidId);
            if (androidId != null)
                s += "Android ID: " + androidId + "\n";

        } catch (Exception e) {
            Log.e("DeviceFingerprint", "Error retrieving device fingerprint: " + e.getMessage());
        }


        return s;
    }


    public String getLocalMacAddress() throws SocketException {
        String Addr = "";
        try {
            InetAddress ip = getLocalInetAddress();
            System.out.println(ip);
            Addr += "\n当前ip地址:" + ip.toString() + "\n";
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            System.out.println(NetworkInterface.getByInetAddress(ip));
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i]&0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            Addr += "Mac地址:" + buffer.toString().toLowerCase() + "\n";
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Addr;
    }


    protected InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration en_netInterface = NetworkInterface.getNetworkInterfaces();
            //避免多张网卡
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Enumeration en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = (InetAddress) en_ip.nextElement();
                    System.out.println(ip);
                    if (!ip.isLoopbackAddress() && ip instanceof java.net.Inet4Address)
                        break;
                    else
                        ip = null;
                }
                System.out.println(ip);
                if (ip != null) {
                    break;
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return ip;
    }


    public String getSystemProperties() {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            StringBuilder s = new StringBuilder("\n系统:\n");

            String[] properties = {
                    "ro.build.fingerprint",
                    "ro.build.build.fingerprint",
                    "ro.bootimage.build.fingerprint",
                    "ro.odm.build.fingerprint",
                    "ro.product.build.fingerprint",
                    "ro.system_ext.build.fingerprint",
                    "ro.system.build.fingerprint",
                    "ro.vendor.build.fingerprint",
                    "ro.build.description"
            };

            for (String property : properties) {
                String value = (String) get.invoke(c, property);
                Log.d("DeviceProperties", property + ": " + value);
                s.append(property).append(":").append(value).append("\n");
            }
            return s.toString();
        } catch (Exception e) {
            Log.e("DeviceProperties", "Error retrieving system properties: " + e.getMessage());
        }
        return "系统属性获取失败";

    }


    public void getAccounts(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccounts();
        for (Account account : accounts) {
            System.out.println(account);
            Log.d("AccountManager", "Account: " + account.name + " Type: " + account.type);
        }
    }


}
