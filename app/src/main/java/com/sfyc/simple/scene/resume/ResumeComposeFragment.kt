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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import com.sfyc.ctpv.CountTimeProgressView
import com.sfyc.ctpv.CountTimeProgressViewCompose

/**
 * 进度恢复场景 — Kotlin + Compose 实现。
 *
 * 使用 Compose Slider 控制剩余时间参数，
 * 通过 AndroidView 包装 CountTimeProgressView 并桥接回调。
 */
class ResumeComposeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setContent { MaterialTheme { ResumeScreen() } }
        }
}

@Composable
private fun ResumeScreen() {
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
        // 大号 CLOCK 模式倒计时控件
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

        // 剩余时间滑块
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("剩余:", modifier = Modifier.width(48.dp))
            Slider(
                value = remainingSeconds,
                onValueChange = { remainingSeconds = it },
                valueRange = 0f..60f,
                modifier = Modifier.weight(1f)
            )
            Text("${"%.0f".format(remainingSeconds)}s", modifier = Modifier.width(40.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 操作按钮
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {
                val ms = (remainingSeconds * 1000).toLong()
                logText = "→ startFromRemaining(${ms}ms)\n"
                viewRef?.startCountTimeAnimationFromRemaining(ms)
            }) { Text("从剩余时间恢复", fontSize = 12.sp) }

            OutlinedButton(onClick = {
                logText = "→ startCountTimeAnimation(0.5f)\n"
                viewRef?.startCountTimeAnimation(0.5f)
            }) { Text("从50%恢复", fontSize = 12.sp) }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 状态日志
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
