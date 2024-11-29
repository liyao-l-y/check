#include <cstdio>
#include <cstdlib>
#include <jni.h>
#include <sys/system_properties.h>
#include <media/NdkMediaDrm.h>
#include <cstring>

#include "com_example_app1_fingerprintjni.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_app1_fingerprintjni_fingerprint(JNIEnv *env, jobject){

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

    // 创建 Java 字符串并返回
    jstring jResult = (*env).NewStringUTF(result);

    // 清理
    free(result); // 释放分配的内存

    return jResult; // 返回合并后的字符串
}
