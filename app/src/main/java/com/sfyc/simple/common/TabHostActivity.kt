package com.sfyc.simple.common

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sfyc.simple.R

abstract class TabHostActivity : AppCompatActivity() {

    @get:StringRes
    abstract val pageTitleResId: Int

    abstract fun createFragments(): List<Fragment>

    private val tabTitleResIds = listOf(
        R.string.tab_java_xml,
        R.string.tab_kotlin_xml,
        R.string.tab_compose
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_host)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(pageTitleResId)
        toolbar.setNavigationOnClickListener { finish() }

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val fragments = createFragments()

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getString(tabTitleResIds[position])
        }.attach()
    }
}
