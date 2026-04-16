package com.sfyc.simple.animation

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sfyc.ctpv.CountTimeProgressView
import com.sfyc.simple.R

/**
 * 动画控制页 — Kotlin + XML 实现。
 *
 * 展示 Kotlin with DSL 风格的属性批量配置方式。
 */
class AnimationKotlinFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_animation_control, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ctpv = view.findViewById<CountTimeProgressView>(R.id.ctpv_anim)
        val tvState = view.findViewById<TextView>(R.id.tv_state)
        val tvProgress = view.findViewById<TextView>(R.id.tv_progress)
        val tvTick = view.findViewById<TextView>(R.id.tv_tick)

        // Kotlin 风格：with DSL 批量配置
        with(ctpv) {
            countTime = 10_000L
            textStyle = CountTimeProgressView.TEXT_STYLE_CLOCK
            titleCenterTextSize = 16f
            borderWidth = 5f
            borderDrawColor = Color.parseColor("#6750A4")
            borderBottomColor = Color.parseColor("#E8DEF8")
            backgroundColorCenter = Color.parseColor("#FAFAFA")
            titleCenterTextColor = Color.parseColor("#1C1B1F")
            markBallFlag = true
            markBallWidth = 8f
            markBallColor = Color.parseColor("#6750A4")
            strokeCap = Paint.Cap.ROUND

            bindLifecycle(viewLifecycleOwner)

            setOnStateChangedListener { state ->
                tvState.text = "状态: ${state.name}"
            }

            addOnProgressChangedListener { progress, remainingMillis ->
                tvProgress.text = "进度: ${"%.2f".format(progress)} | 剩余: ${"%.1f".format(remainingMillis / 1000f)}s"
            }

            setOnTickListener { remainingMillis, remainingSeconds ->
                tvTick.text = "Tick: ${remainingSeconds}s (${remainingMillis}ms)"
            }

            setOnCountdownEndListener {
                Toast.makeText(requireContext(), "倒计时结束", Toast.LENGTH_SHORT).show()
            }
        }

        // 控制按钮
        view.findViewById<View>(R.id.btn_start).setOnClickListener { ctpv.startCountTimeAnimation() }
        view.findViewById<View>(R.id.btn_pause).setOnClickListener { ctpv.pauseCountTimeAnimation() }
        view.findViewById<View>(R.id.btn_resume).setOnClickListener { ctpv.resumeCountTimeAnimation() }
        view.findViewById<View>(R.id.btn_reset).setOnClickListener { ctpv.resetCountTimeAnimation() }
    }
}
