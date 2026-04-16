package com.sfyc.simple.scene.resume

import androidx.fragment.app.Fragment
import com.sfyc.simple.common.TabHostActivity

/**
 * 场景：进度恢复。
 *
 * 模拟从服务器获取剩余时间后恢复倒计时进度，展示：
 * - startCountTimeAnimationFromRemaining(millis)
 * - startCountTimeAnimation(fromProgress)
 * - SavedState（屏幕旋转自动恢复）
 */
class ResumeProgressSceneActivity : TabHostActivity() {
    override val pageTitle = "场景：进度恢复"
    override fun createFragments(): List<Fragment> = listOf(
        ResumeJavaFragment(),
        ResumeKotlinFragment(),
        ResumeComposeFragment()
    )
}
