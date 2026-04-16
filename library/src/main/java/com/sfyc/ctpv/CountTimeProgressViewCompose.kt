@file:Suppress("unused")

package com.sfyc.ctpv

import android.animation.TimeInterpolator
import android.graphics.Color
import android.graphics.Paint
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * Jetpack Compose 适配层。
 *
 * 通过 AndroidView 包装 [CountTimeProgressView]，在 Compose 中使用：
 *
 * ```kotlin
 * AndroidView(
 *     factory = { context ->
 *         CountTimeProgressViewCompose.create(context) {
 *             countTime = 5000L
 *             textStyle = CountTimeProgressView.TEXT_STYLE_SECOND
 *
 *             // v2.1 新增：阈值提醒
 *             warningTime = 3000L
 *             warningColor = Color.RED
 *
 *             // v2.1 新增：跳过延迟
 *             clickableAfterMillis = 2000L
 *             disabledText = "请稍候"
 *
 *             // v2.1 新增：状态回调
 *             setOnStateChangedListener { state -> }
 *             setOnTickListener { millis, seconds -> }
 *             setOnWarningListener { millis -> }
 *
 *             // v2.1 新增：从指定进度启动
 *             startCountTimeAnimation(fromProgress = 0.3f)
 *         }
 *     },
 *     modifier = Modifier.size(84.dp)
 * )
 * ```
 *
 * 需要在 app 模块添加 Compose 依赖后才能使用 AndroidView，
 * 此文件仅提供工厂配置函数，不引入任何 Compose 依赖。
 */
object CountTimeProgressViewCompose {

    /**
     * 创建并配置 [CountTimeProgressView] 实例，适用于 AndroidView 的 factory 参数。
     *
     * 使用示例（在 Compose 中）：
     * ```kotlin
     * AndroidView(
     *     factory = { context ->
     *         CountTimeProgressViewCompose.create(context) {
     *             countTime = 5000L
     *             textStyle = CountTimeProgressView.TEXT_STYLE_CLOCK
     *         }
     *     },
     *     modifier = Modifier.size(84.dp)
     * )
     * ```
     */
    @JvmStatic
    fun create(
        context: android.content.Context,
        config: CountTimeProgressView.() -> Unit = {}
    ): CountTimeProgressView {
        return CountTimeProgressView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            config()
        }
    }

    /**
     * 更新已有 [CountTimeProgressView] 实例的属性，适用于 AndroidView 的 update 参数。
     */
    @JvmStatic
    fun update(
        view: CountTimeProgressView,
        config: CountTimeProgressView.() -> Unit
    ) {
        view.config()
    }
}
