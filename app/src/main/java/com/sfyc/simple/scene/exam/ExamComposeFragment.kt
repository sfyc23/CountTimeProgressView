package com.sfyc.simple.scene.exam

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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExamComposeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setContent { MaterialTheme { ExamScreen() } }
        }
}

@Composable
private fun ExamScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val sdf = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val initialStatus = stringResource(R.string.exam_status_not_started)

    var statusText by remember(initialStatus) { mutableStateOf(initialStatus) }
    var logText by remember { mutableStateOf("") }
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
                    countTime = 300_000L
                    textStyle = CountTimeProgressView.TEXT_STYLE_CLOCK
                    titleCenterTextSize = 20f
                    borderWidth = 6f
                    borderDrawColor = Color.parseColor("#6750A4")
                    borderBottomColor = Color.parseColor("#E0E0E0")
                    backgroundColorCenter = Color.WHITE
                    titleCenterTextColor = Color.parseColor("#1C1B1F")
                    markBallFlag = false
                    strokeCap = Paint.Cap.ROUND
                    warningTime = 60_000L
                    warningColor = Color.parseColor("#FF3B30")

                    bindLifecycle(lifecycleOwner)

                    setOnStateChangedListener { state ->
                        statusText = when (state.name) {
                            "RUNNING" -> ctx.getString(R.string.exam_status_running)
                            "PAUSED" -> ctx.getString(R.string.exam_status_paused)
                            "FINISHED" -> ctx.getString(R.string.exam_status_finished)
                            else -> ctx.getString(R.string.exam_status_format, state.name)
                        }
                    }

                    setOnWarningListener {
                        logText += "${sdf.format(Date())} ${ctx.getString(R.string.log_exam_one_minute_left)}\n"
                    }

                    setOnCountdownEndListener {
                        logText += "${sdf.format(Date())} ${ctx.getString(R.string.log_exam_time_up)}\n"
                        Toast.makeText(ctx, ctx.getString(R.string.toast_exam_finished), Toast.LENGTH_SHORT).show()
                    }
                }.also { viewRef = it }
            },
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(statusText, fontFamily = FontFamily.Monospace, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilledTonalButton(
                onClick = {
                    logText = "${sdf.format(Date())} ${context.getString(R.string.log_exam_started)}\n"
                    viewRef?.startCountTimeAnimation()
                },
                modifier = Modifier.weight(1f)
            ) { Text(stringResource(R.string.action_start_exam), fontSize = 12.sp) }
            FilledTonalButton(
                onClick = {
                    viewRef?.pauseCountTimeAnimation()
                    logText += "${sdf.format(Date())} ${context.getString(R.string.log_exam_paused)}\n"
                },
                modifier = Modifier.weight(1f)
            ) { Text(stringResource(R.string.action_pause_exam), fontSize = 12.sp) }
            FilledTonalButton(
                onClick = {
                    viewRef?.resumeCountTimeAnimation()
                    logText += "${sdf.format(Date())} ${context.getString(R.string.log_exam_resumed)}\n"
                },
                modifier = Modifier.weight(1f)
            ) { Text(stringResource(R.string.action_resume_exam), fontSize = 12.sp) }
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
