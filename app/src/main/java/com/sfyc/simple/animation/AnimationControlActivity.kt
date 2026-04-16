package com.sfyc.simple.animation

import androidx.fragment.app.Fragment
import com.sfyc.simple.common.TabHostActivity

/**
 * 动画控制页。
 *
 * 展示倒计时动画的完整生命周期控制：
 * - 开始 / 暂停 / 恢复 / 重置
 * - 实时状态面板（State / Progress / Tick）
 * - 倒计时结束回调
 */
class AnimationControlActivity : TabHostActivity() {
    override val pageTitle = "动画控制"
    override fun createFragments(): List<Fragment> = listOf(
        AnimationJavaFragment(),
        AnimationKotlinFragment(),
        AnimationComposeFragment()
    )
}
