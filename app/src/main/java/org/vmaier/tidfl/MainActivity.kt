package org.vmaier.tidfl

import android.content.res.Resources.Theme
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack
import org.vmaier.tidfl.data.AppDatabase
import org.vmaier.tidfl.data.Status
import org.vmaier.tidfl.databinding.ActivityMainBinding
import org.vmaier.tidfl.features.skills.SkillCreateFragment
import org.vmaier.tidfl.features.skills.SkillEditFragment
import org.vmaier.tidfl.features.skills.SkillFragment
import org.vmaier.tidfl.features.tasks.TaskCreateFragment
import org.vmaier.tidfl.features.tasks.TaskEditFragment
import org.vmaier.tidfl.features.tasks.TaskFragment
import org.vmaier.tidfl.utils.Const
import org.vmaier.tidfl.utils.decodeBase64


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener, IconDialog.Callback {

    private lateinit var navController: NavController
    private lateinit var iconDialog: IconDialog
    private lateinit var drawerNav: NavigationView
    lateinit var binding: ActivityMainBinding

    companion object {
        lateinit var toolbar: Toolbar
        lateinit var drawerLayout: DrawerLayout
        lateinit var bottomNav: BottomNavigationView
        lateinit var xpCounterView: TextView
        lateinit var levelCounterView: TextView
        lateinit var userNameView: TextView
        lateinit var avatarView: ImageView
        lateinit var db: AppDatabase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // --- Fragment Navigation Settings
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // --- Toolbar Settings
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.heading_tasks)
        setSupportActionBar(toolbar)

        // --- Drawer Settings
        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // --- Drawer Navigation Settings
        drawerNav = findViewById(R.id.drawer_nav)
        drawerNav.setNavigationItemSelectedListener(this)

        // --- Bottom Navigation Settings
        bottomNav = findViewById(R.id.bottom_nav)
        bottomNav.setOnNavigationItemSelectedListener {
            onNavigationItemSelected(it)
        }
        val taskAmount = db.taskDao().countByStatus(Status.OPEN)
        bottomNav.getOrCreateBadge(R.id.nav_tasks).number = taskAmount
        bottomNav.getOrCreateBadge(R.id.nav_tasks).isVisible = taskAmount > 0

        // --- Icon Dialog Settings
        iconDialog = supportFragmentManager
            .findFragmentByTag(Const.Tag.ICON_DIALOG_TAG) as IconDialog?
            ?: IconDialog.newInstance(IconDialogSettings())
    }

    override fun onStart() {
        super.onStart()
        val headerView = drawerNav.getHeaderView(0)

        // --- User name settings
        userNameView = headerView.findViewById(R.id.user_name) as TextView
        userNameView.text = getDefaultSharedPreferences(this)
            .getString(Const.Prefs.USER_NAME, getString(R.string.app_name))

        // --- User avatar settings
        avatarView = headerView.findViewById(R.id.user_avatar)
        avatarView.clipToOutline = true
        val avatar = getDefaultSharedPreferences(this)
            .getString(Const.Prefs.USER_AVATAR, null)
        val fallbackImage = getDrawable(R.mipmap.ic_launcher_round)
        if (avatar != null) {
            val bitmap = avatar.decodeBase64()
            if (bitmap != null) avatarView.setImageBitmap(bitmap)
            else avatarView.setImageDrawable(fallbackImage)
        } else avatarView.setImageDrawable(fallbackImage)

        // --- XP value settings
        xpCounterView = headerView.findViewById(R.id.xp_counter) as TextView
        val xpValue = db.taskDao().countOverallXpValue()
        xpCounterView.text = getString(R.string.term_xp_value, xpValue)

        // --- Level settings
        levelCounterView = headerView.findViewById(R.id.level_counter) as TextView
        val level = xpValue.div(10000) + 1
        levelCounterView.text = getString(R.string.term_level_value, level)
    }

    fun selectIconButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        iconDialog.show(supportFragmentManager, Const.Tag.ICON_DIALOG_TAG)
    }

    override val iconDialogIconPack: IconPack?
        get() = App.iconPack

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_tasks -> {
                navController.navigate(R.id.taskListFragment)
            }
            R.id.nav_skills -> {
                navController.navigate(R.id.skillListFragment)
            }
        }
        // update selected menu item in bottom navigation as well
        bottomNav.menu.findItem(item.itemId).isChecked = true
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
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
                                this, selectedIcon, TaskCreateFragment.binding.iconButton
                            )
                        }
                        is TaskEditFragment -> {
                            TaskFragment.setIcon(
                                this, selectedIcon, TaskEditFragment.binding.iconButton
                            )
                        }
                        is SkillCreateFragment -> {
                            SkillFragment.setIcon(
                                this, selectedIcon, SkillCreateFragment.binding.iconButton
                            )
                        }
                        is SkillEditFragment -> {
                            SkillFragment.setIcon(
                                this, selectedIcon, SkillEditFragment.binding.iconButton
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            val fragment = supportFragmentManager.primaryNavigationFragment
            if (fragment != null) {
                val fragments = fragment.childFragmentManager.fragments
                fragments.forEach {
                    when (it) {
                        is SkillEditFragment -> {
                            val name = SkillEditFragment.binding.name.editText?.text.toString()
                            if (name.isBlank()) {
                                SkillEditFragment.binding.name.requestFocus()
                                SkillEditFragment.binding.name.error =
                                    getString(R.string.error_name_cannot_be_empty)
                            } else {
                                val foundSkill = db.skillDao().findByName(name)
                                if (foundSkill != null && foundSkill.id != SkillEditFragment.skill.id) {
                                    SkillEditFragment.binding.name.requestFocus()
                                    SkillEditFragment.binding.name.error =
                                        getString(R.string.error_skill_already_exists)
                                } else {
                                    super.onBackPressed()
                                }
                            }
                        }
                        is TaskEditFragment -> {
                            val goal = TaskEditFragment.binding.goal.editText?.text.toString()
                            if (goal.isBlank()) {
                                TaskEditFragment.binding.goal.requestFocus()
                                TaskEditFragment.binding.goal.error =
                                    getString(R.string.error_goal_cannot_be_empty)
                            } else super.onBackPressed()
                        }
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

    override fun getTheme(): Theme? {
        val theme: Theme = super.getTheme()
        val selectedTheme = getDefaultSharedPreferences(this)
            .getString(Const.Prefs.APP_THEME, "Theme.Default")
        if (selectedTheme == "Theme.Default") {
            theme.applyStyle(R.style.Theme_Default, true)
        } else if (selectedTheme == "Theme.Sailor") {
            theme.applyStyle(R.style.Theme_Sailor, true)
        }
        return theme
    }
}
