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

/**
 * 广告跳过场景 — Kotlin + XML 实现。
 *
 * 与 [AdSkipJavaFragment] 功能完全一致，展示 Kotlin 风格的属性赋值和 with DSL。
 */
class AdSkipKotlinFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_ad_skip, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ctpv = view.findViewById<CountTimeProgressView>(R.id.ctpv_ad)
        val tvStatus = view.findViewById<TextView>(R.id.tv_status)

        with(ctpv) {
            // 绑定生命周期
            bindLifecycle(viewLifecycleOwner)

            // 状态变更回调
            setOnStateChangedListener { state ->
                tvStatus.text = "状态: ${state.name}"
            }

            // 倒计时结束
            setOnCountdownEndListener {
                tvStatus.text = "状态: 广告结束，自动进入"
                Toast.makeText(requireContext(), "广告结束", Toast.LENGTH_SHORT).show()
            }

            // 点击跳过
            setOnClickCallback { overageTime ->
                cancelCountTimeAnimation()
                tvStatus.text = "状态: 用户跳过，剩余 ${overageTime}ms"
                Toast.makeText(requireContext(), "已跳过", Toast.LENGTH_SHORT).show()
            }

            startCountTimeAnimation()
        }

        // 重新演示
        view.findViewById<View>(R.id.btn_replay).setOnClickListener {
            tvStatus.text = "状态: 重新开始"
            ctpv.startCountTimeAnimation()
        }
    }
}
