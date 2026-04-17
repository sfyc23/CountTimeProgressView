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

class VerifyCodeComposeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setContent { MaterialTheme { VerifyCodeScreen() } }
        }
}

@Composable
private fun VerifyCodeScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val initialResend = stringResource(R.string.action_resend_initial)
    var btnText by remember(initialResend) { mutableStateOf(initialResend) }
    var btnEnabled by remember { mutableStateOf(false) }
    var logText by remember { mutableStateOf("") }
    var viewRef by remember { mutableStateOf<CountTimeProgressView?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.phone_masked), fontSize = 16.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text(stringResource(R.string.hint_verification_code)) },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

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
                            btnText = ctx.getString(R.string.action_resend_with_seconds, remainingSeconds)
                            logText += "Tick: ${remainingSeconds}s\n"
                        }

                        setOnWarningListener {
                            logText += "${ctx.getString(R.string.log_warning_last_10_seconds)}\n"
                        }

                        setOnCountdownEndListener {
                            btnEnabled = true
                            btnText = ctx.getString(R.string.action_resend)
                            logText += "${ctx.getString(R.string.log_countdown_finished_resend)}\n"
                        }
                    }.also { viewRef = it }
                },
                modifier = Modifier.size(64.dp)
            )
        }

        TextButton(onClick = {}, enabled = btnEnabled) { Text(btnText) }

        Text(
            logText,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 16.dp),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp
        )

        Button(
            onClick = {
                logText = "${context.getString(R.string.log_sending_code)}\n"
                btnEnabled = false
                btnText = context.getString(R.string.action_resend_initial)
                viewRef?.startCountTimeAnimation()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text(stringResource(R.string.action_send_code)) }
    }
}
