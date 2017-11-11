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
            @Override
            public void onClick(long overageTime) {
                countTimeProgressView.cancelCountTimeAnimation();
                startActivity(new Intent(SplashActivity.this, SimpleActivity.class));
                finish();
            }

        });

/*        countTimeProgressView.setBackgroundColorCenter(Color.WHITE);
        countTimeProgressView.setBorderWidth(3);
        countTimeProgressView.setBorderBottomColor(Color.GRAY);
        countTimeProgressView.setBorderDrawColor(Color.RED);
        countTimeProgressView.setMarkBallColor(Color.GREEN);

        countTimeProgressView.setMarkBallFlag(true);
        countTimeProgressView.setMarkBallWidth(4);
        countTimeProgressView.setTitleCenterText("");
        countTimeProgressView.setTitleCenterTextSize(16);
        countTimeProgressView.setTitleCenterTextColor(Color.BLACK);

        countTimeProgressView.setCountTime(5000L);
        countTimeProgressView.setStartAngle(0);
        countTimeProgressView.setTextStyle(CountTimeProgressView.TextStyle.INSTANCE.getCLOCK());
        countTimeProgressView.setClockwise(true);*/

        countTimeProgressView.startCountTimeAnimation();
        //            gradlew install
//            gradlew bintrayUpload
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
