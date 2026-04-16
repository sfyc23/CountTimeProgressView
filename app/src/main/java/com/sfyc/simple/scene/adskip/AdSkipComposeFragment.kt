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
 * 广告跳过场景 — Kotlin + Compose 实现。
 *
 * 通过 AndroidView 包装 CountTimeProgressView，
 * 使用 Compose State 桥接控件回调来驱动 UI 更新。
 */
class AdSkipComposeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setContent { MaterialTheme { AdSkipScreen() } }
        }
}

@Composable
private fun AdSkipScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var statusText by remember { mutableStateOf("状态: 等待开始") }
    var viewRef by remember { mutableStateOf<CountTimeProgressView?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 模拟广告区域
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(androidx.compose.ui.graphics.Color(0xFFE8DEF8))
        ) {
            Text(
                "广告内容区域",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                color = androidx.compose.ui.graphics.Color(0xFF6750A4)
            )

            // 右上角倒计时控件
            AndroidView(
                factory = { ctx ->
                    CountTimeProgressViewCompose.create(ctx) {
                        countTime = 5000L
                        textStyle = CountTimeProgressView.TEXT_STYLE_JUMP
                        titleCenterText = "跳过"
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
                        disabledText = "广告"
                        finishedText = "进入"

                        bindLifecycle(lifecycleOwner)

                        setOnStateChangedListener { state ->
                            statusText = "状态: ${state.name}"
                        }

                        setOnCountdownEndListener {
                            statusText = "状态: 广告结束，自动进入"
                            Toast.makeText(ctx, "广告结束", Toast.LENGTH_SHORT).show()
                        }

                        setOnClickCallback { overageTime ->
                            cancelCountTimeAnimation()
                            statusText = "状态: 用户跳过，剩余 ${overageTime}ms"
                            Toast.makeText(ctx, "已跳过", Toast.LENGTH_SHORT).show()
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

        // 底部状态面板
        Column(modifier = Modifier.padding(16.dp)) {
            Text(statusText, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            FilledTonalButton(
                onClick = {
                    statusText = "状态: 重新开始"
                    viewRef?.startCountTimeAnimation()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("重新演示") }
        }
    }
}
