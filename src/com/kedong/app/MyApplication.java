package com.kedong.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by ding on 2018/6/28.
 */

public class MyApplication extends Application {
    public int count = 0;

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Toast.makeText(getApplicationContext(),("程序切换到后台，如不是您操作，可能被劫持\n"), Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStopped(Activity activity) {
                Log.v("viclee", activity + "onActivityStopped");
                count--;
                if (count == 0) {
                    Log.v("viclee", ">>>>>>>>>>>>>>>>>>>切到后台  lifecycle");

                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.v("viclee", activity + "onActivityStarted");
                if (count == 0) {
                    Log.v("viclee", ">>>>>>>>>>>>>>>>>>>切到前台  lifecycle");
                }
                count++;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.v("viclee", activity + "onActivitySaveInstanceState");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.v("viclee", activity + "onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.v("viclee", activity + "onActivityPaused");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.v("viclee", activity + "onActivityDestroyed");
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.v("viclee", activity + "onActivityCreated");
            }
        });
    }
}
