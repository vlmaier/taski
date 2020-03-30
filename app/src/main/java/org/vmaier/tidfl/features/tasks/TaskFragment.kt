package org.vmaier.tidfl.features.tasks

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconDrawableLoader
import org.vmaier.tidfl.App
import org.vmaier.tidfl.util.setThemeTint
import kotlin.random.Random


/**
 * Created by Vladas Maier
 * on 15/02/2020.
 * at 15:22
 */
open class TaskFragment : Fragment() {

    companion object {
        lateinit var cntxt: Context

        const val KEY_GOAL = "goal"
        const val KEY_DETAILS = "details"
        const val KEY_DIFFICULTY = "difficulty"
        const val KEY_DURATION = "duration"
        const val KEY_SKILLS = "skills"
        const val KEY_ICON_ID = "icon_id"

        fun setIcon(context: Context, icon: Icon, button: ImageButton) {
            val drawable = IconDrawableLoader(context).loadDrawable(icon)
            drawable.setThemeTint(context)
            button.background = drawable
            button.tag = icon.id
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        cntxt = context
    }

    fun setTaskIcon(saved: Bundle?, button: ImageButton) {

        // if the icon is not saved / available choose a random one
        val iconId =
            saved?.getInt(KEY_ICON_ID)
                ?:
                Random.nextInt(App.iconPack.allIcons.size)

        val iconDrawable =
            App.iconPack.getIconDrawable(iconId, IconDrawableLoader(cntxt))

        iconDrawable.setThemeTint(cntxt)

        button.background = iconDrawable
        button.tag = iconId
    }
}