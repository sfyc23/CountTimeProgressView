package com.sfyc.simple.scene.verifycode

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
 * 验证码倒计时场景 — Kotlin + Compose 实现。
 *
 * 使用 Compose State 桥接 CountTimeProgressView 的回调，
 * 驱动按钮文字和日志文本的响应式更新。
 */
class VerifyCodeComposeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setContent { MaterialTheme { VerifyCodeScreen() } }
        }
}

@Composable
private fun VerifyCodeScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current
    var btnText by remember { mutableStateOf("重新发送 (60s)") }
    var btnEnabled by remember { mutableStateOf(false) }
    var logText by remember { mutableStateOf("") }
    var viewRef by remember { mutableStateOf<CountTimeProgressView?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("手机号: 138****8888", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(24.dp))

        // 验证码输入 + 圆形倒计时
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("验证码") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 倒计时控件：通过 AndroidView 包装
            AndroidView(
                factory = { ctx ->
                    CountTimeProgressViewCompose.create(ctx) {
                        countTime = 60_000L
                        textStyle = CountTimeProgressView.TEXT_STYLE_SECOND
                        titleCenterTextSize = 13f
                        borderWidth = 3f
                        borderDrawColor = Color.parseColor("#6750A4")
                        borderBottomColor = Color.parseColor("#E0E0E0")
                        backgroundColorCenter = Color.parseColor("#FAFAFA")
                        titleCenterTextColor = Color.parseColor("#1C1B1F")
                        markBallFlag = false
                        strokeCap = Paint.Cap.ROUND
                        warningTime = 10_000L
                        warningColor = Color.parseColor("#FF3B30")

                        bindLifecycle(lifecycleOwner)

                        setOnTickListener { _, remainingSeconds ->
                            btnText = "重新发送 (${remainingSeconds}s)"
                            logText += "Tick: ${remainingSeconds}s\n"
                        }

                        setOnWarningListener { _ ->
                            logText += "⚠ 最后10秒！\n"
                        }

                        setOnCountdownEndListener {
                            btnEnabled = true
                            btnText = "重新发送"
                            logText += "✅ 倒计时结束\n"
                        }
                    }.also { viewRef = it }
                },
                modifier = Modifier.size(64.dp)
            )
        }

        // 重发按钮
        TextButton(onClick = {}, enabled = btnEnabled) { Text(btnText) }

        // 日志区域
        Text(
            logText,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 16.dp),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp
        )

        // 发送验证码按钮
        Button(
            onClick = {
                logText = "→ 发送验证码...\n"
                btnEnabled = false
                btnText = "重新发送 (60s)"
                viewRef?.startCountTimeAnimation()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("发送验证码") }
    }
}
