package com.sfyc.simple.animation

import androidx.fragment.app.Fragment
import com.sfyc.simple.R
import com.sfyc.simple.common.TabHostActivity

class AnimationControlActivity : TabHostActivity() {
    override val pageTitleResId = R.string.nav_animation

    override fun createFragments(): List<Fragment> = listOf(
        AnimationJavaFragment(),
        AnimationKotlinFragment(),
        AnimationComposeFragment()
    )
}
