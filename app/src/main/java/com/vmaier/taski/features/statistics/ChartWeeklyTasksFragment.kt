package com.vmaier.taski.features.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.vmaier.taski.MainActivity.Companion.toolbar
import com.vmaier.taski.R
import com.vmaier.taski.data.StartOfTheWeek
import com.vmaier.taski.databinding.FragmentChartWeeklyTasksBinding
import com.vmaier.taski.features.tasks.TaskFragment
import com.vmaier.taski.utils.Utils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.floor


/**
 * Created by Vladas Maier
 * on 19.06.2020
 * at 14:28
 */
class ChartWeeklyTasksFragment : TaskFragment() {

    companion object {
        lateinit var binding: FragmentChartWeeklyTasksBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saved: Bundle?
    ): View {
        super.onCreateView(inflater, container, saved)
        toolbar.title = getString(R.string.heading_statistics)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_chart_weekly_tasks,
            container,
            false
        )

        val prefStartOfTheWeek = StartOfTheWeek
            .valueOf(prefService.getStartOfTheWeek().uppercase(Locale.getDefault()))
        val daysOfWeekWithValue = mutableListOf<Float>()
        daysOfWeekWithValue.add(getAmountOfTasksForDayOfTheWeek(Calendar.MONDAY))
        daysOfWeekWithValue.add(getAmountOfTasksForDayOfTheWeek(Calendar.TUESDAY))
        daysOfWeekWithValue.add(getAmountOfTasksForDayOfTheWeek(Calendar.WEDNESDAY))
        daysOfWeekWithValue.add(getAmountOfTasksForDayOfTheWeek(Calendar.THURSDAY))
        daysOfWeekWithValue.add(getAmountOfTasksForDayOfTheWeek(Calendar.FRIDAY))
        daysOfWeekWithValue.add(getAmountOfTasksForDayOfTheWeek(Calendar.SATURDAY))
        daysOfWeekWithValue.add(getAmountOfTasksForDayOfTheWeek(Calendar.SUNDAY))
        val captions = mutableListOf(
            getString(R.string.term_monday_short),
            getString(R.string.term_tuesday_short),
            getString(R.string.term_wednesday_short),
            getString(R.string.term_thursday_short),
            getString(R.string.term_friday_short),
            getString(R.string.term_saturday_short),
            getString(R.string.term_sunday_short)
        )
        when (prefStartOfTheWeek) {
            StartOfTheWeek.SATURDAY -> {
                Utils.swapFirstAndLastElements(daysOfWeekWithValue, 2)
                Utils.swapFirstAndLastElements(captions, 2)
            }
            StartOfTheWeek.SUNDAY -> {
                Utils.swapFirstAndLastElements(daysOfWeekWithValue)
                Utils.swapFirstAndLastElements(captions)
            }
            else -> {
                // nothing to do
            }
        }
        val values = ArrayList<Entry>()
        if (daysOfWeekWithValue.sum() > 0) {
            for (i in 0..6) {
                values.add(Entry(i.toFloat(), daysOfWeekWithValue[i]))
            }
        }

        val dataSet = LineDataSet(values, "")
        dataSet.axisDependency = YAxis.AxisDependency.LEFT
        dataSet.lineWidth = 3f
        dataSet.setColors(Utils.getThemeColor(requireContext(), R.attr.colorSecondary))
        dataSet.setCircleColor(Utils.getThemeColor(requireContext(), R.attr.colorPrimary))
        dataSet.circleRadius = 6f
        dataSet.circleHoleRadius = 3f
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Utils.getThemeColor(requireContext(), R.attr.colorOnSurface)

        val data = LineData(dataSet)
        data.setValueTextSize(14f)
        data.setDrawValues(true)
        data.setValueTextColor(Utils.getThemeColor(requireContext(), R.attr.colorOnSurface))
        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return floor(value.toDouble()).toInt().toString()
            }
        })

        val xAxis = binding.chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        val formatter = IndexAxisValueFormatter(captions)
        xAxis.valueFormatter = formatter
        xAxis.textSize = 14f
        xAxis.textColor = Utils.getThemeColor(requireContext(), R.attr.colorOnSurface)

        val yAxis = binding.chart.axisLeft
        yAxis.granularity = 1f
        yAxis.textSize = 14f
        yAxis.textColor = Utils.getThemeColor(requireContext(), R.attr.colorOnSurface)

        binding.chart.axisRight.isEnabled = false

        if (values.isNotEmpty()) binding.chart.data = data

        binding.chart.setExtraOffsets(20f, 20f, 35f, 20f)
        binding.chart.description.isEnabled = false
        binding.chart.legend.isEnabled = false
        binding.chart.setTouchEnabled(false)

        val p = binding.chart.getPaint(Chart.PAINT_INFO)
        p.textSize = 48f
        p.color = Utils.getThemeColor(requireContext(), R.attr.colorOnSurface)
        binding.chart.setNoDataText(getString(R.string.description_no_data))

        binding.chart.invalidate()
        binding.chart.animateXY(1000, 1000)
        return binding.root
    }

    private fun getAmountOfTasksForDayOfTheWeek(day: Int): Float {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, day)
        return taskRepository.countDailyTasks(calendar).toFloat()
    }
}