package com.sfyc.simple.scene.adskip

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import com.sfyc.ctpv.CountTimeProgressView
import com.sfyc.ctpv.CountTimeProgressViewCompose
import com.sfyc.simple.R

class AdSkipComposeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setContent { MaterialTheme { AdSkipScreen() } }
        }
}

@Composable
private fun AdSkipScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val initialStatus = stringResource(R.string.status_waiting_start)
    val restartedStatus = stringResource(R.string.status_restarted)
    var statusText by remember(initialStatus) { mutableStateOf(initialStatus) }
    var viewRef by remember { mutableStateOf<CountTimeProgressView?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(androidx.compose.ui.graphics.Color(0xFFE8DEF8))
        ) {
            Text(
                stringResource(R.string.ad_content_area),
                modifier = Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                color = androidx.compose.ui.graphics.Color(0xFF6750A4)
            )

            AndroidView(
                factory = { ctx ->
                    CountTimeProgressViewCompose.create(ctx) {
                        countTime = 5000L
                        textStyle = CountTimeProgressView.TEXT_STYLE_JUMP
                        titleCenterText = ctx.getString(R.string.text_skip)
                        titleCenterTextSize = 11f
                        titleCenterTextColor = Color.WHITE
                        backgroundColorCenter = Color.parseColor("#88000000")
                        borderWidth = 3f
                        borderBottomColor = Color.parseColor("#80FFFFFF")
                        borderDrawColor = Color.WHITE
                        markBallFlag = true
                        markBallWidth = 3f
                        markBallColor = Color.WHITE
                        strokeCap = Paint.Cap.ROUND
                        clickableAfterMillis = 2000L
                        disabledText = ctx.getString(R.string.text_ad)
                        finishedText = ctx.getString(R.string.text_enter)

                        bindLifecycle(lifecycleOwner)

                        setOnStateChangedListener { state ->
                            statusText = ctx.getString(R.string.format_state, state.name)
                        }

                        setOnCountdownEndListener {
                            statusText = ctx.getString(R.string.status_ad_finished_auto)
                            Toast.makeText(ctx, ctx.getString(R.string.toast_ad_finished), Toast.LENGTH_SHORT).show()
                        }

                        setOnClickCallback { overageTime ->
                            cancelCountTimeAnimation()
                            statusText = ctx.getString(R.string.status_user_skipped_ms, overageTime)
                            Toast.makeText(ctx, ctx.getString(R.string.toast_skipped), Toast.LENGTH_SHORT).show()
                        }

                        startCountTimeAnimation()
                    }.also { viewRef = it }
                },
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(modifier = Modifier.padding(16.dp)) {
            Text(statusText, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            FilledTonalButton(
                onClick = {
                    statusText = restartedStatus
                    viewRef?.startCountTimeAnimation()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.action_replay)) }
        }
    }
}
