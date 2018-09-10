package com.kedong.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.pm.newenergyapp.LoadingActivity;
import com.pm.newenergyapp.LoginActivity;

import java.util.Timer;
import java.util.TimerTask;

public class BaseActivity extends Activity {

    // 都是static声明的变量，避免被实例化多次；因为整个app只需要一个计时任务就可以了。
    private static Timer mTimer; // 计时器，每1秒执行一次任务
    private static MyTimerTask mTimerTask; // 计时任务，判断是否未操作时间到达5s
    private static long mLastActionTime; // 上一次操作时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // 每当用户接触了屏幕，都会执行此方法
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mLastActionTime = System.currentTimeMillis();
        Log.e("wanghang", "user action");
        return super.dispatchTouchEvent(ev);
    }

    private  class MyTimerTask extends TimerTask {
        private Activity inAc;
        public MyTimerTask(Activity iii){
            inAc = iii;
        }
        @Override
        public void run() {
            Log.e("wanghang", "check time");
            // 5s未操作
            if (System.currentTimeMillis() - mLastActionTime > 1000*60*20) {
                // 退出登录
//                Intent intent = new Intent();
//                intent.setClass(inAc,LoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(intent);
//                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
//                LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(LaunchIntent);


                Intent intent = new Intent(inAc, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivity(intent);
//                inAc.finish();
                // 停止计时任务
                stopTimer();
            }
        }
    }

    // 退出登录
    protected static void exit() {
    }

    // 登录成功，开始计时
    protected  void startTimer() {
        mTimer = new Timer();
        mTimerTask = new MyTimerTask(this);
        // 初始化上次操作时间为登录成功的时间
        mLastActionTime = System.currentTimeMillis();
        // 每过1s检查一次
        mTimer.schedule(mTimerTask, 0, 1000);
        Log.e("wanghang", "start timer");
    }

    // 停止计时任务
    protected static void stopTimer() {
        mTimer.cancel();
        Log.e("wanghang", "cancel timer");
    }
}