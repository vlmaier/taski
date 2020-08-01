package com.vmaier.taski.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.vmaier.taski.Const
import com.vmaier.taski.R


class Onboarding : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isColorTransitionsEnabled = true
        setImmersiveMode()

        addSlide(AppIntroFragment.newInstance(
            title = resources.getString(R.string.intro_slide_1_top),
            imageDrawable = R.drawable.ic_octopus,
            backgroundColor = resources.getColor(R.color.colorPrimaryLightDefault),
            description = resources.getString(R.string.intro_slide_1_bottom)
        ))
        addSlide(AppIntroFragment.newInstance(
            title = resources.getString(R.string.intro_slide_2_top),
            imageDrawable = R.drawable.ic_skills_200,
            backgroundColor = resources.getColor(R.color.colorPrimaryLightSailor),
            description = resources.getString(R.string.intro_slide_2_bottom)
        ))
        addSlide(AppIntroFragment.newInstance(
            title = resources.getString(R.string.intro_slide_3_top),
            imageDrawable = R.drawable.ic_tasks_200,
            backgroundColor = resources.getColor(R.color.colorPrimaryVariantLightRoyal),
            description = resources.getString(R.string.intro_slide_3_bottom)
        ))
        addSlide(AppIntroFragment.newInstance(
            title = resources.getString(R.string.intro_slide_4_top),
            imageDrawable = R.drawable.ic_progress_200,
            backgroundColor = resources.getColor(R.color.colorPrimaryDarkPilot),
            description = resources.getString(R.string.intro_slide_4_bottom)
        ))
        addSlide(AppIntroFragment.newInstance(
            title = resources.getString(R.string.intro_slide_5_top),
            imageDrawable = R.drawable.ic_statistics_200,
            backgroundColor = resources.getColor(R.color.colorPrimaryVariantLightCreeper),
            description = resources.getString(R.string.intro_slide_5_bottom)
        ))
        addSlide(AppIntroFragment.newInstance(
            title = resources.getString(R.string.intro_slide_6_top),
            imageDrawable = R.drawable.ic_theme_200,
            backgroundColor = resources.getColor(R.color.colorSecondaryCoral),
            description = resources.getString(R.string.intro_slide_6_bottom)
        ))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        completeIntro()
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        completeIntro()
        finish()
    }

    private fun completeIntro() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit()
            .putBoolean(Const.Prefs.ONBOARDING, false)
            .apply()
    }
}