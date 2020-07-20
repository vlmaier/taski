package com.vmaier.taski.features.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.maltaisn.icondialog.pack.IconDrawableLoader
import com.vmaier.taski.App
import com.vmaier.taski.MainActivity
import com.vmaier.taski.R
import com.vmaier.taski.databinding.FragmentChartSkillXpBinding
import com.vmaier.taski.features.skills.SkillFragment
import com.vmaier.taski.utils.Utils
import kotlin.math.floor


/**
 * Created by Vladas Maier
 * on 11.06.2020
 * at 16:04
 */
class ChartSkillXpFragment : SkillFragment() {

    companion object {
        lateinit var binding: FragmentChartSkillXpBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        MainActivity.toolbar.title = getString(R.string.heading_statistics)
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_chart_skill_xp, container, false
        )

        val skills = db.skillDao().findAll()
        val values = ArrayList<PieEntry>()
        skills.forEach {
            val xpValue = db.skillDao().countSkillXpValue(it.id)
            if (xpValue > 0) {
                val icon = App.iconPack
                    .getIconDrawable(it.iconId, IconDrawableLoader(requireContext()))
                icon?.setTint(ContextCompat.getColor(requireContext(), R.color.colorLightDefault))
                values.add(PieEntry(xpValue.toFloat(), it.name, icon))
            }
        }

        val dataSet = PieDataSet(values, "")
        dataSet.setDrawIcons(true)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(30f, 0f)
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = PieData(dataSet)
        data.setValueTextSize(14f)
        data.setValueTextColor(Color.WHITE)
        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return floor(value.toDouble()).toInt().toString()
            }
        })

        if (values.isNotEmpty()) {
            binding.chart.data = data
        }

        binding.chart.setDrawCenterText(false)
        binding.chart.setExtraOffsets(10f, 0f, 10f, 0f)
        binding.chart.description.isEnabled = false
        binding.chart.isDrawHoleEnabled = true
        binding.chart.holeRadius = 30f
        binding.chart.transparentCircleRadius = 40f
        binding.chart.setEntryLabelColor(Color.WHITE)
        binding.chart.setHoleColor(Utils.getThemeColor(requireContext(), R.attr.colorSurface))
        binding.chart.setTouchEnabled(false)

        val legend = binding.chart.legend
        binding.chart.legend.isWordWrapEnabled = true
        binding.chart.legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.formSize = 20f
        legend.formToTextSpace = 5f
        legend.yOffset = 20f
        legend.form = Legend.LegendForm.CIRCLE
        legend.textSize = 14f
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.isWordWrapEnabled = true
        legend.textColor = Utils.getThemeColor(requireContext(), R.attr.colorOnSurface)
        legend.setDrawInside(true)

        val p = binding.chart.getPaint(Chart.PAINT_INFO);
        p.textSize = 48f
        p.color = Utils.getThemeColor(requireContext(), R.attr.colorOnSurface)
        binding.chart.setNoDataText(getString(R.string.description_no_data))

        binding.chart.invalidate()
        binding.chart.animateXY(1000, 1000)
        return binding.root
    }
}