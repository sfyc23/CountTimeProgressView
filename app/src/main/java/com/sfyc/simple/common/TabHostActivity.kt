package com.sfyc.simple.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sfyc.simple.R

/**
 * Tab 页宿主基类。
 *
 * 所有功能页/场景页继承此类，只需提供 [pageTitle] 和 [createFragments] 即可，
 * 内部统一管理 Toolbar + TabLayout + ViewPager2 结构。
 */
abstract class TabHostActivity : AppCompatActivity() {

    /** 页面标题（显示在 Toolbar 上） */
    abstract val pageTitle: String

    /** 创建三种实现对应的 Fragment 列表 */
    abstract fun createFragments(): List<Fragment>

    /** Tab 标签文字，对应 Java+XML / Kotlin+XML / Compose 三种实现 */
    private val tabTitles = listOf("Java+XML", "Kotlin+XML", "Compose")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_host)

        // 配置 Toolbar：标题 + 返回按钮
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = pageTitle
        toolbar.setNavigationOnClickListener { finish() }

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        // 将 Fragment 列表交给 ViewPager2
        val fragments = createFragments()
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }

        // 将 TabLayout 与 ViewPager2 联动
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}
