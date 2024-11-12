package com.example.app1;


import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyInfo;
import android.security.keystore.KeyProperties;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;


public class rootcheck {
    //root检测
    //检查SU命令能否执行
    public boolean checkSuCommand() {
        Process process = null;
        Process process1 = null;
        try {
            // 尝试运行su命令
            process = Runtime.getRuntime().exec("which su" );
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = in.readLine();
            System.out.println(line);
            if (line != null) {
                return true; // su命令执行成功，可能被Root
            }
            return false;
        } catch (Exception e) {
            System.out.println(e);
            return false; // 执行su命令失败，可能未Root
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
    //检查root文件是否存在
    public boolean checkRootFiles() {
        String[] rootFilesPaths = {
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/bin/su",
                "/magisk",
                "/data/adb/magisk",
        };
        for (String path : rootFilesPaths) {
            if (new File(path).exists()) {
                System.out.println(path);
                return true; // 发现Root相关文件
            }
        }
        return false;
    }
    //检查系统属性
    public boolean checkSystemTags() {
        try {
            // 读取ro.build.tags系统属性
            String buildTags = Build.TAGS;
            System.out.println(buildTags);
            if (buildTags != null && !buildTags.equals("release-keys")) {
                return true; // 设备使用了带有test-keys签名的固件，通常是Root设备
            }
        } catch (Exception e) {
            // 读取系统属性失败
        }
        return false;
    }
    //检查分区读写模式
    public boolean checkMountInfo() {
        try {
            Process process = Runtime.getRuntime().exec("mount");
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine())!= null) {

                if (line.contains("system") && line.contains("rw")) {


                    return true; // /system分区被挂载为读写模式，可能是Root设备
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    //检查系统属性
    public boolean checkSystemProperty() {
        String debuggable = getSystemProperty("ro.debuggable");
        String secure = getSystemProperty("ro.secure");
        System.out.println("ro.debuggable:"+debuggable);
        System.out.println("ro.secure:"+secure);
        if(debuggable.equals("1") || secure.equals("0")){
            return true;
        }
        return false;
    }
    //获取系统属性值。
    private String getSystemProperty(String key) {
        String value = null;
        try {
            Process process = Runtime.getRuntime().exec("getprop " + key);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            value = reader.readLine();
        } catch (Exception e) {
            System.out.println(e);
        }
        return value;
    }
    //检测SELinux状态
    public String checkSELinuxStatus() {
        String selinuxStatus = getSELinuxStatus();
        System.out.println(selinuxStatus);
        if (selinuxStatus.equalsIgnoreCase("disabled") || selinuxStatus.equalsIgnoreCase("permissive")) {
            return "y"; // SELinux处于禁用或宽松模式，可能是Root设备
        }else {
            if (selinuxStatus.equalsIgnoreCase("unknown"))
                return "unknown";
        }
        return "n";
    }
    //获取SELinux状态
    private String getSELinuxStatus() {
        try {
            Process process = Runtime.getRuntime().exec("getenforce");
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            System.out.println(in.readLine());
            return in.readLine().trim(); // 返回SELinux的状态
        } catch (Exception e) {
            System.out.println(e);
            return "unknown"; // 检测失败
        }
    }
    //检测bootloader
    public static final String TAG = "BootloaderChecker";
    public static boolean isBootloaderUnlocked() {
        boolean isUnlocked = false;

        try {
            Process process = Runtime.getRuntime().exec("getprop ro.boot.verifiedbootstate");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            if ((line = reader.readLine()) != null) {
                Log.d(TAG, "Bootloader state: " + line);
                // "orange" 表示Bootloader解锁; "green" 表示Bootloader锁定
                if ("orange".equalsIgnoreCase(line)) {
                    isUnlocked = true;
                }
            }
            reader.close();
            process.waitFor();
        } catch (Exception e) {
            Log.e(TAG, "Error checking bootloader status", e);
        }
        return isUnlocked;
    }
    //检测TEE状态
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public boolean isDeviceLocked() {
        try {
            // 从 Android Keystore 中获取密钥
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            // 生成一个非对称密钥对（RSA）如果还没有的话
            if (!keyStore.containsAlias("rsa_test_key")) {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
                keyPairGenerator.initialize(
                        new KeyGenParameterSpec.Builder(
                                "rsa_test_key",
                                KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                                .build());
                keyPairGenerator.generateKeyPair();
            }
            // 获取生成的密钥对
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("rsa_test_key", null);
            // 使用 KeyFactory 获取 KeyInfo
            KeyFactory keyFactory = KeyFactory.getInstance(privateKeyEntry.getPrivateKey().getAlgorithm(), "AndroidKeyStore");
            KeyInfo keyInfo = keyFactory.getKeySpec(privateKeyEntry.getPrivateKey(), KeyInfo.class);
            // 检查密钥是否存储在安全硬件中（即 TEE 中）
            System.out.println(keyInfo.isInsideSecureHardware());
            return keyInfo.isInsideSecureHardware();
        } catch (NoSuchAlgorithmException | CertificateException | IOException |
                 InvalidKeySpecException | NoSuchProviderException |
                 InvalidAlgorithmParameterException | UnrecoverableEntryException |
                 java.security.KeyStoreException e) {
            e.printStackTrace();
            return false;
        }
    }
}
