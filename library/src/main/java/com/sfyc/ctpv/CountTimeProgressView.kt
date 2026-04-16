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

/**
 * 倒计时状态枚举，用于统一描述控件的生命周期状态。
 */
enum class CountdownState {
    /** 空闲：尚未开始或已重置 */
    IDLE,
    /** 运行中 */
    RUNNING,
    /** 已暂停 */
    PAUSED,
    /** 已被手动取消 */
    CANCELED,
    /** 自然结束 */
    FINISHED
}

/**
 * 圆形倒计时进度控件。
 *
 * 支持以下核心能力：
 * - 圆环进度绘制（顺/逆时针）
 * - 可配置的轨迹小球
 * - 多种中心文本样式（固定文字 / 秒数 / 时钟格式 / 无文字）
 * - 自定义文本格式化器
 * - 暂停 / 恢复 / 重置动画
 * - 进度变化回调
 * - 渐变色进度条
 * - 端点样式（BUTT / ROUND / SQUARE）
 * - 屏幕旋转状态恢复（SavedState）
 * - 无障碍支持
 * - Jetpack Compose 适配（见 [CountTimeProgressViewCompose]）
 */
class CountTimeProgressView
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        View(context, attrs, defStyleAttr), View.OnClickListener {

    // ==================== 画笔与路径 ====================

    /** 圆环底部轨迹路径 */
    private var mBorderPath: Path = Path()
    /** 动画运动中的绘制路径（随进度增长） */
    private var mSportPath: Path = Path()

    /** 圆环底色画笔 */
    private var mBorderBottomPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    /** 圆环进度绘制画笔 */
    private var mBorderDrawPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    /** 轨迹小球画笔 */
    private var mMarkBallPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    /** 圆形内部背景画笔 */
    private var mBgPaint: Paint = Paint()
    /** 中心文字画笔 */
    private var mTextPaint: Paint = Paint()

    /** 路径测量器，用于在路径上取点绘制小球和进度 */
    private var mPathMeasure: PathMeasure = PathMeasure()
    /** 倒计时动画实例 */
    private var mAnimator: ValueAnimator? = null

    /** 小球在路径上的坐标 [x, y] */
    private var mSportPos: FloatArray = FloatArray(2)
    /** 小球在路径上的切线方向 [dx, dy] */
    private var mSportTan: FloatArray = FloatArray(2)

    /** 当前动画进度值 0f..1f */
    private var mCurrentValue: Float = 0f
    /** 圆环路径的总长度（像素） */
    private var mLength: Float = 0f

    // ==================== 小球属性 ====================

    /** 是否显示轨迹小球 */
    var markBallFlag = true
        set(value) {
            field = value
            calcRadiusInternal()
        }

    /** 小球宽度内部存储值（像素），setter 接收 dp */
    private var _markBallWidth = 0f
    var markBallWidth: Float
        set(value) {
            _markBallWidth = dpToPx(value)
            calcRadiusInternal()
        }
        get() = _markBallWidth

    /** 以像素为单位直接设置小球宽度 */
    fun setMarkBallWidthPx(px: Float) {
        _markBallWidth = px
        calcRadiusInternal()
    }

    /** 小球颜色 */
    var markBallColor = Color.RED
        set(value) {
            field = value
            mMarkBallPaint.color = value
            invalidate()
        }

    // ==================== 圆环边框属性 ====================

    /** 边框宽度内部存储值（像素），setter 接收 dp */
    private var _borderWidth = 0f
    var borderWidth: Float
        set(value) {
            _borderWidth = dpToPx(value)
            mBorderBottomPaint.strokeWidth = _borderWidth
            mBorderDrawPaint.strokeWidth = _borderWidth
            calcRadiusInternal()
        }
        get() = _borderWidth

    /** 以像素为单位直接设置边框宽度 */
    fun setBorderWidthPx(px: Float) {
        _borderWidth = px
        mBorderBottomPaint.strokeWidth = px
        mBorderDrawPaint.strokeWidth = px
        calcRadiusInternal()
    }

    /** 进度绘制颜色 */
    var borderDrawColor = 0
        set(value) {
            field = value
            originalBorderDrawColor = value
            mBorderDrawPaint.color = value
            gradientShader = null
            invalidate()
        }

    /** 圆环底色 */
    var borderBottomColor = 0
        set(value) {
            field = value
            mBorderBottomPaint.color = value
            invalidate()
        }

    /** 圆形内部填充色 */
    var backgroundColorCenter: Int = 0
        set(value) {
            field = value
            mBgPaint.color = value
            invalidate()
        }

    // ==================== 中心文字属性 ====================

    /** 中心文字内容。SECOND 模式下可包含 %s 占位符用于格式化秒数 */
    var titleCenterText: String = ""
        set(value) {
            field = value
            invalidate()
        }

    /** 中心文字大小内部存储值（像素），setter 接收 sp */
    private var _titleCenterTextSize = 0f
    var titleCenterTextSize: Float
        set(value) {
            _titleCenterTextSize = spToPx(value)
            mTextPaint.textSize = _titleCenterTextSize
            invalidate()
        }
        get() = _titleCenterTextSize

    /** 以像素为单位直接设置中心文字大小 */
    fun setTitleCenterTextSizePx(px: Float) {
        _titleCenterTextSize = px
        mTextPaint.textSize = px
        invalidate()
    }

    /** 中心文字颜色 */
    var titleCenterTextColor = 0
        set(value) {
            field = value
            mTextPaint.color = value
            invalidate()
        }

    // ==================== 倒计时与动画属性 ====================

    /** 倒计时总时长（毫秒）。设置 <= 0 的值会自动修正为默认值 5000ms */
    var countTime = 0L
        set(value) {
            field = if (value > 0) value else DEFAULT_COUNT_TIME
            initAnimation()
        }

    /**
     * 起始角度（度数）。get/set 语义一致，用户设多少读回来就是多少。
     * 内部绘制时通过 [drawStartAngle] 做 +270 偏移，使 0 度对应顶部 12 点钟方向。
     */
    var startAngle: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    /** 内部绘制用的角度偏移值 */
    private val drawStartAngle: Float
        get() = (startAngle + 270) % 360

    /** 是否顺时针方向运动 */
    var clockwise = true
        set(value) {
            field = value
            calcRadiusInternal()
            invalidate()
        }

    /** 进度条端点样式（BUTT / ROUND / SQUARE） */
    var strokeCap: Paint.Cap = Paint.Cap.BUTT
        set(value) {
            field = value
            mBorderDrawPaint.strokeCap = value
            mBorderBottomPaint.strokeCap = value
            invalidate()
        }

    /** 动画插值器，默认线性插值以保证倒计时和真实时间一致 */
    var interpolator: TimeInterpolator = LinearInterpolator()
        set(value) {
            field = value
            mAnimator?.interpolator = value
        }

    /**
     * 自定义文本格式化器。
     * 设置后将覆盖 [textStyle] 的文本逻辑，参数为剩余毫秒数。
     */
    var textFormatter: ((remainingMillis: Long) -> String)? = null

    /** 进度变化回调，每帧触发，参数为 (进度0..1, 剩余毫秒) */
    private var mOnProgressChangedListener: ((progress: Float, remainingMillis: Long) -> Unit)? = null

    // ==================== 渐变色 ====================

    /** 渐变色起始颜色。同时设置 [gradientStartColor] 和 [gradientEndColor] 后启用渐变 */
    @ColorInt
    var gradientStartColor: Int = 0
        set(value) {
            field = value
            gradientShader = null
            invalidate()
        }

    /** 渐变色结束颜色 */
    @ColorInt
    var gradientEndColor: Int = 0
        set(value) {
            field = value
            gradientShader = null
            invalidate()
        }

    /** 缓存的 SweepGradient 着色器，尺寸变化时需重建 */
    private var gradientShader: Shader? = null

    /** 渐变色是否已配置 */
    private val hasGradient: Boolean
        get() = gradientStartColor != 0 && gradientEndColor != 0

    /**
     * 批量设置渐变色。
     * @param startColor 起始颜色
     * @param endColor 结束颜色
     */
    fun setGradientColors(@ColorInt startColor: Int, @ColorInt endColor: Int) {
        gradientStartColor = startColor
        gradientEndColor = endColor
    }

    // ==================== 新增 XML 属性 ====================

    /** 是否在 attach 后自动开始倒计时 */
    var autoStart: Boolean = false

    /** 倒计时结束后显示的文本，为空则不改变显示 */
    var finishedText: String? = null

    /** 是否显示中心文字 */
    var showCenterText: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    // ==================== 内部布局状态 ====================

    /** 圆环半径 */
    private var radius = 0f
    /** 绘制中心点 X 坐标 */
    private var centerPaintX = 0f
    /** 绘制中心点 Y 坐标 */
    private var centerPaintY = 0f

    /** 动画取消标记，用于区分正常结束和手动取消 */
    private var onAnimationCancelMark = false

    // ==================== 状态模型（P0-1） ====================

    /** 当前倒计时状态 */
    private var _countdownState: CountdownState = CountdownState.IDLE

    /** 只读访问当前倒计时状态 */
    val countdownState: CountdownState get() = _countdownState

    /** 状态变更回调 */
    private var mOnStateChangedListener: ((CountdownState) -> Unit)? = null

    // ==================== Tick 回调（P0-2） ====================

    /** 按秒 Tick 回调，仅在秒数变化时触发，参数为 (剩余毫秒, 剩余秒数) */
    private var mOnTickListener: ((remainingMillis: Long, remainingSeconds: Int) -> Unit)? = null

    /** 上一次触发 Tick 时的秒数，用于判断秒数是否变化 */
    private var lastTickSecond = -1

    // ==================== 阈值提醒（P0-3） ====================

    /** 警告阈值（毫秒），剩余时间首次 <= 此值时触发警告回调并切换颜色 */
    var warningTime: Long = 0L

    /** 警告状态下的进度条颜色 */
    @ColorInt
    var warningColor: Int = Color.parseColor("#FF3B30")

    /** 警告回调，首次触达阈值时触发 */
    private var mOnWarningListener: ((remainingMillis: Long) -> Unit)? = null

    /** 是否已触发警告（每次启动/重置时复位） */
    private var warningTriggered = false

    /** 进入警告前的原始进度颜色，用于 reset 时恢复 */
    private var originalBorderDrawColor: Int = 0

    // ==================== 跳过延迟（P0-5） ====================

    /** 倒计时开始后经过多少毫秒才可点击（0 表示始终可点击） */
    var clickableAfterMillis: Long = 0L

    /** 不可点击期间显示的替代文本 */
    var disabledText: String? = null

    // ==================== 回调 ====================

    /** 旧版回调（已废弃，同时包含结束和点击事件） */
    private var mOnEndListener: OnEndListener? = null
    /** 新版倒计时结束回调 */
    private var mOnCountdownEndListener: OnCountdownEndListener? = null
    /** 新版点击回调，参数为剩余毫秒数 */
    private var mOnClickCallback: ((overageTime: Long) -> Unit)? = null

    // ==================== 文字缓存（性能优化） ====================

    /** 缓存的秒数，仅在秒数变化时重新格式化文本 */
    private var cachedSecond = -1
    /** 缓存的显示文本 */
    private var cachedDisplayText: String = ""

    // ==================== 文本样式 ====================

    /** 中心文字显示样式 */
    @TextStyle
    var textStyle = TEXT_STYLE_JUMP

    /** 动画是否正在运行 */
    val isRunning: Boolean
        get() = mAnimator?.isRunning ?: false

    /** 动画是否已暂停 */
    val isPaused: Boolean
        get() = mAnimator?.isPaused ?: false

    /**
     * 当前进度（0f..1f），可手动设置以跳转到指定位置。
     * 设置后会自动刷新绘制。
     */
    var progress: Float
        get() = mCurrentValue
        set(value) {
            mCurrentValue = value.coerceIn(0f, 1f)
            invalidate()
        }

    /** 剩余时间（毫秒） */
    val remainingTime: Long
        get() = overageTime

    /** 内部计算用的剩余时间 */
    private val overageTime: Long
        get() = (countTime * (1 - mCurrentValue)).toLong()

    // ==================== 初始化 ====================

    init {
        initAttrs(context, attrs, defStyleAttr)
    }

    /**
     * 从 XML 属性或默认值初始化所有可配置属性和画笔。
     */
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

        // 2.0 新增属性
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

        // v2.1 新增属性
        warningTime = attr.getInt(R.styleable.CountTimeProgressView_warningTime, 0).toLong()
        warningColor = attr.getColor(R.styleable.CountTimeProgressView_warningColor, Color.parseColor("#FF3B30"))
        clickableAfterMillis = attr.getInt(R.styleable.CountTimeProgressView_clickableAfter, 0).toLong()
        disabledText = attr.getString(R.styleable.CountTimeProgressView_disabledText)

        attr.recycle()

        // 记录原始进度颜色，用于警告恢复
        originalBorderDrawColor = borderDrawColor

        // 初始化各画笔
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

    /**
     * 初始化或更新倒计时动画。
     * 如果动画已创建，仅更新 duration；否则创建新的 ValueAnimator。
     */
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

    /** 动画自然结束时的内部处理（设置 finishedText 等） */
    private fun handleAnimationFinished() {
        finishedText?.let { text ->
            if (text.isNotEmpty()) {
                titleCenterText = text
            }
        }
    }

    /** 统一派发状态变更，避免重复触发 */
    private fun dispatchState(newState: CountdownState) {
        if (_countdownState != newState) {
            _countdownState = newState
            mOnStateChangedListener?.invoke(newState)
        }
    }

    /** 检查秒数是否变化，变化时触发 Tick 回调 */
    private fun checkTickCallback() {
        val listener = mOnTickListener ?: return
        val currentSecond = (overageTime / 1000).toInt()
        if (currentSecond != lastTickSecond) {
            lastTickSecond = currentSecond
            listener.invoke(overageTime, currentSecond)
        }
    }

    /** 检查是否触达警告阈值，首次触达时切换颜色并触发回调 */
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

    // ==================== 生命周期 ====================

    /**
     * View 从窗口分离时取消动画。
     * 不清空 listener，避免 RecyclerView 等复用场景下回调丢失。
     */
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

    // ==================== 布局计算 ====================

    /**
     * 根据当前尺寸和属性重新计算圆环半径、中心点和路径。
     */
    private fun calcRadiusInternal() {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        val centerLength = Math.min(availableWidth, availableHeight) / 2f

        centerPaintX = paddingLeft + centerLength
        centerPaintY = paddingTop + centerLength

        // 有小球时，需要为小球预留空间
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

        // 尺寸变化后需要重建渐变着色器
        gradientShader = null
        invalidate()
    }

    /**
     * 支持 wrap_content，默认尺寸为 84dp，保持正方形。
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val defaultSize = dpToPx(DEFAULT_VIEW_SIZE).toInt()

        val width = resolveSize(defaultSize, widthMeasureSpec)
        val height = resolveSize(defaultSize, heightMeasureSpec)
        val size = Math.min(width, height)
        setMeasuredDimension(size, size)
    }

    // ==================== 绘制 ====================

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

    /** 绘制圆形内部背景色和底部轨迹圆环 */
    private fun drawBackground(canvas: Canvas) {
        canvas.drawCircle(0f, 0f, radius, mBgPaint)
        canvas.drawPath(mBorderPath, mBorderBottomPaint)
    }

    /** 绘制当前进度对应的圆弧路径（支持渐变色） */
    private fun drawProgressPath(canvas: Canvas) {
        if (mCurrentValue <= 0f) return

        mSportPath.reset()

        val stop = mLength * mCurrentValue
        mPathMeasure.getSegment(0f, stop, mSportPath, true)

        // 配置渐变色着色器
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

    /** 绘制轨迹小球（在当前进度位置） */
    private fun drawMarkBall(canvas: Canvas) {
        if (!markBallFlag) return
        mPathMeasure.getPosTan(mCurrentValue * mLength, mSportPos, mSportTan)
        canvas.drawCircle(mSportPos[0], mSportPos[1], markBallWidth / 2f, mMarkBallPaint)
    }

    /**
     * 根据 [textFormatter] 或 [textStyle] 解析当前应显示的中心文本。
     * 不可点击期间显示 [disabledText]（如果已配置）。
     * SECOND 模式下带文字缓存，仅在秒数变化时重新格式化，减少 GC 压力。
     */
    private fun resolveDisplayText(): String {
        // 跳过延迟期间显示替代文本
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
                // 仅秒数发生变化时才重新格式化
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

    /** 绘制居中文字，需先反转角度使文字保持水平 */
    private fun drawCenterText(canvas: Canvas, text: String) {
        val middle = mTextPaint.measureText(text)
        canvas.rotate(-drawStartAngle)
        canvas.drawText(text, 0 - middle / 2, 0 - (mTextPaint.descent() + mTextPaint.ascent()) / 2, mTextPaint)
    }

    // ==================== 尺寸变化 ====================

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

    // ==================== 点击事件 ====================

    override fun onClick(view: View) {
        // 跳过延迟：倒计时进行不足 clickableAfterMillis 期间忽略点击
        if (clickableAfterMillis > 0 && isRunning) {
            val elapsed = countTime - overageTime
            if (elapsed < clickableAfterMillis) return
        }
        mOnEndListener?.onClick(overageTime)
        mOnClickCallback?.invoke(overageTime)
    }

    // ==================== 动画控制 ====================

    /** 开始倒计时动画（会先取消上一次的动画） */
    fun startCountTimeAnimation() {
        resetWarningState()
        mAnimator?.let {
            cachedSecond = -1
            lastTickSecond = -1
            it.cancel()
            it.start()
        }
    }

    /**
     * 从指定进度开始倒计时。
     * @param fromProgress 起始进度 0f..1f，例如 0.5f 表示从一半处开始
     */
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

    /**
     * 从指定剩余时间开始倒计时。
     * @param remainingMillis 剩余毫秒数
     */
    fun startCountTimeAnimationFromRemaining(remainingMillis: Long) {
        val elapsed = countTime - remainingMillis.coerceIn(0, countTime)
        val fromProgress = if (countTime > 0) elapsed.toFloat() / countTime else 0f
        startCountTimeAnimation(fromProgress)
    }

    /** 取消倒计时动画 */
    fun cancelCountTimeAnimation() {
        mAnimator?.cancel()
    }

    /** 暂停倒计时动画（API 19+） */
    fun pauseCountTimeAnimation() {
        mAnimator?.pause()
        dispatchState(CountdownState.PAUSED)
    }

    /** 恢复已暂停的倒计时动画（API 19+） */
    fun resumeCountTimeAnimation() {
        mAnimator?.resume()
        dispatchState(CountdownState.RUNNING)
    }

    /** 重置动画到初始状态（进度归零，清除缓存，恢复警告颜色） */
    fun resetCountTimeAnimation() {
        mAnimator?.cancel()
        mCurrentValue = 0f
        cachedSecond = -1
        lastTickSecond = -1
        resetWarningState()
        dispatchState(CountdownState.IDLE)
        invalidate()
    }

    /** 恢复警告相关状态（颜色、标记） */
    private fun resetWarningState() {
        if (warningTriggered) {
            mBorderDrawPaint.color = originalBorderDrawColor
            mBorderDrawPaint.shader = null
            gradientShader = null
        }
        warningTriggered = false
    }

    // ==================== 回调注册 ====================

    /**
     * 添加旧版回调（同时包含动画结束和点击事件）。
     * 推荐使用 [setOnCountdownEndListener] + [setOnClickCallback] 替代。
     */
    fun addOnEndListener(onEndListener: OnEndListener) {
        this.mOnEndListener = onEndListener
    }

    /** 设置倒计时结束回调（接口形式） */
    fun setOnCountdownEndListener(listener: OnCountdownEndListener?) {
        this.mOnCountdownEndListener = listener
    }

    /** 设置倒计时结束回调（lambda 形式） */
    fun setOnCountdownEndListener(listener: () -> Unit) {
        this.mOnCountdownEndListener = object : OnCountdownEndListener {
            override fun onCountdownEnd() = listener()
        }
    }

    /** 设置点击回调，参数为剩余毫秒数 */
    fun setOnClickCallback(callback: ((overageTime: Long) -> Unit)?) {
        this.mOnClickCallback = callback
    }

    /** 设置进度变化监听，每帧触发，参数为 (当前进度, 剩余毫秒) */
    fun addOnProgressChangedListener(listener: (progress: Float, remainingMillis: Long) -> Unit) {
        this.mOnProgressChangedListener = listener
    }

    /** 设置状态变更回调，在 IDLE/RUNNING/PAUSED/CANCELED/FINISHED 之间切换时触发 */
    fun setOnStateChangedListener(listener: ((CountdownState) -> Unit)?) {
        this.mOnStateChangedListener = listener
    }

    /** 设置按秒 Tick 回调，仅在秒数变化时触发，参数为 (剩余毫秒, 剩余秒数) */
    fun setOnTickListener(listener: ((remainingMillis: Long, remainingSeconds: Int) -> Unit)?) {
        this.mOnTickListener = listener
    }

    /** 设置警告阈值回调，首次剩余时间 <= warningTime 时触发 */
    fun setOnWarningListener(listener: ((remainingMillis: Long) -> Unit)?) {
        this.mOnWarningListener = listener
    }

    // ==================== Lifecycle 绑定 ====================

    /**
     * 绑定 LifecycleOwner，在 onPause 时自动暂停动画，onResume 时自动恢复。
     * 比 onDetachedFromWindow 更精细，适用于 Fragment 场景。
     */
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

    // ==================== 单位转换 ====================

    /** sp 转像素 */
    private fun spToPx(sp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)
    }

    /** dp 转像素 */
    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }

    // ==================== 状态保存与恢复 ====================

    /** 保存当前进度和倒计时时长，支持屏幕旋转等配置变更 */
    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.progress = mCurrentValue
        ss.countTime = countTime
        ss.wasRunning = isRunning
        return ss
    }

    /** 恢复之前保存的进度和倒计时时长，若之前正在运行则自动从保存进度续播 */
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

    /**
     * 用于屏幕旋转时保存/恢复控件状态的 Parcelable 实现。
     */
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

    // ==================== 无障碍 ====================

    /** 为屏幕阅读器提供当前显示文本和可点击语义 */
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

    // ==================== 公开接口与常量 ====================

    /**
     * 旧版回调接口，同时包含动画结束和点击事件。
     * @see OnCountdownEndListener 推荐使用新接口替代
     * @see setOnClickCallback 推荐使用新回调替代
     */
    @Deprecated("建议拆分使用 OnCountdownEndListener 和 setOnClickCallback，职责更清晰。")
    interface OnEndListener {
        /** 倒计时自然结束时触发 */
        fun onAnimationEnd()
        /** 用户点击控件时触发，参数为剩余毫秒数 */
        fun onClick(overageTime: Long)
    }

    /** 倒计时结束回调接口（仅关注结束事件） */
    interface OnCountdownEndListener {
        fun onCountdownEnd()
    }

    /** 中心文本显示样式的类型安全注解 */
    @IntDef(TEXT_STYLE_JUMP, TEXT_STYLE_SECOND, TEXT_STYLE_CLOCK, TEXT_STYLE_NONE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class TextStyle

    @Deprecated("请直接使用 TEXT_STYLE_JUMP 等顶层常量。",
        replaceWith = ReplaceWith("CountTimeProgressView.TEXT_STYLE_JUMP"))
    object TextStyle_Legacy {
        const val JUMP = 0
        const val SECOND = 1
        const val CLOCK = 2
        const val NONE = 3
    }

    companion object {
        private const val TAG = "CountTimeProgressView"

        /** 固定文字模式（例如"跳过"） */
        const val TEXT_STYLE_JUMP = 0
        /** 秒数倒计时模式（例如"5s"） */
        const val TEXT_STYLE_SECOND = 1
        /** 时钟格式倒计时模式（例如"00:05:30"） */
        const val TEXT_STYLE_CLOCK = 2
        /** 不显示任何中心文字 */
        const val TEXT_STYLE_NONE = 3

        @Deprecated("请使用 TEXT_STYLE_JUMP 等常量。", replaceWith = ReplaceWith("CountTimeProgressView"))
        val TextStyle = TextStyle_Compat

        // ---- 默认值 ----
        private val DEFAULT_BACKGROUND_COLOR_CENTER = Color.parseColor("#00BCD4")
        private const val DEFAULT_BORDER_WIDTH = 3f       // dp
        private val DEFAULT_BORDER_DRAW_COLOR = Color.parseColor("#4dd0e1")
        private val DEFAULT_BORDER_BOTTOM_COLOR = Color.parseColor("#D32F2F")
        private const val DEFAULT_MARK_BALL_WIDTH = 6f    // dp

        private val DEFAULT_MARK_BALL_COLOR = Color.parseColor("#536DFE")
        private const val DEFAULT_MARK_BALL_FLAG = true
        private const val DEFAULT_START_ANGLE = 0f        // 度
        private const val DEFAULT_CLOCKWISE = true
        private const val DEFAULT_COUNT_TIME = 5000L      // 毫秒

        private const val DEFAULT_TEXTSTYLE = TEXT_STYLE_JUMP
        private const val DEFAULT_TITLE_CENTER_TEXT = "jump"
        private val DEFAULT_TITLE_CENTER_COLOR = Color.parseColor("#FFFFFF")
        private const val DEFAULT_TITLE_CENTER_SIZE = 16f // sp
        private const val DEFAULT_VIEW_SIZE = 84f         // dp，wrap_content 时的默认尺寸

        /** 格式化毫秒为 HH:MM:SS，委托给 [ClockTimeFormatter] */
        @JvmStatic
        fun formatClockTime(millis: Long): String = ClockTimeFormatter.format(millis)
    }

    /**
     * 向后兼容对象，使 `CountTimeProgressView.TextStyle.JUMP` 等旧写法继续可用。
     */
    object TextStyle_Compat {
        const val JUMP = TEXT_STYLE_JUMP
        const val SECOND = TEXT_STYLE_SECOND
        const val CLOCK = TEXT_STYLE_CLOCK
        const val NONE = TEXT_STYLE_NONE
    }
}
