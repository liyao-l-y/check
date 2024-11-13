package com.example.app1;

public class fingerprint_jni {

    static {
        System.loadLibrary("app1");
    }

    public native String fingerprint();

}
