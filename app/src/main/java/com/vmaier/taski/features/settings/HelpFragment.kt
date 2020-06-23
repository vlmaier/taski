package com.vmaier.taski.features.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mikepenz.aboutlibraries.LibsBuilder
import com.vmaier.taski.MainActivity
import com.vmaier.taski.databinding.FragmentHelpBinding
import com.vmaier.taski.BuildConfig
import com.vmaier.taski.R


/**
 * Created by Vladas Maier
 * on 20.05.2020
 * at 20:23
 */
class HelpFragment : Fragment() {

    companion object {
        lateinit var binding: FragmentHelpBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saved: Bundle?
    ): View? {
        super.onCreateView(inflater, container, saved)
        MainActivity.toolbar.title = getString(R.string.heading_help)
        MainActivity.fab.hide()
        MainActivity.bottomNav.visibility = View.GONE
        MainActivity.bottomBar.visibility = View.GONE
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_help, container, false
        )
        binding.licensesButton.setOnClickListener {
            LibsBuilder()
                .withAboutVersionShown(false)
                .withActivityTitle(getString(R.string.heading_licenses))
                .withSortEnabled(true)
                .start(requireContext())
        }
        binding.versionButton.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder
                .setTitle(getString(R.string.app_name))
                .setMessage(resources.getString(R.string.version, BuildConfig.VERSION_NAME))
                .setIcon(R.mipmap.ic_launcher_round)
                .setCancelable(true)
            dialogBuilder.create().show()
        }
        return binding.root
    }
}