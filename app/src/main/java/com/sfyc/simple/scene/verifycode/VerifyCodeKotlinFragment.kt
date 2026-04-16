package com.sfyc.simple.scene.verifycode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.sfyc.ctpv.CountTimeProgressView
import com.sfyc.simple.R

/**
 * 验证码倒计时场景 — Kotlin + XML 实现。
 *
 * 与 [VerifyCodeJavaFragment] 功能一致，展示 Kotlin with DSL 风格。
 */
class VerifyCodeKotlinFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_verify_code, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ctpv = view.findViewById<CountTimeProgressView>(R.id.ctpv_verify)
        val btnResend = view.findViewById<Button>(R.id.btn_resend)
        val btnSend = view.findViewById<Button>(R.id.btn_send)
        val tvLog = view.findViewById<TextView>(R.id.tv_tick_log)

        with(ctpv) {
            bindLifecycle(viewLifecycleOwner)

            // 每秒 Tick 回调
            setOnTickListener { _, remainingSeconds ->
                btnResend.text = "重新发送 (${remainingSeconds}s)"
                tvLog.append("Tick: ${remainingSeconds}s\n")
            }

            // 警告回调
            setOnWarningListener { _ ->
                tvLog.append("⚠ 最后10秒！\n")
            }

            // 倒计时结束
            setOnCountdownEndListener {
                btnResend.isEnabled = true
                btnResend.text = "重新发送"
                tvLog.append("✅ 倒计时结束，可重新发送\n")
            }
        }

        // 发送验证码
        btnSend.setOnClickListener {
            tvLog.text = ""
            tvLog.append("→ 发送验证码...\n")
            btnResend.isEnabled = false
            btnResend.text = "重新发送 (60s)"
            ctpv.startCountTimeAnimation()
        }
    }
}
