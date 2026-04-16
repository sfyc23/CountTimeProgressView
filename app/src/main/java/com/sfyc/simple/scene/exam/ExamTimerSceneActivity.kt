package com.sfyc.simple.scene.exam

import androidx.fragment.app.Fragment
import com.sfyc.simple.common.TabHostActivity

/**
 * 场景：考试计时器。
 *
 * 展示长时间倒计时场景：
 * - CLOCK 文本样式显示时分秒
 * - warningTime：最后 60 秒警告变红
 * - 暂停/继续考试
 * - 状态变更和警告回调
 */
class ExamTimerSceneActivity : TabHostActivity() {
    override val pageTitle = "场景：考试计时器"
    override fun createFragments(): List<Fragment> = listOf(
        ExamJavaFragment(),
        ExamKotlinFragment(),
        ExamComposeFragment()
    )
}
