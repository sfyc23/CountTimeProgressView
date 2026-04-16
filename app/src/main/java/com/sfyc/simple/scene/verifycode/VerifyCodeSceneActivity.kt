package com.sfyc.simple.scene.verifycode

import androidx.fragment.app.Fragment
import com.sfyc.simple.common.TabHostActivity

/**
 * 场景：验证码倒计时。
 *
 * 模拟发送验证码后的 60 秒倒计时按钮，展示：
 * - SECOND 文本样式
 * - warningTime（最后 10 秒变红）
 * - setOnTickListener（每秒更新按钮文字）
 * - setOnCountdownEndListener（重新启用按钮）
 */
class VerifyCodeSceneActivity : TabHostActivity() {
    override val pageTitle = "场景：验证码倒计时"
    override fun createFragments(): List<Fragment> = listOf(
        VerifyCodeJavaFragment(),
        VerifyCodeKotlinFragment(),
        VerifyCodeComposeFragment()
    )
}
