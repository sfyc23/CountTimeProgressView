package com.sfyc.simple

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sfyc.ctpv.CountTimeProgressView

class SplashActivity : AppCompatActivity() {

    private lateinit var countTimeProgressView: CountTimeProgressView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        countTimeProgressView = findViewById(R.id.countTimeProgressView1)
        countTimeProgressView.bindLifecycle(this)

        countTimeProgressView.setOnCountdownEndListener {
            navigateToMain()
        }

        countTimeProgressView.setOnClickCallback {
            countTimeProgressView.cancelCountTimeAnimation()
            navigateToMain()
        }

        countTimeProgressView.startCountTimeAnimation()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
