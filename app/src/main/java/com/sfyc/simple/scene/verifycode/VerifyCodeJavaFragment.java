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

        ctpv.bindLifecycle(getViewLifecycleOwner());

        ctpv.setOnTickListener((remainingMillis, remainingSeconds) -> {
            btnResend.setText(getString(R.string.action_resend_with_seconds, remainingSeconds));
            tvLog.append("Tick: " + remainingSeconds + "s\n");
            return kotlin.Unit.INSTANCE;
        });

        ctpv.setOnWarningListener(remainingMillis -> {
            tvLog.append(getString(R.string.log_warning_last_10_seconds) + "\n");
            return kotlin.Unit.INSTANCE;
        });

        ctpv.setOnCountdownEndListener(() -> {
            btnResend.setEnabled(true);
            btnResend.setText(getString(R.string.action_resend));
            tvLog.append(getString(R.string.log_countdown_finished_resend) + "\n");
        });

        btnSend.setOnClickListener(v -> {
            tvLog.setText("");
            tvLog.append(getString(R.string.log_sending_code) + "\n");
            btnResend.setEnabled(false);
            btnResend.setText(getString(R.string.action_resend_initial));
            ctpv.startCountTimeAnimation();
        });
    }
}
