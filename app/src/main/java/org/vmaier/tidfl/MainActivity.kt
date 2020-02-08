package org.vmaier.tidfl

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack
import org.vmaier.tidfl.databinding.ActivityMainBinding
import org.vmaier.tidfl.features.tasks.CreateTaskFragment
import org.vmaier.tidfl.features.tasks.EditTaskFragment


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
class MainActivity : AppCompatActivity(), IconDialog.Callback {

    lateinit var navController: NavController
    lateinit var iconDialog: IconDialog
    lateinit var binding: ActivityMainBinding

    companion object {
        private const val ICON_DIALOG_TAG = "icon_dialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navController = this.findNavController(R.id.nav_host_fragment)
        iconDialog = supportFragmentManager.findFragmentByTag(ICON_DIALOG_TAG) as IconDialog?
            ?: IconDialog.newInstance(IconDialogSettings())
    }

    fun selectIconButtonClicked(view: View) {
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
                if (it is CreateTaskFragment) {
                    CreateTaskFragment.setIcon(this, selectedIcon)
                } else if (it is EditTaskFragment) {
                    EditTaskFragment.setIcon(this, selectedIcon)
                }
            }
        }
    }
}
