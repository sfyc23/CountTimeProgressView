package com.sfyc.simple.scene.adskip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sfyc.ctpv.CountTimeProgressView
import com.sfyc.simple.R

class AdSkipKotlinFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_ad_skip, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ctpv = view.findViewById<CountTimeProgressView>(R.id.ctpv_ad)
        val tvStatus = view.findViewById<TextView>(R.id.tv_status)

        with(ctpv) {
            bindLifecycle(viewLifecycleOwner)

            setOnStateChangedListener { state ->
                tvStatus.text = getString(R.string.format_state, state.name)
            }

            setOnCountdownEndListener {
                tvStatus.text = getString(R.string.status_ad_finished_auto)
                Toast.makeText(requireContext(), getString(R.string.toast_ad_finished), Toast.LENGTH_SHORT).show()
            }

            setOnClickCallback { overageTime ->
                cancelCountTimeAnimation()
                tvStatus.text = getString(R.string.status_user_skipped_ms, overageTime)
                Toast.makeText(requireContext(), getString(R.string.toast_skipped), Toast.LENGTH_SHORT).show()
            }

            startCountTimeAnimation()
        }

        view.findViewById<View>(R.id.btn_replay).setOnClickListener {
            tvStatus.text = getString(R.string.status_restarted)
            ctpv.startCountTimeAnimation()
        }
    }
}
