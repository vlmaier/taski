package org.vmaier.tidfl

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class SkillsTests {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    companion object {
        @BeforeClass fun setup() {
            InstrumentationRegistry.getInstrumentation()
                .targetContext.applicationContext.deleteDatabase("tidfl.db")
        }
    }

    @Test
    fun createSkill() {

        val hamburger = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.toolbar),
                        childAtPosition(
                            withClassName(`is`("com.google.android.material.appbar.AppBarLayout")),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        hamburger.perform(click())

        val navigationMenuItemView = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(
                            withId(R.id.nav_view),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        navigationMenuItemView.perform(click())

        val fab = onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(
                    allOf(
                        withId(R.id.task_list_layout),
                        childAtPosition(
                            withId(R.id.nav_host_fragment),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        fab.perform(click())

        val nameView = onView(
            allOf(
                withId(R.id.name),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    1
                )
            )
        )
        nameView.perform(scrollTo(), replaceText("Coding"), closeSoftKeyboard())

        val categoryView = onView(
            allOf(
                withId(R.id.category),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    5
                )
            )
        )
        categoryView.perform(
            scrollTo(),
            replaceText("Intellect"),
            closeSoftKeyboard()
        )

        val selectIconButton = onView(
            allOf(
                withId(R.id.select_icon_button),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        selectIconButton.perform(scrollTo(), click())

        val searchTextInput = onView(
            allOf(
                withId(R.id.icd_edt_search),
                childAtPosition(
                    allOf(
                        withId(R.id.icd_layout),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        searchTextInput.perform(replaceText("braces"), closeSoftKeyboard())

        Thread.sleep(500)

        val rv = onView(
            allOf(
                withId(R.id.icd_rcv_icon_list),
                childAtPosition(
                    withId(R.id.icd_layout),
                    5
                )
            )
        )
        rv.perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        val selectDialogButton = onView(
            allOf(
                withId(R.id.icd_btn_select), withText("Select"),
                childAtPosition(
                    allOf(
                        withId(R.id.icd_layout),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    11
                ),
                isDisplayed()
            )
        )
        selectDialogButton.perform(click())

        val createButton = onView(
            allOf(
                withId(R.id.create_skill_button), withText("Create"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    2
                )
            )
        )
        createButton.perform(scrollTo(), click())

        val viewGroup = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.cv),
                        childAtPosition(
                            withId(R.id.rv),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        viewGroup.check(matches(isDisplayed()))

        val name = onView(
            allOf(
                withId(R.id.skill_name), withText("Coding")
            )
        )
        name.check(matches(isDisplayed()))

        val category = onView(
            allOf(
                withId(R.id.skill_category), withText("Intellect")
            )
        )
        category.check(matches(isDisplayed()))

        val level = onView(
            allOf(
                withId(R.id.skill_level), withText("Level 1")
            )
        )
        level.check(matches(isDisplayed()))

        val xpValue = onView(
            allOf(
                withId(R.id.skill_xp_gain), withText("0 XP")
            )
        )
        xpValue.check(matches(isDisplayed()))

        val icon = onView(
            allOf(
                withId(R.id.skill_icon)
            )
        )
        icon.check(matches(isDisplayed()))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
