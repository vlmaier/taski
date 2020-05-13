package org.vmaier.tidfl

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack
import org.vmaier.tidfl.data.AppDatabase
import org.vmaier.tidfl.databinding.ActivityMainBinding
import org.vmaier.tidfl.features.skills.SkillCreateFragment
import org.vmaier.tidfl.features.skills.SkillEditFragment
import org.vmaier.tidfl.features.skills.SkillFragment
import org.vmaier.tidfl.features.tasks.TaskCreateFragment
import org.vmaier.tidfl.features.tasks.TaskEditFragment
import org.vmaier.tidfl.features.tasks.TaskFragment


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener, IconDialog.Callback {

    private lateinit var navController: NavController
    private lateinit var iconDialog: IconDialog
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    lateinit var binding: ActivityMainBinding

    companion object {
        private const val ICON_DIALOG_TAG = "icon_dialog"
        lateinit var drawerLayout: DrawerLayout
        lateinit var xpCounter: TextView
        lateinit var levelCounter: TextView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navController = this.findNavController(R.id.nav_host_fragment)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        iconDialog = supportFragmentManager.findFragmentByTag(ICON_DIALOG_TAG) as IconDialog?
                ?: IconDialog.newInstance(IconDialogSettings())
    }

    override fun onStart() {
        super.onStart()
        val db = AppDatabase(this)
        val headerView = navView.getHeaderView(0)
        xpCounter = headerView.findViewById<View>(R.id.xp_counter) as TextView
        levelCounter = headerView.findViewById<View>(R.id.level_counter) as TextView
        val xp = db.taskDao().countOverallXpValue()
        val level = xp.div(10000) + 1
        xpCounter.text = "$xp XP"
        levelCounter.text = "Level $level"
    }

    fun selectIconButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        iconDialog.show(supportFragmentManager, ICON_DIALOG_TAG)
    }

    override val iconDialogIconPack: IconPack?
        get() = App.iconPack

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
                                this, selectedIcon, TaskCreateFragment.binding.iconButton)
                        }
                        is TaskEditFragment -> {
                            TaskFragment.setIcon(
                                this, selectedIcon, TaskEditFragment.binding.iconButton)
                        }
                        is SkillCreateFragment -> {
                            SkillFragment.setIcon(
                                this, selectedIcon, SkillCreateFragment.binding.iconButton)
                        }
                        is SkillEditFragment -> {
                            SkillFragment.setIcon(
                                this, selectedIcon, SkillEditFragment.binding.iconButton)
                        }
                    }
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_tasks -> {
                navController.navigate(R.id.taskListFragment)
            }
            R.id.nav_skills -> {
                navController.navigate(R.id.skillListFragment)
            }
        }
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
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
