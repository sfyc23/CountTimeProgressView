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

        ctpv.setCountTime(300000L);
        ctpv.setTextStyle(CountTimeProgressView.TEXT_STYLE_CLOCK);
        ctpv.setTitleCenterTextSize(20f);
        ctpv.setBorderWidth(6f);
        ctpv.setBorderDrawColor(Color.parseColor("#6750A4"));
        ctpv.setBorderBottomColor(Color.parseColor("#E0E0E0"));
        ctpv.setBackgroundColorCenter(Color.WHITE);
        ctpv.setTitleCenterTextColor(Color.parseColor("#1C1B1F"));
        ctpv.setMarkBallFlag(false);
        ctpv.setStrokeCap(Paint.Cap.ROUND);
        ctpv.setWarningTime(60000L);
        ctpv.setWarningColor(Color.parseColor("#FF3B30"));
        ctpv.bindLifecycle(getViewLifecycleOwner());

        ctpv.setOnStateChangedListener(state -> {
            switch (state.name()) {
                case "RUNNING":
                    tvStatus.setText(getString(R.string.exam_status_running));
                    break;
                case "PAUSED":
                    tvStatus.setText(getString(R.string.exam_status_paused));
                    break;
                case "FINISHED":
                    tvStatus.setText(getString(R.string.exam_status_finished));
                    break;
                default:
                    tvStatus.setText(getString(R.string.exam_status_format, state.name()));
                    break;
            }
            return kotlin.Unit.INSTANCE;
        });

        ctpv.setOnWarningListener(remainingMillis -> {
            tvLog.append(sdf.format(new Date()) + " " + getString(R.string.log_exam_one_minute_left) + "\n");
            return kotlin.Unit.INSTANCE;
        });

        ctpv.setOnCountdownEndListener(() -> {
            tvLog.append(sdf.format(new Date()) + " " + getString(R.string.log_exam_time_up) + "\n");
            Toast.makeText(requireContext(), getString(R.string.toast_exam_finished), Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.btn_start_exam).setOnClickListener(v -> {
            tvLog.setText(sdf.format(new Date()) + " " + getString(R.string.log_exam_started) + "\n");
            ctpv.startCountTimeAnimation();
        });

        view.findViewById(R.id.btn_pause_exam).setOnClickListener(v -> {
            ctpv.pauseCountTimeAnimation();
            tvLog.append(sdf.format(new Date()) + " " + getString(R.string.log_exam_paused) + "\n");
        });

        view.findViewById(R.id.btn_resume_exam).setOnClickListener(v -> {
            ctpv.resumeCountTimeAnimation();
            tvLog.append(sdf.format(new Date()) + " " + getString(R.string.log_exam_resumed) + "\n");
        });
    }
}
