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

        ctpv.setOnStateChangedListener(state -> {
            tvLog.append("State: " + state.name() + "\n");
            return kotlin.Unit.INSTANCE;
        });

        seekRemaining.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) tvRemaining.setText(progress + "s");
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        view.findViewById(R.id.btn_from_remaining).setOnClickListener(v -> {
            long remaining = seekRemaining.getProgress() * 1000L;
            tvLog.setText(getString(R.string.log_start_from_remaining, remaining) + "\n");
            ctpv.startCountTimeAnimationFromRemaining(remaining);
        });

        view.findViewById(R.id.btn_from_50).setOnClickListener(v -> {
            tvLog.setText(getString(R.string.log_start_from_half) + "\n");
            ctpv.startCountTimeAnimation(0.5f);
        });

        view.findViewById(R.id.btn_rotate_hint).setOnClickListener(v -> {
            tvLog.append(getString(R.string.log_rotation_hint_line_1) + "\n");
            tvLog.append(getString(R.string.log_rotation_hint_line_2) + "\n");
        });
    }
}
