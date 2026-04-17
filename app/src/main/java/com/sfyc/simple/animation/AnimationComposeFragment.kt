package com.sfyc.simple.animation

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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

class AnimationComposeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setContent { MaterialTheme { AnimationScreen() } }
        }
}

@Composable
private fun AnimationScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val initialState = stringResource(R.string.status_idle)
    val initialProgress = stringResource(R.string.progress_initial)
    val initialTick = stringResource(R.string.tick_placeholder)

    var stateText by remember(initialState) { mutableStateOf(initialState) }
    var progressText by remember(initialProgress) { mutableStateOf(initialProgress) }
    var tickText by remember(initialTick) { mutableStateOf(initialTick) }
    var viewRef by remember { mutableStateOf<CountTimeProgressView?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(
            factory = { ctx ->
                CountTimeProgressViewCompose.create(ctx) {
                    countTime = 10_000L
                    textStyle = CountTimeProgressView.TEXT_STYLE_CLOCK
                    titleCenterTextSize = 16f
                    borderWidth = 5f
                    borderDrawColor = Color.parseColor("#6750A4")
                    borderBottomColor = Color.parseColor("#E8DEF8")
                    backgroundColorCenter = Color.parseColor("#FAFAFA")
                    titleCenterTextColor = Color.parseColor("#1C1B1F")
                    markBallFlag = true
                    markBallWidth = 8f
                    markBallColor = Color.parseColor("#6750A4")
                    strokeCap = Paint.Cap.ROUND

                    bindLifecycle(lifecycleOwner)

                    setOnStateChangedListener { state ->
                        stateText = ctx.getString(R.string.format_state, state.name)
                    }

                    addOnProgressChangedListener { progress, remainingMillis ->
                        progressText = ctx.getString(
                            R.string.format_progress_remaining,
                            progress,
                            remainingMillis / 1000f
                        )
                    }

                    setOnTickListener { remainingMillis, remainingSeconds ->
                        tickText = ctx.getString(R.string.format_tick_millis, remainingSeconds, remainingMillis)
                    }

                    setOnCountdownEndListener {
                        Toast.makeText(ctx, ctx.getString(R.string.toast_countdown_finished), Toast.LENGTH_SHORT)
                            .show()
                    }
                }.also { viewRef = it }
            },
            modifier = Modifier.size(160.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilledTonalButton(
                onClick = { viewRef?.startCountTimeAnimation() },
                modifier = Modifier.weight(1f)
            ) { Text(stringResource(R.string.action_start), fontSize = 12.sp) }
            FilledTonalButton(
                onClick = { viewRef?.pauseCountTimeAnimation() },
                modifier = Modifier.weight(1f)
            ) { Text(stringResource(R.string.action_pause), fontSize = 12.sp) }
            FilledTonalButton(
                onClick = { viewRef?.resumeCountTimeAnimation() },
                modifier = Modifier.weight(1f)
            ) { Text(stringResource(R.string.action_resume), fontSize = 12.sp) }
            FilledTonalButton(
                onClick = { viewRef?.resetCountTimeAnimation() },
                modifier = Modifier.weight(1f)
            ) { Text(stringResource(R.string.action_reset), fontSize = 12.sp) }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(stateText, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
        Text(progressText, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
        Text(tickText, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
    }
}
