package com.vmaier.taski.intro

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.vmaier.taski.R
import com.vmaier.taski.services.PreferenceService


class Onboarding : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isColorTransitionsEnabled = true
        setImmersiveMode()

        addSlide(
            AppIntroFragment.newInstance(
                title = resources.getString(R.string.intro_slide_1_top),
                imageDrawable = R.drawable.ic_octopus,
                backgroundColor = ContextCompat.getColor(this, R.color.colorPrimaryLightDefault),
                description = resources.getString(R.string.intro_slide_1_bottom)
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                title = resources.getString(R.string.intro_slide_2_top),
                imageDrawable = R.drawable.ic_skills_200,
                backgroundColor = ContextCompat.getColor(this, R.color.colorPrimaryLightSailor),
                description = resources.getString(R.string.intro_slide_2_bottom)
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                title = resources.getString(R.string.intro_slide_3_top),
                imageDrawable = R.drawable.ic_tasks_200,
                backgroundColor = ContextCompat.getColor(
                    this,
                    R.color.colorPrimaryVariantLightRoyal
                ),
                description = resources.getString(R.string.intro_slide_3_bottom)
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                title = resources.getString(R.string.intro_slide_4_top),
                imageDrawable = R.drawable.ic_progress_200,
                backgroundColor = ContextCompat.getColor(this, R.color.colorPrimaryDarkPilot),
                description = resources.getString(R.string.intro_slide_4_bottom)
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                title = resources.getString(R.string.intro_slide_5_top),
                imageDrawable = R.drawable.ic_statistics_200,
                backgroundColor = ContextCompat.getColor(
                    this,
                    R.color.colorPrimaryVariantLightCreeper
                ),
                description = resources.getString(R.string.intro_slide_5_bottom)
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                title = resources.getString(R.string.intro_slide_6_top),
                imageDrawable = R.drawable.ic_theme_200,
                backgroundColor = ContextCompat.getColor(this, R.color.colorSecondaryCoral),
                description = resources.getString(R.string.intro_slide_6_bottom)
            )
        )
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        completeIntro()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        completeIntro()
    }

    private fun completeIntro() {
        val prefService = PreferenceService(this)
        prefService.setOnboardingEnabled(false)
        finish()
    }
}