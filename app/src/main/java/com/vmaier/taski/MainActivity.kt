package com.vmaier.taski

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources.Theme
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.recurpicker.Recurrence
import com.maltaisn.recurpicker.RecurrencePickerSettings
import com.maltaisn.recurpicker.format.RecurrenceFormatter
import com.maltaisn.recurpicker.list.RecurrenceListCallback
import com.maltaisn.recurpicker.list.RecurrenceListDialog
import com.maltaisn.recurpicker.picker.RecurrencePickerCallback
import com.maltaisn.recurpicker.picker.RecurrencePickerDialog
import com.maltaisn.recurpicker.picker.RecurrencePickerFragment
import com.vmaier.taski.data.Status
import com.vmaier.taski.data.entity.Category
import com.vmaier.taski.data.repository.CategoryRepository
import com.vmaier.taski.data.repository.SkillRepository
import com.vmaier.taski.data.repository.TaskRepository
import com.vmaier.taski.databinding.ActivityMainBinding
import com.vmaier.taski.features.categories.CategoryListFragment
import com.vmaier.taski.features.categories.CategoryListFragment.Companion.categoryAdapter
import com.vmaier.taski.features.categories.CategoryListFragmentDirections
import com.vmaier.taski.features.settings.HelpFragment
import com.vmaier.taski.features.settings.SettingsFragment
import com.vmaier.taski.features.skills.*
import com.vmaier.taski.features.statistics.StatisticsFragmentDirections
import com.vmaier.taski.features.tasks.*
import com.vmaier.taski.intro.Onboarding
import com.vmaier.taski.services.LevelService
import com.vmaier.taski.services.PreferenceService
import com.vmaier.taski.utils.PermissionUtils
import com.vmaier.taski.utils.Utils
import com.vmaier.taski.views.EditTextDialog
import timber.log.Timber
import java.util.*


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener,
    IconDialog.Callback, RecurrenceListCallback, RecurrencePickerCallback {

    private lateinit var navController: NavController
    private lateinit var drawerNav: NavigationView
    private lateinit var binding: ActivityMainBinding
    private lateinit var levelService: LevelService
    private lateinit var prefService: PreferenceService
    private var isBackButtonPressedOnce = false

    companion object {
        lateinit var iconDialog: IconDialog
        lateinit var toolbar: Toolbar
        lateinit var drawerLayout: DrawerLayout
        lateinit var bottomNav: BottomNavigationView
        lateinit var bottomBar: BottomAppBar
        lateinit var fab: FloatingActionButton
        lateinit var userNameView: TextView
        lateinit var avatarView: ImageView
        lateinit var taskRepository: TaskRepository
        lateinit var skillRepository: SkillRepository
        lateinit var categoryRepository: CategoryRepository

        private val recurrenceSettings = RecurrencePickerSettings()
        val recurrenceListDialog by lazy {
            RecurrenceListDialog.newInstance(recurrenceSettings)
        }
        val recurrencePickerDialog by lazy {
            RecurrencePickerDialog.newInstance(recurrenceSettings)
        }
        var selectedRecurrence = Recurrence(Recurrence.Period.NONE)

        fun toggleBottomMenu(showFab: Boolean = false, visibility: Int = View.GONE) {
            if (showFab) fab.show() else fab.hide()
            bottomNav.visibility = visibility
            bottomBar.visibility = visibility
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        prefService = PreferenceService(this)

        // Onboarding settings
        val firstStart = prefService.isOnboardingEnabled()
        if (firstStart) {
            val intent = Intent(this, Onboarding::class.java)
            startActivity(intent)
        }

        // Language settings
        val prefLanguage = prefService.getLanguage()
        val prefLocale = Locale(prefLanguage)
        val currentLocale: Locale = resources.configuration.locale
        if (prefLocale != currentLocale) {
            val config: Configuration = resources.configuration
            config.setLocale(prefLocale)
            resources.updateConfiguration(config, null)
            Locale.setDefault(prefLocale)
        }

        super.onCreate(savedInstanceState)

        taskRepository = TaskRepository(this)
        skillRepository = SkillRepository(this)
        categoryRepository = CategoryRepository(this)
        levelService = LevelService(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // "Fragment Navigation" settings
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Toolbar settings
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.heading_tasks)
        setSupportActionBar(toolbar)

        // Drawer settings
        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // "Drawer Navigation" settings
        drawerNav = findViewById(R.id.drawer_nav)
        drawerNav.setNavigationItemSelectedListener(this)

        // "Bottom Navigation" settings
        bottomNav = findViewById(R.id.bottom_nav)
        bottomNav.setOnNavigationItemSelectedListener { onNavigationItemSelected(it) }
        val taskAmount = taskRepository.countByStatus(Status.OPEN)
        bottomNav.getOrCreateBadge(R.id.nav_tasks).number = taskAmount
        bottomNav.getOrCreateBadge(R.id.nav_tasks).isVisible = taskAmount > 0

        // "Icon Dialog" settings
        iconDialog = supportFragmentManager
            .findFragmentByTag(Const.Tags.ICON_DIALOG_TAG) as IconDialog?
            ?: IconDialog.newInstance(IconDialogSettings())

        // "Bottom Bar" settings
        bottomBar = findViewById(R.id.bottom_bar)

        // "Floating Action Button" settings
        fab = findViewById(R.id.fab)
        setFabOnClickListener()

        // Theme settings
        val prefTheme = prefService.getTheme()
        val prefThemeId = Utils.getThemeByName(this, prefTheme)
        setTheme(prefThemeId)

        // "Dark mode" settings
        val isDarkModeOn = prefService.isDarkModeEnabled()
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            val darkModeFlags: Int = this.resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK
            // check if dark mode is enabled by the system
            // do not override by default
            when (darkModeFlags) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
        val decorView = window.decorView
        when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                // set status bar text dark
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            Configuration.UI_MODE_NIGHT_NO,
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                // set status bar text light
                decorView.systemUiVisibility =
                    decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }

        // "Status Bar" settings
        this.window.statusBarColor = Utils.getThemeColor(this, R.attr.colorPrimary)
        val launchCounter = prefService.incrementAppLaunchCounter()
        if (launchCounter == Const.Defaults.APP_LAUNCH_COUNTER_FOR_REVIEW) {
            val manager: ReviewManager = ReviewManagerFactory.create(this)
            val request: Task<ReviewInfo> = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo: ReviewInfo = task.result
                    val flow: Task<Void> = manager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener { }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val headerView = drawerNav.getHeaderView(0)

        // "User name" settings
        userNameView = headerView.findViewById(R.id.user_name) as TextView
        userNameView.text = prefService.getUserName()

        // "User avatar" settings
        avatarView = headerView.findViewById(R.id.user_avatar)
        avatarView.clipToOutline = true
        val avatar = prefService.getUserAvatar()
        val fallbackImage = AppCompatResources.getDrawable(this, R.mipmap.ic_launcher)
        if (avatar != null) {
            val bitmap = avatar.decodeBase64()
            if (bitmap != null) avatarView.setImageBitmap(bitmap)
            else avatarView.setImageDrawable(fallbackImage)
        } else avatarView.setImageDrawable(fallbackImage)

        drawerLayout.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(view: View, v: Float) {
                // XP settings
                val xpView = headerView.findViewById(R.id.xp_counter) as TextView
                val overallXp = taskRepository.countOverallXp()
                xpView.text = getString(R.string.term_xp_value, overallXp)

                // Level settings
                val levelView = headerView.findViewById(R.id.level_counter) as TextView
                val overallLevel = levelService.getOverallLevel(overallXp)
                levelView.text = getString(R.string.term_level_value, overallLevel)
            }

            override fun onDrawerOpened(view: View) {
                // Nothing to do here
            }

            override fun onDrawerClosed(view: View) {
                // Nothing to do here
            }

            override fun onDrawerStateChanged(i: Int) {
                // Nothing to do here
            }
        })
    }

    override val iconDialogIconPack: IconPack get() = App.iconPack

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_tasks -> {
                when (navController.currentDestination?.id) {
                    R.id.skillListFragment -> {
                        navController.navigate(
                            SkillListFragmentDirections
                                .actionSkillListFragmentToTaskListFragment()
                        )
                    }
                    R.id.statisticsFragment -> {
                        navController.navigate(
                            StatisticsFragmentDirections
                                .actionStatisticsFragmentToTaskListFragment()
                        )
                    }
                    R.id.categoryListFragment -> {
                        navController.navigate(
                            CategoryListFragmentDirections
                                .actionCategoryListFragmentToTaskListFragment()
                        )
                    }
                    else -> navController.navigate(R.id.taskListFragment)
                }
            }
            R.id.nav_skills -> {
                when (navController.currentDestination?.id) {
                    R.id.taskListFragment -> {
                        navController.navigate(
                            TaskListFragmentDirections
                                .actionTaskListFragmentToSkillListFragment()
                        )
                    }
                    R.id.statisticsFragment -> {
                        navController.navigate(
                            StatisticsFragmentDirections
                                .actionStatisticsFragmentToSkillListFragment()
                        )
                    }
                    R.id.categoryListFragment -> {
                        navController.navigate(
                            CategoryListFragmentDirections
                                .actionCategoryListFragmentToSkillListFragment()
                        )
                    }
                    else -> navController.navigate(R.id.skillListFragment)
                }
            }
            R.id.nav_statistics -> {
                when (navController.currentDestination?.id) {
                    R.id.taskListFragment -> {
                        navController.navigate(
                            TaskListFragmentDirections
                                .actionTaskListFragmentToStatisticsFragment()
                        )
                    }
                    R.id.skillListFragment -> {
                        navController.navigate(
                            SkillListFragmentDirections
                                .actionSkillListFragmentToStatisticsFragment()
                        )
                    }
                    R.id.categoryListFragment -> {
                        navController.navigate(
                            CategoryListFragmentDirections
                                .actionCategoryListFragmentToStatisticsFragment()
                        )
                    }
                    else -> navController.navigate(R.id.statisticsFragment)
                }
            }
            R.id.nav_categories -> {
                when (navController.currentDestination?.id) {
                    R.id.taskListFragment -> {
                        navController.navigate(
                            TaskListFragmentDirections
                                .actionTaskListFragmentToCategoryListFragment()
                        )
                    }
                    R.id.skillListFragment -> {
                        navController.navigate(
                            SkillListFragmentDirections
                                .actionSkillListFragmentToCategoryListFragment()
                        )
                    }
                    R.id.statisticsFragment -> {
                        navController.navigate(
                            StatisticsFragmentDirections
                                .actionStatisticsFragmentToCategoryListFragment()
                        )
                    }
                    else -> navController.navigate(R.id.categoryListFragment)
                }
            }
            R.id.nav_settings -> {
                navController.navigate(R.id.settingsFragment)
            }
            R.id.nav_help -> {
                navController.navigate(R.id.helpFragment)
            }
        }
        // update selected menu item in bottom navigation as well
        val foundItem = bottomNav.menu.findItem(item.itemId)
        if (foundItem != null) foundItem.isChecked = true
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.basic_options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.settings -> {
            navController.navigate(R.id.settingsFragment)
            true
        }
        R.id.help -> {
            navController.navigate(R.id.helpFragment)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        val selectedIcon = icons[0]
        if (icons.isNotEmpty()) {
            val fragment = supportFragmentManager.primaryNavigationFragment
            if (fragment != null) {
                val fragments = fragment.childFragmentManager.fragments
                fragments.forEach {
                    when (it) {
                        is TaskCreateFragment -> {
                            TaskFragment.setIcon(
                                this,
                                selectedIcon,
                                TaskCreateFragment.binding.iconButton
                            )
                        }
                        is TaskEditFragment -> {
                            TaskFragment.setIcon(
                                this,
                                selectedIcon,
                                TaskEditFragment.binding.iconButton
                            )
                        }
                        is SkillCreateFragment -> {
                            SkillFragment.setIcon(
                                this,
                                selectedIcon,
                                SkillCreateFragment.binding.iconButton
                            )
                        }
                        is SkillEditFragment -> {
                            SkillFragment.setIcon(
                                this,
                                selectedIcon,
                                SkillEditFragment.binding.iconButton
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onRecurrenceCustomClicked() {
        // "Custom..." item in the recurrence list dialog was clicked
        recurrencePickerDialog.selectedRecurrence = selectedRecurrence
        recurrencePickerDialog.startDate = System.currentTimeMillis()
        recurrencePickerDialog.show(supportFragmentManager, Const.Tags.RECURRENCE_PICKER_DIALOG)
    }

    override fun onRecurrencePresetSelected(recurrence: Recurrence) {
        // recurrence preset item in the recurrence list dialog was selected
        updateRecurrence(recurrence)
    }

    override fun onRecurrenceCreated(recurrence: Recurrence) {
        // custom recurrence was created with the recurrence picker fragment
        updateRecurrence(recurrence)
    }

    override fun onBackPressed() {
        if (isBackButtonPressedOnce) {
            // close app if back button was pressed twice in last 2 seconds
            finish()
        }
        isBackButtonPressedOnce = true
        Handler().postDelayed({ isBackButtonPressedOnce = false }, 500)

        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            val fragment = supportFragmentManager.primaryNavigationFragment
            if (fragment != null) {
                val fragments = fragment.childFragmentManager.fragments
                fragments.forEach {
                    when (it) {
                        is TaskCreateFragment -> onBackPressedTaskCreateFragment()
                        is TaskEditFragment -> onBackPressedTaskEditFragment()
                        is SkillEditFragment -> onBackPressedSkillEditFragment()
                        is CategoryListFragment -> onBackPressedCategoryListFragment()
                        is HelpFragment -> onBackPressedHelpFragment()
                        else -> {
                            super.onBackPressed()
                        }
                    }
                }
            }
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun getTheme(): Theme {
        val theme: Theme = super.getTheme()
        val prefTheme = prefService.getTheme()
        val prefThemeId = Utils.getThemeByName(this, prefTheme)
        theme.applyStyle(prefThemeId, true)
        return theme
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionUtils.ACCESS_CALENDAR_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Timber.d("Requested permission has been denied by user")
                    if (SettingsFragment.isCalendarSyncPrefInitialized()) {
                        SettingsFragment.calendarSyncPref.isChecked = false
                    }
                    Timber.d("Due to lack of permissions calendar synchronization was disabled")
                } else {
                    Timber.d("Requested permission has been granted by user")
                }
            }
        }
    }

    private fun onBackPressedSkillEditFragment() {
        val name = SkillEditFragment.binding.name.editText?.text.toString()
        val category = SkillEditFragment.binding.category.editText?.text.toString()
        if (name.isBlank()) {
            SkillEditFragment.binding.name.requestFocus()
            SkillEditFragment.binding.name.error = getString(R.string.error_cannot_be_empty)
        } else {
            val foundSkill = skillRepository.get(name.trim())
            if (name.length < Const.Defaults.MINIMAL_INPUT_LENGTH) {
                SkillEditFragment.binding.name.requestFocus()
                SkillEditFragment.binding.name.error = getString(
                    R.string.error_too_short,
                    Const.Defaults.MINIMAL_INPUT_LENGTH
                )
            } else if (foundSkill != null && foundSkill.id != SkillEditFragment.skill.id) {
                SkillEditFragment.binding.name.requestFocus()
                SkillEditFragment.binding.name.error =
                    getString(R.string.error_skill_already_exists)
            } else if (category.isNotBlank() && category.length < Const.Defaults.MINIMAL_INPUT_LENGTH) {
                SkillEditFragment.binding.category.requestFocus()
                SkillEditFragment.binding.category.error = getString(
                    R.string.error_too_short,
                    Const.Defaults.MINIMAL_INPUT_LENGTH
                )
            } else {
                super.onBackPressed()
            }
        }
    }

    private fun onBackPressedTaskCreateFragment() {
        selectedRecurrence = Recurrence(Recurrence.Period.NONE)
        super.onBackPressed()
    }

    private fun onBackPressedTaskEditFragment() {
        val goal = TaskEditFragment.binding.goal.editText?.text.toString()
        when {
            goal.isBlank() -> {
                TaskEditFragment.binding.goal.requestFocus()
                TaskEditFragment.binding.goal.error = getString(R.string.error_cannot_be_empty)
            }
            goal.length < Const.Defaults.MINIMAL_INPUT_LENGTH -> {
                TaskEditFragment.binding.goal.requestFocus()
                TaskEditFragment.binding.goal.error = getString(
                    R.string.error_too_short,
                    Const.Defaults.MINIMAL_INPUT_LENGTH
                )
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    private fun onBackPressedHelpFragment() {
        toolbar.title = getString(R.string.heading_help)
        HelpFragment.binding.manualButton.visibility = View.VISIBLE
        HelpFragment.binding.replayIntroButton.visibility = View.VISIBLE
        HelpFragment.binding.licensesButton.visibility = View.VISIBLE
        HelpFragment.binding.versionButton.visibility = View.VISIBLE
        super.onBackPressed()
    }

    private fun onBackPressedCategoryListFragment() {
        categoryAdapter.closeMenu()
        super.onBackPressed()
    }

    private fun setFabOnClickListener() {
        fab.setOnClickListener {
            val fragment = supportFragmentManager.primaryNavigationFragment
            if (fragment != null) {
                val fragments = fragment.childFragmentManager.fragments
                fragments.forEach {
                    when (it) {
                        is TaskListFragment -> navController.navigate(R.id.action_taskListFragment_to_createTaskFragment)
                        is SkillListFragment -> navController.navigate(R.id.action_skillListFragment_to_createSkillFragment)
                        is CategoryListFragment -> showCreateCategoryDialog()
                    }
                }
            }
        }
    }

    private fun showCreateCategoryDialog() {
        categoryAdapter.closeMenu()
        val dialog = EditTextDialog.newInstance(
            title = getString(R.string.heading_create_category),
            hint = getString(R.string.hint_category_name),
            positiveButton = R.string.action_create
        )
        dialog.onPositiveButtonListener = {
            val name = dialog.editText.text.toString().trim()
            if (name.length < Const.Defaults.MINIMAL_INPUT_LENGTH) {
                dialog.editText.requestFocus()
                dialog.editText.error = getString(
                    R.string.error_too_short,
                    Const.Defaults.MINIMAL_INPUT_LENGTH
                )
            } else {
                val foundCategory = categoryRepository.get(name)
                if (foundCategory != null) {
                    dialog.editText.requestFocus()
                    dialog.editText.error = getString(R.string.error_category_already_exists)
                } else {
                    val categoryId = categoryRepository.create(name, null)
                    categoryId.observe(this, { id ->
                        if (id != null) {
                            categoryAdapter.categories.add(Category(id = id, name = name))
                            CategoryListFragment.sortCategories(
                                dialog.editText.context,
                                categoryAdapter.categories
                            )
                            categoryAdapter.notifyDataSetChanged()
                        }
                    })
                    dialog.dismiss()
                }
            }
        }
        dialog.onNegativeButtonClicked = {
            dialog.dismiss()
        }
        dialog.show(supportFragmentManager, EditTextDialog::class.simpleName)
    }

    private fun updateRecurrence(recurrence: Recurrence) {
        selectedRecurrence = recurrence
        val fragment = supportFragmentManager.primaryNavigationFragment
        if (fragment != null) {
            val fragments = fragment.childFragmentManager.fragments
            fragments.forEach {
                when (it) {
                    is TaskCreateFragment ->
                        TaskCreateFragment.recurrenceButton.text =
                            RecurrenceFormatter(App.dateTimeFormat).format(this, selectedRecurrence)
                    is TaskEditFragment ->
                        TaskEditFragment.recurrenceButton.text =
                            RecurrenceFormatter(App.dateTimeFormat).format(this, selectedRecurrence)
                    is RecurrencePickerFragment -> {
                        if (TaskCreateFragment.isRecurrenceButtonInitialized()) {
                            TaskCreateFragment.recurrenceButton.text =
                                RecurrenceFormatter(App.dateTimeFormat).format(
                                    this,
                                    selectedRecurrence
                                )
                        }
                        if (TaskEditFragment.isRecurrenceButtonInitialized()) {
                            TaskEditFragment.recurrenceButton.text =
                                RecurrenceFormatter(App.dateTimeFormat).format(
                                    this,
                                    selectedRecurrence
                                )
                        }
                    }
                }
            }
        }
    }
}
