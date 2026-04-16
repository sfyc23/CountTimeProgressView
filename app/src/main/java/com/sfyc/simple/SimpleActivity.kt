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

/**
 * 演示界面，展示 CountTimeProgressView 的各项属性配置和交互效果。
 * 仅使用 AndroidX / Material 原生控件，不依赖任何过时第三方库。
 */
class SimpleActivity : AppCompatActivity() {

    private lateinit var countTimeProgressView: CountTimeProgressView
    /** 当前选中的文本样式索引 */
    private var checkedItem = 0
    /** 当前选中的 StrokeCap 索引 */
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

    /** 文本样式选择对话框 */
    private fun setupTextStyleSelector() {
        findViewById<View>(R.id.tv_countTime_style).setOnClickListener {
            AlertDialog.Builder(this).setTitle("CountTime Style").setSingleChoiceItems(
                arrayOf("JUMP", "SECOND", "CLOCK", "NONE"), checkedItem
            ) { dialog, which ->
                checkedItem = which
                when (which) {
                    0 -> {
                        countTimeProgressView.textStyle = CountTimeProgressView.TEXT_STYLE_JUMP
                        countTimeProgressView.titleCenterText = "跳过"
                    }
                    1 -> {
                        countTimeProgressView.textStyle = CountTimeProgressView.TEXT_STYLE_SECOND
                        countTimeProgressView.titleCenterText = "跳过（%s）s"
                    }
                    2 -> countTimeProgressView.textStyle = CountTimeProgressView.TEXT_STYLE_CLOCK
                    3 -> countTimeProgressView.textStyle = CountTimeProgressView.TEXT_STYLE_NONE
                }
                countTimeProgressView.startCountTimeAnimation()
                dialog.dismiss()
            }.show()
        }
    }

    /** StrokeCap 选择对话框 */
    private fun setupStrokeCapSelector() {
        val capNames = arrayOf("BUTT", "ROUND", "SQUARE")
        val caps = arrayOf(Paint.Cap.BUTT, Paint.Cap.ROUND, Paint.Cap.SQUARE)
        val tvStrokeCap = findViewById<View>(R.id.tv_stroke_cap)

        tvStrokeCap.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Stroke Cap").setSingleChoiceItems(
                capNames, strokeCapIndex
            ) { dialog, which ->
                strokeCapIndex = which
                countTimeProgressView.strokeCap = caps[which]
                countTimeProgressView.startCountTimeAnimation()
                dialog.dismiss()
            }.show()
        }
    }

    /** 配置所有 SeekBar 控件 */
    private fun setupSeekBars() {
        // 倒计时时长（秒 × 1000 = 毫秒）
        findViewById<SeekBar>(R.id.seek_bar_countTime).setOnSeekBarChangeListener(
            onStopListener { progress ->
                countTimeProgressView.countTime = (progress.coerceAtLeast(1)) * 1000L
                countTimeProgressView.startCountTimeAnimation()
            }
        )

        // 起始角度
        findViewById<SeekBar>(R.id.seek_bar_start_angle).setOnSeekBarChangeListener(
            onStopListener { progress ->
                countTimeProgressView.startAngle = progress.toFloat()
                countTimeProgressView.startCountTimeAnimation()
            }
        )

        // 小球宽度
        findViewById<SeekBar>(R.id.seekbar_ball_width).setOnSeekBarChangeListener(
            onStopListener { progress ->
                countTimeProgressView.markBallWidth = progress.toFloat()
            }
        )

        // 边框宽度
        findViewById<SeekBar>(R.id.seekbar_border_width).setOnSeekBarChangeListener(
            onStopListener { progress ->
                countTimeProgressView.borderWidth = progress.toFloat()
            }
        )
    }

    /** 配置所有 Switch 控件 */
    private fun setupSwitches() {
        // 小球显示开关
        findViewById<SwitchMaterial>(R.id.sb_ball_flag).setOnCheckedChangeListener { _, isChecked ->
            countTimeProgressView.markBallFlag = isChecked
        }

        // 顺/逆时针切换
        findViewById<SwitchMaterial>(R.id.sb_clockwise).setOnCheckedChangeListener { _, isChecked ->
            countTimeProgressView.clockwise = isChecked
            countTimeProgressView.startCountTimeAnimation()
        }

        // 渐变色开关
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

    /** 初始化控件属性并启动动画 */
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
            titleCenterText = "跳过（%s）s"
            titleCenterTextColor = Color.BLACK
            titleCenterTextSize = 16f

            // v2.1 阈值提醒：最后 3 秒圆弧变红
            warningTime = 3000L
            warningColor = Color.parseColor("#FF3B30")

            // v2.1 跳过延迟：前 2 秒不可点击，显示"请稍候"
            clickableAfterMillis = 2000L
            disabledText = "请稍候"

            // 状态变更回调
            setOnStateChangedListener { state ->
                Log.d(TAG, "状态变更: $state")
            }

            // 按秒 Tick 回调
            setOnTickListener { remainingMillis, remainingSeconds ->
                Log.d(TAG, "Tick: 剩余 ${remainingSeconds}s ($remainingMillis ms)")
            }

            // 警告阈值回调
            setOnWarningListener { remainingMillis ->
                Log.d(TAG, "⚠ 即将结束，剩余 ${remainingMillis}ms")
            }

            setOnCountdownEndListener {
                Toast.makeText(this@SimpleActivity, "时间到", Toast.LENGTH_SHORT).show()
            }

            setOnClickCallback { overageTime ->
                if (isRunning) {
                    cancelCountTimeAnimation()
                    Log.d(TAG, "点击取消，剩余 = $overageTime ms")
                } else {
                    startCountTimeAnimation()
                }
            }

            addOnProgressChangedListener { progress, remainingMillis ->
                Log.d(TAG, "progress=$progress, remaining=$remainingMillis")
            }

            // 绑定生命周期，Activity 进入后台自动暂停、恢复
            bindLifecycle(this@SimpleActivity)

            startCountTimeAnimation()
        }
    }

    companion object {
        private const val TAG = "SimpleActivity"
    }

    /** 简化 SeekBar 监听器：仅在松手时触发回调 */
    private fun onStopListener(action: (Int) -> Unit): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                action(seekBar.progress)
            }
        }
    }
}
