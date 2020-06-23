package com.vmaier.taski.features.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.vmaier.taski.App
import com.vmaier.taski.MainActivity
import com.vmaier.taski.R
import com.vmaier.taski.databinding.FragmentChartDailyXpBinding
import com.vmaier.taski.features.tasks.TaskFragment
import com.vmaier.taski.utils.Utils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.floor


/**
 * Created by Vladas Maier
 * on 13.06.2020
 * at 22:06
 */
class ChartDailyXpFragment : TaskFragment() {

    companion object {
        lateinit var binding: FragmentChartDailyXpBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        MainActivity.toolbar.title = getString(R.string.heading_statistics)
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_chart_daily_xp, container, false
        )

        val closedAt = App.dateFormat.format(Date()).split(" ")[0]
        val tasks = db.taskDao().findByClosedAt("%$closedAt%")
        var skillWithXpValue: MutableMap<String, Long> = mutableMapOf()
        val values = ArrayList<BarEntry>()
        tasks.forEach { task ->
            val assignedSkills = db.skillDao().findAssignedSkills(task.id)
            if (assignedSkills.isEmpty()) {
                fillSkillWithXpValue(
                    skillWithXpValue,
                    getString(R.string.heading_unassigned), task.xpValue.toLong()
                )
            } else {
                assignedSkills.forEach { skill ->
                    fillSkillWithXpValue(skillWithXpValue, skill.name, task.xpValue.toLong())
                }
            }
        }
        // sort asc by value
        skillWithXpValue = skillWithXpValue
            .toList().sortedBy { (_, value) -> value }
            .toMap().toMutableMap()
        var i = 0f
        skillWithXpValue.forEach {
            values.add(BarEntry(i++, it.value.toFloat()))
        }

        val dataSet = BarDataSet(values, "")
        dataSet.barBorderWidth = 0.9f
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = BarData(dataSet)
        data.barWidth = 0.9f
        data.setValueTextSize(14f)
        data.setValueTextColor(Utils.getThemeColor(requireContext(), R.attr.colorOnSurface))
        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return floor(value.toDouble()).toInt().toString()
            }
        })

        val xAxis = binding.chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        val captions = skillWithXpValue.keys
        val formatter = IndexAxisValueFormatter(captions)
        xAxis.valueFormatter = formatter
        xAxis.textSize = 14f
        xAxis.textColor = Utils.getThemeColor(requireContext(), R.attr.colorOnSurface)

        val yAxis = binding.chart.axisLeft
        yAxis.granularity = 1f
        yAxis.textSize = 14f
        yAxis.textColor = Utils.getThemeColor(requireContext(), R.attr.colorOnSurface)

        binding.chart.axisRight.isEnabled = false

        if (values.isNotEmpty()) {
            binding.chart.data = data
        }

        binding.chart.setExtraOffsets(20f, 20f, 35f, 20f)
        binding.chart.description.isEnabled = false
        binding.chart.setFitBars(true)
        binding.chart.legend.isEnabled = false
        binding.chart.setTouchEnabled(false)

        val p = binding.chart.getPaint(Chart.PAINT_INFO);
        p.textSize = 48f
        p.color = Utils.getThemeColor(requireContext(), R.attr.colorOnSurface)
        binding.chart.setNoDataText(getString(R.string.description_no_data))

        binding.chart.invalidate()
        binding.chart.animateXY(1000, 1000)
        return binding.root
    }

    private fun fillSkillWithXpValue(
        skillWithXpValue: MutableMap<String, Long>, skillName: String, xpValue: Long
    ) {
        val entry = skillWithXpValue[skillName]
        if (entry != null) {
            skillWithXpValue[skillName] = entry + xpValue
        } else {
            skillWithXpValue[skillName] = xpValue
        }
    }
}