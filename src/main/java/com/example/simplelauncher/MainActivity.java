package com.example.simplelauncher;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.system.Os;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final int REQ_PICK_WIDGET = 1;

    private ImageButton mContactButton;
    private ImageButton mGalleryButton;
    private AppWidgetManager mAppWidgetManager;
    private AppWidgetHost    mAppWidgetHost;
    private int mPreviousAppWidgetID;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppWidgetManager = (AppWidgetManager) getSystemService(APPWIDGET_SERVICE);
        mAppWidgetHost    = new AppWidgetHost(this, Os.getpid());

        mContactButton = findViewById(R.id.contacts);
        mContactButton.setOnClickListener(this);

        mGalleryButton = findViewById(R.id.gallery);
        mGalleryButton.setOnClickListener(this);

        hideStatusBarNavigationBar();
        Drawable drawable = WallpaperManager.getInstance(this).getDrawable();
        findViewById(android.R.id.content).setBackground(drawable);
        initAppWidget();

        PermissionsUtil.checkAndRequestPermissions(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppWidgetHost.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAppWidgetHost.stopListening();
    }

    @Override
    public void onClick(View v) {
        if(v.equals(mContactButton)){
            startActivity(new Intent(this,ContactsActivity.class));
        }else if(v.equals(mGalleryButton)){
            startActivity(new Intent(this,GalleryActivity.class));
        }
        overridePendingTransition(R.anim.scale_up,R.anim.scale_down);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_PICK_WIDGET){
            if(resultCode == RESULT_OK){
                FrameLayout fl = findViewById(R.id.host_view);
                try{
                    int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                    AppWidgetProviderInfo appWidget = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
                    AppWidgetHostView hostView = mAppWidgetHost.createView(this, appWidgetId, appWidget);
                    hostView.setAppWidget(appWidgetId,appWidget);
                    fl.addView(hostView);
                    String scn = appWidget.provider.getPackageName()+"/"+appWidget.provider.getClassName();
                    getSharedPreferences("pref",0).edit().
                            putInt("widget_id",appWidgetId).
                            putString("componentName",scn).apply();
                }catch (SecurityException e){
                    Log.e("TEST","e:"+e.getMessage());
                    addFallbackView(fl);
                }
            }else if(resultCode == RESULT_CANCELED){
                mAppWidgetHost.deleteAppWidgetId(mPreviousAppWidgetID);
            }
        }
    }

    private void hideStatusBarNavigationBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    private void initAppWidget(){
        final SharedPreferences sf = getSharedPreferences("pref",0);
        final int widget_id = sf.getInt("widget_id", -1);
        String componentName = sf.getString("componentName",null);

        if(widget_id != -1 && componentName != null){//widget saved
            String[] spilt = componentName.split("/");
            if(spilt.length == 2){
                ComponentName cn = new ComponentName(spilt[0],spilt[1]);
                final AppWidgetProviderInfo info = loadWidgetInfo(cn);
                if(info == null){
                    pickAppWidget();
                }else{
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addWidgetIntoView(sf,widget_id,info);
                        }
                    },200);
                }
            }else{
                pickAppWidget();
            }
        }else{//no widget saved
            pickAppWidget();
        }
    }

    private AppWidgetProviderInfo loadWidgetInfo(ComponentName componentName){
        List<AppWidgetProviderInfo> installedProviders = mAppWidgetManager.getInstalledProviders();
        for(AppWidgetProviderInfo info : installedProviders){
            if(info.provider.equals(componentName)){
                return info;
            }
        }
        return null;
    }

    private void pickAppWidget(){
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        int widgetID = mAppWidgetHost.allocateAppWidgetId();
        mPreviousAppWidgetID = widgetID;
        pickIntent.putExtra
                (AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        startActivityForResult(pickIntent,REQ_PICK_WIDGET);
    }

    private void addFallbackView(ViewGroup parent){
        float sp = getResources().getDisplayMetrics().scaledDensity;
        TextView tv = new TextView(this);
        tv.setText("无法显示该部件");
        tv.setTextSize(16*sp);
        if(parent instanceof FrameLayout){
            FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER);
            parent.addView(tv,flp);
        }else{
            parent.addView(tv);
        }
    }

    private void addWidgetIntoView(SharedPreferences sf,int widget_id,AppWidgetProviderInfo info){
        FrameLayout fl = findViewById(R.id.host_view);
        try{
            AppWidgetHostView hostView = mAppWidgetHost.createView(this, widget_id, info);
            hostView.setAppWidget(widget_id,info);
            fl.addView(hostView);
        }catch (SecurityException e){
            sf.edit().putInt("pref",-1).putString("componentName",null).apply();
            addFallbackView(fl);
        }
    }

    @Override
    public void onBackPressed() {
        //root view, no finish
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TEST","Main resume :"+getTaskId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("TEST","Main Destory");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("TEST","Main onNewIntent");
        overridePendingTransition(R.anim.scale_up,R.anim.scale_down);
    }
}
