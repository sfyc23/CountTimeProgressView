@file:Suppress("unused")

package com.sfyc.ctpv

import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * Factory helpers for using [CountTimeProgressView] from a Compose AndroidView.
 *
 * This file intentionally does not depend on Compose. Add Compose dependencies in the
 * app module, then call these helpers from AndroidView factory/update lambdas.
 */
object CountTimeProgressViewCompose {

    /**
     * Creates and configures a [CountTimeProgressView] instance for AndroidView factory usage.
     */
    @JvmStatic
    fun create(
        context: android.content.Context,
        config: CountTimeProgressView.() -> Unit = {}
    ): CountTimeProgressView {
        return CountTimeProgressView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            config()
        }
    }

    /**
     * Updates an existing [CountTimeProgressView] instance for AndroidView update usage.
     */
    @JvmStatic
    fun update(
        view: CountTimeProgressView,
        config: CountTimeProgressView.() -> Unit
    ) {
        view.config()
    }
}
