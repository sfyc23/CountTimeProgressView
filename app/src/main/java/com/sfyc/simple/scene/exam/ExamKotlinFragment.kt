package com.sfyc.simple.scene.exam

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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExamKotlinFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_exam_timer, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ctpv = view.findViewById<CountTimeProgressView>(R.id.ctpv_exam)
        val tvStatus = view.findViewById<TextView>(R.id.tv_exam_status)
        val tvLog = view.findViewById<TextView>(R.id.tv_exam_log)
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        with(ctpv) {
            countTime = 300_000L
            textStyle = CountTimeProgressView.TEXT_STYLE_CLOCK
            titleCenterTextSize = 20f
            borderWidth = 6f
            borderDrawColor = Color.parseColor("#6750A4")
            borderBottomColor = Color.parseColor("#E0E0E0")
            backgroundColorCenter = Color.WHITE
            titleCenterTextColor = Color.parseColor("#1C1B1F")
            markBallFlag = false
            strokeCap = Paint.Cap.ROUND
            warningTime = 60_000L
            warningColor = Color.parseColor("#FF3B30")
            bindLifecycle(viewLifecycleOwner)

            setOnStateChangedListener { state ->
                tvStatus.text = when (state.name) {
                    "RUNNING" -> getString(R.string.exam_status_running)
                    "PAUSED" -> getString(R.string.exam_status_paused)
                    "FINISHED" -> getString(R.string.exam_status_finished)
                    else -> getString(R.string.exam_status_format, state.name)
                }
            }

            setOnWarningListener {
                tvLog.append("${sdf.format(Date())} ${getString(R.string.log_exam_one_minute_left)}\n")
            }

            setOnCountdownEndListener {
                tvLog.append("${sdf.format(Date())} ${getString(R.string.log_exam_time_up)}\n")
                Toast.makeText(requireContext(), getString(R.string.toast_exam_finished), Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<View>(R.id.btn_start_exam).setOnClickListener {
            tvLog.text = "${sdf.format(Date())} ${getString(R.string.log_exam_started)}\n"
            ctpv.startCountTimeAnimation()
        }

        view.findViewById<View>(R.id.btn_pause_exam).setOnClickListener {
            ctpv.pauseCountTimeAnimation()
            tvLog.append("${sdf.format(Date())} ${getString(R.string.log_exam_paused)}\n")
        }

        view.findViewById<View>(R.id.btn_resume_exam).setOnClickListener {
            ctpv.resumeCountTimeAnimation()
            tvLog.append("${sdf.format(Date())} ${getString(R.string.log_exam_resumed)}\n")
        }
    }
}
