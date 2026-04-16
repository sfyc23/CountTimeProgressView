package com.sfyc.simple.scene.exam

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sfyc.ctpv.CountTimeProgressView
import com.sfyc.simple.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 考试计时器场景 — Kotlin + XML 实现。
 *
 * 展示 Kotlin with DSL 风格的长时间倒计时 + 警告阈值配置。
 */
class ExamKotlinFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_exam_timer, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ctpv = view.findViewById<CountTimeProgressView>(R.id.ctpv_exam)
        val tvStatus = view.findViewById<TextView>(R.id.tv_exam_status)
        val tvLog = view.findViewById<TextView>(R.id.tv_exam_log)
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        with(ctpv) {
            countTime = 300_000L  // 5 分钟演示
            textStyle = CountTimeProgressView.TEXT_STYLE_CLOCK
            titleCenterTextSize = 20f
            borderWidth = 6f
            borderDrawColor = Color.parseColor("#6750A4")
            borderBottomColor = Color.parseColor("#E0E0E0")
            backgroundColorCenter = Color.WHITE
            titleCenterTextColor = Color.parseColor("#1C1B1F")
            markBallFlag = false
            strokeCap = android.graphics.Paint.Cap.ROUND

            // 最后 60 秒进度条变红
            warningTime = 60_000L
            warningColor = Color.parseColor("#FF3B30")

            bindLifecycle(viewLifecycleOwner)

            setOnStateChangedListener { state ->
                tvStatus.text = when (state.name) {
                    "RUNNING" -> "考试状态: 进行中"
                    "PAUSED" -> "考试状态: 已暂停"
                    "FINISHED" -> "考试状态: 已结束"
                    else -> "考试状态: ${state.name}"
                }
            }

            setOnWarningListener { _ ->
                tvLog.append("${sdf.format(Date())} ⚠ 还剩1分钟！\n")
            }

            setOnCountdownEndListener {
                tvLog.append("${sdf.format(Date())} 考试时间到，自动提交\n")
                Toast.makeText(requireContext(), "考试结束", Toast.LENGTH_SHORT).show()
            }
        }

        // 开始考试
        view.findViewById<View>(R.id.btn_start_exam).setOnClickListener {
            tvLog.text = "${sdf.format(Date())} 考试开始\n"
            ctpv.startCountTimeAnimation()
        }

        // 暂停考试
        view.findViewById<View>(R.id.btn_pause_exam).setOnClickListener {
            ctpv.pauseCountTimeAnimation()
            tvLog.append("${sdf.format(Date())} 考试暂停\n")
        }

        // 继续考试
        view.findViewById<View>(R.id.btn_resume_exam).setOnClickListener {
            ctpv.resumeCountTimeAnimation()
            tvLog.append("${sdf.format(Date())} 考试继续\n")
        }
    }
}
