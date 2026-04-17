package com.sfyc.ctpv

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

enum class CountdownState {
    IDLE,
    RUNNING,
    PAUSED,
    CANCELED,
    FINISHED
}

class CountTimeProgressView
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        View(context, attrs, defStyleAttr), View.OnClickListener {


    private var mBorderPath: Path = Path()
    private var mSportPath: Path = Path()

    private var mBorderBottomPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mBorderDrawPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mMarkBallPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mBgPaint: Paint = Paint()
    private var mTextPaint: Paint = Paint()

    private var mPathMeasure: PathMeasure = PathMeasure()
    private var mAnimator: ValueAnimator? = null

    private var mSportPos: FloatArray = FloatArray(2)
    private var mSportTan: FloatArray = FloatArray(2)

    private var mCurrentValue: Float = 0f
    private var mLength: Float = 0f


    var markBallFlag = true
        set(value) {
            field = value
            calcRadiusInternal()
        }

    private var _markBallWidth = 0f
    var markBallWidth: Float
        set(value) {
            _markBallWidth = dpToPx(value)
            calcRadiusInternal()
        }
        get() = _markBallWidth

    fun setMarkBallWidthPx(px: Float) {
        _markBallWidth = px
        calcRadiusInternal()
    }

    var markBallColor = Color.RED
        set(value) {
            field = value
            mMarkBallPaint.color = value
            invalidate()
        }


    private var _borderWidth = 0f
    var borderWidth: Float
        set(value) {
            _borderWidth = dpToPx(value)
            mBorderBottomPaint.strokeWidth = _borderWidth
            mBorderDrawPaint.strokeWidth = _borderWidth
            calcRadiusInternal()
        }
        get() = _borderWidth

    fun setBorderWidthPx(px: Float) {
        _borderWidth = px
        mBorderBottomPaint.strokeWidth = px
        mBorderDrawPaint.strokeWidth = px
        calcRadiusInternal()
    }

    var borderDrawColor = 0
        set(value) {
            field = value
            originalBorderDrawColor = value
            mBorderDrawPaint.color = value
            gradientShader = null
            invalidate()
        }

    var borderBottomColor = 0
        set(value) {
            field = value
            mBorderBottomPaint.color = value
            invalidate()
        }

    var backgroundColorCenter: Int = 0
        set(value) {
            field = value
            mBgPaint.color = value
            invalidate()
        }


    var titleCenterText: String = ""
        set(value) {
            field = value
            invalidate()
        }

    private var _titleCenterTextSize = 0f
    var titleCenterTextSize: Float
        set(value) {
            _titleCenterTextSize = spToPx(value)
            mTextPaint.textSize = _titleCenterTextSize
            invalidate()
        }
        get() = _titleCenterTextSize

    fun setTitleCenterTextSizePx(px: Float) {
        _titleCenterTextSize = px
        mTextPaint.textSize = px
        invalidate()
    }

    var titleCenterTextColor = 0
        set(value) {
            field = value
            mTextPaint.color = value
            invalidate()
        }


    var countTime = 0L
        set(value) {
            field = if (value > 0) value else DEFAULT_COUNT_TIME
            initAnimation()
        }

    var startAngle: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val drawStartAngle: Float
        get() = (startAngle + 270) % 360

    var clockwise = true
        set(value) {
            field = value
            calcRadiusInternal()
            invalidate()
        }

    var strokeCap: Paint.Cap = Paint.Cap.BUTT
        set(value) {
            field = value
            mBorderDrawPaint.strokeCap = value
            mBorderBottomPaint.strokeCap = value
            invalidate()
        }

    var interpolator: TimeInterpolator = LinearInterpolator()
        set(value) {
            field = value
            mAnimator?.interpolator = value
        }

    var textFormatter: ((remainingMillis: Long) -> String)? = null

    private var mOnProgressChangedListener: ((progress: Float, remainingMillis: Long) -> Unit)? = null


    @ColorInt
    var gradientStartColor: Int = 0
        set(value) {
            field = value
            gradientShader = null
            invalidate()
        }

    @ColorInt
    var gradientEndColor: Int = 0
        set(value) {
            field = value
            gradientShader = null
            invalidate()
        }

    private var gradientShader: Shader? = null

    private val hasGradient: Boolean
        get() = gradientStartColor != 0 && gradientEndColor != 0

    fun setGradientColors(@ColorInt startColor: Int, @ColorInt endColor: Int) {
        gradientStartColor = startColor
        gradientEndColor = endColor
    }


    var autoStart: Boolean = false

    var finishedText: String? = null

    var showCenterText: Boolean = true
        set(value) {
            field = value
            invalidate()
        }


    private var radius = 0f
    private var centerPaintX = 0f
    private var centerPaintY = 0f

    private var onAnimationCancelMark = false


    private var _countdownState: CountdownState = CountdownState.IDLE

    val countdownState: CountdownState get() = _countdownState

    private var mOnStateChangedListener: ((CountdownState) -> Unit)? = null


    private var mOnTickListener: ((remainingMillis: Long, remainingSeconds: Int) -> Unit)? = null

    private var lastTickSecond = -1


    var warningTime: Long = 0L

    @ColorInt
    var warningColor: Int = Color.parseColor("#FF3B30")

    private var mOnWarningListener: ((remainingMillis: Long) -> Unit)? = null

    private var warningTriggered = false

    private var originalBorderDrawColor: Int = 0


    var clickableAfterMillis: Long = 0L

    var disabledText: String? = null


    private var mOnEndListener: OnEndListener? = null
    private var mOnCountdownEndListener: OnCountdownEndListener? = null
    private var mOnClickCallback: ((overageTime: Long) -> Unit)? = null


    private var cachedSecond = -1
    private var cachedDisplayText: String = ""


    @TextStyle
    var textStyle = TEXT_STYLE_JUMP

    val isRunning: Boolean
        get() = mAnimator?.isRunning ?: false

    val isPaused: Boolean
        get() = mAnimator?.isPaused ?: false

    var progress: Float
        get() = mCurrentValue
        set(value) {
            mCurrentValue = value.coerceIn(0f, 1f)
            invalidate()
        }

    val remainingTime: Long
        get() = overageTime

    private val overageTime: Long
        get() = (countTime * (1 - mCurrentValue)).toLong()


    init {
        initAttrs(context, attrs, defStyleAttr)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.CountTimeProgressView, defStyleAttr, 0)

        _titleCenterTextSize = attr.getDimension(R.styleable.CountTimeProgressView_titleCenterSize, spToPx(DEFAULT_TITLE_CENTER_SIZE))
        titleCenterTextColor = attr.getColor(R.styleable.CountTimeProgressView_titleCenterColor, DEFAULT_TITLE_CENTER_COLOR)
        titleCenterText = attr.getString(R.styleable.CountTimeProgressView_titleCenterText) ?: DEFAULT_TITLE_CENTER_TEXT

        _borderWidth = attr.getDimension(R.styleable.CountTimeProgressView_borderWidth, dpToPx(DEFAULT_BORDER_WIDTH))
        borderDrawColor = attr.getColor(R.styleable.CountTimeProgressView_borderDrawColor, DEFAULT_BORDER_DRAW_COLOR)
        borderBottomColor = attr.getColor(R.styleable.CountTimeProgressView_borderBottomColor, DEFAULT_BORDER_BOTTOM_COLOR)

        _markBallWidth = attr.getDimension(R.styleable.CountTimeProgressView_markBallWidth, dpToPx(DEFAULT_MARK_BALL_WIDTH))
        markBallColor = attr.getColor(R.styleable.CountTimeProgressView_markBallColor, DEFAULT_MARK_BALL_COLOR)
        markBallFlag = attr.getBoolean(R.styleable.CountTimeProgressView_markBallFlag, DEFAULT_MARK_BALL_FLAG)

        backgroundColorCenter = attr.getColor(R.styleable.CountTimeProgressView_backgroundColorCenter, DEFAULT_BACKGROUND_COLOR_CENTER)

        startAngle = attr.getFloat(R.styleable.CountTimeProgressView_startAngle, DEFAULT_START_ANGLE)
        clockwise = attr.getBoolean(R.styleable.CountTimeProgressView_clockwise, DEFAULT_CLOCKWISE)

        textStyle = attr.getInteger(R.styleable.CountTimeProgressView_textStyle, DEFAULT_TEXTSTYLE)
        countTime = attr.getInt(R.styleable.CountTimeProgressView_countTime, DEFAULT_COUNT_TIME.toInt()).toLong()

        autoStart = attr.getBoolean(R.styleable.CountTimeProgressView_autoStart, false)
        finishedText = attr.getString(R.styleable.CountTimeProgressView_finishedText)
        showCenterText = attr.getBoolean(R.styleable.CountTimeProgressView_showCenterText, true)

        val capIndex = attr.getInt(R.styleable.CountTimeProgressView_strokeCap, 0)
        strokeCap = when (capIndex) {
            1 -> Paint.Cap.ROUND
            2 -> Paint.Cap.SQUARE
            else -> Paint.Cap.BUTT
        }

        gradientStartColor = attr.getColor(R.styleable.CountTimeProgressView_gradientStartColor, 0)
        gradientEndColor = attr.getColor(R.styleable.CountTimeProgressView_gradientEndColor, 0)

        warningTime = attr.getInt(R.styleable.CountTimeProgressView_warningTime, 0).toLong()
        warningColor = attr.getColor(R.styleable.CountTimeProgressView_warningColor, Color.parseColor("#FF3B30"))
        clickableAfterMillis = attr.getInt(R.styleable.CountTimeProgressView_clickableAfter, 0).toLong()
        disabledText = attr.getString(R.styleable.CountTimeProgressView_disabledText)

        attr.recycle()

        originalBorderDrawColor = borderDrawColor

        with(mBorderBottomPaint) {
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = borderBottomColor
            strokeCap = this@CountTimeProgressView.strokeCap
        }

        with(mMarkBallPaint) {
            style = Paint.Style.FILL
            color = markBallColor
        }

        with(mBorderDrawPaint) {
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = borderDrawColor
            strokeCap = this@CountTimeProgressView.strokeCap
        }

        with(mBgPaint) {
            style = Paint.Style.FILL
            isAntiAlias = true
            color = backgroundColorCenter
        }

        with(mTextPaint) {
            style = Paint.Style.FILL
            color = titleCenterTextColor
            isAntiAlias = true
            this.textSize = titleCenterTextSize
        }

        initAnimation()
        setOnClickListener(this)
    }

    private fun initAnimation() {
        mAnimator?.let {
            it.duration = countTime
            return
        }
        mAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = countTime
            interpolator = this@CountTimeProgressView.interpolator
            addUpdateListener {
                if (it.animatedValue is Float) {
                    mCurrentValue = it.animatedValue as Float
                    mOnProgressChangedListener?.invoke(mCurrentValue, overageTime)
                    checkTickCallback()
                    checkWarningThreshold()
                }
                postInvalidateOnAnimation()
            }

            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    onAnimationCancelMark = false
                    dispatchState(CountdownState.RUNNING)
                }
                override fun onAnimationEnd(animation: Animator) {
                    if (!onAnimationCancelMark) {
                        handleAnimationFinished()
                        dispatchState(CountdownState.FINISHED)
                        mOnEndListener?.onAnimationEnd()
                        mOnCountdownEndListener?.onCountdownEnd()
                    }
                }
                override fun onAnimationCancel(animation: Animator) {
                    onAnimationCancelMark = true
                    dispatchState(CountdownState.CANCELED)
                }
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
    }

    private fun handleAnimationFinished() {
        finishedText?.let { text ->
            if (text.isNotEmpty()) {
                titleCenterText = text
            }
        }
    }

    private fun dispatchState(newState: CountdownState) {
        if (_countdownState != newState) {
            _countdownState = newState
            mOnStateChangedListener?.invoke(newState)
        }
    }

    private fun checkTickCallback() {
        val listener = mOnTickListener ?: return
        val currentSecond = (overageTime / 1000).toInt()
        if (currentSecond != lastTickSecond) {
            lastTickSecond = currentSecond
            listener.invoke(overageTime, currentSecond)
        }
    }

    private fun checkWarningThreshold() {
        if (warningTime <= 0 || warningTriggered) return
        if (overageTime <= warningTime) {
            warningTriggered = true
            mBorderDrawPaint.color = warningColor
            gradientShader = null
            mBorderDrawPaint.shader = null
            mOnWarningListener?.invoke(overageTime)
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (isRunning) {
            cancelCountTimeAnimation()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (autoStart && !isRunning) {
            post { startCountTimeAnimation() }
        }
    }


    private fun calcRadiusInternal() {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        val centerLength = Math.min(availableWidth, availableHeight) / 2f

        centerPaintX = paddingLeft + centerLength
        centerPaintY = paddingTop + centerLength

        radius = if (markBallFlag) {
            centerLength - Math.max(borderWidth, markBallWidth / 2f)
        } else {
            centerLength - borderWidth
        }

        mBorderPath.reset()
        if (!clockwise) {
            mBorderPath.addCircle(0f, 0f, radius, Path.Direction.CCW)
        } else {
            mBorderPath.addCircle(0f, 0f, radius, Path.Direction.CW)
        }

        mPathMeasure.setPath(mBorderPath, false)
        mLength = mPathMeasure.length

        gradientShader = null
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val defaultSize = dpToPx(DEFAULT_VIEW_SIZE).toInt()

        val width = resolveSize(defaultSize, widthMeasureSpec)
        val height = resolveSize(defaultSize, heightMeasureSpec)
        val size = Math.min(width, height)
        setMeasuredDimension(size, size)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        canvas.translate(centerPaintX, centerPaintY)
        canvas.rotate(drawStartAngle)

        drawBackground(canvas)
        drawProgressPath(canvas)
        drawMarkBall(canvas)

        if (showCenterText) {
            val text = resolveDisplayText()
            if (text.isNotEmpty()) {
                drawCenterText(canvas, text)
            }
        }

        canvas.restore()
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawCircle(0f, 0f, radius, mBgPaint)
        canvas.drawPath(mBorderPath, mBorderBottomPaint)
    }

    private fun drawProgressPath(canvas: Canvas) {
        if (mCurrentValue <= 0f) return

        mSportPath.reset()

        val stop = mLength * mCurrentValue
        mPathMeasure.getSegment(0f, stop, mSportPath, true)

        if (hasGradient) {
            if (gradientShader == null && radius > 0) {
                gradientShader = SweepGradient(
                    0f, 0f,
                    intArrayOf(gradientStartColor, gradientEndColor),
                    null
                )
            }
            mBorderDrawPaint.shader = gradientShader
        } else {
            mBorderDrawPaint.shader = null
        }

        canvas.drawPath(mSportPath, mBorderDrawPaint)
    }

    private fun drawMarkBall(canvas: Canvas) {
        if (!markBallFlag) return
        mPathMeasure.getPosTan(mCurrentValue * mLength, mSportPos, mSportTan)
        canvas.drawCircle(mSportPos[0], mSportPos[1], markBallWidth / 2f, mMarkBallPaint)
    }

    private fun resolveDisplayText(): String {
        if (clickableAfterMillis > 0 && isRunning) {
            val elapsed = countTime - overageTime
            if (elapsed < clickableAfterMillis && disabledText != null) {
                return disabledText!!
            }
        }

        val customFormatter = textFormatter
        if (customFormatter != null) {
            return customFormatter(overageTime)
        }

        return when (textStyle) {
            TEXT_STYLE_SECOND -> {
                val currentSecond = (overageTime / 1000).toInt()
                if (currentSecond != cachedSecond) {
                    cachedSecond = currentSecond
                    cachedDisplayText = if (titleCenterText.contains("%")) {
                        try {
                            String.format(titleCenterText, currentSecond)
                        } catch (e: Exception) {
                            "${currentSecond}s"
                        }
                    } else {
                        "${currentSecond}s"
                    }
                }
                cachedDisplayText
            }
            TEXT_STYLE_CLOCK -> ClockTimeFormatter.format(overageTime)
            TEXT_STYLE_JUMP -> if (!TextUtils.isEmpty(titleCenterText)) titleCenterText else ""
            TEXT_STYLE_NONE -> ""
            else -> ""
        }
    }

    private fun drawCenterText(canvas: Canvas, text: String) {
        val middle = mTextPaint.measureText(text)
        canvas.rotate(-drawStartAngle)
        canvas.drawText(text, 0 - middle / 2, 0 - (mTextPaint.descent() + mTextPaint.ascent()) / 2, mTextPaint)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calcRadiusInternal()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        calcRadiusInternal()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        calcRadiusInternal()
    }


    override fun onClick(view: View) {
        if (clickableAfterMillis > 0 && isRunning) {
            val elapsed = countTime - overageTime
            if (elapsed < clickableAfterMillis) return
        }
        mOnEndListener?.onClick(overageTime)
        mOnClickCallback?.invoke(overageTime)
    }


    fun startCountTimeAnimation() {
        resetWarningState()
        mAnimator?.let {
            cachedSecond = -1
            lastTickSecond = -1
            it.cancel()
            it.start()
        }
    }

    fun startCountTimeAnimation(fromProgress: Float) {
        resetWarningState()
        mAnimator?.let {
            cachedSecond = -1
            lastTickSecond = -1
            it.cancel()
            it.start()
            it.currentPlayTime = (countTime * fromProgress.coerceIn(0f, 1f)).toLong()
        }
    }

    fun startCountTimeAnimationFromRemaining(remainingMillis: Long) {
        val elapsed = countTime - remainingMillis.coerceIn(0, countTime)
        val fromProgress = if (countTime > 0) elapsed.toFloat() / countTime else 0f
        startCountTimeAnimation(fromProgress)
    }

    fun cancelCountTimeAnimation() {
        mAnimator?.cancel()
    }

    fun pauseCountTimeAnimation() {
        mAnimator?.pause()
        dispatchState(CountdownState.PAUSED)
    }

    fun resumeCountTimeAnimation() {
        mAnimator?.resume()
        dispatchState(CountdownState.RUNNING)
    }

    fun resetCountTimeAnimation() {
        mAnimator?.cancel()
        mCurrentValue = 0f
        cachedSecond = -1
        lastTickSecond = -1
        resetWarningState()
        dispatchState(CountdownState.IDLE)
        invalidate()
    }

    private fun resetWarningState() {
        if (warningTriggered) {
            mBorderDrawPaint.color = originalBorderDrawColor
            mBorderDrawPaint.shader = null
            gradientShader = null
        }
        warningTriggered = false
    }


    fun addOnEndListener(onEndListener: OnEndListener) {
        this.mOnEndListener = onEndListener
    }

    fun setOnCountdownEndListener(listener: OnCountdownEndListener?) {
        this.mOnCountdownEndListener = listener
    }

    fun setOnCountdownEndListener(listener: () -> Unit) {
        this.mOnCountdownEndListener = object : OnCountdownEndListener {
            override fun onCountdownEnd() = listener()
        }
    }

    fun setOnClickCallback(callback: ((overageTime: Long) -> Unit)?) {
        this.mOnClickCallback = callback
    }

    fun addOnProgressChangedListener(listener: (progress: Float, remainingMillis: Long) -> Unit) {
        this.mOnProgressChangedListener = listener
    }

    fun setOnStateChangedListener(listener: ((CountdownState) -> Unit)?) {
        this.mOnStateChangedListener = listener
    }

    fun setOnTickListener(listener: ((remainingMillis: Long, remainingSeconds: Int) -> Unit)?) {
        this.mOnTickListener = listener
    }

    fun setOnWarningListener(listener: ((remainingMillis: Long) -> Unit)?) {
        this.mOnWarningListener = listener
    }


    fun bindLifecycle(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            private var pausedByLifecycle = false

            override fun onPause(owner: LifecycleOwner) {
                if (isRunning && !isPaused) {
                    pauseCountTimeAnimation()
                    pausedByLifecycle = true
                }
            }

            override fun onResume(owner: LifecycleOwner) {
                if (pausedByLifecycle && isPaused) {
                    resumeCountTimeAnimation()
                    pausedByLifecycle = false
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                owner.lifecycle.removeObserver(this)
            }
        })
    }


    private fun spToPx(sp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }


    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.progress = mCurrentValue
        ss.countTime = countTime
        ss.wasRunning = isRunning
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        mCurrentValue = state.progress
        countTime = state.countTime
        if (state.wasRunning && state.progress < 1f) {
            post { startCountTimeAnimation(state.progress) }
        }
    }

    private class SavedState : BaseSavedState {
        var progress: Float = 0f
        var countTime: Long = 0L
        var wasRunning: Boolean = false

        constructor(superState: Parcelable?) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            progress = parcel.readFloat()
            countTime = parcel.readLong()
            wasRunning = parcel.readInt() == 1
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeFloat(progress)
            out.writeLong(countTime)
            out.writeInt(if (wasRunning) 1 else 0)
        }

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(parcel: Parcel) = SavedState(parcel)
                override fun newArray(size: Int) = arrayOfNulls<SavedState>(size)
            }
        }
    }


    override fun onInitializeAccessibilityNodeInfo(info: android.view.accessibility.AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.className = CountTimeProgressView::class.java.name
        info.isClickable = true
        if (showCenterText) {
            val text = resolveDisplayText()
            if (text.isNotEmpty()) {
                info.text = text
            }
        }
    }


    @Deprecated("Use OnCountdownEndListener and setOnClickCallback separately for clearer responsibilities.")
    interface OnEndListener {
        fun onAnimationEnd()
        fun onClick(overageTime: Long)
    }

    interface OnCountdownEndListener {
        fun onCountdownEnd()
    }

    @IntDef(TEXT_STYLE_JUMP, TEXT_STYLE_SECOND, TEXT_STYLE_CLOCK, TEXT_STYLE_NONE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class TextStyle

    @Deprecated("Use top-level constants such as TEXT_STYLE_JUMP directly.",
        replaceWith = ReplaceWith("CountTimeProgressView.TEXT_STYLE_JUMP"))
    object TextStyle_Legacy {
        const val JUMP = 0
        const val SECOND = 1
        const val CLOCK = 2
        const val NONE = 3
    }

    companion object {
        private const val TAG = "CountTimeProgressView"

        const val TEXT_STYLE_JUMP = 0
        const val TEXT_STYLE_SECOND = 1
        const val TEXT_STYLE_CLOCK = 2
        const val TEXT_STYLE_NONE = 3

        @Deprecated("Use constants such as TEXT_STYLE_JUMP.", replaceWith = ReplaceWith("CountTimeProgressView"))
        val TextStyle = TextStyle_Compat

        private val DEFAULT_BACKGROUND_COLOR_CENTER = Color.parseColor("#00BCD4")
        private const val DEFAULT_BORDER_WIDTH = 3f       // dp
        private val DEFAULT_BORDER_DRAW_COLOR = Color.parseColor("#4dd0e1")
        private val DEFAULT_BORDER_BOTTOM_COLOR = Color.parseColor("#D32F2F")
        private const val DEFAULT_MARK_BALL_WIDTH = 6f    // dp

        private val DEFAULT_MARK_BALL_COLOR = Color.parseColor("#536DFE")
        private const val DEFAULT_MARK_BALL_FLAG = true
        private const val DEFAULT_START_ANGLE = 0f
        private const val DEFAULT_CLOCKWISE = true
        private const val DEFAULT_COUNT_TIME = 5000L

        private const val DEFAULT_TEXTSTYLE = TEXT_STYLE_JUMP
        private const val DEFAULT_TITLE_CENTER_TEXT = "jump"
        private val DEFAULT_TITLE_CENTER_COLOR = Color.parseColor("#FFFFFF")
        private const val DEFAULT_TITLE_CENTER_SIZE = 16f // sp
        private const val DEFAULT_VIEW_SIZE = 84f

        @JvmStatic
        fun formatClockTime(millis: Long): String = ClockTimeFormatter.format(millis)
    }

    object TextStyle_Compat {
        const val JUMP = TEXT_STYLE_JUMP
        const val SECOND = TEXT_STYLE_SECOND
        const val CLOCK = TEXT_STYLE_CLOCK
        const val NONE = TEXT_STYLE_NONE
    }
}
