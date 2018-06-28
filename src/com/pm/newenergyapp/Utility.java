package com.pm.newenergyapp;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import org.apache.http.cookie.Cookie;

import java.util.Locale;

/**
 * Created by ding on 2017/10/27.
 */

public class Utility {
    public static String getUnicId(Context context){
        String androidId=  getAndroidId(context);
        String serialNO= getSerialNO();
        String model = getSystemModel().replaceAll("-","_");
        String version = getSystemVersion().replaceAll("-","_");
        String brand = getDeviceBrand().replaceAll("-","_");
//        return androidId+"-"+serialNO+"-"+model+"-"+brand;
        return serialNO+"-"+model+"-"+brand;
    }

    public static String getSerialNO(){
        return Build.SERIAL;
    }
    /**
     * 获取android id
     * @return
     */
    public static String getAndroidId(Context context){
        return Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
    }
    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return  语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return  手机IMEI
     */
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getDeviceId();
        }
        return null;
    }
}
