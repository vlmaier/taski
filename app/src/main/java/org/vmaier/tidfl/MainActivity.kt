package org.vmaier.tidfl

import android.os.Bundle
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
import org.vmaier.tidfl.data.DatabaseHandler
import org.vmaier.tidfl.databinding.ActivityMainBinding
import org.vmaier.tidfl.features.skills.CreateSkillFragment
import org.vmaier.tidfl.features.tasks.CreateTaskFragment
import org.vmaier.tidfl.features.tasks.EditTaskFragment


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener, IconDialog.Callback {

    lateinit var navController: NavController
    lateinit var iconDialog: IconDialog
    lateinit var binding: ActivityMainBinding
    lateinit var toolbar: Toolbar
    lateinit var navView: NavigationView

    companion object {
        private const val ICON_DIALOG_TAG = "icon_dialog"
        lateinit var drawerLayout: DrawerLayout
        lateinit var xpCounter: TextView
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

        val headerView = navView.getHeaderView(0)
        xpCounter = headerView.findViewById<View>(R.id.xp_counter) as TextView
        xpCounter.text = "${DatabaseHandler(this).calculateOverallXp()}XP"

        iconDialog = supportFragmentManager.findFragmentByTag(ICON_DIALOG_TAG) as IconDialog?
            ?: IconDialog.newInstance(IconDialogSettings())
    }

    fun selectIconButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        iconDialog.show(supportFragmentManager, ICON_DIALOG_TAG)
    }

    override val iconDialogIconPack: IconPack?
        get() = App.iconPack

    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        val selectedIcon = icons[0]
        if (icons.isNotEmpty()) {
            val fragment = supportFragmentManager.primaryNavigationFragment!!
            val fragments = fragment.childFragmentManager.fragments
            fragments.forEach {
                when (it) {
                    is CreateTaskFragment -> {
                        CreateTaskFragment.setIcon(this, selectedIcon)
                    }
                    is EditTaskFragment -> {
                        EditTaskFragment.setIcon(this, selectedIcon)
                    }
                    is CreateSkillFragment -> {
                        CreateSkillFragment.setIcon(this, selectedIcon)
                    }
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {

            }
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
}
