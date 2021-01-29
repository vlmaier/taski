package com.vmaier.taski.features.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.mikepenz.aboutlibraries.LibsBuilder
import com.vmaier.taski.BuildConfig
import com.vmaier.taski.MainActivity.Companion.toggleBottomMenu
import com.vmaier.taski.MainActivity.Companion.toolbar
import com.vmaier.taski.R
import com.vmaier.taski.databinding.FragmentHelpBinding
import com.vmaier.taski.intro.Onboarding


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
    ): View {
        super.onCreateView(inflater, container, saved)
        toolbar.title = getString(R.string.heading_help)
        toggleBottomMenu(false, View.GONE)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_help, container, false)
        binding.manualButton.setOnClickListener {
            it.findNavController().navigate(
                HelpFragmentDirections.actionHelpFragmentToManualFragment()
            )
        }
        binding.replayIntroButton.setOnClickListener {
            val intent = Intent(requireContext(), Onboarding::class.java)
            startActivity(intent)
        }
        binding.licensesButton.setOnClickListener {
            val fragment = LibsBuilder()
                .withAboutVersionShown(false)
                .withSortEnabled(true)
                .supportFragment()
            replaceFragment(fragment)
        }
        binding.versionButton.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder
                .setTitle(getString(R.string.app_name))
                .setMessage(resources.getString(R.string.version, BuildConfig.VERSION_NAME))
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(true)
            dialogBuilder.create().show()
        }
        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        val backStateName = fragment.javaClass.name
        val manager: FragmentManager = childFragmentManager
        val fragmentPopped: Boolean = manager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped) {
            manager.beginTransaction()
                .replace(R.id.help_fragment_container, fragment)
                .addToBackStack(backStateName)
                .commit()
        }
        toolbar.title = getString(R.string.heading_licenses)
        binding.manualButton.visibility = View.GONE
        binding.replayIntroButton.visibility = View.GONE
        binding.licensesButton.visibility = View.GONE
        binding.versionButton.visibility = View.GONE
    }
}