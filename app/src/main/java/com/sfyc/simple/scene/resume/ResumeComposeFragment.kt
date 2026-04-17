package com.sfyc.simple.scene.resume

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
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

class ResumeComposeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setContent { MaterialTheme { ResumeScreen() } }
        }
}

@Composable
private fun ResumeScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var logText by remember { mutableStateOf("") }
    var remainingSeconds by remember { mutableFloatStateOf(42f) }
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
                    countTime = 60_000L
                    textStyle = CountTimeProgressView.TEXT_STYLE_CLOCK
                    titleCenterTextSize = 16f
                    borderWidth = 5f
                    borderDrawColor = Color.parseColor("#6750A4")
                    borderBottomColor = Color.parseColor("#E0E0E0")
                    backgroundColorCenter = Color.WHITE
                    titleCenterTextColor = Color.parseColor("#1C1B1F")
                    markBallFlag = false
                    strokeCap = Paint.Cap.ROUND

                    bindLifecycle(lifecycleOwner)

                    setOnStateChangedListener { state ->
                        logText += "State: ${state.name}\n"
                    }
                }.also { viewRef = it }
            },
            modifier = Modifier.size(160.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.label_remaining_short), modifier = Modifier.width(56.dp))
            Slider(
                value = remainingSeconds,
                onValueChange = { remainingSeconds = it },
                valueRange = 0f..60f,
                modifier = Modifier.weight(1f)
            )
            Text("${"%.0f".format(remainingSeconds)}s", modifier = Modifier.width(40.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {
                val ms = (remainingSeconds * 1000).toLong()
                logText = "${context.getString(R.string.log_start_from_remaining, ms)}\n"
                viewRef?.startCountTimeAnimationFromRemaining(ms)
            }) { Text(stringResource(R.string.action_resume_from_remaining), fontSize = 12.sp) }

            OutlinedButton(onClick = {
                logText = "${context.getString(R.string.log_start_from_half)}\n"
                viewRef?.startCountTimeAnimation(0.5f)
            }) { Text(stringResource(R.string.action_resume_from_50_percent), fontSize = 12.sp) }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            logText,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp
        )
    }
}
