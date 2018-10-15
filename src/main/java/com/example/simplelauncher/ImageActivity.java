package com.example.simplelauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ImageActivity extends FragmentActivity {
    private static final String TAG = "ImageActivity";
    private String[] mImageAddr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        PhotoViewPager viewPager = findViewById(R.id.image_pager);
        mImageAddr = getIntent().getStringArrayExtra("image_address");
        int position = getIntent().getIntExtra("position",0);
        if(mImageAddr == null){
            Log.e(TAG,"no image input");
            return;
        }
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(position);

        hideStatusBarNavigationBar();

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenOffReceiver,intentFilter);
    }

    private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null) return;

            if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScreenOffReceiver);
    }

    private FragmentStatePagerAdapter mPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            String photoname = mImageAddr[position];
            BigImageFragment fragment = new BigImageFragment();
            Bundle bundle = new Bundle();
            bundle.putString("photoname",photoname);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return mImageAddr.length;
        }
    };

    @Override
    public void onBackPressed() {
        PhotoViewPager viewPager = findViewById(R.id.image_pager);
        int index = viewPager.getCurrentItem();
        Intent intent = new Intent();
        intent.putExtra("position",index);
        setResult(RESULT_OK,intent);
        finishAfterTransition();
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
}
