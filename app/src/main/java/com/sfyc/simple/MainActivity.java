package com.sfyc.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.sfyc.ctpv.CountTimeProgressView;

public class MainActivity extends AppCompatActivity {

    private CountTimeProgressView countTimeProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        countTimeProgressView = (CountTimeProgressView) findViewById(R.id.countTimeProgressView);
        countTimeProgressView.addOnEndListener(new CountTimeProgressView.OnEndListener() {
            @Override
            public void onAnimationEnd() {

                startActivity(new Intent(MainActivity.this,SimpleActivity.class));
                finish();
            }
//            gradlew bintrayUpload

            @Override
            public void onClick(long overageTime) {
                startActivity(new Intent(MainActivity.this,SimpleActivity.class));
                finish();
            }

        });
        countTimeProgressView.startCountTimeAnimation();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countTimeProgressView != null && countTimeProgressView.isRunning()) {
            countTimeProgressView.cancelCountTimeAnimation();
        }
    }
}
