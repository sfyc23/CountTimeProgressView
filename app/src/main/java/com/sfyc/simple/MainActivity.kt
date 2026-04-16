package com.sfyc.simple

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sfyc.simple.animation.AnimationControlActivity
import com.sfyc.simple.scene.adskip.AdSkipSceneActivity
import com.sfyc.simple.scene.exam.ExamTimerSceneActivity
import com.sfyc.simple.scene.resume.ResumeProgressSceneActivity
import com.sfyc.simple.scene.verifycode.VerifyCodeSceneActivity

/**
 * 导航首页。
 *
 * 以 Material Design 3 卡片列表展示所有演示页面入口，
 * 点击后跳转到对应的功能演示或真实场景页面。
 */
class MainActivity : AppCompatActivity() {

    /** 演示项数据类：标题 + 副标题 + 目标 Activity */
    data class DemoItem(val title: String, val subtitle: String, val activityClass: Class<*>)

    /** 导航列表数据源 */
    private val items = listOf(
        DemoItem("动画控制", "开始 / 暂停 / 恢复 / 重置 / 实时状态", AnimationControlActivity::class.java),
        DemoItem("场景：广告跳过", "真实闪屏广告跳过场景", AdSkipSceneActivity::class.java),
        DemoItem("场景：验证码倒计时", "发送验证码后的倒计时按钮", VerifyCodeSceneActivity::class.java),
        DemoItem("场景：进度恢复", "从服务器剩余时间恢复 / 屏幕旋转恢复", ResumeProgressSceneActivity::class.java),
        DemoItem("场景：考试计时器", "长时间倒计时 + 警告 + 暂停恢复", ExamTimerSceneActivity::class.java),
        DemoItem("传统演示页", "原始参数面板（兼容旧版）", SimpleActivity::class.java),
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

    /**
     * 导航列表适配器，每个 item 显示卡片标题和描述。
     */
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
            holder.tvTitle.text = item.title
            holder.tvSubtitle.text = item.subtitle
            holder.itemView.setOnClickListener { onClick(item) }
        }

        override fun getItemCount() = items.size
    }
}
