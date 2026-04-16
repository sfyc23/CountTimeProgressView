package com.sfyc.simple.scene.resume;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sfyc.ctpv.CountTimeProgressView;
import com.sfyc.simple.R;

/**
 * 进度恢复场景 — Java + XML 实现。
 * <p>
 * 演示功能：
 * - startCountTimeAnimationFromRemaining(millis)：从服务器返回的剩余时间恢复
 * - startCountTimeAnimation(fromProgress)：从指定进度恢复
 * - SavedState：屏幕旋转自动恢复
 */
public class ResumeJavaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resume_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CountTimeProgressView ctpv = view.findViewById(R.id.ctpv_resume);
        TextView tvLog = view.findViewById(R.id.tv_log);
        TextView tvRemaining = view.findViewById(R.id.tv_remaining_val);
        SeekBar seekRemaining = view.findViewById(R.id.seek_remaining);

        // Java 风格：逐行 setter 配置
        ctpv.setCountTime(60000L);
        ctpv.setTextStyle(CountTimeProgressView.TEXT_STYLE_CLOCK);
        ctpv.setTitleCenterTextSize(16f);
        ctpv.setBorderWidth(5f);
        ctpv.setBorderDrawColor(Color.parseColor("#6750A4"));
        ctpv.setBorderBottomColor(Color.parseColor("#E0E0E0"));
        ctpv.setBackgroundColorCenter(Color.WHITE);
        ctpv.setTitleCenterTextColor(Color.parseColor("#1C1B1F"));
        ctpv.setMarkBallFlag(false);
        ctpv.setStrokeCap(Paint.Cap.ROUND);
        ctpv.bindLifecycle(getViewLifecycleOwner());

        // 状态变更日志
        ctpv.setOnStateChangedListener(state -> {
            tvLog.append("State: " + state.name() + "\n");
            return kotlin.Unit.INSTANCE;
        });

        // 剩余时间滑块
        seekRemaining.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) tvRemaining.setText(progress + "s");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // 从服务器剩余时间恢复
        view.findViewById(R.id.btn_from_remaining).setOnClickListener(v -> {
            long remaining = seekRemaining.getProgress() * 1000L;
            tvLog.setText("→ startFromRemaining(" + remaining + "ms)\n");
            ctpv.startCountTimeAnimationFromRemaining(remaining);
        });

        // 从 50% 进度恢复
        view.findViewById(R.id.btn_from_50).setOnClickListener(v -> {
            tvLog.setText("→ startCountTimeAnimation(0.5f)\n");
            ctpv.startCountTimeAnimation(0.5f);
        });

        // 屏幕旋转恢复提示
        view.findViewById(R.id.btn_rotate_hint).setOnClickListener(v -> {
            tvLog.append("提示: 先开始倒计时，然后旋转屏幕，进度将自动恢复\n");
            tvLog.append("（SavedState 机制，无需额外代码）\n");
        });
    }
}
