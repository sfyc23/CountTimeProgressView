package com.sfyc.simple

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.sfyc.ctpv.CountTimeProgressView

class SimpleActivity : AppCompatActivity() {

    private lateinit var countTimeProgressView: CountTimeProgressView
    private var checkedItem = 0
    private var strokeCapIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple)
        countTimeProgressView = findViewById(R.id.countTimeProgressView)

        setupTextStyleSelector()
        setupSeekBars()
        setupSwitches()
        setupStrokeCapSelector()
        initCountTimeView()
    }

    private fun setupTextStyleSelector() {
        findViewById<View>(R.id.tv_countTime_style).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_count_time_style)
                .setSingleChoiceItems(arrayOf("JUMP", "SECOND", "CLOCK", "NONE"), checkedItem) { dialog, which ->
                    checkedItem = which
                    when (which) {
                        0 -> {
                            countTimeProgressView.textStyle = CountTimeProgressView.TEXT_STYLE_JUMP
                            countTimeProgressView.titleCenterText = getString(R.string.text_skip)
                        }
                        1 -> {
                            countTimeProgressView.textStyle = CountTimeProgressView.TEXT_STYLE_SECOND
                            countTimeProgressView.titleCenterText =
                                getString(R.string.countdown_skip_seconds_template)
                        }
                        2 -> countTimeProgressView.textStyle = CountTimeProgressView.TEXT_STYLE_CLOCK
                        3 -> countTimeProgressView.textStyle = CountTimeProgressView.TEXT_STYLE_NONE
                    }
                    countTimeProgressView.startCountTimeAnimation()
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun setupStrokeCapSelector() {
        val capNames = arrayOf("BUTT", "ROUND", "SQUARE")
        val caps = arrayOf(Paint.Cap.BUTT, Paint.Cap.ROUND, Paint.Cap.SQUARE)
        val tvStrokeCap = findViewById<View>(R.id.tv_stroke_cap)

        tvStrokeCap.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_stroke_cap)
                .setSingleChoiceItems(capNames, strokeCapIndex) { dialog, which ->
                    strokeCapIndex = which
                    countTimeProgressView.strokeCap = caps[which]
                    countTimeProgressView.startCountTimeAnimation()
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun setupSeekBars() {
        findViewById<SeekBar>(R.id.seek_bar_countTime).setOnSeekBarChangeListener(
            onStopListener { progress ->
                countTimeProgressView.countTime = progress.coerceAtLeast(1) * 1000L
                countTimeProgressView.startCountTimeAnimation()
            }
        )

        findViewById<SeekBar>(R.id.seek_bar_start_angle).setOnSeekBarChangeListener(
            onStopListener { progress ->
                countTimeProgressView.startAngle = progress.toFloat()
                countTimeProgressView.startCountTimeAnimation()
            }
        )

        findViewById<SeekBar>(R.id.seekbar_ball_width).setOnSeekBarChangeListener(
            onStopListener { progress ->
                countTimeProgressView.markBallWidth = progress.toFloat()
            }
        )

        findViewById<SeekBar>(R.id.seekbar_border_width).setOnSeekBarChangeListener(
            onStopListener { progress ->
                countTimeProgressView.borderWidth = progress.toFloat()
            }
        )
    }

    private fun setupSwitches() {
        findViewById<SwitchMaterial>(R.id.sb_ball_flag).setOnCheckedChangeListener { _, isChecked ->
            countTimeProgressView.markBallFlag = isChecked
        }

        findViewById<SwitchMaterial>(R.id.sb_clockwise).setOnCheckedChangeListener { _, isChecked ->
            countTimeProgressView.clockwise = isChecked
            countTimeProgressView.startCountTimeAnimation()
        }

        findViewById<SwitchMaterial>(R.id.sb_gradient).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                countTimeProgressView.setGradientColors(
                    Color.parseColor("#FF6B6B"),
                    Color.parseColor("#4ECDC4")
                )
            } else {
                countTimeProgressView.gradientStartColor = 0
                countTimeProgressView.gradientEndColor = 0
                countTimeProgressView.borderDrawColor = Color.RED
            }
            countTimeProgressView.startCountTimeAnimation()
        }
    }

    private fun initCountTimeView() {
        with(countTimeProgressView) {
            startAngle = 0f
            countTime = 6000L
            textStyle = CountTimeProgressView.TEXT_STYLE_SECOND
            borderWidth = 4f
            borderBottomColor = Color.GRAY
            borderDrawColor = Color.RED
            backgroundColorCenter = Color.WHITE
            markBallFlag = true
            markBallWidth = 6f
            markBallColor = Color.GREEN
            titleCenterText = getString(R.string.countdown_skip_seconds_template)
            titleCenterTextColor = Color.BLACK
            titleCenterTextSize = 16f

            warningTime = 3000L
            warningColor = Color.parseColor("#FF3B30")
            clickableAfterMillis = 2000L
            disabledText = getString(R.string.text_please_wait)

            setOnStateChangedListener { state ->
                Log.d(TAG, "State changed: $state")
            }

            setOnTickListener { remainingMillis, remainingSeconds ->
                Log.d(TAG, "Tick: ${remainingSeconds}s ($remainingMillis ms)")
            }

            setOnWarningListener { remainingMillis ->
                Log.d(TAG, "Warning threshold reached, remaining=${remainingMillis}ms")
            }

            setOnCountdownEndListener {
                Toast.makeText(this@SimpleActivity, getString(R.string.toast_times_up), Toast.LENGTH_SHORT).show()
            }

            setOnClickCallback { overageTime ->
                if (isRunning) {
                    cancelCountTimeAnimation()
                    Log.d(TAG, "Click canceled, remaining=$overageTime ms")
                } else {
                    startCountTimeAnimation()
                }
            }

            addOnProgressChangedListener { progress, remainingMillis ->
                Log.d(TAG, "progress=$progress, remaining=$remainingMillis")
            }

            bindLifecycle(this@SimpleActivity)
            startCountTimeAnimation()
        }
    }

    private fun onStopListener(action: (Int) -> Unit): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) = Unit
            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                action(seekBar.progress)
            }
        }
    }

    companion object {
        private const val TAG = "SimpleActivity"
    }
}
