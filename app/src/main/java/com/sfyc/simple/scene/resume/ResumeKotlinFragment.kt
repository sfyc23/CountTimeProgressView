package com.sfyc.simple.scene.resume

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.sfyc.ctpv.CountTimeProgressView
import com.sfyc.simple.R

/**
 * 进度恢复场景 — Kotlin + XML 实现。
 *
 * 展示从服务器剩余时间恢复倒计时的 Kotlin 惯用写法。
 */
class ResumeKotlinFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_resume_progress, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ctpv = view.findViewById<CountTimeProgressView>(R.id.ctpv_resume)
        val tvLog = view.findViewById<TextView>(R.id.tv_log)
        val tvRemaining = view.findViewById<TextView>(R.id.tv_remaining_val)
        val seekRemaining = view.findViewById<SeekBar>(R.id.seek_remaining)

        // Kotlin with DSL 配置
        with(ctpv) {
            countTime = 60_000L
            textStyle = CountTimeProgressView.TEXT_STYLE_CLOCK
            titleCenterTextSize = 16f
            borderWidth = 5f
            borderDrawColor = Color.parseColor("#6750A4")
            borderBottomColor = Color.parseColor("#E0E0E0")
            backgroundColorCenter = Color.WHITE
            titleCenterTextColor = Color.parseColor("#1C1B1F")
            markBallFlag = false
            strokeCap = android.graphics.Paint.Cap.ROUND
            bindLifecycle(viewLifecycleOwner)

            setOnStateChangedListener { state ->
                tvLog.append("State: ${state.name}\n")
            }

            addOnProgressChangedListener { progress, _ ->
                tvLog.append("Progress: ${"%.2f".format(progress)}\n")
            }
        }

        // 剩余时间滑块
        seekRemaining.max = 60
        seekRemaining.progress = 42
        tvRemaining.text = "42s"
        seekRemaining.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) tvRemaining.text = "${progress}s"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        // 从服务器剩余时间恢复
        view.findViewById<View>(R.id.btn_from_remaining).setOnClickListener {
            val remaining = seekRemaining.progress * 1000L
            tvLog.text = "→ startFromRemaining(${remaining}ms)\n"
            ctpv.startCountTimeAnimationFromRemaining(remaining)
        }

        // 从 50% 进度恢复
        view.findViewById<View>(R.id.btn_from_50).setOnClickListener {
            tvLog.text = "→ startCountTimeAnimation(0.5f)\n"
            ctpv.startCountTimeAnimation(0.5f)
        }

        // 屏幕旋转恢复提示
        view.findViewById<View>(R.id.btn_rotate_hint).setOnClickListener {
            tvLog.append("提示: 先开始倒计时，然后旋转屏幕，进度将自动恢复\n")
            tvLog.append("（SavedState 机制，无需额外代码）\n")
        }
    }
}
