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

/**
 * 动画控制页 — Kotlin + Compose 实现。
 *
 * 展示 Compose 风格的 AndroidView factory 配置方式，
 * 使用 Compose State 桥接控件回调驱动 UI 更新。
 */
class AnimationComposeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setContent { MaterialTheme { AnimationScreen() } }
        }
}

@Composable
private fun AnimationScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Compose State 桥接控件回调
    var stateText by remember { mutableStateOf("状态: IDLE") }
    var progressText by remember { mutableStateOf("进度: 0.00 | 剩余: 10.0s") }
    var tickText by remember { mutableStateOf("Tick: --") }
    var viewRef by remember { mutableStateOf<CountTimeProgressView?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 大号倒计时控件
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

                    setOnStateChangedListener { state -> stateText = "状态: ${state.name}" }

                    addOnProgressChangedListener { progress, remainingMillis ->
                        progressText = "进度: ${"%.2f".format(progress)} | " +
                                "剩余: ${"%.1f".format(remainingMillis / 1000f)}s"
                    }

                    setOnTickListener { remainingMillis, remainingSeconds ->
                        tickText = "Tick: ${remainingSeconds}s (${remainingMillis}ms)"
                    }

                    setOnCountdownEndListener {
                        Toast.makeText(ctx, "倒计时结束", Toast.LENGTH_SHORT).show()
                    }
                }.also { viewRef = it }
            },
            modifier = Modifier.size(160.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 控制按钮行
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            FilledTonalButton(
                onClick = { viewRef?.startCountTimeAnimation() },
                modifier = Modifier.weight(1f)
            ) { Text("开始", fontSize = 12.sp) }
            FilledTonalButton(
                onClick = { viewRef?.pauseCountTimeAnimation() },
                modifier = Modifier.weight(1f)
            ) { Text("暂停", fontSize = 12.sp) }
            FilledTonalButton(
                onClick = { viewRef?.resumeCountTimeAnimation() },
                modifier = Modifier.weight(1f)
            ) { Text("恢复", fontSize = 12.sp) }
            FilledTonalButton(
                onClick = { viewRef?.resetCountTimeAnimation() },
                modifier = Modifier.weight(1f)
            ) { Text("重置", fontSize = 12.sp) }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 实时状态面板
        Text(stateText, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
        Text(progressText, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
        Text(tickText, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
    }
}
