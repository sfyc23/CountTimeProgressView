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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import com.sfyc.ctpv.CountTimeProgressView
import com.sfyc.ctpv.CountTimeProgressViewCompose
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 考试计时器场景 — Kotlin + Compose 实现。
 *
 * 展示 Compose 风格的长时间倒计时场景，
 * 通过 State 桥接考试状态和日志。
 */
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

    var statusText by remember { mutableStateOf("考试状态: 未开始") }
    var logText by remember { mutableStateOf("") }
    var viewRef by remember { mutableStateOf<CountTimeProgressView?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 超大号 CLOCK 倒计时控件
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
                            "RUNNING" -> "考试状态: 进行中"
                            "PAUSED" -> "考试状态: 已暂停"
                            "FINISHED" -> "考试状态: 已结束"
                            else -> "考试状态: ${state.name}"
                        }
                    }

                    setOnWarningListener { _ ->
                        logText += "${sdf.format(Date())} ⚠ 还剩1分钟！\n"
                    }

                    setOnCountdownEndListener {
                        logText += "${sdf.format(Date())} 考试时间到，自动提交\n"
                        Toast.makeText(ctx, "考试结束", Toast.LENGTH_SHORT).show()
                    }
                }.also { viewRef = it }
            },
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(statusText, fontFamily = FontFamily.Monospace, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))

        // 控制按钮
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            FilledTonalButton(
                onClick = {
                    logText = "${sdf.format(Date())} 考试开始\n"
                    viewRef?.startCountTimeAnimation()
                },
                modifier = Modifier.weight(1f)
            ) { Text("开始", fontSize = 12.sp) }
            FilledTonalButton(
                onClick = {
                    viewRef?.pauseCountTimeAnimation()
                    logText += "${sdf.format(Date())} 考试暂停\n"
                },
                modifier = Modifier.weight(1f)
            ) { Text("暂停", fontSize = 12.sp) }
            FilledTonalButton(
                onClick = {
                    viewRef?.resumeCountTimeAnimation()
                    logText += "${sdf.format(Date())} 考试继续\n"
                },
                modifier = Modifier.weight(1f)
            ) { Text("继续", fontSize = 12.sp) }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 提醒日志
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
