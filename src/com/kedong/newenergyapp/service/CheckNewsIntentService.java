package com.kedong.newenergyapp.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.kedong.newenergyapp.R;
import com.kedong.newenergyapp.bussiness.LoginActivity;
import com.kedong.newenergyapp.bussiness.MainActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class CheckNewsIntentService extends Service {
    MqttMessage message;
    private MqttProxy mqqtProxy;
    private SharedPreferences sharedPrefer;
    private String dateTime;
    private String title;
    private String content;


    Handler showMsgHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 0x111) {
                notific(title,content);
            }else if(msg.what==0x201){
                notific("通知",msg.obj.toString());
            }
        }
    };




    /**
     * 播放系统默认提示音
     *
     * @return MediaPlayer对象
     *
     * @throws Exception
     */
    public void defaultMediaPlayer() throws Exception {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(this, notification);
        r.play();
    }

    public boolean notifyOrNot(String newsDate){
        boolean ret = false;
        sharedPrefer = getSharedPreferences(getString(R.string.projectStore),this.MODE_PRIVATE);
        String lastNewsDate = sharedPrefer.getString("lastNewsDate","1990-01-01 00:00:01");
        if(lastNewsDate.compareToIgnoreCase(newsDate)<0){
            ret = true;
            SharedPreferences.Editor editor = sharedPrefer.edit();
            editor.putString("lastNewsDate",newsDate);
            editor.commit();
        }
        return ret;
    }


    public void getNewsInfo(){
        new Thread(){
            public void run(){
                URL url = null;
                BufferedReader reader = null;
                HttpURLConnection connection = null;
                try {
                    url = new URL(getApplication().getString(R.string.latestNews));
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.connect();
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    String newsStr = response.toString();
                    JSONObject jo = new JSONObject(newsStr);
                    String newsDate = jo.getString("timeC");
                    connection.disconnect();
                    if(notifyOrNot(newsDate)){
                        content = jo.getString("contentC");
                        title = jo.getString("titleC");

                        Message msg = new Message();
                        msg.what = 0x111;
                        msg.obj = "";
                        showMsgHandler.sendMessage(msg);
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null){
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null){
                        connection.disconnect();
                    }
                }

            }
        }.start();
    }

    public void notific(String title,String content){
        NotificationManager manager;
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(CheckNewsIntentService.this);
        builder.setSmallIcon(R.drawable.login_error_icon);
        builder.setTicker("World");
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(title);
        builder.setContentText(content);

        Intent broadcastIntent = new Intent(getApplicationContext(), MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.
                getBroadcast(getApplicationContext(), 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);//设置点击过后跳转的activity
        builder.setDefaults(Notification.DEFAULT_SOUND);//设置声音
        builder.setDefaults(Notification.DEFAULT_LIGHTS);//设置指示灯
        builder.setDefaults(Notification.DEFAULT_VIBRATE);//设置震动
        builder.setDefaults(Notification.DEFAULT_ALL);//设置全部
        Notification notification = builder.build();//4.1以上用.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;// 点击通知的时候cancel掉
        int notification_id = 10086;
        manager.notify(notification_id,notification);
        Toast.makeText(this,("电厂云图有新的通知\n"), Toast.LENGTH_LONG).show();
        //要执行的事件
        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(3000);
        try {
            defaultMediaPlayer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        flags =  Service.START_STICKY;
//        super.onStartCommand(intent,flags,startId);
      //  handler.postDelayed(runnable, 1000*5);
        mqqtProxy = new MqttProxy(getApplicationContext(),showMsgHandler);
        mqqtProxy.setMqtt();

        Log.w("ssss","===========================================onStartCommand");
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this,("service要销毁了\n"), Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}
