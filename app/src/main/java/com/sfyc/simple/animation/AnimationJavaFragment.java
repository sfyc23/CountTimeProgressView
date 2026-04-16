package com.sfyc.simple.animation;

import android.graphics.Color;
import android.graphics.Paint;
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

import java.util.Locale;

/**
 * 动画控制页 — Java + XML 实现。
 * <p>
 * 展示 Java 风格的逐行 setter 属性配置方式，
 * 以及开始/暂停/恢复/重置四个动画控制操作。
 */
public class AnimationJavaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_animation_control, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CountTimeProgressView ctpv = view.findViewById(R.id.ctpv_anim);
        TextView tvState = view.findViewById(R.id.tv_state);
        TextView tvProgress = view.findViewById(R.id.tv_progress);
        TextView tvTick = view.findViewById(R.id.tv_tick);

        // Java 风格：逐行 setter 配置属性
        ctpv.setCountTime(10000L);
        ctpv.setTextStyle(CountTimeProgressView.TEXT_STYLE_CLOCK);
        ctpv.setTitleCenterTextSize(16f);
        ctpv.setBorderWidth(5f);
        ctpv.setBorderDrawColor(Color.parseColor("#6750A4"));
        ctpv.setBorderBottomColor(Color.parseColor("#E8DEF8"));
        ctpv.setBackgroundColorCenter(Color.parseColor("#FAFAFA"));
        ctpv.setTitleCenterTextColor(Color.parseColor("#1C1B1F"));
        ctpv.setMarkBallFlag(true);
        ctpv.setMarkBallWidth(8f);
        ctpv.setMarkBallColor(Color.parseColor("#6750A4"));
        ctpv.setStrokeCap(Paint.Cap.ROUND);

        // 绑定生命周期
        ctpv.bindLifecycle(getViewLifecycleOwner());

        // 状态变更回调
        ctpv.setOnStateChangedListener(state -> {
            tvState.setText("状态: " + state.name());
            return kotlin.Unit.INSTANCE;
        });

        // 进度变化回调（每帧触发）
        ctpv.addOnProgressChangedListener((progress, remainingMillis) -> {
            tvProgress.setText(String.format(Locale.getDefault(),
                    "进度: %.2f | 剩余: %.1fs", progress, remainingMillis / 1000f));
            return kotlin.Unit.INSTANCE;
        });

        // 每秒 Tick 回调
        ctpv.setOnTickListener((remainingMillis, remainingSeconds) -> {
            tvTick.setText(String.format(Locale.getDefault(),
                    "Tick: %ds (%dms)", remainingSeconds, remainingMillis));
            return kotlin.Unit.INSTANCE;
        });

        // 倒计时结束回调
        ctpv.setOnCountdownEndListener(() ->
                Toast.makeText(requireContext(), "倒计时结束", Toast.LENGTH_SHORT).show());

        // 控制按钮
        view.findViewById(R.id.btn_start).setOnClickListener(v -> ctpv.startCountTimeAnimation());
        view.findViewById(R.id.btn_pause).setOnClickListener(v -> ctpv.pauseCountTimeAnimation());
        view.findViewById(R.id.btn_resume).setOnClickListener(v -> ctpv.resumeCountTimeAnimation());
        view.findViewById(R.id.btn_reset).setOnClickListener(v -> ctpv.resetCountTimeAnimation());
    }
}
