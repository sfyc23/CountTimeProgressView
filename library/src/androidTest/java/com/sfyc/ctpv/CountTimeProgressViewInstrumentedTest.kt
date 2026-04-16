package com.sfyc.ctpv

import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.FrameLayout
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * CountTimeProgressView 仪器测试。
 * 在真实 Android 设备/模拟器上验证控件的基础行为。
 */
@RunWith(AndroidJUnit4::class)
class CountTimeProgressViewInstrumentedTest {

    private lateinit var view: CountTimeProgressView

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view = CountTimeProgressView(context)
        }
    }

    @Test
    fun defaultValues_areCorrect() {
        assertEquals(5000L, view.countTime)
        assertEquals(0f, view.startAngle, 0.01f)
        assertTrue(view.clockwise)
        assertTrue(view.markBallFlag)
        assertEquals(CountTimeProgressView.TEXT_STYLE_JUMP, view.textStyle)
        assertTrue(view.showCenterText)
        assertFalse(view.autoStart)
        assertNull(view.finishedText)
        assertEquals(Paint.Cap.BUTT, view.strokeCap)
    }

    @Test
    fun progress_clampsTo0And1() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.progress = -0.5f
        }
        assertEquals(0f, view.progress, 0.01f)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.progress = 1.5f
        }
        assertEquals(1f, view.progress, 0.01f)
    }

    @Test
    fun countTime_protectsAgainstInvalidValues() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.countTime = -100L
        }
        assertEquals(5000L, view.countTime)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.countTime = 0L
        }
        assertEquals(5000L, view.countTime)
    }

    @Test
    fun startAngle_getterSetterConsistent() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.startAngle = 90f
        }
        assertEquals(90f, view.startAngle, 0.01f)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.startAngle = 0f
        }
        assertEquals(0f, view.startAngle, 0.01f)
    }

    @Test
    fun strokeCap_canBeChanged() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.strokeCap = Paint.Cap.ROUND
        }
        assertEquals(Paint.Cap.ROUND, view.strokeCap)
    }

    @Test
    fun gradientColors_canBeSet() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.setGradientColors(Color.RED, Color.BLUE)
        }
        assertEquals(Color.RED, view.gradientStartColor)
        assertEquals(Color.BLUE, view.gradientEndColor)
    }

    @Test
    fun animationLifecycle_startCancelReset() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.countTime = 10000L
            view.startCountTimeAnimation()
        }
        assertTrue(view.isRunning)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.cancelCountTimeAnimation()
        }
        assertFalse(view.isRunning)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.resetCountTimeAnimation()
        }
        assertEquals(0f, view.progress, 0.01f)
    }

    @Test
    fun pauseResume_works() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.countTime = 10000L
            view.startCountTimeAnimation()
        }
        assertTrue(view.isRunning)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.pauseCountTimeAnimation()
        }
        assertTrue(view.isPaused)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.resumeCountTimeAnimation()
        }
        assertFalse(view.isPaused)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.cancelCountTimeAnimation()
        }
    }

    @Test
    fun onMeasure_supportsWrapContent() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val parent = FrameLayout(view.context)
            parent.addView(view, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ))
            val wSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            val hSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(wSpec, hSpec)
        }
        // wrap_content 默认 84dp，转换后应大于 0
        assertTrue(view.measuredWidth > 0)
        assertTrue(view.measuredHeight > 0)
        assertEquals(view.measuredWidth, view.measuredHeight)
    }

    @Test
    fun showCenterText_controlsTextVisibility() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.showCenterText = false
        }
        assertFalse(view.showCenterText)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.showCenterText = true
        }
        assertTrue(view.showCenterText)
    }

    @Test
    fun explicitPxApis_work() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.setBorderWidthPx(10f)
        }
        assertEquals(10f, view.borderWidth, 0.01f)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            view.setMarkBallWidthPx(8f)
        }
        assertEquals(8f, view.markBallWidth, 0.01f)
    }
}
