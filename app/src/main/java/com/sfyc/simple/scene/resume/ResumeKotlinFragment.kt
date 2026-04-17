package com.sfyc.simple.scene.resume

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.sfyc.ctpv.CountTimeProgressView
import com.sfyc.simple.R

class ResumeKotlinFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_resume_progress, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ctpv = view.findViewById<CountTimeProgressView>(R.id.ctpv_resume)
        val tvLog = view.findViewById<TextView>(R.id.tv_log)
        val tvRemaining = view.findViewById<TextView>(R.id.tv_remaining_val)
        val seekRemaining = view.findViewById<SeekBar>(R.id.seek_remaining)

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
            strokeCap = Paint.Cap.ROUND
            bindLifecycle(viewLifecycleOwner)

            setOnStateChangedListener { state ->
                tvLog.append("State: ${state.name}\n")
            }

            addOnProgressChangedListener { progress, _ ->
                tvLog.append("Progress: ${"%.2f".format(progress)}\n")
            }
        }

        seekRemaining.max = 60
        seekRemaining.progress = 42
        tvRemaining.text = "42s"
        seekRemaining.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) tvRemaining.text = "${progress}s"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
        })

        view.findViewById<View>(R.id.btn_from_remaining).setOnClickListener {
            val remaining = seekRemaining.progress * 1000L
            tvLog.text = "${getString(R.string.log_start_from_remaining, remaining)}\n"
            ctpv.startCountTimeAnimationFromRemaining(remaining)
        }

        view.findViewById<View>(R.id.btn_from_50).setOnClickListener {
            tvLog.text = "${getString(R.string.log_start_from_half)}\n"
            ctpv.startCountTimeAnimation(0.5f)
        }

        view.findViewById<View>(R.id.btn_rotate_hint).setOnClickListener {
            tvLog.append("${getString(R.string.log_rotation_hint_line_1)}\n")
            tvLog.append("${getString(R.string.log_rotation_hint_line_2)}\n")
        }
    }
}
