#include <cstdio>
#include <cstdlib>
#include <jni.h>
#include <sys/system_properties.h>
#include <media/NdkMediaDrm.h>
#include <cstring>
#include <android/log.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <net/if.h>
#include <unistd.h>
#include <linux/in.h>
#include <sys/endian.h>



#include "com_example_app1_fingerprintjni.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_app1_fingerprintjni_fingerprint(JNIEnv *env, jobject thiz){

    const char *properties[] = {
            "ro.build.fingerprint",
            "ro.build.build.fingerprint",
            "ro.bootimage.build.fingerprint",
            "ro.odm.build.fingerprint",
            "ro.product.build.fingerprint",
            "ro.system_ext.build.fingerprint",
            "ro.system.build.fingerprint",
            "ro.vendor.build.fingerprint",
            "ro.build.description",

            "ro.product.board",
            "ro.bootloader",
            "ro.product.brand",
            "ro.product.cpu.abi",
            "ro.product.device",
            "ro.build.display.id",
            "ro.hardware",
            "ro.build.host",
            "ro.build.id",
            "ro.product.model",
            "ro.product.manufacturer",
            "ro.product.name",
            "ro.product.radio",
            "ro.build.tags",
            "ro.build.time",
            "ro.build.type",
            "ro.build.user",
            "ro.build.version.release",
            "ro.build.version.codename",
            "ro.build.version.incremental",
            "ro.build.version.sdk",
            "ro.build.version.sdk_int"
    };

    int numProperties = sizeof(properties) / sizeof(properties[0]);
    char *result = (char *)malloc(4096); // 分配足够的内存以存储结果
    if (result == nullptr) {
        return nullptr; // 处理内存分配失败的情况
    }
    result[0] = '\0'; // 初始化字符串

    for (int i = 0; i < numProperties; i++) {
        char value[PROP_VALUE_MAX] = {0};
        __system_property_get(properties[i], value);

        if (strlen(value) == 0) {
            snprintf(value, sizeof(value), "无结果");
        }

        // 将属性名和属性值合并到结果字符串中
        snprintf(result + strlen(result), 4096 - strlen(result), "\n%s: %s", properties[i], value);
    }



    /*
    jclass contextClass = (*env).GetObjectClass(thiz);
    jmethodID getSystemService = (*env).GetMethodID(contextClass, "getSystemService", "(Ljava/lang/String;)Ljava/lang/Object;");
    jstring wifiService = (*env).NewStringUTF("wifi");
    jobject wifiManager = (*env).CallObjectMethod(thiz, getSystemService, wifiService);

    // 获取WifiManager类的class
    jclass wifiManagerClass = (*env).GetObjectClass(wifiManager);
    jmethodID getConnectionInfo = (*env).GetMethodID(wifiManagerClass, "getConnectionInfo", "()Landroid/net/wifi/WifiInfo;");

    // 获取WifiInfo对象
    jobject wifiInfo = (*env).CallObjectMethod(wifiManager, getConnectionInfo);

    // 获取WifiInfo类的class
    jclass wifiInfoClass = (*env).GetObjectClass(wifiInfo);
    jmethodID getMacAddress = (*env).GetMethodID(wifiInfoClass, "getMacAddress", "()Ljava/lang/String;");

    // 获取MAC地址
    jstring macAddress = (jstring)(*env).CallObjectMethod(wifiInfo, getMacAddress);

     */

    int sock;
    struct ifreq ifr;

    sock = socket(AF_INET, SOCK_DGRAM, 0);
    strcpy(ifr.ifr_name, "wlan0");
    ioctl(sock, SIOCGIFHWADDR, &ifr);
    close(sock);

    unsigned char* mac = reinterpret_cast<unsigned char*>(ifr.ifr_hwaddr.sa_data);

    char macAddress[18];
    sprintf(macAddress, "%02x:%02x:%02x:%02x:%02x:%02x",
            mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);

    sprintf(result,"%s\n%s", result,macAddress);

    // 创建 Java 字符串并返回
    jstring jResult = (*env).NewStringUTF(result);
    // 清理
    free(result); // 释放分配的内存

    return jResult; // 返回合并后的字符串
}

JNIEXPORT jstring JNICALL Java_com_example_app1_fingerprintjni_check(JNIEnv *env, jobject){

    const char* result1 = "检测到frida服务器端口";
    const char* result2 = "未检测到frida服务器端口";

    struct sockaddr_in sa{};
    // 创建一个socket文件描述符
    int sock;
    // 定义一个字符数组res，用于存储接收到的数据
    char res[7];
    // 循环遍历所有可能的端口号
    for(int i = 27042; i <= 27042; i++) {
        // 创建一个新的socket连接
        sock = socket(AF_INET, SOCK_STREAM, 0);
        // 设置socket地址结构体的端口号
        sa.sin_port = htons(i);
        // 尝试连接到当前端口
        if (connect(sock, (struct sockaddr*)&sa, sizeof(sa)) != -1) {
            // 如果连接成功，记录日志信息，表示发现了一个开放的端口
            //__android_log_print(ANDROID_LOG_VERBOSE, "ZJ595", "FRIDA DETECTION [1]: Open Port: %d", i);
            // 初始化res数组，清零
            memset(res, 0, 7);
            // 向socket发送一个空字节
            send(sock, "\x00", 1, 0); // 注意这里的NULL被替换为0
            // 发送AUTH请求
            send(sock, "AUTH\r\n", 6, 0);
            // 等待100微秒
            usleep(100);
            // 尝试接收响应
            if (recv(sock, res, 6, MSG_DONTWAIT) != -1) {
                // 如果接收到响应，检查响应内容是否为"REJECT"
                if (strcmp(res, "REJECT") == 0) {
                    // 如果是，关闭socket并返回true，表示检测到了Frida服务器
                    close(sock);
                    jstring jResult = (*env).NewStringUTF(result1);
                    return jResult; // Frida server detected
                }
            }
        }
        // 如果当前端口连接失败或没有检测到Frida服务器，关闭socket
        close(sock);
    }
    // 如果遍历完所有端口都没有检测到Frida服务器
    jstring jResult = (*env).NewStringUTF(result2);
    return jResult; // No Frida server detected
};

JNIEXPORT jstring JNICALL Java_com_example_app1_fingerprintjni_mapscheck(JNIEnv * env, jobject){
    char line[512];
    const char* result1 = "检测到frida特征文件";
    const char* result2 = "未检测到frida特征文件";
    const char* result3 = "系统状态异常";
    // 打开当前进程的内存映射文件/proc/self/maps进行读取
    FILE* fp = fopen("/proc/self/maps", "r");
    if (fp) {
        // 如果文件成功打开，循环读取每一行
        while (fgets(line, sizeof(line), fp)) {
            // 使用strstr函数检查当前行是否包含"frida"字符串
            if (strstr(line, "frida") || strstr(line, "gadget")) {
                // 如果找到了"frida"，关闭文件并返回true，表示检测到了恶意库
                fclose(fp);
                jstring jResult = (*env).NewStringUTF(result1);
                return jResult; // Evil library is loaded.
            }
        }
        // 遍历完文件后，关闭文件
        fclose(fp);
    } else {
        //如果无法打开文件，记录错误。这可能意味着系统状态异常
        //没有处理错误
        jstring jResult = (*env).NewStringUTF(result3);
        return jResult;
    }
    // 如果没有在内存映射文件中找到"frida"，表示没有检测到恶意库
    jstring jResult = (*env).NewStringUTF(result2);
    return jResult; // No evil library detected.
};
