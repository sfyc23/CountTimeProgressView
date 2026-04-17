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

            setOnTickListener { _, remainingSeconds ->
                btnResend.text = getString(R.string.action_resend_with_seconds, remainingSeconds)
                tvLog.append("Tick: ${remainingSeconds}s\n")
            }

            setOnWarningListener {
                tvLog.append("${getString(R.string.log_warning_last_10_seconds)}\n")
            }

            setOnCountdownEndListener {
                btnResend.isEnabled = true
                btnResend.text = getString(R.string.action_resend)
                tvLog.append("${getString(R.string.log_countdown_finished_resend)}\n")
            }
        }

        btnSend.setOnClickListener {
            tvLog.text = ""
            tvLog.append("${getString(R.string.log_sending_code)}\n")
            btnResend.isEnabled = false
            btnResend.text = getString(R.string.action_resend_initial)
            ctpv.startCountTimeAnimation()
        }
    }
}
