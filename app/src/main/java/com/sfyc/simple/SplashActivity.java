package com.sfyc.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.sfyc.ctpv.CountTimeProgressView;


public class SplashActivity extends AppCompatActivity {

    private CountTimeProgressView countTimeProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        countTimeProgressView = (CountTimeProgressView) findViewById(R.id.countTimeProgressView1);
        countTimeProgressView.addOnEndListener(new CountTimeProgressView.OnEndListener() {
            @Override
            public void onAnimationEnd() {
                Log.e("Main", "onAnimationEnd");
                startActivity(new Intent(SplashActivity.this, SimpleActivity.class));
                finish();
            }
//            gradlew install
//            gradlew bintrayUpload

            @Override
            public void onClick(long overageTime) {
                countTimeProgressView.cancelCountTimeAnimation();
                startActivity(new Intent(SplashActivity.this, SimpleActivity.class));
                finish();
            }

        });
        countTimeProgressView.startCountTimeAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
