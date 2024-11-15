// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("app1");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("app1")
//      }
//    }

#include <stdio.h>
#include <stdlib.h>
#include <jni.h>

extern "C";
JNIEXPORT jstring JNICALLJava_com_example_app1_fingerprintjni_fingerprint(JNIEnv *env, jobject thiz) {

    // TODO: implement fingerprint()
    return env->NewStringUTF("text");
}