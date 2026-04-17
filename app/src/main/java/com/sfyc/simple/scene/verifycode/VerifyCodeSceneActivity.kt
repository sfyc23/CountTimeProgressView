package com.sfyc.simple.scene.verifycode

import androidx.fragment.app.Fragment
import com.sfyc.simple.R
import com.sfyc.simple.common.TabHostActivity

class VerifyCodeSceneActivity : TabHostActivity() {
    override val pageTitleResId = R.string.nav_verify_code

    override fun createFragments(): List<Fragment> = listOf(
        VerifyCodeJavaFragment(),
        VerifyCodeKotlinFragment(),
        VerifyCodeComposeFragment()
    )
}
