package org.vmaier.tidfl.features.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import org.vmaier.tidfl.MainActivity
import org.vmaier.tidfl.R
import org.vmaier.tidfl.databinding.FragmentHelpBinding


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
        MainActivity.bottomNav.visibility = View.GONE
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_help, container, false
        )
        binding.licensesButton.setOnClickListener {
            val intent = Intent(activity, OssLicensesMenuActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }
}