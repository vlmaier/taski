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
import org.vmaier.tidfl.databinding.FragmentChartWeeklyXpBinding
import org.vmaier.tidfl.features.tasks.TaskFragment
import org.vmaier.tidfl.utils.Utils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.floor


/**
 * Created by Vladas Maier
 * on 14.06.2020
 * at 20:08
 */
class ChartWeeklyXpFragment : TaskFragment() {

    companion object {
        lateinit var binding: FragmentChartWeeklyXpBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        MainActivity.toolbar.title = getString(R.string.heading_statistics)
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_chart_weekly_xp, container, false
        )

        val calendar = Calendar.getInstance(Locale.GERMANY)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val day1 = App.dateFormat.format(calendar.time).split(" ")[0]
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
        val day2 = App.dateFormat.format(calendar.time).split(" ")[0]
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
        val day3 = App.dateFormat.format(calendar.time).split(" ")[0]
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
        val day4 = App.dateFormat.format(calendar.time).split(" ")[0]
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        val day5 = App.dateFormat.format(calendar.time).split(" ")[0]
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        val day6 = App.dateFormat.format(calendar.time).split(" ")[0]
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val day7 = App.dateFormat.format(calendar.time).split(" ")[0]

        val xpValueDay1 = db.taskDao().coundDailyXpValue("%$day1%").toFloat()
        val xpValueDay2 = db.taskDao().coundDailyXpValue("%$day2%").toFloat()
        val xpValueDay3 = db.taskDao().coundDailyXpValue("%$day3%").toFloat()
        val xpValueDay4 = db.taskDao().coundDailyXpValue("%$day4%").toFloat()
        val xpValueDay5 = db.taskDao().coundDailyXpValue("%$day5%").toFloat()
        val xpValueDay6 = db.taskDao().coundDailyXpValue("%$day6%").toFloat()
        val xpValueDay7 = db.taskDao().coundDailyXpValue("%$day7%").toFloat()

        val values = ArrayList<Entry>()
        values.add(Entry(0f, xpValueDay1))
        values.add(Entry(1f, xpValueDay2))
        values.add(Entry(2f, xpValueDay3))
        values.add(Entry(3f, xpValueDay4))
        values.add(Entry(4f, xpValueDay5))
        values.add(Entry(5f, xpValueDay6))
        values.add(Entry(6f, xpValueDay7))

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
        val captions = arrayListOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
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

        binding.chart.setExtraOffsets(20f, 20f, 10f, 20f)
        binding.chart.description.isEnabled = false
        binding.chart.legend.isEnabled = false
        binding.chart.setTouchEnabled(false)

        val p = binding.chart.getPaint(Chart.PAINT_INFO);
        p.textSize = 64f
        p.color = Utils.getThemeColor(requireContext(), R.attr.colorOnSurface)
        binding.chart.setNoDataText("No data available.")

        binding.chart.invalidate()
        binding.chart.animateXY(1000, 1000)
        return binding.root
    }
}