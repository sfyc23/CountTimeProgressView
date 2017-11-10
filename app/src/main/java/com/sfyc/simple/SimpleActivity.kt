package com.sfyc.simple

import android.graphics.Color
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.kyleduo.switchbutton.SwitchButton
import com.larswerkman.lobsterpicker.OnColorListener
import com.larswerkman.lobsterpicker.sliders.LobsterShadeSlider
import com.sfyc.ctpv.CountTimeProgressView
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar


/**
 * Author :leilei on 2016/12/20 0115.
 */
class SimpleActivity : AppCompatActivity() {

    private lateinit var countTimeProgressView: CountTimeProgressView
    private var checkedItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple)
        countTimeProgressView = findViewById<CountTimeProgressView>(R.id.countTimeProgressView) 
        findViewById<View>(R.id.tv_countTime_style).setOnClickListener {
            AlertDialog.Builder(this@SimpleActivity).setTitle("CountTime Style").setSingleChoiceItems(
                    arrayOf("JUMP", "SECOND", "CLOCK", "NONE"), checkedItem
            ) { dialog, which ->
                checkedItem = which
                when (which) {
                    0 -> {
                        countTimeProgressView.textStyle = CountTimeProgressView.TextStyle.JUMP
                        countTimeProgressView.titleCenterText = "跳过"
                        countTimeProgressView.startCountTimeAnimation()
                        dialog.dismiss()
                    }
                    1 -> {
                        countTimeProgressView.textStyle = CountTimeProgressView.TextStyle.SECOND
                        countTimeProgressView.titleCenterText = "跳过（%s）s"
                        countTimeProgressView.startCountTimeAnimation()
                        dialog.dismiss()
                    }
                    2 -> {
                        countTimeProgressView.textStyle = CountTimeProgressView.TextStyle.CLOCK
                        countTimeProgressView.startCountTimeAnimation()
                        dialog.dismiss()
                    }
                    3 -> {
                        countTimeProgressView.textStyle = CountTimeProgressView.TextStyle.NONE
                        countTimeProgressView.startCountTimeAnimation()
                        dialog.dismiss()
                    }
                    else -> dialog.dismiss()
                }
            }.show()
        }
        // start_angle
        findViewById<DiscreteSeekBar>(R.id.seek_bar_start_angle).setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar) {}

            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar) {
                countTimeProgressView.startAngle = (seekBar.progress).toFloat()
                countTimeProgressView.startCountTimeAnimation()
            }
        })

        // seek_bar_countTime
        findViewById<DiscreteSeekBar>(R.id.seek_bar_countTime).setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar) {}

            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar) {
                countTimeProgressView.countTime = seekBar.progress * 1000L
                countTimeProgressView.startCountTimeAnimation()
            }
        })
        // Border
        findViewById<DiscreteSeekBar>(R.id.seekbar_border_width).setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {

            }
            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar) {}

            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar) {

                countTimeProgressView.borderWidth = seekBar.progress.toFloat()
            }
        })

        // setBorderBottomColor
        findViewById<LobsterShadeSlider>(R.id.shadeslider_bottom_color).addOnColorListener(object : OnColorListener {
            override fun onColorChanged(@ColorInt color: Int) {
                countTimeProgressView.borderBottomColor = color
            }

            override fun onColorSelected(@ColorInt color: Int) {}
        })

        //setBorderDrawColor
        findViewById<LobsterShadeSlider>(R.id.shadeslider_draw_color).addOnColorListener(object : OnColorListener {
            override fun onColorChanged(@ColorInt color: Int) {
                countTimeProgressView.borderDrawColor = color
            }

            override fun onColorSelected(@ColorInt color: Int) {}
        })
        //setBorderDrawColor
        findViewById<LobsterShadeSlider>(R.id.shadeslider_bg_color).addOnColorListener(object : OnColorListener {
            override fun onColorChanged(@ColorInt color: Int) {
                countTimeProgressView.setBackgroundColor(color)
            }

            override fun onColorSelected(@ColorInt color: Int) {}
        })

        findViewById<DiscreteSeekBar>(R.id.seekbar_ball_width).setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar) {}

            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar) {
                countTimeProgressView.markBallWidth = seekBar.progress.toFloat()
            }
        })
        // setBorderBottomColor
        findViewById<LobsterShadeSlider>(R.id.shadeslider_ball_color).addOnColorListener(object : OnColorListener {
            override fun onColorChanged(@ColorInt color: Int) {
                countTimeProgressView.markBallColor = color
            }

            override fun onColorSelected(@ColorInt color: Int) {}
        })

       findViewById<SwitchButton>(R.id.sb_ball_flag).apply {
            isChecked = true
            setOnCheckedChangeListener { _, isChecked ->
                countTimeProgressView.markBallFlag = isChecked
            }
        }


        findViewById<SwitchButton>(R.id.sb_clockwise).apply {
            isChecked = true
            setOnCheckedChangeListener { _, isChecked ->
                countTimeProgressView.clockwise = isChecked
                countTimeProgressView.startCountTimeAnimation()
            }
        }


        with(countTimeProgressView){
            startAngle = 0f
            countTime = 6000L
            textStyle = CountTimeProgressView.TextStyle.SECOND
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
            addOnEndListener(object : CountTimeProgressView.OnEndListener {
                override fun onAnimationEnd() {
                    Toast.makeText(this@SimpleActivity, "时间到", Toast.LENGTH_SHORT).show()
                }

                override fun onClick(overageTime: Long) {
                    if (countTimeProgressView.isRunning) {
                        countTimeProgressView.cancelCountTimeAnimation()
                        Log.e("overageTime", "overageTime = " + overageTime)
                    } else {
                        countTimeProgressView.startCountTimeAnimation()
                    }
                }
            })
            startCountTimeAnimation()
        }


    }
}
