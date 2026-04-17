package com.sfyc.simple.scene.adskip

import androidx.fragment.app.Fragment
import com.sfyc.simple.R
import com.sfyc.simple.common.TabHostActivity

class AdSkipSceneActivity : TabHostActivity() {
    override val pageTitleResId = R.string.nav_ad_skip

    override fun createFragments(): List<Fragment> = listOf(
        AdSkipJavaFragment(),
        AdSkipKotlinFragment(),
        AdSkipComposeFragment()
    )
}
