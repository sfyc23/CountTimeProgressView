package com.sfyc.ctpv

/**
 * Author :leilei on 2016/12/19 1512.
 */
import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View


class CountTimeProgressView
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        View(context, attrs, defStyleAttr), View.OnClickListener {

    private var mContext: Context

    /**
     * 小球的运动轨迹
     */
    private var mBorderPath: Path = Path()
    private var mSportPath: Path = Path()

    //背景色画笔
    private var mBorderBottomPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //绘制画笔
    private var mBorderDrawPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //标记的小球
    private var mMarkBallPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //背景色
    private var mBgPaint: Paint = Paint()
    private var mTextPaint: Paint = Paint()

    private var mPathMeasure: PathMeasure = PathMeasure()
    private var mAnimator: ValueAnimator? = null

    private var mSportPos: FloatArray = FloatArray(2)
    private var mSportTan: FloatArray = FloatArray(2)

    private var mCurrentValue: Float = 0f
    private var mLength: Float = 0f


    //MarkBall parameter
    var markBallFlag = true
        set(value) {
            field = value
            calcRadius()
        }

    private var _markBallWidth = 0f
    var markBallWidth :Float
        set(value) {
            _markBallWidth = dpToPx(value)

            calcRadius()
        }
        get() = _markBallWidth

    var markBallColor = Color.RED
        set(value) {
            field = value
            mMarkBallPaint.color = value
            invalidate()
        }

    private var _borderWidth = 0f
    var borderWidth:Float
        set(value) {
            _borderWidth = dpToPx(value)
            mBorderBottomPaint.strokeWidth = _borderWidth
            mBorderDrawPaint.strokeWidth = _borderWidth
            calcRadius()
        }
        get() = _borderWidth

    var borderDrawColor = 0
        set(value) {
            field = value
            mBorderDrawPaint.color = value
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

    //center text
    var titleCenterText: String? = ""
        set(value) {
            field = value
            invalidate()
        }

    private var _titleCenterTextSize = 0f
    var titleCenterTextSize: Float
        set(value) {
            _titleCenterTextSize = spToPx(value)
//            field = value
            mTextPaint.textSize = _titleCenterTextSize
            invalidate()
        }
        get() = _titleCenterTextSize

    var titleCenterTextColor = 0
        set(value) {
            field = value
            mTextPaint.color = value
            invalidate()
        }

    var countTime = 0L
        set(value) {
            field = value
            initAnimation()
        }

    var startAngle :Float = 0f
        set(value) {
            field = value
            invalidate()
        }
        get() = (field + 270) % 360

    /**
     * true is clockwise(顺时针)
     */
    var clockwise = true
        set(value) {
            field = value
            calcRadius()
            invalidate()
        }

    //view radius
    private var radius = 0f
    private var centerPaintX = 0f
    private var centerPaintY = 0f

    //动画取消标记
    private var onAnimationCancelMark = false

    private var displayText: String? = null
    private var mOnEndListener: OnEndListener? = null

    /**
     * 选择显示的类型
     * TextStyle.JUMP,固定文字（例如"跳过"）
     * TextStyle.SECOND,倒计时（5s）
     * TextStyle.CLOCK，倒计时（时钟00:00:02）
     *
     */
    var textStyle = TextStyle.JUMP

    val isRunning: Boolean
        get() =  mAnimator?.isRunning ?: false

    /**
     * @return Get the overage time
     */
    private val overageTime: Long
        get() = (countTime * (1 - mCurrentValue)).toLong()

    init {
        mContext = context
        init(context, attrs, defStyleAttr)

    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {

        // Load the styled attr and set their properties
        val attr = context.obtainStyledAttributes(attrs, R.styleable.CountTimeProgressView, defStyleAttr, 0)
        if (attr != null) {
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

            //起始位置角度
            startAngle = attr.getFloat(R.styleable.CountTimeProgressView_startAngle, DEFAULT_START_ANGLE)
            clockwise = attr.getBoolean(R.styleable.CountTimeProgressView_clockwise, DEFAULT_CLOCKWISE)

            textStyle = attr.getInteger(R.styleable.CountTimeProgressView_textStyle, DEFAULT_TEXTSTYLE)
            countTime = attr.getInt(R.styleable.CountTimeProgressView_countTime, DEFAULT_COUNT_TIME.toInt()).toLong()
            attr.recycle()
        } else {
            _titleCenterTextSize = spToPx(DEFAULT_TITLE_CENTER_SIZE)
            titleCenterTextColor = DEFAULT_TITLE_CENTER_COLOR
            titleCenterText = DEFAULT_TITLE_CENTER_TEXT

            _borderWidth = dpToPx(DEFAULT_BORDER_WIDTH)
            borderDrawColor = DEFAULT_BORDER_DRAW_COLOR
            borderBottomColor = DEFAULT_BORDER_BOTTOM_COLOR

            _markBallWidth = dpToPx(DEFAULT_MARK_BALL_WIDTH)
            markBallColor = DEFAULT_MARK_BALL_COLOR
            markBallFlag = DEFAULT_MARK_BALL_FLAG

            backgroundColorCenter = DEFAULT_BACKGROUND_COLOR_CENTER

            //起始位置角度
            startAngle = DEFAULT_START_ANGLE
            clockwise = DEFAULT_CLOCKWISE

            textStyle = DEFAULT_TEXTSTYLE
            countTime = DEFAULT_COUNT_TIME
        }


        with(mBorderBottomPaint){
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = borderBottomColor
        }

        with(mMarkBallPaint){
            style = Paint.Style.FILL
            color = markBallColor
        }

        with(mBorderDrawPaint){
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = borderDrawColor
        }

        with(mBgPaint){
            style = Paint.Style.FILL
            isAntiAlias = true
            color = backgroundColorCenter
        }

        with(mTextPaint){
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
            addUpdateListener {
                if(it.animatedValue is Float){
                    mCurrentValue = it.animatedValue as Float
                }
                invalidate()
            }

            addListener(object : Animator.AnimatorListener {

                override fun onAnimationStart(animation: Animator) {
                    onAnimationCancelMark = false
                }
                override fun onAnimationEnd(animation: Animator) {

                    if (mOnEndListener != null && !onAnimationCancelMark) {
                        Log.e("CountTimeProgressView", "AnimationOver")
                        mOnEndListener?.onAnimationEnd()
                    }
                }
                override fun onAnimationCancel(animation: Animator) {
                    onAnimationCancelMark = true
                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
        }

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mOnEndListener = null
        if (isRunning) {
            cancelCountTimeAnimation()
        }
    }

    fun calcRadius() {

        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
//        val sideLength = Math.min(availableWidth, availableHeight)
        val centerLength = Math.min(availableWidth, availableHeight)/2f

        centerPaintX = paddingLeft + centerLength
        centerPaintY = paddingTop + centerLength

        if (markBallFlag) {
            radius = centerLength - Math.max(borderWidth, markBallWidth / 2f)
        } else {
            radius = centerLength - borderWidth
        }
        mBorderPath.reset()
        if (!clockwise) {
            mBorderPath.addCircle(0f, 0f, radius, Path.Direction.CCW)
        } else {
            mBorderPath.addCircle(0f, 0f, radius, Path.Direction.CW)
        }


        mPathMeasure.setPath(mBorderPath, false)
        mLength = mPathMeasure.length
        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        canvas.translate(centerPaintX, centerPaintY)
        canvas.rotate(startAngle)


        //绘制背景色
        canvas.drawCircle(0f, 0f, radius, mBgPaint)
        canvas.drawPath(mBorderPath, mBorderBottomPaint)

        mSportPath.reset()
        mSportPath.lineTo(0f, 0f)

        //draw sport path。
        val stop = mLength * mCurrentValue
        mPathMeasure.getSegment(0f, stop, mSportPath, true)
        canvas.drawPath(mSportPath, mBorderDrawPaint)

        mPathMeasure.getPosTan(mCurrentValue * mLength, mSportPos, mSportTan)

        if (markBallFlag) {
            canvas.drawCircle(mSportPos[0], mSportPos[1], markBallWidth/2f, mMarkBallPaint)
        }

        when (textStyle) {
            TextStyle.SECOND ->
                if (titleCenterText!!.contains("%")) {
                    displayText = String.format(titleCenterText!!, (countTime * (1 - mCurrentValue) / 1000).toInt())
                } else {
                    displayText = (countTime * (1 - mCurrentValue) / 1000).toInt().toString() + "s"
                }
            TextStyle.CLOCK -> displayText = ((countTime * (1 - mCurrentValue)).toLong()).clock
            TextStyle.JUMP -> if (!TextUtils.isEmpty(titleCenterText)) {
                displayText = titleCenterText
            }
            TextStyle.NONE -> displayText = ""
            else -> displayText = ""
        }
        if (!TextUtils.isEmpty(displayText)) {
            val middle = mTextPaint.measureText(displayText)
            canvas.rotate(-startAngle)
            canvas.drawText(displayText, 0 - middle / 2, 0 - (mTextPaint.descent() + mTextPaint.ascent()) / 2, mTextPaint)
        }

        canvas.restore()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calcRadius()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        calcRadius()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        calcRadius()
    }

    override fun onClick(view: View) {
        if (mOnEndListener != null) {
            mOnEndListener?.onClick(overageTime)
        }
    }
    /**
     * start countTime
     */
    fun startCountTimeAnimation() {
        mAnimator?.let {
            it.cancel()
            it.start()
        }
    }

    /**
     * cancel countTime
     */
    fun cancelCountTimeAnimation() {
        mAnimator?.cancel()
    }

    fun addOnEndListener(onEndListener: OnEndListener) {
        this.mOnEndListener = onEndListener
    }

    /**
     * Convert sp to pixel.
     */
    private fun spToPx(sp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, mContext.resources.displayMetrics)
    }

    /**
     * Convert dp to pixel.
     */
    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.resources.displayMetrics)
    }

    //扩展属性，显示时针
    private val Long.clock: String
        get() {
            var totalTime = (this / 1000).toInt()//秒
            var hour = 0
            var minute = 0
            var second = 0

            if (3600 <= totalTime) {
                hour = totalTime / 3600
                totalTime = totalTime - 3600 * hour
            }
            if (60 <= totalTime) {
                minute = totalTime / 60
                totalTime = totalTime - 60 * minute
            }
            if (0 <= totalTime) {
                second = totalTime
            }
            val sb = StringBuilder()

            if (hour < 10) {
                sb.append("0").append(hour).append(":")
            } else {
                sb.append(hour).append(":")
            }
            if (minute < 10) {
                sb.append("0").append(minute).append(":")
            } else {
                sb.append(hour).append(":")
            }
            if (second < 10) {
                sb.append("0").append(second)
            } else {
                sb.append(second)
            }
            return sb.toString()
        }


    interface OnEndListener {
        fun onAnimationEnd()

        fun onClick(overageTime: Long)
    }


    object TextStyle {
        /**
         * 固定文字（例如"跳过"）
         */
        val JUMP = 0
        /**
         * 倒计时（5s）
         */
        val SECOND = 1
        /**
         * 倒计时（时钟00:00:02）
         */
        val CLOCK = 2
        /**
         * 不显示任何东西
         */
        val NONE = 3
    }

    companion object {

        private val TAG = "CountTimeProgressView"
        private val DEFAULT_BACKGROUND_COLOR_CENTER = Color.parseColor("#00BCD4")
        private val DEFAULT_BORDER_WIDTH = 3f
        private val DEFAULT_BORDER_DRAW_COLOR =  Color.parseColor("#4dd0e1")
        private val DEFAULT_BORDER_BOTTOM_COLOR =  Color.parseColor("#D32F2F")
        private val DEFAULT_MARK_BALL_WIDTH = 6f

        private val DEFAULT_MARK_BALL_COLOR =  Color.parseColor("#536DFE")
        private val DEFAULT_MARK_BALL_FLAG =  true
        private val DEFAULT_START_ANGLE = 0f
        private val DEFAULT_CLOCKWISE = true
        private val DEFAULT_COUNT_TIME = 5L

        private val DEFAULT_TEXTSTYLE = TextStyle.JUMP
        private val DEFAULT_TITLE_CENTER_TEXT = "jump"
        private val DEFAULT_TITLE_CENTER_COLOR = Color.parseColor("#FFFFFF")
        private val DEFAULT_TITLE_CENTER_SIZE = 16f

    }
}

