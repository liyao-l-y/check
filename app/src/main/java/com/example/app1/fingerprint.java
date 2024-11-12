package com.example.app1;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class fingerprint {
    //-----------------------------------------------设备------------------------------------------------------
    public String getDeviceID(ContentResolver contentResolver) {
        String s = "设备指纹：\n";
        try {
            // 获取ANDROID_ID
            @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
            Log.d("DeviceFingerprint", "Android ID: " + androidId);
            if (androidId != null)
                s += "Android ID：" + androidId + "\n";
        } catch (Exception e) {
            Log.e("DeviceFingerprint", "Error retrieving device fingerprint: " + e.getMessage());
        }

        s += getUUID() + "\n";

        return s;
    }

    public String getUUID(){
        String s = "UUID：";
        StringBuilder n = new StringBuilder();
        List<String> l = getSystemProperties2();
        for (String property : l){
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(property);
            while (matcher.find()) {
                n.append(matcher.group());
            }
        }
        BigInteger a = new BigInteger(n.toString());
        BigInteger b = new BigInteger(Build.TIME + Build.VERSION.INCREMENTAL);
        BigInteger uuid = a.divideAndRemainder(b)[1];
        s += uuid.toString(16);
        return s;
    }

    //-----------------------------------------------网络------------------------------------------------------
    public String getLocalMacAddress() throws SocketException {
        String Addr = "网络地址：\n";
        try {
            InetAddress ip = getLocalInetAddress();
            Addr += "当前ip地址:" + ip.toString() + "\n";
            System.out.println("当前ip地址:" + ip);
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
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            //避免多张网卡
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = en_netInterface.nextElement();//得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
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

    //-----------------------------------------------系统------------------------------------------------------
    public String getSystemProperties() {
        try {
            @SuppressLint("PrivateApi") Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            StringBuilder s = new StringBuilder("系统指纹：\n");

            String[] properties = {
                    "ro.build.fingerprint",
                    //"ro.build.build.fingerprint",
                    //"ro.bootimage.build.fingerprint",
                    "ro.odm.build.fingerprint",
                    "ro.product.build.fingerprint",
                    "ro.system_ext.build.fingerprint",
                    "ro.system.build.fingerprint",
                    "ro.vendor.build.fingerprint",
                    "ro.build.description"
            };

            for (String property : properties) {
                String value = (String) get.invoke(c, property);
                Log.d("DeviceProperties", property + ": \n" + value);
                s.append(property).append(":").append(value).append("\n");
            }
            List<String> p = getSystemProperties2();
            for (String property : p){
                s.append(property).append("\n");
            }
            return s.toString();
        } catch (Exception e) {
            Log.e("DeviceProperties", "Error retrieving system properties: " + e.getMessage());
        }
        return "系统属性获取失败";

    }

    public List<String> getSystemProperties2(){
        List<String> properties = new ArrayList<>();
        properties.add("BOARD:" + Build.BOARD);
        properties.add("BOOTLOADER:" + Build.BOOTLOADER);
        properties.add("BRAND:" + Build.BRAND);
        properties.add("CPU_ABI:" + Build.CPU_ABI);
        properties.add("DEVICE:" + Build.DEVICE);
        properties.add("DISPLAY:" + Build.DISPLAY);
        properties.add("HARDWARE:" + Build.HARDWARE);
        properties.add("HOST:" + Build.HOST);
        properties.add("ID:" + Build.ID);
        properties.add("MODEL:" + Build.MODEL);
        properties.add("MANUFACTURER:" + Build.MANUFACTURER);
        properties.add("PRODUCT:" + Build.PRODUCT);
        properties.add("RADIO:" + Build.RADIO);
        properties.add("TAGS:" + Build.TAGS);
        properties.add("TIME:" + Build.TIME);
        properties.add("TYPE):" + Build.TYPE);
        properties.add("USER:" + Build.USER);
        properties.add("RELEASE:" + Build.VERSION.RELEASE);
        properties.add("CODENAME:" + Build.VERSION.CODENAME);
        properties.add("INCREMENTAL:" + Build.VERSION.INCREMENTAL);
        properties.add("SDK:" + Build.VERSION.SDK);
        properties.add("SDK_INT:" + Build.VERSION.SDK_INT);
        return properties;
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