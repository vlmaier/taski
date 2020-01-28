package org.vmaier.tidfl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack
import kotlinx.android.synthetic.main.fragment_create_task.*
import org.vmaier.tidfl.data.Difficulty
import org.vmaier.tidfl.data.Task
import org.vmaier.tidfl.databinding.ActivityMainBinding
import org.vmaier.tidfl.features.tasks.DatabaseHandler


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
    }

    fun createTaskButtonClicked(view : View) {

        val dbHandler = DatabaseHandler(this)
        val goal = goal.text.toString()
        val details = details.text.toString()
        val duration = duration_value.selectedItem.toString().toInt()
        val finalDuration = when (duration_unit.selectedItem.toString()) {
            "minutes" -> duration
            "hours" -> duration * 60
            "days" -> duration * 60 * 24
            else -> {
                30
            }
        }
        val difficulty = when (difficulty.selectedItem.toString()) {
            "trivial" -> Difficulty.TRIVIAL
            "regular" -> Difficulty.REGULAR
            "hard" -> Difficulty.HARD
            "insane" -> Difficulty.INSANE
            else -> {
                Difficulty.REGULAR
            }
        }
        val icon : Int = Integer.parseInt(select_icon_button.tag.toString())
        val task = Task(goal = goal, details = details, duration = finalDuration, difficulty = difficulty, icon = icon)
        dbHandler.addTask(task)
    }

    fun selectIconButtonClicked(view: View) {
        iconDialog.show(supportFragmentManager, ICON_DIALOG_TAG)
    }

    override val iconDialogIconPack: IconPack?
        get() = App.iconPack

    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        val selectedIcon = icons[0]
        select_icon_button.background = selectedIcon.drawable
        select_icon_button.tag = selectedIcon.id
    }

    companion object {
        private const val ICON_DIALOG_TAG = "icon_dialog"
    }
}
