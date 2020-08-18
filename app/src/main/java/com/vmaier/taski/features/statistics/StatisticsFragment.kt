package com.vmaier.taski.features.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vmaier.taski.MainActivity.Companion.toggleBottomMenu
import com.vmaier.taski.MainActivity.Companion.toolbar
import com.vmaier.taski.R
import com.vmaier.taski.databinding.FragmentStatisticsBinding


/**
 * Created by Vladas Maier
 * on 11.06.2020
 * at 20:43
 */
class StatisticsFragment : Fragment() {

    lateinit var binding: FragmentStatisticsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        toolbar.title = getString(R.string.heading_statistics)
        toggleBottomMenu(false, View.GONE)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false)
        setupViewPager()
        return binding.root
    }

    private fun setupViewPager() {
        val adapter = StatisticsPagerAdapter(childFragmentManager)
        adapter.addFragment(ChartSkillXpFragment(), getString(R.string.heading_skill_xp_title))
        adapter.addFragment(ChartCategoryXpFragment(), getString(R.string.heading_category_xp_title))
        adapter.addFragment(ChartDailyXpFragment(), getString(R.string.heading_daily_xp_title))
        adapter.addFragment(ChartWeeklyXpFragment(), getString(R.string.heading_weekly_xp_title))
        adapter.addFragment(ChartWeeklyTasksFragment(), getString(R.string.heading_weekly_tasks_title))
        binding.viewpager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewpager)
    }

    override fun onResume() {
        super.onResume()
        binding.viewpager.adapter?.notifyDataSetChanged()
    }
}