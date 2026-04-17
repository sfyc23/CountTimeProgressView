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
        ctpv.bindLifecycle(getViewLifecycleOwner());

        ctpv.setOnStateChangedListener(state -> {
            tvState.setText(getString(R.string.format_state, state.name()));
            return kotlin.Unit.INSTANCE;
        });

        ctpv.addOnProgressChangedListener((progress, remainingMillis) -> {
            tvProgress.setText(getString(
                    R.string.format_progress_remaining,
                    progress,
                    remainingMillis / 1000f
            ));
            return kotlin.Unit.INSTANCE;
        });

        ctpv.setOnTickListener((remainingMillis, remainingSeconds) -> {
            tvTick.setText(getString(R.string.format_tick_millis, remainingSeconds, remainingMillis));
            return kotlin.Unit.INSTANCE;
        });

        ctpv.setOnCountdownEndListener(() ->
                Toast.makeText(requireContext(), getString(R.string.toast_countdown_finished), Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.btn_start).setOnClickListener(v -> ctpv.startCountTimeAnimation());
        view.findViewById(R.id.btn_pause).setOnClickListener(v -> ctpv.pauseCountTimeAnimation());
        view.findViewById(R.id.btn_resume).setOnClickListener(v -> ctpv.resumeCountTimeAnimation());
        view.findViewById(R.id.btn_reset).setOnClickListener(v -> ctpv.resetCountTimeAnimation());
    }
}
