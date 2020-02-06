package org.vmaier.tidfl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack
import kotlinx.android.synthetic.main.fragment_create_task.*
import org.vmaier.tidfl.databinding.ActivityMainBinding


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
class MainActivity : AppCompatActivity(), IconDialog.Callback {

    lateinit var navController: NavController
    lateinit var iconDialog: IconDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        navController = this.findNavController(R.id.nav_host_fragment)
        iconDialog = supportFragmentManager.findFragmentByTag(ICON_DIALOG_TAG) as IconDialog?
            ?: IconDialog.newInstance(IconDialogSettings())

        // this.deleteDatabase("tidfl")
    }

    fun selectIconButtonClicked(view: View) {
        iconDialog.show(supportFragmentManager, ICON_DIALOG_TAG)
        view.invalidate()
    }

    override val iconDialogIconPack: IconPack?
        get() = App.iconPack

    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        val selectedIcon = icons[0]
        if (icons.isNotEmpty()) {
            val iconDrawable = selectedIcon.drawable
            iconDrawable?.clearColorFilter()
            select_icon_button.background = iconDrawable
            select_icon_button.tag = selectedIcon.id
            Toast.makeText(this, "Selected Icon ID: ${selectedIcon.id}", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val ICON_DIALOG_TAG = "icon_dialog"
    }
}
