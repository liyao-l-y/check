LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := app1
LOCAL_SRC_FILES =: app1.cpp
include $(BUILD_SHARED_LIBRARY)