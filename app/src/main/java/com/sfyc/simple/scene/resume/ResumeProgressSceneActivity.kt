package com.sfyc.simple.scene.resume

import androidx.fragment.app.Fragment
import com.sfyc.simple.R
import com.sfyc.simple.common.TabHostActivity

class ResumeProgressSceneActivity : TabHostActivity() {
    override val pageTitleResId = R.string.nav_resume

    override fun createFragments(): List<Fragment> = listOf(
        ResumeJavaFragment(),
        ResumeKotlinFragment(),
        ResumeComposeFragment()
    )
}
