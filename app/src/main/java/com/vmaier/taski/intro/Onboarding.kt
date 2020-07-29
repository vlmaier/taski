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
            title = "Welcome to Taski",
            imageDrawable = R.drawable.ic_octopus,
            backgroundColor = resources.getColor(R.color.colorPrimaryLightDefault),
            description = "your gamified task scheduler."
        ))
        addSlide(AppIntroFragment.newInstance(
            title = "Create skills",
            imageDrawable = R.drawable.ic_skills_200,
            backgroundColor = resources.getColor(R.color.colorPrimaryLightSailor),
            description = "to represent your personal abilities."
        ))
        addSlide(AppIntroFragment.newInstance(
            title = "Create tasks",
            imageDrawable = R.drawable.ic_tasks_200,
            backgroundColor = resources.getColor(R.color.colorPrimaryVariantLightRoyal),
            description = "to accomplish your goals."
        ))
        addSlide(AppIntroFragment.newInstance(
            title = "Assign skills to tasks",
            imageDrawable = R.drawable.ic_progress_200,
            backgroundColor = resources.getColor(R.color.colorPrimaryDarkPilot),
            description = "and see the progress while accomplishing."
        ))
        addSlide(AppIntroFragment.newInstance(
            title = "Keep track of your progress",
            imageDrawable = R.drawable.ic_statistics_200,
            backgroundColor = resources.getColor(R.color.colorPrimaryVariantLightCreeper),
            description = "with various charts and graphs."
        ))
        addSlide(AppIntroFragment.newInstance(
            title = "Enjoy the selection",
            imageDrawable = R.drawable.ic_theme_200,
            backgroundColor = resources.getColor(R.color.colorSecondaryCoral),
            description = "of numerous hand-picked themes"
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