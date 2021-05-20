package com.vmaier.taski.features.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vmaier.taski.BuildConfig
import com.vmaier.taski.MainActivity.Companion.toggleBottomMenu
import com.vmaier.taski.MainActivity.Companion.toolbar
import com.vmaier.taski.R
import com.vmaier.taski.databinding.FragmentManualBinding
import io.noties.markwon.Markwon


/**
 * Created by Vladas Maier
 * on 19.08.2020
 * at 20:23
 */
class ManualFragment : Fragment() {

    companion object {
        lateinit var binding: FragmentManualBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saved: Bundle?
    ): View {
        super.onCreateView(inflater, container, saved)
        toolbar.title = getString(R.string.heading_manual)
        toggleBottomMenu(false, View.GONE)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manual, container, false)

        val version = "v${BuildConfig.VERSION_NAME}"
        binding.version.text = version
        val md = Markwon.create(requireContext())
        md.setMarkdown(binding.text, resources.getString(R.string.text_manual))

        return binding.root
    }
}