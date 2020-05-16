package org.vmaier.tidfl

import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*


@LargeTest
@RunWith(AndroidJUnit4::class)
class TaskTests {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun createWithDefaultDifficulty() {

        // click on fab button
        onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
                isDisplayed()
            )
        )
            .perform(click())

        // type in the goal field
        onView(
            withId(R.id.goal)
        )
            .perform(
                typeText("Work on App"),
                closeSoftKeyboard()
            )

        // type in the details field
        onView(
            withId(R.id.details)
        )
            .perform(
                typeText("Things I Do For Loot"),
                closeSoftKeyboard()
            )

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button)
        )
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search)
        )
            .perform(
                typeText("phone"),
                closeSoftKeyboard()
            )

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(
            allOf(
                withId(R.id.icd_rcv_icon_list),
                childAtPosition(withId(R.id.icd_layout), 5)
            )
        )
            .perform(actionOnItemAtPosition<ViewHolder>(12, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select)
        )
            .perform(click())

        // drag on seekbar and change duration
        onView(
            withId(R.id.duration_bar)
        )
            .perform(setProgress(9))

        // validate task duration value
        onView(
            allOf(
                withId(R.id.duration_value),
                withText(
                    mActivityTestRule.activity.applicationContext.resources
                        .getQuantityString(R.plurals.duration_hour, 4, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task xp gain value
        onView(
            allOf(
                withId(R.id.xp_gain_value),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 240)
                )
            )
        )
            .check(matches(isDisplayed()))

        // click on due date
        onView(
            withId(R.id.deadline_date)
        )
            .perform(scrollTo(), click())

        // click on OK button within the date picker dialog
        onView(
            allOf(
                withId(android.R.id.button1),
                withText("OK")
            )
        )
            .perform(click())

        // click on due time
        onView(
            withId(R.id.deadline_time)
        )
            .perform(scrollTo(), click())

        // click on OK button within the time picker dialog
        onView(
            allOf(
                withId(android.R.id.button1),
                withText("OK")
            )
        )
            .perform(click())

        // click on create to create task
        onView(
            withId(R.id.create_task_button)
        )
            .perform(click())

        // validate that task was created and task item is displayed in the recycler view
        onView(
            allOf(
                withId(R.id.cv),
                childAtPosition(childAtPosition(withId(R.id.rv), 0), 0),
                isDisplayed()
            )
        )
            .check(matches(isDisplayed()))

        // validate task item goal
        onView(
            allOf(
                withId(R.id.task_goal),
                withText("Work on App")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item details
        onView(
            allOf(
                withId(R.id.task_details),
                withText("Things I Do For Loot")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item duration
        onView(
            allOf(
                withId(R.id.task_duration),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.unit_hour_short, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item xp gain value
        onView(
            allOf(
                withId(R.id.task_xp),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 240)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item icon
        onView(
            allOf(
                withId(R.id.task_icon),
                // validate icon ID
                withTagValue(Matchers.`is`(201))
            )
        )
            .check(matches(isDisplayed()))
    }

    @Test
    fun createWithTrivialDifficulty() {

        // click on fab button
        onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
                isDisplayed()
            )
        )
            .perform(click())

        // type in the goal field
        onView(
            withId(R.id.goal)
        )
            .perform(
                typeText("Work on App"),
                closeSoftKeyboard()
            )

        // type in the details field
        onView(
            withId(R.id.details)
        )
            .perform(
                typeText("Things I Do For Loot"),
                closeSoftKeyboard()
            )

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button)
        )
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search)
        )
            .perform(
                typeText("phone"),
                closeSoftKeyboard()
            )

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(
            allOf(
                withId(R.id.icd_rcv_icon_list),
                childAtPosition(withId(R.id.icd_layout), 5)
            )
        )
            .perform(actionOnItemAtPosition<ViewHolder>(12, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select)
        )
            .perform(click())

        // drag on seekbar and change duration
        onView(
            withId(R.id.duration_bar)
        )
            .perform(setProgress(9))

        // validate task duration value
        onView(
            allOf(
                withId(R.id.duration_value),
                withText(
                    mActivityTestRule.activity.applicationContext.resources
                        .getQuantityString(R.plurals.duration_hour, 4, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // choose difficulty
        onView(
            withId(R.id.trivial)
        )
            .perform(click())

        // validate task xp gain value
        onView(
            allOf(
                withId(R.id.xp_gain_value),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 120)
                )
            )
        )
            .check(matches(isDisplayed()))

        // click on due date
        onView(
            withId(R.id.deadline_date)
        )
            .perform(scrollTo(), click())

        // click on OK button within the date picker dialog
        onView(
            allOf(
                withId(android.R.id.button1),
                withText("OK")
            )
        )
            .perform(click())

        // click on due time
        onView(
            withId(R.id.deadline_time)
        )
            .perform(scrollTo(), click())

        // click on OK button within the time picker dialog
        onView(
            allOf(
                withId(android.R.id.button1),
                withText("OK")
            )
        )
            .perform(click())

        // click on create to create task
        onView(
            withId(R.id.create_task_button)
        )
            .perform(click())

        // validate that task was created and task item is displayed in the recycler view
        onView(
            allOf(
                withId(R.id.cv),
                childAtPosition(childAtPosition(withId(R.id.rv), 0), 0),
                isDisplayed()
            )
        )
            .check(matches(isDisplayed()))

        // validate task item goal
        onView(
            allOf(
                withId(R.id.task_goal),
                withText("Work on App")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item details
        onView(
            allOf(
                withId(R.id.task_details),
                withText("Things I Do For Loot")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item duration
        onView(
            allOf(
                withId(R.id.task_duration),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.unit_hour_short, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item xp gain value
        onView(
            allOf(
                withId(R.id.task_xp),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 120)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item icon
        onView(
            allOf(
                withId(R.id.task_icon),
                // validate icon ID
                withTagValue(Matchers.`is`(201))
            )
        )
            .check(matches(isDisplayed()))
    }

    @Test
    fun createWithRegularDifficulty() {

        // click on fab button
        onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
                isDisplayed()
            )
        )
            .perform(click())

        // type in the goal field
        onView(
            withId(R.id.goal)
        )
            .perform(
                typeText("Work on App"),
                closeSoftKeyboard()
            )

        // type in the details field
        onView(
            withId(R.id.details)
        )
            .perform(
                typeText("Things I Do For Loot"),
                closeSoftKeyboard()
            )

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button)
        )
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search)
        )
            .perform(
                typeText("phone"),
                closeSoftKeyboard()
            )

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(
            allOf(
                withId(R.id.icd_rcv_icon_list),
                childAtPosition(withId(R.id.icd_layout), 5)
            )
        )
            .perform(actionOnItemAtPosition<ViewHolder>(12, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select)
        )
            .perform(click())

        // drag on seekbar and change duration
        onView(
            withId(R.id.duration_bar)
        )
            .perform(setProgress(9))

        // validate task duration value
        onView(
            allOf(
                withId(R.id.duration_value),
                withText(
                    mActivityTestRule.activity.applicationContext.resources
                        .getQuantityString(R.plurals.duration_hour, 4, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // choose difficulty
        onView(
            withId(R.id.regular)
        )
            .perform(click())

        // validate task xp gain value
        onView(
            allOf(
                withId(R.id.xp_gain_value),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 240)
                )
            )
        )
            .check(matches(isDisplayed()))

        // click on due date
        onView(
            withId(R.id.deadline_date)
        )
            .perform(scrollTo(), click())

        // click on OK button within the date picker dialog
        onView(
            allOf(
                withId(android.R.id.button1),
                withText("OK")
            )
        )
            .perform(click())

        // click on due time
        onView(
            withId(R.id.deadline_time)
        )
            .perform(scrollTo(), click())

        // click on OK button within the time picker dialog
        onView(
            allOf(
                withId(android.R.id.button1),
                withText("OK")
            )
        )
            .perform(click())

        // click on create to create task
        onView(
            withId(R.id.create_task_button)
        )
            .perform(click())

        // validate that task was created and task item is displayed in the recycler view
        onView(
            allOf(
                withId(R.id.cv),
                childAtPosition(childAtPosition(withId(R.id.rv), 0), 0),
                isDisplayed()
            )
        )
            .check(matches(isDisplayed()))

        // validate task item goal
        onView(
            allOf(
                withId(R.id.task_goal),
                withText("Work on App")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item details
        onView(
            allOf(
                withId(R.id.task_details),
                withText("Things I Do For Loot")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item duration
        onView(
            allOf(
                withId(R.id.task_duration),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.unit_hour_short, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item xp gain value
        onView(
            allOf(
                withId(R.id.task_xp),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 240)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item icon
        onView(
            allOf(
                withId(R.id.task_icon),
                // validate icon ID
                withTagValue(Matchers.`is`(201))
            )
        )
            .check(matches(isDisplayed()))
    }

    @Test
    fun createWithHardDifficulty() {

        // click on fab button
        onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
                isDisplayed()
            )
        )
            .perform(click())

        // type in the goal field
        onView(
            withId(R.id.goal)
        )
            .perform(
                typeText("Work on App"),
                closeSoftKeyboard()
            )

        // type in the details field
        onView(
            withId(R.id.details)
        )
            .perform(
                typeText("Things I Do For Loot"),
                closeSoftKeyboard()
            )

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button)
        )
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search)
        )
            .perform(
                typeText("phone"),
                closeSoftKeyboard()
            )

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(
            allOf(
                withId(R.id.icd_rcv_icon_list),
                childAtPosition(withId(R.id.icd_layout), 5)
            )
        )
            .perform(actionOnItemAtPosition<ViewHolder>(12, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select)
        )
            .perform(click())

        // drag on seekbar and change duration
        onView(
            withId(R.id.duration_bar)
        )
            .perform(setProgress(9))

        // validate task duration value
        onView(
            allOf(
                withId(R.id.duration_value),
                withText(
                    mActivityTestRule.activity.applicationContext.resources
                        .getQuantityString(R.plurals.duration_hour, 4, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // choose difficulty
        onView(
            withId(R.id.hard)
        )
            .perform(click())

        // validate task xp gain value
        onView(
            allOf(
                withId(R.id.xp_gain_value),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 360)
                )
            )
        )
            .check(matches(isDisplayed()))

        // click on due date
        onView(
            withId(R.id.deadline_date)
        )
            .perform(scrollTo(), click())

        // click on OK button within the date picker dialog
        onView(
            allOf(
                withId(android.R.id.button1),
                withText("OK")
            )
        )
            .perform(click())

        // click on due time
        onView(
            withId(R.id.deadline_time)
        )
            .perform(scrollTo(), click())

        // click on OK button within the time picker dialog
        onView(
            allOf(
                withId(android.R.id.button1),
                withText("OK")
            )
        )
            .perform(click())

        // click on create to create task
        onView(
            withId(R.id.create_task_button)
        )
            .perform(click())

        // validate that task was created and task item is displayed in the recycler view
        onView(
            allOf(
                withId(R.id.cv),
                childAtPosition(childAtPosition(withId(R.id.rv), 0), 0),
                isDisplayed()
            )
        )
            .check(matches(isDisplayed()))

        // validate task item goal
        onView(
            allOf(
                withId(R.id.task_goal),
                withText("Work on App")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item details
        onView(
            allOf(
                withId(R.id.task_details),
                withText("Things I Do For Loot")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item duration
        onView(
            allOf(
                withId(R.id.task_duration),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.unit_hour_short, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item xp gain value
        onView(
            allOf(
                withId(R.id.task_xp),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 360)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item icon
        onView(
            allOf(
                withId(R.id.task_icon),
                // validate icon ID
                withTagValue(Matchers.`is`(201))
            )
        )
            .check(matches(isDisplayed()))
    }

    @Test
    fun createWithInsaneDifficulty() {

        // click on fab button
        onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
                isDisplayed()
            )
        )
            .perform(click())

        // type in the goal field
        onView(
            withId(R.id.goal)
        )
            .perform(
                typeText("Work on App"),
                closeSoftKeyboard()
            )

        // type in the details field
        onView(
            withId(R.id.details)
        )
            .perform(
                typeText("Things I Do For Loot"),
                closeSoftKeyboard()
            )

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button)
        )
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search)
        )
            .perform(
                typeText("phone"),
                closeSoftKeyboard()
            )

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(
            allOf(
                withId(R.id.icd_rcv_icon_list),
                childAtPosition(withId(R.id.icd_layout), 5)
            )
        )
            .perform(actionOnItemAtPosition<ViewHolder>(12, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select)
        )
            .perform(click())

        // drag on seekbar and change duration
        onView(
            withId(R.id.duration_bar)
        )
            .perform(setProgress(9))

        // validate task duration value
        onView(
            allOf(
                withId(R.id.duration_value),
                withText(
                    mActivityTestRule.activity.applicationContext.resources
                        .getQuantityString(R.plurals.duration_hour, 4, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // choose difficulty
        onView(
            withId(R.id.insane)
        )
            .perform(click())

        // validate task xp gain value
        onView(
            allOf(
                withId(R.id.xp_gain_value),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 480)
                )
            )
        )
            .check(matches(isDisplayed()))

        // click on due date
        onView(
            withId(R.id.deadline_date)
        )
            .perform(scrollTo(), click())

        // click on OK button within the date picker dialog
        onView(
            allOf(
                withId(android.R.id.button1),
                withText("OK")
            )
        )
            .perform(click())

        // click on due time
        onView(
            withId(R.id.deadline_time)
        )
            .perform(scrollTo(), click())

        // click on OK button within the time picker dialog
        onView(
            allOf(
                withId(android.R.id.button1),
                withText("OK")
            )
        )
            .perform(click())

        // click on create to create task
        onView(
            withId(R.id.create_task_button)
        )
            .perform(click())

        // validate that task was created and task item is displayed in the recycler view
        onView(
            allOf(
                withId(R.id.cv),
                childAtPosition(childAtPosition(withId(R.id.rv), 0), 0),
                isDisplayed()
            )
        )
            .check(matches(isDisplayed()))

        // validate task item goal
        onView(
            allOf(
                withId(R.id.task_goal),
                withText("Work on App")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item details
        onView(
            allOf(
                withId(R.id.task_details),
                withText("Things I Do For Loot")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item duration
        onView(
            allOf(
                withId(R.id.task_duration),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.unit_hour_short, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item xp gain value
        onView(
            allOf(
                withId(R.id.task_xp),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 480)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item icon
        onView(
            allOf(
                withId(R.id.task_icon),
                // validate icon ID
                withTagValue(Matchers.`is`(201))
            )
        )
            .check(matches(isDisplayed()))
    }

    @Test
    fun createWithGoalOnly() {

        // click on fab button
        onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
                isDisplayed()
            )
        )
            .perform(click())

        // type in the goal field
        onView(
            withId(R.id.goal)
        )
            .perform(
                typeText("Work on App"),
                closeSoftKeyboard()
            )

        // validate task default duration value
        onView(
            allOf(
                withId(R.id.duration_value),
                withText(
                    mActivityTestRule.activity.applicationContext.resources
                        .getQuantityString(R.plurals.duration_minute, 15, 15)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task default xp gain value
        onView(
            allOf(
                withId(R.id.xp_gain_value),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 15)
                )
            )
        )
            .check(matches(isDisplayed()))

        // click on create to create task
        onView(
            withId(R.id.create_task_button)
        )
            .perform(click())

        // validate that task was created and task item is displayed in the recycler view
        onView(
            allOf(
                withId(R.id.cv),
                childAtPosition(childAtPosition(withId(R.id.rv), 0), 0),
                isDisplayed()
            )
        )
            .check(matches(isDisplayed()))

        // validate task item goal
        onView(
            allOf(
                withId(R.id.task_goal),
                withText("Work on App")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item details
        onView(
            allOf(
                withId(R.id.task_details),
                withText("")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item duration
        onView(
            allOf(
                withId(R.id.task_duration),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.unit_minute_short, 15)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item xp gain value
        onView(
            allOf(
                withId(R.id.task_xp),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 15)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item icon
        onView(
            withId(R.id.task_icon)
        )
            .check(matches(isDisplayed()))
    }

    @Test
    fun createWithoutGoal() {

        // click on fab button
        onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
                isDisplayed()
            )
        )
            .perform(click())

        // type in the goal field
        onView(
            withId(R.id.goal)
        )
            .perform(
                clearText(),
                closeSoftKeyboard()
            )

        // click on create to create task
        onView(
            withId(R.id.create_task_button)
        )
            .perform(click())

        // validate error message
        onView(
            withId(R.id.goal)
        )
            .check(
                matches(
                    hasErrorText(
                        mActivityTestRule.activity.applicationContext
                            .getString(R.string.error_goal_cannot_be_empty)
                    )
                )
            )
    }

    @Test
    fun createWithoutDueTime() {

        // click on fab button
        onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
                isDisplayed()
            )
        )
            .perform(click())

        // type in the goal field
        onView(
            withId(R.id.goal)
        )
            .perform(
                typeText("Work on App"),
                closeSoftKeyboard()
            )

        // type in the details field
        onView(
            withId(R.id.details)
        )
            .perform(
                typeText("Things I Do For Loot"),
                closeSoftKeyboard()
            )

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button)
        )
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search)
        )
            .perform(
                typeText("phone"),
                closeSoftKeyboard()
            )

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(
            allOf(
                withId(R.id.icd_rcv_icon_list),
                childAtPosition(withId(R.id.icd_layout), 5)
            )
        )
            .perform(actionOnItemAtPosition<ViewHolder>(12, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select)
        )
            .perform(click())

        // drag on seekbar and change duration
        onView(
            withId(R.id.duration_bar)
        )
            .perform(setProgress(9))

        // validate task duration value
        onView(
            allOf(
                withId(R.id.duration_value),
                withText(
                    mActivityTestRule.activity.applicationContext.resources
                        .getQuantityString(R.plurals.duration_hour, 4, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task xp gain value
        onView(
            allOf(
                withId(R.id.xp_gain_value),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 240)
                )
            )
        )
            .check(matches(isDisplayed()))

        // click on due date
        onView(
            withId(R.id.deadline_date)
        )
            .perform(scrollTo(), click())

        // click on OK button within the date picker dialog
        onView(
            allOf(
                withId(android.R.id.button1),
                withText("OK")
            )
        )
            .perform(click())

        // click on create to create task
        onView(
            withId(R.id.create_task_button)
        )
            .perform(click())

        // validate that task was created and task item is displayed in the recycler view
        onView(
            allOf(
                withId(R.id.cv),
                childAtPosition(childAtPosition(withId(R.id.rv), 0), 0),
                isDisplayed()
            )
        )
            .check(matches(isDisplayed()))

        // validate task item goal
        onView(
            allOf(
                withId(R.id.task_goal),
                withText("Work on App")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item details
        onView(
            allOf(
                withId(R.id.task_details),
                withText("Things I Do For Loot")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item duration
        onView(
            allOf(
                withId(R.id.task_duration),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.unit_hour_short, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item xp gain value
        onView(
            allOf(
                withId(R.id.task_xp),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 240)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item icon
        onView(
            allOf(
                withId(R.id.task_icon),
                // validate icon ID
                withTagValue(Matchers.`is`(201))
            )
        )
            .check(matches(isDisplayed()))

        // click on task item to view it
        onView(
            allOf(
                withId(R.id.rv),
                childAtPosition(withId(R.id.task_list_layout), 0)
            )
        )
            .perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        // validate task due date
        onView(
            withId(R.id.deadline_date)
        )
            .check(matches(withText(App.dateFormat.format(Date()).split(" ")[0])))

        // validate default task due time
        onView(
            withId(R.id.deadline_time)
        )
            .check(matches(withText("08:00")))
    }

    @Test
    fun createWithOneSkill() {

        // click on hamburger menu
        onView(
            allOf(
                childAtPosition(withId(R.id.toolbar), 2),
                isDisplayed()
            )
        )
            .perform(click())

        // click on skills menu item
        onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(withId(R.id.nav_view), 0)
                    ), 2
                ),
                isDisplayed()
            )
        )
            .perform(click())

        // click on fab buttom
        onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(allOf(withId(R.id.skill_list_layout)), 1),
                isDisplayed()
            )
        )
            .perform(click())

        // type in the name field
        onView(
            withId(R.id.name)
        )
            .perform(
                typeText("Coding"),
                closeSoftKeyboard()
            )

        // type in the category field
        onView(
            withId(R.id.category)
        )
            .perform(
                typeText("Intellect"),
                closeSoftKeyboard()
            )

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button)
        )
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search)
        )
            .perform(
                typeText("braces"),
                closeSoftKeyboard()
            )

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(
            allOf(
                withId(R.id.icd_rcv_icon_list),
                childAtPosition(withId(R.id.icd_layout), 5)
            )
        )
            .perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select)
        )
            .perform(click())

        // click on create to create skill
        onView(
            withId(R.id.create_skill_button)
        )
            .perform(click())

        // validate that skill was created and skill item is displayed in the recycler view
        onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.cv),
                        childAtPosition(withId(R.id.rv), 0)
                    ), 0
                )
            )
        )
            .check(matches(isDisplayed()))

        // click on hamburger menu
        onView(
            allOf(
                childAtPosition(withId(R.id.toolbar), 2),
                isDisplayed()
            )
        )
            .perform(click())

        // click on task menu item
        onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(withId(R.id.nav_view), 0)
                    ), 1
                ),
                isDisplayed()
            )
        )
            .perform(click())

        // click on fab button
        onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
                isDisplayed()
            )
        )
            .perform(click())

        // type in the goal field
        onView(
            withId(R.id.goal)
        )
            .perform(
                typeText("Work on App"),
                closeSoftKeyboard()
            )

        // type in the details field
        onView(
            withId(R.id.details)
        )
            .perform(
                typeText("Things I Do For Loot"),
                closeSoftKeyboard()
            )

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button)
        )
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search)
        )
            .perform(
                typeText("phone"),
                closeSoftKeyboard()
            )

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(
            allOf(
                withId(R.id.icd_rcv_icon_list),
                childAtPosition(withId(R.id.icd_layout), 5)
            )
        )
            .perform(actionOnItemAtPosition<ViewHolder>(12, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select)
        )
            .perform(click())

        // drag on seekbar and change duration
        onView(
            withId(R.id.duration_bar)
        )
            .perform(setProgress(9))

        // validate task duration value
        onView(
            allOf(
                withId(R.id.duration_value),
                withText(
                    mActivityTestRule.activity.applicationContext.resources
                        .getQuantityString(R.plurals.duration_hour, 4, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // choose difficulty
        onView(
            withId(R.id.hard)
        )
            .perform(click())

        // validate task xp gain value
        onView(
            allOf(
                withId(R.id.xp_gain_value),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 360)
                )
            )
        )
            .check(matches(isDisplayed()))

        // type in the skills field
        onView(
            withId(R.id.skills)
        )
            .perform(
                scrollTo(),
                click(),
                typeText("cod"),
                closeSoftKeyboard()
            )

        // wait a bit for found skill to appear
        Thread.sleep(500)

        // click on the found skill chip
        onView(
            withText("Coding")
        )
            .inRoot(withDecorView(not(`is`(mActivityTestRule.activity.window.decorView))))
            .perform(click())

        // click on create to create task
        onView(
            withId(R.id.create_task_button)
        )
            .perform(click())

        // validate that task was created and task item is displayed in the recycler view
        onView(
            allOf(
                withId(R.id.cv),
                childAtPosition(childAtPosition(withId(R.id.rv), 0), 0),
                isDisplayed()
            )
        )
            .check(matches(isDisplayed()))

        // validate task item goal
        onView(
            allOf(
                withId(R.id.task_goal),
                withText("Work on App")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item details
        onView(
            allOf(
                withId(R.id.task_details),
                withText("Things I Do For Loot")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item duration
        onView(
            allOf(
                withId(R.id.task_duration),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.unit_hour_short, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item xp gain value
        onView(
            allOf(
                withId(R.id.task_xp),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 360)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item icon
        onView(
            allOf(
                withId(R.id.task_icon),
                // validate icon ID
                withTagValue(Matchers.`is`(201))
            )
        )
            .check(matches(isDisplayed()))

        // validate amount of assigned skills
        onView(
            withId(R.id.skill_amount)
        )
            .check(
                matches(
                    withText(
                        mActivityTestRule.activity.resources
                            .getQuantityString(R.plurals.term_skill, 1, 1)
                    )
                )
            )

        // validate existence of skill icon
        onView(
            withId(R.id.skill_icon)
        )
            .check(matches(isDisplayed()))
    }

    @Test
    fun createWithTwoSkills() {

        // click on hamburger menu
        onView(
            allOf(
                childAtPosition(withId(R.id.toolbar), 2),
                isDisplayed()
            )
        )
            .perform(click())

        // click on skills menu item
        onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(withId(R.id.nav_view), 0)
                    ), 2
                ),
                isDisplayed()
            )
        )
            .perform(click())

        // click on fab buttom
        onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(allOf(withId(R.id.skill_list_layout)), 1),
                isDisplayed()
            )
        )
            .perform(click())

        // type in the name field
        onView(
            withId(R.id.name)
        )
            .perform(
                typeText("Coding"),
                closeSoftKeyboard()
            )

        // type in the category field
        onView(
            withId(R.id.category)
        )
            .perform(
                typeText("Intellect"),
                closeSoftKeyboard()
            )

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button)
        )
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search)
        )
            .perform(
                typeText("braces"),
                closeSoftKeyboard()
            )

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(
            allOf(
                withId(R.id.icd_rcv_icon_list),
                childAtPosition(withId(R.id.icd_layout), 5)
            )
        )
            .perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select)
        )
            .perform(click())

        // click on create to create first skill
        onView(
            withId(R.id.create_skill_button)
        )
            .perform(click())

        // click on fab buttom
        onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(allOf(withId(R.id.skill_list_layout)), 1),
                isDisplayed()
            )
        )
            .perform(click())

        // type in the name field
        onView(
            withId(R.id.name)
        )
            .perform(
                typeText("Kotlin"),
                closeSoftKeyboard()
            )

        // type in the category field
        onView(
            withId(R.id.category)
        )
            .perform(
                typeText("Programming"),
                closeSoftKeyboard()
            )

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button)
        )
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search)
        )
            .perform(
                typeText("braces"),
                closeSoftKeyboard()
            )

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(
            allOf(
                withId(R.id.icd_rcv_icon_list),
                childAtPosition(withId(R.id.icd_layout), 5)
            )
        )
            .perform(actionOnItemAtPosition<ViewHolder>(2, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select)
        )
            .perform(click())

        // click on create to create second skill
        onView(
            withId(R.id.create_skill_button)
        )
            .perform(click())

        // validate that skills were created and skill items are displayed in the recycler view
        onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.cv),
                        childAtPosition(withId(R.id.rv), 0)
                    ), 0
                )
            )
        )
            .check(matches(isDisplayed()))
        onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.cv),
                        childAtPosition(withId(R.id.rv), 1)
                    ), 0
                )
            )
        )
            .check(matches(isDisplayed()))

        // click on hamburger menu
        onView(
            allOf(
                childAtPosition(withId(R.id.toolbar), 2),
                isDisplayed()
            )
        )
            .perform(click())

        // click on task menu item
        onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(withId(R.id.nav_view), 0)
                    ), 1
                ),
                isDisplayed()
            )
        )
            .perform(click())

        // click on fab button
        onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
                isDisplayed()
            )
        )
            .perform(click())

        // type in the goal field
        onView(
            withId(R.id.goal)
        )
            .perform(
                typeText("Work on App"),
                closeSoftKeyboard()
            )

        // type in the details field
        onView(
            withId(R.id.details)
        )
            .perform(
                typeText("Things I Do For Loot"),
                closeSoftKeyboard()
            )

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button)
        )
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search)
        )
            .perform(
                typeText("phone"),
                closeSoftKeyboard()
            )

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(
            allOf(
                withId(R.id.icd_rcv_icon_list),
                childAtPosition(withId(R.id.icd_layout), 5)
            )
        )
            .perform(actionOnItemAtPosition<ViewHolder>(12, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select)
        )
            .perform(click())

        // drag on seekbar and change duration
        onView(
            withId(R.id.duration_bar)
        )
            .perform(setProgress(9))

        // validate task duration value
        onView(
            allOf(
                withId(R.id.duration_value),
                withText(
                    mActivityTestRule.activity.applicationContext.resources
                        .getQuantityString(R.plurals.duration_hour, 4, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // choose difficulty
        onView(
            withId(R.id.hard)
        )
            .perform(click())

        // validate task xp gain value
        onView(
            allOf(
                withId(R.id.xp_gain_value),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 360)
                )
            )
        )
            .check(matches(isDisplayed()))

        // type in the skills field
        onView(
            withId(R.id.skills)
        )
            .perform(
                scrollTo(),
                click(),
                typeText("cod"),
                closeSoftKeyboard()
            )

        // wait a bit for found skill to appear
        Thread.sleep(500)

        // click on the found skill chip
        onView(
            withText("Coding")
        )
            .inRoot(withDecorView(not(`is`(mActivityTestRule.activity.window.decorView))))
            .perform(click())

        // type in the skills field
        onView(
            withId(R.id.skills)
        )
            .perform(
                scrollTo(),
                click(),
                typeText("kot"),
                closeSoftKeyboard()
            )

        // wait a bit for found skill to appear
        Thread.sleep(500)

        // click on the found skill chip
        onView(
            withText("Kotlin")
        )
            .inRoot(withDecorView(not(`is`(mActivityTestRule.activity.window.decorView))))
            .perform(click())

        // click on create to create task
        onView(
            withId(R.id.create_task_button)
        )
            .perform(click())

        // validate that task was created and task item is displayed in the recycler view
        onView(
            allOf(
                withId(R.id.cv),
                childAtPosition(childAtPosition(withId(R.id.rv), 0), 0),
                isDisplayed()
            )
        )
            .check(matches(isDisplayed()))

        // validate task item goal
        onView(
            allOf(
                withId(R.id.task_goal),
                withText("Work on App")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item details
        onView(
            allOf(
                withId(R.id.task_details),
                withText("Things I Do For Loot")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item duration
        onView(
            allOf(
                withId(R.id.task_duration),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.unit_hour_short, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item xp gain value
        onView(
            allOf(
                withId(R.id.task_xp),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 360)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item icon
        onView(
            allOf(
                withId(R.id.task_icon),
                // validate icon ID
                withTagValue(Matchers.`is`(201))
            )
        )
            .check(matches(isDisplayed()))

        // validate amount of assigned skills
        onView(
            withId(R.id.skill_amount)
        )
            .check(
                matches(
                    withText(
                        mActivityTestRule.activity.resources
                            .getQuantityString(R.plurals.term_skill, 2, 2)
                    )
                )
            )

        // validate existence of skill icon
        onView(
            withId(R.id.skill_icon)
        )
            .check(matches(isDisplayed()))
    }

    @Test
    fun createWithNonExistingSkill() {

        // click on fab button
        onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
                isDisplayed()
            )
        )
            .perform(click())

        // type in the goal field
        onView(
            withId(R.id.goal)
        )
            .perform(
                typeText("Work on App"),
                closeSoftKeyboard()
            )

        // type in the details field
        onView(
            withId(R.id.details)
        )
            .perform(
                typeText("Things I Do For Loot"),
                closeSoftKeyboard()
            )

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button)
        )
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search)
        )
            .perform(
                typeText("phone"),
                closeSoftKeyboard()
            )

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(
            allOf(
                withId(R.id.icd_rcv_icon_list),
                childAtPosition(withId(R.id.icd_layout), 5)
            )
        )
            .perform(actionOnItemAtPosition<ViewHolder>(12, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select)
        )
            .perform(click())

        // drag on seekbar and change duration
        onView(
            withId(R.id.duration_bar)
        )
            .perform(setProgress(9))

        // validate task duration value
        onView(
            allOf(
                withId(R.id.duration_value),
                withText(
                    mActivityTestRule.activity.applicationContext.resources
                        .getQuantityString(R.plurals.duration_hour, 4, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // choose difficulty
        onView(
            withId(R.id.hard)
        )
            .perform(click())

        // validate task xp gain value
        onView(
            allOf(
                withId(R.id.xp_gain_value),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 360)
                )
            )
        )
            .check(matches(isDisplayed()))

        // type in the skills field
        onView(
            withId(R.id.skills)
        )
            .perform(
                scrollTo(),
                click(),
                typeText("Coding"),
                closeSoftKeyboard()
            )

        // click on create to create task
        onView(
            withId(R.id.create_task_button)
        )
            .perform(click())

        // validate that task was created and task item is displayed in the recycler view
        onView(
            allOf(
                withId(R.id.cv),
                childAtPosition(childAtPosition(withId(R.id.rv), 0), 0),
                isDisplayed()
            )
        )
            .check(matches(isDisplayed()))

        // validate task item goal
        onView(
            allOf(
                withId(R.id.task_goal),
                withText("Work on App")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item details
        onView(
            allOf(
                withId(R.id.task_details),
                withText("Things I Do For Loot")
            )
        )
            .check(matches(isDisplayed()))

        // validate task item duration
        onView(
            allOf(
                withId(R.id.task_duration),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.unit_hour_short, 4)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item xp gain value
        onView(
            allOf(
                withId(R.id.task_xp),
                withText(
                    mActivityTestRule.activity.applicationContext
                        .getString(R.string.term_xp_value, 360)
                )
            )
        )
            .check(matches(isDisplayed()))

        // validate task item icon
        onView(
            allOf(
                withId(R.id.task_icon),
                // validate icon ID
                withTagValue(Matchers.`is`(201))
            )
        )
            .check(matches(isDisplayed()))

        // validate amount of assigned skills
        onView(
            withId(R.id.skill_amount)
        )
            .check(matches(not(isDisplayed())))

        // validate existence of skill icon
        onView(
            withId(R.id.skill_icon)
        )
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun validateNoSkillsHint() {

        // click on fab button
        onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
                isDisplayed()
            )
        )
            .perform(click())

        // validate hint
        onView(
            withId(R.id.skills)
        )
            .check(matches(withHint(R.string.hint_no_skills)))
    }

    private fun setProgress(progress: Int): ViewAction? {
        return object : ViewAction {
            override fun perform(uiController: UiController?, view: View) {
                val seekBar = view as SeekBar
                seekBar.progress = progress
            }

            override fun getDescription(): String {
                return "Set a progress"
            }

            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(SeekBar::class.java)
            }
        }
    }

    private fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {
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
