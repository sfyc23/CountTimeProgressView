package com.sfyc.simple.scene.adskip

import androidx.fragment.app.Fragment
import com.sfyc.simple.common.TabHostActivity

/**
 * 场景：广告跳过。
 *
 * 模拟真实闪屏广告场景，展示 clickableAfterMillis、disabledText、finishedText 等功能。
 * 内含 Java+XML / Kotlin+XML / Compose 三种实现，通过 Tab 切换。
 */
class AdSkipSceneActivity : TabHostActivity() {
    override val pageTitle = "场景：广告跳过"
    override fun createFragments(): List<Fragment> = listOf(
        AdSkipJavaFragment(),
        AdSkipKotlinFragment(),
        AdSkipComposeFragment()
    )
}
