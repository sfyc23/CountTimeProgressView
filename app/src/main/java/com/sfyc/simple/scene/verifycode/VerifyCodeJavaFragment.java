package com.sfyc.simple.scene.verifycode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sfyc.ctpv.CountTimeProgressView;
import com.sfyc.simple.R;

/**
 * 验证码倒计时场景 — Java + XML 实现。
 * <p>
 * 演示功能：
 * - 60 秒倒计时，SECOND 文本样式
 * - warningTime：最后 10 秒进度条变红
 * - setOnTickListener：每秒更新重发按钮文字
 * - setOnWarningListener：警告回调
 * - setOnCountdownEndListener：倒计时结束后启用重发按钮
 */
public class VerifyCodeJavaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_verify_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CountTimeProgressView ctpv = view.findViewById(R.id.ctpv_verify);
        Button btnResend = view.findViewById(R.id.btn_resend);
        Button btnSend = view.findViewById(R.id.btn_send);
        TextView tvLog = view.findViewById(R.id.tv_tick_log);

        // 绑定生命周期
        ctpv.bindLifecycle(getViewLifecycleOwner());

        // 每秒更新重发按钮文字
        ctpv.setOnTickListener((remainingMillis, remainingSeconds) -> {
            btnResend.setText("重新发送 (" + remainingSeconds + "s)");
            tvLog.append("Tick: " + remainingSeconds + "s\n");
            return kotlin.Unit.INSTANCE;
        });

        // 警告回调：最后 10 秒
        ctpv.setOnWarningListener(remainingMillis -> {
            tvLog.append("⚠ 最后10秒！\n");
            return kotlin.Unit.INSTANCE;
        });

        // 倒计时结束 → 启用重发按钮
        ctpv.setOnCountdownEndListener(() -> {
            btnResend.setEnabled(true);
            btnResend.setText("重新发送");
            tvLog.append("✅ 倒计时结束，可重新发送\n");
        });

        // 发送验证码按钮
        btnSend.setOnClickListener(v -> {
            tvLog.setText("");
            tvLog.append("→ 发送验证码...\n");
            btnResend.setEnabled(false);
            btnResend.setText("重新发送 (60s)");
            ctpv.startCountTimeAnimation();
        });
    }
}
