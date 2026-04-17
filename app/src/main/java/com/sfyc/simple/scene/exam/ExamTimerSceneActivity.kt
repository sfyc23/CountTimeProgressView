package com.sfyc.simple.scene.exam

import androidx.fragment.app.Fragment
import com.sfyc.simple.R
import com.sfyc.simple.common.TabHostActivity

class ExamTimerSceneActivity : TabHostActivity() {
    override val pageTitleResId = R.string.nav_exam

    override fun createFragments(): List<Fragment> = listOf(
        ExamJavaFragment(),
        ExamKotlinFragment(),
        ExamComposeFragment()
    )
}
