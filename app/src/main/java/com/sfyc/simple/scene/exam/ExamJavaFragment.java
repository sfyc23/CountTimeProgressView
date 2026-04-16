package com.sfyc.simple.scene.exam;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 考试计时器场景 — Java + XML 实现。
 * <p>
 * 演示功能：
 * - 5 分钟倒计时（演示用，实际考试可设为 3600000）
 * - CLOCK 文本样式
 * - warningTime：最后 60 秒警告变红
 * - 暂停 / 继续考试
 */
public class ExamJavaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exam_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CountTimeProgressView ctpv = view.findViewById(R.id.ctpv_exam);
        TextView tvStatus = view.findViewById(R.id.tv_exam_status);
        TextView tvLog = view.findViewById(R.id.tv_exam_log);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        // Java 风格配置
        ctpv.setCountTime(300000L);   // 5 分钟演示
        ctpv.setTextStyle(CountTimeProgressView.TEXT_STYLE_CLOCK);
        ctpv.setTitleCenterTextSize(20f);
        ctpv.setBorderWidth(6f);
        ctpv.setBorderDrawColor(Color.parseColor("#6750A4"));
        ctpv.setBorderBottomColor(Color.parseColor("#E0E0E0"));
        ctpv.setBackgroundColorCenter(Color.WHITE);
        ctpv.setTitleCenterTextColor(Color.parseColor("#1C1B1F"));
        ctpv.setMarkBallFlag(false);
        ctpv.setStrokeCap(Paint.Cap.ROUND);

        // 最后 60 秒警告
        ctpv.setWarningTime(60000L);
        ctpv.setWarningColor(Color.parseColor("#FF3B30"));

        ctpv.bindLifecycle(getViewLifecycleOwner());

        // 状态变更
        ctpv.setOnStateChangedListener(state -> {
            switch (state.name()) {
                case "RUNNING": tvStatus.setText("考试状态: 进行中"); break;
                case "PAUSED":  tvStatus.setText("考试状态: 已暂停"); break;
                case "FINISHED":tvStatus.setText("考试状态: 已结束"); break;
                default:        tvStatus.setText("考试状态: " + state.name()); break;
            }
            return kotlin.Unit.INSTANCE;
        });

        // 警告回调
        ctpv.setOnWarningListener(remainingMillis -> {
            tvLog.append(sdf.format(new Date()) + " ⚠ 还剩1分钟！\n");
            return kotlin.Unit.INSTANCE;
        });

        // 倒计时结束
        ctpv.setOnCountdownEndListener(() -> {
            tvLog.append(sdf.format(new Date()) + " 考试时间到，自动提交\n");
            Toast.makeText(requireContext(), "考试结束", Toast.LENGTH_SHORT).show();
        });

        // 开始考试
        view.findViewById(R.id.btn_start_exam).setOnClickListener(v -> {
            tvLog.setText(sdf.format(new Date()) + " 考试开始\n");
            ctpv.startCountTimeAnimation();
        });

        // 暂停考试
        view.findViewById(R.id.btn_pause_exam).setOnClickListener(v -> {
            ctpv.pauseCountTimeAnimation();
            tvLog.append(sdf.format(new Date()) + " 考试暂停\n");
        });

        // 继续考试
        view.findViewById(R.id.btn_resume_exam).setOnClickListener(v -> {
            ctpv.resumeCountTimeAnimation();
            tvLog.append(sdf.format(new Date()) + " 考试继续\n");
        });
    }
}
