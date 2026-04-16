package com.sfyc.simple.scene.adskip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sfyc.ctpv.CountTimeProgressView;
import com.sfyc.simple.R;

/**
 * 广告跳过场景 — Java + XML 实现。
 * <p>
 * 演示功能：
 * - clickableAfterMillis：前 2 秒不可点击
 * - disabledText：不可点击期间显示"广告"
 * - finishedText：倒计时结束后显示"进入"
 * - setOnClickCallback：点击跳过回调
 * - setOnCountdownEndListener：倒计时结束回调
 */
public class AdSkipJavaFragment extends Fragment {

    private CountTimeProgressView ctpv;
    private TextView tvStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ad_skip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ctpv = view.findViewById(R.id.ctpv_ad);
        tvStatus = view.findViewById(R.id.tv_status);

        // 绑定生命周期，进入后台时自动暂停
        ctpv.bindLifecycle(getViewLifecycleOwner());

        // 状态变更回调
        ctpv.setOnStateChangedListener(state -> {
            tvStatus.setText("状态: " + state.name());
            return kotlin.Unit.INSTANCE;
        });

        // 倒计时结束 → 自动进入
        ctpv.setOnCountdownEndListener(() -> {
            tvStatus.setText("状态: 广告结束，自动进入");
            Toast.makeText(requireContext(), "广告结束", Toast.LENGTH_SHORT).show();
        });

        // 点击跳过
        ctpv.setOnClickCallback(overageTime -> {
            ctpv.cancelCountTimeAnimation();
            tvStatus.setText("状态: 用户跳过，剩余 " + overageTime + "ms");
            Toast.makeText(requireContext(), "已跳过", Toast.LENGTH_SHORT).show();
            return kotlin.Unit.INSTANCE;
        });

        // 启动倒计时
        ctpv.startCountTimeAnimation();

        // 重新演示按钮
        view.findViewById(R.id.btn_replay).setOnClickListener(v -> {
            tvStatus.setText("状态: 重新开始");
            ctpv.startCountTimeAnimation();
        });
    }
}
