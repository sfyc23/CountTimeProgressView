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

        ctpv.bindLifecycle(getViewLifecycleOwner());

        ctpv.setOnStateChangedListener(state -> {
            tvStatus.setText(getString(R.string.format_state, state.name()));
            return kotlin.Unit.INSTANCE;
        });

        ctpv.setOnCountdownEndListener(() -> {
            tvStatus.setText(getString(R.string.status_ad_finished_auto));
            Toast.makeText(requireContext(), getString(R.string.toast_ad_finished), Toast.LENGTH_SHORT).show();
        });

        ctpv.setOnClickCallback(overageTime -> {
            ctpv.cancelCountTimeAnimation();
            tvStatus.setText(getString(R.string.status_user_skipped_ms, overageTime));
            Toast.makeText(requireContext(), getString(R.string.toast_skipped), Toast.LENGTH_SHORT).show();
            return kotlin.Unit.INSTANCE;
        });

        ctpv.startCountTimeAnimation();

        view.findViewById(R.id.btn_replay).setOnClickListener(v -> {
            tvStatus.setText(getString(R.string.status_restarted));
            ctpv.startCountTimeAnimation();
        });
    }
}
