package com.sfyc.simple

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sfyc.ctpv.CountTimeProgressView

/**
 * 闪屏页（Kotlin + XML 实现）。
 *
 * 展示 CountTimeProgressView 的倒计时跳过功能：
 * - 5 秒倒计时，前 2 秒显示"请稍候"不可点击
 * - 2 秒后显示"跳过"，点击可直接跳转
 * - 倒计时结束自动跳转到 [MainActivity]
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var countTimeProgressView: CountTimeProgressView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        countTimeProgressView = findViewById(R.id.countTimeProgressView1)

        // 绑定生命周期，进入后台时自动暂停
        countTimeProgressView.bindLifecycle(this)

        // 倒计时自然结束 → 跳转到导航首页
        countTimeProgressView.setOnCountdownEndListener {
            navigateToMain()
        }

        // 用户点击跳过 → 取消动画并跳转
        countTimeProgressView.setOnClickCallback { _ ->
            countTimeProgressView.cancelCountTimeAnimation()
            navigateToMain()
        }

        countTimeProgressView.startCountTimeAnimation()
    }

    /** 跳转到导航首页并结束闪屏页 */
    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
