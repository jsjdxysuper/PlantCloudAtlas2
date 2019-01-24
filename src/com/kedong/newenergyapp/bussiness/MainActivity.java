package com.kedong.newenergyapp.bussiness;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.kedong.newenergyapp.R;
import com.kedong.newenergyapp.service.CheckNewsIntentService;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import ezy.boost.update.IUpdateParser;
import ezy.boost.update.UpdateInfo;
import ezy.boost.update.UpdateManager;


public class MainActivity extends Activity {
    protected static final String TAG = "MainActivity";
    public static List<UserPage> listUserPage;
    private View currentButton;

    int currentPageIndex = 0;
    List<Fragment_Two> fragList = new ArrayList<Fragment_Two>();
    List<View>btnLayoutList = new ArrayList<View>();
    List<String>btnText = new ArrayList<String>();
    List<Integer>btnImg = new ArrayList<Integer>();
    List<String>webViewUrl = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Intent intentServ = new Intent(this, CheckNewsIntentService.class);
//        startService(intentServ);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

        Button btnFresh = (Button)findViewById(R.id.btnFresh);
        btnFresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressWebView progressWebView = (ProgressWebView) fragList.get(currentPageIndex).getView().findViewById(R.id.webview);
                progressWebView.reload();
            }
        });
        Button btnSet = (Button)findViewById(R.id.btnSet);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                UpdateManager.create(view.getContext()).setUrl( getApplication().getString(R.string.update_check_url)).setParser(new IUpdateParser() {
//                    @Override
//                    public UpdateInfo parse(String source) throws Exception {
//                        JSONObject jo = new JSONObject(source);
//                        UpdateInfo info = new UpdateInfo();// todo
//                        info.md5 = jo.getString("md5");
//                        info.size = jo.getInt("size");
//                        info.updateContent = jo.getString("updatecontent");
//                        info.url = jo.getString("url");
//                        info.versionCode = jo.getInt("versioncode");
//                        info.versionName = jo.getString("versionname");
//
//                        if(Utility.getVersionCode(view.getContext())<info.versionCode){
//                            info.hasUpdate = true;
//                            info.isForce = jo.getInt("isforce")==1?true:false;
//                            info.isSilent = jo.getInt("issilent")==1?true:false;
//                            info.isAutoInstall = jo.getInt("isautoinstall")==1?true:false;
//                            info.isIgnorable = jo.getInt("isignorable")==1?true:false;
//                            info.maxTimes = jo.getInt("maxtimes");
//                        }
//
//                        return info;
//                    }
//                }).setWifiOnly(false).check();
            }
        });
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int phoneWidth = dm.widthPixels;
        int phoneHeight = dm.heightPixels;

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        for(int i=0;i<listUserPage.size();i++){
            Fragment_Two fragment_two = new Fragment_Two();

            fragList.add(fragment_two);
            fragList.get(i).setIndex(i);
            fragList.get(i).setUrl(listUserPage.get(i).getPageUrl());
            ft.add(R.id.fl_content, fragList.get(i),""+i);


            LinearLayout btnLayout = (LinearLayout)findViewById(R.id.buttom_bar_group);
            RelativeLayout rl = (RelativeLayout)View.inflate(MainActivity.this, R.layout.bottom_button, null);
            btnLayoutList.add(rl);
            Button imgBtn = (Button)rl.findViewById(R.id.buttom_two);
            imgBtn.setTag(i);
            imgBtn.setText(listUserPage.get(i).getPagename());
            btnLayout.addView(rl);
            final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
            int heightll = (int)(60 * scale + 0.5f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(phoneWidth/listUserPage.size(),heightll);
            rl.setLayoutParams(params);
            btnText.add("按钮"+(i+1));

            btnImg.add(R.drawable.bar_news);

            webViewUrl.add("我是URL"+(i+1));
        }
//        ft.show(fm.findFragmentByTag(0+""));
        ft.commitAllowingStateLoss();
        fm.executePendingTransactions();
        initComponents();

    }

    /**
     * 已经放入fragmentList中的fragment已经显示的是否有现在要传入的fragment
     * @param fragment_two
     * @return
     */
//    public boolean hasThisFragment(Fragment_Two fragment_two){
//        FragmentManager fm = getFragmentManager();
//        fm.
//        List<Fragment> frList = fm.getFragments();
//        int fragIndexThis = fragment_two.getIndex();
//        if(frList==null)return false;
//        for(int i=0;i<frList.size();i++) {
//            int fragIndexTemp = ((Fragment_Two)frList.get(i)).getIndex();
//            if (fragIndexTemp==fragIndexThis)
//                return true;
//        }
//        return false;
//    }

    /**
     * 隐藏所有显示过的fragment
     * @param ft
     * @param fm
     */
    public void hideFragment(FragmentTransaction ft, FragmentManager fm) {
        //如果不为空，就先隐藏起来ft.

            for (int i = 0; i < fragList.size(); i++) {
                Fragment_Two fragment_two = (Fragment_Two)fm.findFragmentByTag(i+"");
                ft.hide(fragment_two);
            }

    }
    @Override
    protected void onStart() {
        super.onStart();
        for(currentPageIndex=0;currentPageIndex<listUserPage.size();currentPageIndex++){
            Button imgBtn = (Button)btnLayoutList.get(currentPageIndex).findViewById(R.id.buttom_two);
            imgBtn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View v) {
                    int btnIndex = (Integer)((Button)v).getTag();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    hideFragment(ft,fm);
//                    if(!hasThisFragment(fragList.get(btnIndex))){
//                        ft.add(R.id.fl_content, fragList.get(btnIndex));
//                    }else{
//                        ft.show(fragList.get(btnIndex));
//                    }
                    Fragment_Two fragment_two = (Fragment_Two)fm.findFragmentByTag(btnIndex+"");
                    if(listUserPage.get(btnIndex).isFirstLoad()){
                        ProgressWebView progressWebView = (ProgressWebView) fragList.get(btnIndex).getView().findViewById(R.id.webview);
                        progressWebView.reload();
                        listUserPage.get(btnIndex).setFirstLoad(false);
                    }

                    ft.show(fragment_two);
                    ft.commitAllowingStateLoss();
                    fm.executePendingTransactions();

                    setButton(v);
                }
            });
        }
        /**
         * 默认第一个按钮点击
         */
        btnLayoutList.get(0).findViewById(R.id.buttom_two).performClick();
    }
    private void initComponents() {
    }

    /**
     * 设置按钮的背景图片
     *
     * @param v
     */
    private void setButton(View v) {
//        v.setEnabled(false);
        v.setSelected(true);
        int btnIndex = (Integer)v.getTag();
        for(int i=0;i<listUserPage.size();i++){
            if(btnIndex!=i){
                btnLayoutList.get(i).findViewById(R.id.buttom_two).setSelected(false);
            }
        }
        currentButton = v;
        currentPageIndex = btnIndex;
    }


}
