package org.vmaier.tidfl

import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class SkillsTests {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun create() {

        // click on hamburger menu
        onView(allOf(
            childAtPosition(withId(R.id.toolbar), 2),
            isDisplayed()))
            .perform(click())

        // click on skills menu item
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.design_navigation_view),
                childAtPosition(withId(R.id.nav_view), 0)), 2),
            isDisplayed()))
            .perform(click())

        // click on fab buttom
        onView(allOf(
            withId(R.id.fab),
            childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
            isDisplayed()))
            .perform(click())

        // type in the name field
        onView(
            withId(R.id.name))
            .perform(
                replaceText("Coding"),
                closeSoftKeyboard())

        // type in the category field
        onView(
            withId(R.id.category))
            .perform(
                replaceText("Intellect"),
                closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // click on create to create skill
        onView(
            withId(R.id.create_skill_button))
            .perform(click())

        // validate that skill was created and skill item is displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(matches(isDisplayed()))

        // validate skill name
        onView(allOf(
            withId(R.id.skill_name),
            withText("Coding")))
            .check(matches(isDisplayed()))

        // validate skill category
        onView(allOf(withId(
            R.id.skill_category),
            withText("Intellect")))
            .check(matches(isDisplayed()))

        // validate initial skill level
        onView(allOf(
            withId(R.id.skill_level),
            withText("Level 1")))
            .check(matches(isDisplayed()))

        // validate initial skill xp value
        onView(allOf(
            withId(R.id.skill_xp_gain),
            withText("0 XP")))
            .check(matches(isDisplayed()))

        // validate skill icon
        onView(allOf(
            withId(R.id.skill_icon),
            // validate icon ID
            withTagValue(`is`(896))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun view() {

        // click on hamburger menu
        onView(allOf(
            childAtPosition(withId(R.id.toolbar), 2),
            isDisplayed()))
            .perform(click())

        // click on skills menu item
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.design_navigation_view),
                childAtPosition(withId(R.id.nav_view), 0)), 2),
            isDisplayed()))
            .perform(click())

        // click on fab buttom
        onView(allOf(
            withId(R.id.fab),
            childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
            isDisplayed()))
            .perform(click())

        // type in the name field
        onView(
            withId(R.id.name))
            .perform(
                replaceText("Coding"),
                closeSoftKeyboard())

        // type in the category field
        onView(
            withId(R.id.category))
            .perform(
                replaceText("Intellect"),
                closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // click on button to create skill
        onView(
            withId(R.id.create_skill_button))
            .perform(click())

        // validate that skill was created and skill item is displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(matches(isDisplayed()))

        // click on skill item
        onView(allOf(
            withId(R.id.rv),
            childAtPosition(withId(R.id.task_list_layout), 0)))
            .perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        // validate skill name
        onView(allOf(
            withId(R.id.name),
            withText("Coding")))
            .check(matches(isDisplayed()))

        // validate skill category
        onView(allOf(withId(
            R.id.category),
            withText("Intellect")))
            .check(matches(isDisplayed()))

        // validate skill icon
        onView(allOf(
            withId(R.id.icon_button),
            // validate icon ID
            withTagValue(`is`(896))))
            .check(matches(isDisplayed()))

        // validate initial skill level
        onView(allOf(
            withId(R.id.skill_level_value),
            withText("1")))
            .check(matches(isDisplayed()))

        // validate initial skill xp value
        onView(allOf(
            withId(R.id.skill_xp_value),
            withText("0 XP")))
            .check(matches(isDisplayed()))

        // validate initial open tasks value
        onView(allOf(
            withId(R.id.skill_open_tasks_value),
            withText("0")))
            .check(matches(isDisplayed()))

        // validate initial done tasks value
        onView(allOf(
            withId(R.id.skill_done_tasks_value),
            withText("0")))
            .check(matches(isDisplayed()))
    }

    @Test
    fun edit() {

        // click on hamburger menu
        onView(allOf(
            childAtPosition(withId(R.id.toolbar), 2),
            isDisplayed()))
            .perform(click())

        // click on skills menu item
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.design_navigation_view),
                childAtPosition(withId(R.id.nav_view), 0)), 2),
            isDisplayed()))
            .perform(click())

        // click on fab buttom
        onView(allOf(
            withId(R.id.fab),
            childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
            isDisplayed()))
            .perform(click())

        // type in the name field
        onView(
            withId(R.id.name))
            .perform(
                replaceText("Coding"),
                closeSoftKeyboard())

        // type in the category field
        onView(
            withId(R.id.category))
            .perform(
                replaceText("Intellect"),
                closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // click on create to create skill
        onView(
            withId(R.id.create_skill_button))
            .perform(click())

        // validate that skill was created and skill item is displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(matches(isDisplayed()))

        // click on skill item
        onView(allOf(
            withId(R.id.rv),
            childAtPosition(withId(R.id.task_list_layout), 0)))
            .perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        // change skill name
        onView(allOf(
            withId(R.id.name),
            withText("Coding")))
            .perform(replaceText("Programming"))
        onView(allOf(
            withId(R.id.name),
            withText("Programming")))
            .perform(closeSoftKeyboard())

        // change category name
        onView(allOf(
            withId(R.id.category),
            withText("Intellect")))
            .perform(replaceText("Computer Science"))
        onView(allOf(
            withId(R.id.category),
            withText("Computer Science")))
            .perform(closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(scrollTo(), click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(2, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // press back
        UiDevice
            .getInstance(InstrumentationRegistry.getInstrumentation())
            .pressBack()

        // validate that skill is still displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(matches(isDisplayed()))

        // validate toast message was shown
        onView(withText("Skill updated"))
            .inRoot(withDecorView(not(mActivityTestRule.activity.window.decorView)))
            .check(matches(isDisplayed()))

        // validate skill name
        onView(allOf(
            withId(R.id.skill_name),
            withText("Programming")))
            .check(matches(isDisplayed()))

        // validate skill category
        onView(allOf(withId(
            R.id.skill_category),
            withText("Computer Science")))
            .check(matches(isDisplayed()))

        // validate initial skill level
        onView(allOf(
            withId(R.id.skill_level),
            withText("Level 1")))
            .check(matches(isDisplayed()))

        // validate initial skill xp value
        onView(allOf(
            withId(R.id.skill_xp_gain),
            withText("0 XP")))
            .check(matches(isDisplayed()))

        // validate skill icon
        onView(allOf(
            withId(R.id.skill_icon),
            // validate icon ID
            withTagValue(`is`(897))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun delete() {

        // click on hamburger menu
        onView(allOf(
            childAtPosition(withId(R.id.toolbar), 2),
            isDisplayed()))
            .perform(click())

        // click on skills menu item
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.design_navigation_view),
                childAtPosition(withId(R.id.nav_view), 0)), 2),
            isDisplayed()))
            .perform(click())

        // click on fab buttom
        onView(allOf(
            withId(R.id.fab),
            childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
            isDisplayed()))
            .perform(click())

        // type in the name field
        onView(
            withId(R.id.name))
            .perform(
                replaceText("Coding"),
                closeSoftKeyboard())

        // type in the category field
        onView(
            withId(R.id.category))
            .perform(
                replaceText("Intellect"),
                closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // click on create to create skill
        onView(
            withId(R.id.create_skill_button))
            .perform(click())

        // validate that skill was created and skill item is displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(matches(isDisplayed()))

        // click on skill item
        onView(allOf(
            withId(R.id.rv),
            childAtPosition(withId(R.id.task_list_layout), 0)))
            .perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        // click on button to delete skill
        onView(
            withId(R.id.delete_skill_button))
            .perform(click())

        // validate that skill was deleted and skill item is not displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(doesNotExist())
    }

    @Test
    fun restore() {

        // click on hamburger menu
        onView(allOf(
            childAtPosition(withId(R.id.toolbar), 2),
            isDisplayed()))
            .perform(click())

        // click on skills menu item
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.design_navigation_view),
                childAtPosition(withId(R.id.nav_view), 0)), 2),
            isDisplayed()))
            .perform(click())

        // click on fab buttom
        onView(allOf(
            withId(R.id.fab),
            childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
            isDisplayed()))
            .perform(click())

        // type in the name field
        onView(
            withId(R.id.name))
            .perform(
                replaceText("Coding"),
                closeSoftKeyboard())

        // type in the category field
        onView(
            withId(R.id.category))
            .perform(
                replaceText("Intellect"),
                closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // click on create to create skill
        onView(
            withId(R.id.create_skill_button))
            .perform(click())

        // validate that skill was created and skill item is displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(matches(isDisplayed()))

        // click on skill item
        onView(allOf(
            withId(R.id.rv),
            childAtPosition(withId(R.id.task_list_layout), 0)))
            .perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        // click on button to delete skill
        onView(
            withId(R.id.delete_skill_button))
            .perform(click())

        // validate that skill was deleted and skill item is not displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(doesNotExist())

        // click on undo button to restore skill
        onView(allOf(
            withId(R.id.snackbar_action),
            withText("UNDO")))
            .perform(click())

        // validate that skill was restored and skill item is displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun editAndDelete() {

        // click on hamburger menu
        onView(allOf(
            childAtPosition(withId(R.id.toolbar), 2),
            isDisplayed()))
            .perform(click())

        // click on skills menu item
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.design_navigation_view),
                childAtPosition(withId(R.id.nav_view), 0)), 2),
            isDisplayed()))
            .perform(click())

        // click on fab buttom
        onView(allOf(
            withId(R.id.fab),
            childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
            isDisplayed()))
            .perform(click())

        // type in the name field
        onView(
            withId(R.id.name))
            .perform(
                replaceText("Coding"),
                closeSoftKeyboard())

        // type in the category field
        onView(
            withId(R.id.category))
            .perform(
                replaceText("Intellect"),
                closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // click on create to create skill
        onView(
            withId(R.id.create_skill_button))
            .perform(click())

        // validate that skill was created and skill item is displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(matches(isDisplayed()))

        // click on skill item
        onView(allOf(
            withId(R.id.rv),
            childAtPosition(withId(R.id.task_list_layout), 0)))
            .perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        // change skill name
        onView(allOf(
            withId(R.id.name),
            withText("Coding")))
            .perform(replaceText("Programming"))
        onView(allOf(
            withId(R.id.name),
            withText("Programming")))
            .perform(closeSoftKeyboard())

        // change category name
        onView(allOf(
            withId(R.id.category),
            withText("Intellect")))
            .perform(replaceText("Computer Science"))
        onView(allOf(
            withId(R.id.category),
            withText("Computer Science")))
            .perform(closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(scrollTo(), click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(2, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // click on button to delete skill
        onView(
            withId(R.id.delete_skill_button))
            .perform(click())

        // validate that skill was deleted and skill item is not displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(doesNotExist())
    }

    @Test
    fun cancelWhileCreating() {

        // click on hamburger menu
        onView(allOf(
            childAtPosition(withId(R.id.toolbar), 2),
            isDisplayed()))
            .perform(click())

        // click on skills menu item
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.design_navigation_view),
                childAtPosition(withId(R.id.nav_view), 0)), 2),
            isDisplayed()))
            .perform(click())

        // click on fab buttom
        onView(allOf(
            withId(R.id.fab),
            childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
            isDisplayed()))
            .perform(click())

        // type in the name field
        onView(
            withId(R.id.name))
            .perform(
                replaceText("Coding"),
                closeSoftKeyboard())

        // type in the category field
        onView(
            withId(R.id.category))
            .perform(
                replaceText("Intellect"),
                closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // click on cancel button
        onView(
            withId(R.id.cancel_button))
            .perform(click())

        // validate that skill was not created and skill item is not displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(doesNotExist())
    }

    @Test
    fun createWithoutName() {

        // click on hamburger menu
        onView(allOf(
            childAtPosition(withId(R.id.toolbar), 2),
            isDisplayed()))
            .perform(click())

        // click on skills menu item
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.design_navigation_view),
                childAtPosition(withId(R.id.nav_view), 0)), 2),
            isDisplayed()))
            .perform(click())

        // click on fab buttom
        onView(allOf(
            withId(R.id.fab),
            childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
            isDisplayed()))
            .perform(click())

        // type in the name field
        onView(
            withId(R.id.name))
            .perform(
                replaceText(""),
                closeSoftKeyboard())

        // type in the category field
        onView(
            withId(R.id.category))
            .perform(
                replaceText("Intellect"),
                closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // click on create to create skill
        onView(
            withId(R.id.create_skill_button))
            .perform(click())

        // validate error message
        onView(
            withId(R.id.name))
            .check(matches(hasErrorText("Name cannot be empty")))
    }

    @Test
    fun createWithoutCategory() {

        // click on hamburger menu
        onView(allOf(
            childAtPosition(withId(R.id.toolbar), 2),
            isDisplayed()))
            .perform(click())

        // click on skills menu item
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.design_navigation_view),
                childAtPosition(withId(R.id.nav_view), 0)), 2),
            isDisplayed()))
            .perform(click())

        // click on fab buttom
        onView(allOf(
            withId(R.id.fab),
            childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
            isDisplayed()))
            .perform(click())

        // type in the name field
        onView(
            withId(R.id.name))
            .perform(
                replaceText("Coding"),
                closeSoftKeyboard())

        // type in the category field
        onView(
            withId(R.id.category))
            .perform(
                replaceText(""),
                closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // click on create to create skill
        onView(
            withId(R.id.create_skill_button))
            .perform(click())

        // validate that skill was created and skill item is displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(matches(isDisplayed()))

        // validate skill name
        onView(allOf(
            withId(R.id.skill_name),
            withText("Coding")))
            .check(matches(isDisplayed()))

        // validate initial skill level
        onView(allOf(
            withId(R.id.skill_level),
            withText("Level 1")))
            .check(matches(isDisplayed()))

        // validate initial skill xp value
        onView(allOf(
            withId(R.id.skill_xp_gain),
            withText("0 XP")))
            .check(matches(isDisplayed()))

        // validate skill icon
        onView(allOf(
            withId(R.id.skill_icon),
            // validate icon ID
            withTagValue(`is`(896))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun leaveNameEmptyWhileEditing() {

        // click on hamburger menu
        onView(allOf(
            childAtPosition(withId(R.id.toolbar), 2),
            isDisplayed()))
            .perform(click())

        // click on skills menu item
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.design_navigation_view),
                childAtPosition(withId(R.id.nav_view), 0)), 2),
            isDisplayed()))
            .perform(click())

        // click on fab buttom
        onView(allOf(
            withId(R.id.fab),
            childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
            isDisplayed()))
            .perform(click())

        // type in the name field
        onView(
            withId(R.id.name))
            .perform(
                replaceText("Coding"),
                closeSoftKeyboard())

        // type in the category field
        onView(
            withId(R.id.category))
            .perform(
                replaceText("Intellect"),
                closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // click on create to create skill
        onView(
            withId(R.id.create_skill_button))
            .perform(click())

        // validate that skill was created and skill item is displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(matches(isDisplayed()))

        // click on skill item
        onView(allOf(
            withId(R.id.rv),
            childAtPosition(withId(R.id.task_list_layout), 0)))
            .perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        // change skill name
        onView(allOf(
            withId(R.id.name),
            withText("Coding")))
            .perform(replaceText(""))
        onView(allOf(
            withId(R.id.name),
            withText("")))
            .perform(closeSoftKeyboard())

        // change category name
        onView(allOf(
            withId(R.id.category),
            withText("Intellect")))
            .perform(replaceText("Computer Science"))
        onView(allOf(
            withId(R.id.category),
            withText("Computer Science")))
            .perform(closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(scrollTo(), click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(2, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // press back
        UiDevice
            .getInstance(InstrumentationRegistry.getInstrumentation())
            .pressBack()

        // validate error message
        onView(
            withId(R.id.name))
            .check(matches(hasErrorText("Name cannot be empty")))
    }

    @Test
    fun leaveCategoryEmptyWhileEditing() {

        // click on hamburger menu
        onView(allOf(
            childAtPosition(withId(R.id.toolbar), 2),
            isDisplayed()))
            .perform(click())

        // click on skills menu item
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.design_navigation_view),
                childAtPosition(withId(R.id.nav_view), 0)), 2),
            isDisplayed()))
            .perform(click())

        // click on fab buttom
        onView(allOf(
            withId(R.id.fab),
            childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
            isDisplayed()))
            .perform(click())

        // type in the name field
        onView(
            withId(R.id.name))
            .perform(
                replaceText("Coding"),
                closeSoftKeyboard())

        // type in the category field
        onView(
            withId(R.id.category))
            .perform(
                replaceText("Intellect"),
                closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // click on create to create skill
        onView(
            withId(R.id.create_skill_button))
            .perform(click())

        // validate that skill was created and skill item is displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(matches(isDisplayed()))

        // click on skill item
        onView(allOf(
            withId(R.id.rv),
            childAtPosition(withId(R.id.task_list_layout), 0)))
            .perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        // change skill name
        onView(allOf(
            withId(R.id.name),
            withText("Coding")))
            .perform(replaceText("Programming"))
        onView(allOf(
            withId(R.id.name),
            withText("Programming")))
            .perform(closeSoftKeyboard())

        // change category name
        onView(allOf(
            withId(R.id.category),
            withText("Intellect")))
            .perform(replaceText(""))
        onView(
            withId(R.id.category))
            .perform(closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(scrollTo(), click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(2, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // press back
        UiDevice
            .getInstance(InstrumentationRegistry.getInstrumentation())
            .pressBack()

        // validate that skill is still displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(matches(isDisplayed()))

        // validate toast message was shown
        onView(withText("Skill updated"))
            .inRoot(withDecorView(not(mActivityTestRule.activity.window.decorView)))
            .check(matches(isDisplayed()))

        // validate skill name
        onView(allOf(
            withId(R.id.skill_name),
            withText("Programming")))
            .check(matches(isDisplayed()))

        // validate initial skill level
        onView(allOf(
            withId(R.id.skill_level),
            withText("Level 1")))
            .check(matches(isDisplayed()))

        // validate initial skill xp value
        onView(allOf(
            withId(R.id.skill_xp_gain),
            withText("0 XP")))
            .check(matches(isDisplayed()))

        // validate skill icon
        onView(allOf(
            withId(R.id.skill_icon),
            // validate icon ID
            withTagValue(`is`(897))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun rotateScreenWhileCreating() {

        // click on hamburger menu
        onView(allOf(
            childAtPosition(withId(R.id.toolbar), 2),
            isDisplayed()))
            .perform(click())

        // click on skills menu item
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.design_navigation_view),
                childAtPosition(withId(R.id.nav_view), 0)), 2),
            isDisplayed()))
            .perform(click())

        // click on fab buttom
        onView(allOf(
            withId(R.id.fab),
            childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
            isDisplayed()))
            .perform(click())

        // type in the name field
        onView(
            withId(R.id.name))
            .perform(
                replaceText("Coding"),
                closeSoftKeyboard())

        // type in the category field
        onView(
            withId(R.id.category))
            .perform(
                replaceText("Intellect"),
                closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // rotate screen
        mActivityTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        // rotate back to portrait
        mActivityTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // click on create to create skill
        onView(
            withId(R.id.create_skill_button))
            .perform(click())

        // validate that skill was created and skill item is displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(matches(isDisplayed()))

        // validate skill name
        onView(allOf(
            withId(R.id.skill_name),
            withText("Coding")))
            .check(matches(isDisplayed()))

        // validate skill category
        onView(allOf(withId(
            R.id.skill_category),
            withText("Intellect")))
            .check(matches(isDisplayed()))

        // validate initial skill level
        onView(allOf(
            withId(R.id.skill_level),
            withText("Level 1")))
            .check(matches(isDisplayed()))

        // validate initial skill xp value
        onView(allOf(
            withId(R.id.skill_xp_gain),
            withText("0 XP")))
            .check(matches(isDisplayed()))

        // validate skill icon
        onView(allOf(
            withId(R.id.skill_icon),
            // validate icon ID
            withTagValue(`is`(896))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun rotateScreenWhileEditing() {

        // click on hamburger menu
        onView(allOf(
            childAtPosition(withId(R.id.toolbar), 2),
            isDisplayed()))
            .perform(click())

        // click on skills menu item
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.design_navigation_view),
                childAtPosition(withId(R.id.nav_view), 0)), 2),
            isDisplayed()))
            .perform(click())

        // click on fab buttom
        onView(allOf(
            withId(R.id.fab),
            childAtPosition(allOf(withId(R.id.task_list_layout)), 1),
            isDisplayed()))
            .perform(click())

        // type in the name field
        onView(
            withId(R.id.name))
            .perform(
                replaceText("Coding"),
                closeSoftKeyboard())

        // type in the category field
        onView(
            withId(R.id.category))
            .perform(
                replaceText("Intellect"),
                closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // click on create to create skill
        onView(
            withId(R.id.create_skill_button))
            .perform(click())

        // validate that skill was created and skill item is displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(matches(isDisplayed()))

        // click on skill item
        onView(allOf(
            withId(R.id.rv),
            childAtPosition(withId(R.id.task_list_layout), 0)))
            .perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        // change skill name
        onView(allOf(
            withId(R.id.name),
            withText("Coding")))
            .perform(replaceText("Programming"))
        onView(allOf(
            withId(R.id.name),
            withText("Programming")))
            .perform(closeSoftKeyboard())

        // change category name
        onView(allOf(
            withId(R.id.category),
            withText("Intellect")))
            .perform(replaceText("Computer Science"))
        onView(allOf(
            withId(R.id.category),
            withText("Computer Science")))
            .perform(closeSoftKeyboard())

        // click on button to open the select icons dialog
        onView(
            withId(R.id.icon_button))
            .perform(scrollTo(), click())

        // type in the search field
        onView(
            withId(R.id.icd_edt_search))
            .perform(
                replaceText("braces"),
                closeSoftKeyboard())

        // wait a bit for search results to load
        Thread.sleep(500)

        // click on the found icon
        onView(allOf(
            withId(R.id.icd_rcv_icon_list),
            childAtPosition(withId(R.id.icd_layout), 5)))
            .perform(actionOnItemAtPosition<ViewHolder>(2, click()))

        // click on select in dialog
        onView(
            withId(R.id.icd_btn_select))
            .perform(click())

        // rotate screen
        mActivityTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        // rotate back to portrait
        mActivityTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // press back
        UiDevice
            .getInstance(InstrumentationRegistry.getInstrumentation())
            .pressBack()

        // validate that skill is still displayed in the recycler view
        onView(allOf(
            childAtPosition(allOf(
                withId(R.id.cv),
                childAtPosition(withId(R.id.rv), 0)), 0)))
            .check(matches(isDisplayed()))

        // validate toast message was shown
        onView(withText("Skill updated"))
            .inRoot(withDecorView(not(mActivityTestRule.activity.window.decorView)))
            .check(matches(isDisplayed()))

        // validate skill name
        onView(allOf(
            withId(R.id.skill_name),
            withText("Programming")))
            .check(matches(isDisplayed()))

        // validate skill category
        onView(allOf(withId(
            R.id.skill_category),
            withText("Computer Science")))
            .check(matches(isDisplayed()))

        // validate initial skill level
        onView(allOf(
            withId(R.id.skill_level),
            withText("Level 1")))
            .check(matches(isDisplayed()))

        // validate initial skill xp value
        onView(allOf(
            withId(R.id.skill_xp_gain),
            withText("0 XP")))
            .check(matches(isDisplayed()))

        // validate skill icon
        onView(allOf(
            withId(R.id.skill_icon),
            // validate icon ID
            withTagValue(`is`(897))))
            .check(matches(isDisplayed()))
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
