package com.sfyc.simple

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sfyc.simple.animation.AnimationControlActivity
import com.sfyc.simple.scene.adskip.AdSkipSceneActivity
import com.sfyc.simple.scene.exam.ExamTimerSceneActivity
import com.sfyc.simple.scene.resume.ResumeProgressSceneActivity
import com.sfyc.simple.scene.verifycode.VerifyCodeSceneActivity

class MainActivity : AppCompatActivity() {

    data class DemoItem(
        @StringRes val titleResId: Int,
        @StringRes val subtitleResId: Int,
        val activityClass: Class<*>
    )

    private val items = listOf(
        DemoItem(R.string.nav_animation, R.string.nav_animation_desc, AnimationControlActivity::class.java),
        DemoItem(R.string.nav_ad_skip, R.string.nav_ad_skip_desc, AdSkipSceneActivity::class.java),
        DemoItem(R.string.nav_verify_code, R.string.nav_verify_code_desc, VerifyCodeSceneActivity::class.java),
        DemoItem(R.string.nav_resume, R.string.nav_resume_desc, ResumeProgressSceneActivity::class.java),
        DemoItem(R.string.nav_exam, R.string.nav_exam_desc, ExamTimerSceneActivity::class.java),
        DemoItem(R.string.nav_legacy, R.string.nav_legacy_desc, SimpleActivity::class.java),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DemoAdapter(items) { item ->
            startActivity(Intent(this, item.activityClass))
        }
    }

    class DemoAdapter(
        private val items: List<DemoItem>,
        private val onClick: (DemoItem) -> Unit
    ) : RecyclerView.Adapter<DemoAdapter.VH>() {

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvTitle: TextView = view.findViewById(R.id.tv_title)
            val tvSubtitle: TextView = view.findViewById(R.id.tv_subtitle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_demo, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = items[position]
            holder.tvTitle.setText(item.titleResId)
            holder.tvSubtitle.setText(item.subtitleResId)
            holder.itemView.setOnClickListener { onClick(item) }
        }

        override fun getItemCount() = items.size
    }
}
