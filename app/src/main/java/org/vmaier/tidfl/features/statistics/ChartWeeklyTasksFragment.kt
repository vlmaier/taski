package org.vmaier.tidfl.features.statistics

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
import org.vmaier.tidfl.App
import org.vmaier.tidfl.MainActivity
import org.vmaier.tidfl.R
import org.vmaier.tidfl.databinding.FragmentChartWeeklyTasksBinding
import org.vmaier.tidfl.features.tasks.TaskFragment
import org.vmaier.tidfl.utils.Utils
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
        inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        MainActivity.toolbar.title = getString(R.string.heading_statistics)
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_chart_weekly_tasks, container, false
        )

        val values = ArrayList<Entry>()
        values.add(Entry(0f, getAmountOfTasksForDayOfTheWeek(Calendar.MONDAY)))
        values.add(Entry(1f, getAmountOfTasksForDayOfTheWeek(Calendar.TUESDAY)))
        values.add(Entry(2f, getAmountOfTasksForDayOfTheWeek(Calendar.WEDNESDAY)))
        values.add(Entry(3f, getAmountOfTasksForDayOfTheWeek(Calendar.THURSDAY)))
        values.add(Entry(4f, getAmountOfTasksForDayOfTheWeek(Calendar.FRIDAY)))
        values.add(Entry(5f, getAmountOfTasksForDayOfTheWeek(Calendar.SATURDAY)))
        values.add(Entry(6f, getAmountOfTasksForDayOfTheWeek(Calendar.SUNDAY)))

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
        val captions = arrayListOf(
            getString(R.string.term_monday_short),
            getString(R.string.term_tuesday_short),
            getString(R.string.term_wednesday_short),
            getString(R.string.term_thursday_short),
            getString(R.string.term_friday_short),
            getString(R.string.term_saturday_short),
            getString(R.string.term_sunday_short)
        )
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

    private fun getAmountOfTasksForDayOfTheWeek(day: Int): Float {
        val calendar = Calendar.getInstance(Locale.GERMANY)
        calendar.set(Calendar.DAY_OF_WEEK, day)
        val closedAtDay = App.dateFormat.format(calendar.time).split(" ")[0]
        return db.taskDao().countDailyTasks("%$closedAtDay%").toFloat()
    }
}